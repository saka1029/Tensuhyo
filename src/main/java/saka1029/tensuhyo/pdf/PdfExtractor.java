package saka1029.tensuhyo.pdf;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

/**
 * 縦書きのとき（施設基準の告示など）はhorizontal = falseとする。
 * この場合「(1)」などを１文字の組み文字にしているため、行の幅を広くとる必要がある。
 * そうするとルビを独立した行としてとらえられなくなるので、
 * ルビは行単位ではなく文字単位で除外する。
 */
public class PdfExtractor extends PDFTextStripper {
	
	private static final Logger logger = Logger.getLogger(PdfExtractor.class.getName());
	private static void log(Level level, String format, Object... args) {
		if (!logger.isLoggable(level)) return;
		logger.log(level, String.format(format, args));
	}
	
	interface LineWriter {
	    void write(int page, String line) throws IOException;
	}

	public boolean debug = false;
	public boolean sort = true;
	public boolean writeRuby = false;
	public boolean horizontal = true;
	public float rubySizeRate = 0.6f;
	public float maxX = 5000;
	public String newLine = "\r\n";
	
	private static final String SPACE = " ";
	
	private String file;
	private LineWriter writer;
	private List<TextPosition> texts = new ArrayList<>();

	private float freqFontSize = 10.0f;
	private float freqLineDistance = 14.8f;
	private float freqCharDistance = 10.3f;
	private float defaultSpaceSize() { return freqFontSize / 2.0f; }
	private float maxRubyFontSize() { return freqFontSize * rubySizeRate; }
	private float xDelta() { return horizontal ? freqCharDistance * 0.01f : freqLineDistance * 0.3f; }
	private float yDelta() { return horizontal ? freqLineDistance * 0.3f : freqCharDistance * 0.95f; }
	
	public PdfExtractor() throws IOException { super(); }
	
	private float x(TextPosition tp) { return horizontal ? tp.getX() : tp.getY(); }
	private float y(TextPosition tp) { return horizontal ? tp.getY() : maxX - tp.getX(); }

	private class CompTextPosition implements Comparator<TextPosition> {
		
		private float xDelta;
		private float yDelta;
		
		public CompTextPosition(float xDelta, float yDelta) {
			this.xDelta = xDelta;
			this.yDelta = yDelta;
		}

//        @Override
//        public int compare(TextPosition o1, TextPosition o2) {
//            float factor = 2.0F;
//            long y1 = (long)(y(o1) / yDelta / factor);
//            long x1 = (long)(x(o1) / xDelta / factor);
//            long y2 = (long)(y(o2) / yDelta / factor);
//            long x2 = (long)(x(o2) / xDelta / factor);
//            long diff = y1 - y2;
//            if (diff == 0)
//                diff = x1 - x2;
//            return (int)diff;
//        }

		@Override
		public int compare(TextPosition left, TextPosition right) {
//			return horizontal ? horizontal(left, right) : vertical(left, right);
			float diff = y(left) - y(right);
			if (Math.abs(diff) <= yDelta) {
				diff = x(left) - x(right);
				if (Math.abs(diff) <= xDelta)
					return 0;
			}
			return diff < 0 ? -1 : 1;
		}
	}
	
	@Override
	protected void processTextPosition(TextPosition tp) {
		texts.add(tp);
	}
	
	private static final float roundDistance(float a, float b) {
		return Math.round((a - b) * 10.0f) / 10.0f;
	}
	
	private void profiling(PDDocument doc, int page) throws IOException {
		// 縦書き・横書きに関係なく常にPORTRAITとなる。
//		try {
//			PDPageable pdp = new PDPageable(doc);
//			int o = pdp.getPageFormat(page).getOrientation();
//			log(Level.INFO, "### page %d format=%s", page,
//				o == PageFormat.LANDSCAPE ? "LANDSCAPE" : o == PageFormat.PORTRAIT ? "PORTRAIT" : "REVERSE_LANDSCAPE");
//		} catch (IllegalArgumentException | PrinterException e1) {
//		}
		this.texts.clear();
		setStartPage(page);
		setEndPage(page);
		getText(doc);
		Collections.sort(texts, new CompTextPosition(0, 0));
		Map<Float, Integer> fontSizeFreq = new TreeMap<>();
		Map<Float, Integer> lineDistanceFreq = new TreeMap<>();
		Map<Float, Integer> charDistanceFreq = new TreeMap<>();
		float prevX = 0.0f;
		float prevY = 0.0f;
		for (TextPosition tp : texts) {
			float fontSize = tp.getFontSizeInPt();
			Integer fontSizeCount = fontSizeFreq.get(fontSize);
			if (fontSizeCount == null) fontSizeCount = 0;
			fontSizeFreq.put(fontSize, fontSizeCount + 1);
			float lineDistance = roundDistance(y(tp), prevY);
			if (lineDistance > 0) {
				Integer lineDistanceCount = lineDistanceFreq.get(lineDistance);
				if (lineDistanceCount == null) lineDistanceCount = 0;
				lineDistanceFreq.put(lineDistance, lineDistanceCount + 1);
				prevY = y(tp);
			}
			if (prevX != 0.0f) {
				float charDistance = roundDistance(x(tp), prevX);
				if (charDistance > 0) {
					Integer charDistanceCount = charDistanceFreq.get(charDistance);
					if (charDistanceCount == null) charDistanceCount = 0;
					charDistanceFreq.put(charDistance, charDistanceCount + 1);
				}
			}
			prevX = x(tp);
		}
		int maxFontSizeCount = 0;
		log(Level.INFO, "%s(page:%d): profiling result", file, page);
		for (Entry<Float, Integer> e : fontSizeFreq.entrySet()) {
			if (debug)
				log(Level.INFO, "  font size=%f count=%d", e.getKey(), e.getValue());
			if (e.getValue() > maxFontSizeCount) {
				maxFontSizeCount = e.getValue();
				freqFontSize = e.getKey();
			}
		}
		int maxLineDistanceCount = 0;
		for (Entry<Float, Integer> e : lineDistanceFreq.entrySet()) {
			if (debug)
				log(Level.INFO, "  line distance=%f count=%d", e.getKey(), e.getValue());
			if (e.getValue() > maxLineDistanceCount) {
				maxLineDistanceCount = e.getValue();
				freqLineDistance = e.getKey();
			}
		}
		int maxCharDistanceCount = 0;
		for (Entry<Float, Integer> e : charDistanceFreq.entrySet()) {
			if (debug)
				log(Level.INFO, "  char distance=%f count=%d", e.getKey(), e.getValue());
			if (e.getValue() > maxCharDistanceCount) {
				maxCharDistanceCount = e.getValue();
				freqCharDistance = e.getKey();
			}
		}
	}
	
	private void write(int page, StringBuilder line) throws IOException {
		writer.write(page, line.toString());
	}
	
	private void write(int page) throws IOException {
		float prevX = 0.0f;
		float prevY = -1.0f;
		float prevW = freqFontSize;
		float spaceSize = defaultSpaceSize();
		float maxH = 0.0f;
		float maxRubyFontSize = maxRubyFontSize();
		float yDelta = yDelta();
		StringBuilder line = new StringBuilder();
		int lineNo = 0;
		for (TextPosition tp : texts) {
			float x = x(tp);
			float y = y(tp);
			float w = tp.getWidth();
			float h = tp.getFontSizeInPt();
			boolean wrote = false;
			// 縦書きの場合はルビを行単位ではなく文字単位で除去します。
			if (!horizontal && h <= maxRubyFontSize)
			    continue;
			if (x < prevX || prevY > 0.0f && y - prevY > yDelta) {
				++lineNo;
				if (writeRuby || maxH > maxRubyFontSize)
					write(page, line);
				else
					log(Level.INFO, "%s(page:%d line:%d): drop ruby line:%s", file, page, lineNo, line);
				line.setLength(0);
				prevX = 0;
				maxH = 0.0f;
				wrote = true;
				prevW = freqFontSize;
				spaceSize = defaultSpaceSize();	// 先頭の空白は平均的半角サイズ
			}
			if (h > maxH) maxH = h;
			for (float p = prevX + prevW + spaceSize; p < x; p += spaceSize)
				line.append(SPACE);
			line.append(tp.getCharacter());
			prevY = y;
			prevX = x;
			if (!wrote) {
				prevW = w;
				spaceSize = h / 2;	// 先頭以外は直前文字の半角サイズ
			}
		}
		write(page, line);
	}
	
	private void extract(PDDocument doc, int page) throws IOException {
		this.texts.clear();
		setStartPage(page);
		setEndPage(page);
		getText(doc);
		if (sort)
			Collections.sort(texts, new CompTextPosition(xDelta(), yDelta()));
		if (debug)
			for (TextPosition tp : texts)
				writer.write(page, String.format("file=%s:%d,xy=(%f,%f),size=(%f,%f),font=%f,%s%n",
					file, page,
					tp.getX(), tp.getY(),
					tp.getWidth(), tp.getHeight(),
					tp.getFontSizeInPt(),
					tp.getCharacter()));
		else
			write(page);
	}

	private void extract(PDDocument doc, String file, LineWriter writer) throws IOException {
		int size = doc.getNumberOfPages();
		if (size <= 0) return;
		this.file = file;
		int profilingPage = size / 2;
		if (profilingPage <= 0) profilingPage = 1;
		profiling(doc, profilingPage);
		this.writer = writer;
		for (int i = 1; i <= size; ++i)
			extract(doc, i);
	}

	public void extract(PDDocument doc, String file, Writer writer) throws IOException {
	    extract(doc, file, new LineWriter() {
            @Override
            public void write(int page, String line) throws IOException {
                writer.write(line);
                writer.write(newLine);
            }
		});
		writer.flush();
	}
	
	public TreeMap<Integer, List<String>> extract(PDDocument doc, String file) throws IOException {
		TreeMap<Integer, List<String>> r = new TreeMap<>();
		extract(doc, file, new LineWriter() {
            @Override
            public void write(int page, String line) throws IOException {
                List<String> list = r.get(page);
                if (list == null)
                    r.put(page, list = new ArrayList<>());
                list.add(line);
            }
		});
		return r;
	}
	
}
