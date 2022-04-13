package saka1029.tensuhyo.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
// import java.util.logging.Logger;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;
import org.apache.pdfbox.util.TextPosition;

import saka1029.tensuhyo.util.TextWriter;

public class Pdf {

    // private static Logger logger = Logger.getLogger(Pdf.class.getName());
    private static void info(String format, Object... args) {
//        logger.info(String.format(format, args));
    }
//    private static void fine(String format, Object... args) { logger.fine(String.format(format, args)); }

    public class PageLine {
        public int page, line;
        public PageLine(int page, int line) { this.page = page; this.line = line; }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PageLine))
                return false;
            PageLine o = (PageLine)obj;
            return o.page == page && o.line == line;
        }
        @Override public int hashCode() { return (page << 8) ^ line; }
    }

    private final Set<PageLine> debugLines = new HashSet<>();
    /**
     * 指定した頁および行のSVGイメージを出力します。
     * 出力先はカレントディレクトリで、ファイル名は元のファイル名 + "debug.svg"です。
     * @param pageNo SVGイメージを出力したい頁番号を指定します。先頭は１です。
     * @param lineNo SVGイメージを出力したい行番号を指定します。先頭は１です。
     */
    public void addDebugLine(int pageNo, int lineNo) { debugLines.add(new PageLine(pageNo, lineNo)); }

    private TextWriter debugWriter = null;

    final File file;
    final boolean horizontal;
    final List<Page> pages = new ArrayList<>();
    final Map<Float, Integer> freqFontSize = new TreeMap<>();
    final Map<Float, Integer> freqLineHeight = new TreeMap<>();
    float leftMargin = Float.MAX_VALUE;

    public enum Skip {
        Text, Line;
        public static final EnumSet<Skip> NONE = EnumSet.noneOf(Skip.class);
        public static final EnumSet<Skip> TEXT = EnumSet.of(Text);
        public static final EnumSet<Skip> LINE = EnumSet.of(Line);
        public static final EnumSet<Skip> ALL = EnumSet.of(Text, Line);
    }

    class Text {

        final TextPosition text;

        float fontSize() { return text.getFontSizeInPt(); }
        float x() { return horizontal ? text.getX() : text.getY(); }
        float y() { return horizontal ? text.getY() : -text.getX(); }
        String text() { return text.getCharacter(); }

        Text(TextPosition text) { this.text = text; }

        @Override
        public String toString() {
            return String.format("Text(%s@%s,font=%s,text=\"%s\")",
                x(), y(), fontSize(), text());
        }

        private float width() {
            return text.getCharacter().charAt(0) < 256 ?
                text.getFontSizeInPt() / 2F :
                text.getFontSizeInPt();
        }

        public void writeSVG() {
            debugWriter.printf(
                "<g>" +
                "<rect x='%s' y='%s' width='%s' height='%s' fill-opacity='0' stroke='#f00' stroke-width='0.5' />" +
                "<text x='%s' y='%s' font-size='%s'>%s</text>" +
                "</g>\n",
                text.getX(), text.getY(), width(), text.getFontSizeInPt(),
                text.getX(), text.getY() + text.getFontSizeInPt(), text.getFontSizeInPt(), text.getCharacter());
        }

    }

    static Comparator<Text> TEXT_COMP =
        (o1, o2) -> {
            float r = o2.y() - o1.y();
            if (r == 0F)
                r = o1.x() - o2.x();
            return (int)Math.signum(r);
        };

    class Line {

        final TreeMap<Float, TreeSet<Text>> texts = new TreeMap<>();
        float maxFontSize = -Float.MAX_VALUE;

        void add(Text text) {
            float k = text.x();
            TreeSet<Text> v = texts.get(k);
            if (v == null)
                texts.put(k, v = new TreeSet<>(TEXT_COMP));
            v.add(text);
            if (text.fontSize() > maxFontSize)
                maxFontSize = text.fontSize();
        }

        private float lastSize(String s, float fontSize) {
            if (s == null || s.length() == 0) return fontSize;
            char c = s.charAt(s.length() - 1);
            return c <= 0xff ? fontSize / 2 : fontSize;
        }

        void merge(Line line) {
            for (Entry<Float, TreeSet<Text>> e : line.texts.entrySet()) {
                float key = e.getKey();
                TreeSet<Text> value = e.getValue();
                if (texts.containsKey(key))
                    texts.get(key).addAll(value);
                else
                    texts.put(key, new TreeSet<>(value));
            }
            maxFontSize = Math.max(maxFontSize, line.maxFontSize);
        }

        String toString(int pageNo, int lineNo, float leftMargin, float fontSize, float rubyRate, EnumSet<Skip> skip) {
            float half = fontSize / 2;
            float prev = leftMargin;
            float rubySize = fontSize * rubyRate;
            StringBuilder sb = new StringBuilder();
            for (Entry<Float, TreeSet<Text>> e : texts.entrySet()) {
                float k = e.getKey();
                TreeSet<Text> v = e.getValue();
                for (float f = prev; f < k; f += half)
                    sb.append(" ");
                String last = null;
                for (Text t : v)
                    if (skip.contains(Skip.Text) && t.fontSize() <= rubySize)
                        info("skip text: page=%s line=%s : %s", pageNo, lineNo, t);
                    else
                        sb.append(last = t.text());
                prev = k + lastSize(last, fontSize) + half;
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return String.format("Line(maxFontSize=%s,texts=%s)",
                maxFontSize, texts);
        }
    }

    class Page {

        final TreeMap<Float, Line> lines = new TreeMap<>();

        void add(TextPosition t) {
            Text text = new Text(t);
            float k = text.y();
            Line v = lines.get(k);
            if (v == null)
                lines.put(k, v = new Line());
            v.add(text);
            float fontSize = text.fontSize();
            Integer c0 = freqFontSize.get(fontSize);
            if (c0 == null) c0 = 0;
            freqFontSize.put(fontSize, c0 + 1);
            Float prev = lines.lowerKey(k);
            if (prev != null) {
                float height = k - prev;
                Integer c1 = freqLineHeight.get(height);
                if (c1 == null) c1 = 0;
                freqLineHeight.put(height, c1 + 1);
            }
            if (text.x() < leftMargin) leftMargin = text.x();
        }

        private void debugLine(int pageNo, int lineNo, Line line) {
            info("debugLine: page=%s line=%s max font=%s", pageNo, lineNo, line.maxFontSize);
            debugWriter.printf("<g id='p%d-%d'>\n", pageNo, lineNo);
            for (Entry<Float, TreeSet<Text>> t : line.texts.entrySet()) {
                info("%s=%s", t.getKey(), t.getValue());
                for (Text e : t.getValue())
                    e.writeSVG();
            }
            debugWriter.printf("</g>\n");
        }

        private void add(int pageNo, List<String> p, Line line, float leftMargin, float fontSize, float rubyRate, EnumSet<Skip> skip, StringFunction mod) {
            int lineNo = p.size() + 1;
            float rubySize = fontSize * rubyRate;
            // 行全体がルビなら無視します。
            if (skip.contains(Skip.Line) && line.maxFontSize <= rubySize)
                info("skip line: page=%s line=%s : %s", pageNo, lineNo, line);
            else {
                String s = line.toString(pageNo, lineNo, leftMargin, fontSize, rubyRate, skip);
                s = mod.eval(s);
                if (s != null) {
                    if (debugLines.contains(new PageLine(pageNo, lineNo)))
                        debugLine(pageNo, lineNo, line);
                    p.add(s);
                }
            }
        }

        void toStringList(List<List<String>> r, float lineHeight, float leftMargin, float fontSize, float rubyRate, EnumSet<Skip> skip, StringFunction mod) {
            List<String> p = new ArrayList<>();
            r.add(p);
            int pageNo = r.size();
            float prevPos = 0F;
            Line prevLine = null;
            if (debugWriter != null)
                debugWriter.printf("<g id='p%s'>\n", pageNo);
            for (Entry<Float, Line> e : lines.entrySet()) {
                float pos = e.getKey();
                Line line = e.getValue();
                if (prevLine == null || pos > prevPos + lineHeight) {
                    if (prevLine != null)
                        add(pageNo, p, prevLine, leftMargin, fontSize, rubyRate, skip, mod);
                    prevPos =pos;
                    prevLine = new Line();
                }
                prevLine.merge(line);
            }
            if (prevLine != null)
                add(pageNo, p, prevLine, leftMargin, fontSize, rubyRate, skip, mod);
            if (debugWriter != null)
                debugWriter.printf("</g>\n");
        }

    }

    static class Stripper extends PDFTextStripper {

        final Page page;

        public Stripper(PDDocument doc, int i, Page page) throws IOException {
            super();
            this.page = page;
            setStartPage(i);
            setEndPage(i);
            getText(doc);
        }

        @Override
        protected void processTextPosition(TextPosition text) {
            page.add(text);
        }

    }

    void load(File file) throws IOException {
        PDDocument doc = PDDocument.load(file);
        try {
            int size = doc.getNumberOfPages();
            for (int i = 1; i <= size; ++i) {
                Page page = new Page();
                pages.add(page);
                new Stripper(doc, i, page);
            }
        } finally {
            doc.close();
        }
    }

    public Pdf(String file, boolean horizontal) throws IOException {
        this.file = new File(file);
        this.horizontal = horizontal;
        load(this.file);
    }

    public List<List<String>> toStringListInternal(float lineHeight, float fontSize, float rubyRate, EnumSet<Skip> skip, StringFunction mod) {
        List<List<String>> r = new ArrayList<>();
        for (Page e : pages)
            e.toStringList(r, lineHeight, leftMargin, fontSize, rubyRate, skip, mod);
        return r;
    }

    /**
     *
     * PDFからList<String>の形式で文字列抽出します。
     *
     * @param lineHeight
     *     行間隔を指定します。
     *     平成26年までのデータでは横書きの場合は5.0F、縦書きの場合は22.0Fを指定しました。
     * @param fontSize
     *     代表的な文字サイズを指定します。
     *     この文字サイズの半分の空白を半角スペースとします。
     *     平成26年までのデータでは横書きの場合は10.0F、縦書きの場合は14.0Fを指定しました。
     * @param rubyRate
     *     除外するルビの文字サイズをfontSizeに対する比率で指定します。
     *     fontSize = 10F rubyRate = 0.6Fの場合
     *     6ポイント以下の文字をルビとみなして除外します。
     *     平成26年までのデータでは0.6Fを指定しました。
     * @param skip
     *     ルビをスキップする単位を指定します。
     *     行単位にスキップする場合はPdf.Skip.LINE
     *     文字単位にスキップする場合はPdf.Skip.TEXT
     *     全てスキップする場合はPdf.Skip.ALL
     *     を指定します。
     *     平成26年までのデータでは横書きの場合はPdf.Skip.LINE、
     *     縦書きの場合はPdf.Skip.TEXTを指定しました。
     * @param mod
     *     行単位に編集を行う関数を指定します。
     *     この関数を適用した後の行が出力されます。
     *     eval(String)がnullを返した場合はその行が出力から除外されます。
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public List<List<String>> toStringList(float lineHeight, float fontSize, float rubyRate, EnumSet<Skip> skip, StringFunction mod) throws IOException {
        if (debugLines.size() > 0) {
            File debugFile = new File(file.getName() + ".debug.svg");
            try (TextWriter w = new TextWriter(
                new OutputStreamWriter(
                    new FileOutputStream(debugFile), "utf-8"))) {
                debugWriter = w;
                w.printf("<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'>\n");
                List<List<String>> r = toStringListInternal(lineHeight, fontSize, rubyRate, skip, mod);
                w.printf("</svg>\n");
                return r;
            }
        } else {
            debugWriter = null;
            return toStringListInternal(lineHeight, fontSize, rubyRate, skip, mod);
        }
    }


//    public List<List<String>> toStringList(float lineHeight, float fontSize, float rubyRate, EnumSet<Skip> skip) throws IOException {
//        return toStringList(lineHeight, fontSize, rubyRate, skip, new StringFunction() {
//            @Override
//            public String eval(String line) {
//                if (line.matches(頁番号) || line.matches(漢字頁番号))
//                    return "#" + line;
//                return line;
//            }
//        });
//    }

    private float mostFreq(Map<Float, Integer> map) {
        float freq = 0;
        int max = -1;
        for (Entry<Float, Integer> e : map.entrySet())
            if (e.getValue() > max) {
                max = e.getValue();
                freq = e.getKey();
            }
        return freq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("file=%s\n", file));
        sb.append(String.format("pageSize=%d\n", pages.size()));
        sb.append(String.format("freqFontSize=%s\n", freqFontSize));
        sb.append(String.format("freqLineHeight=%s\n", freqLineHeight));
        sb.append(String.format("most freqFontSize=%s\n", mostFreq(freqFontSize)));
        sb.append(String.format("most freqLineHeight=%s\n", mostFreq(freqLineHeight)));
        return sb.toString();
    }

    public static void toText(String[] pdfs, String out, boolean horizontal,
        float lineHeight, float fontSize, float rubyRate, EnumSet<Skip> skip, StringFunction mod)
            throws IOException {
        try (TextWriter w = new TextWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"))) {
            for (String fileName : pdfs) {
                File file = new File(fileName);
                Pdf pdf = new Pdf(fileName, horizontal);
                info("toText: %s", file);
                List<List<String>> pages = pdf.toStringList(lineHeight, fontSize, rubyRate, skip, mod);
                for (int i = 0, pageSize = pages.size(); i < pageSize; ++i) {
                    w.printf("# file: %s page: %d\n", file.getName(), i + 1);
                    List<String> lines = pages.get(i);
                    for (int j = 0, lineSize = lines.size(); j < lineSize; ++j)
                        w.printf("%s\n", lines.get(j));
                }
            }
        }
    }

    private static void writePdf(PDDocument doc, int startPage, int endPage, File outDir, 別添 b,  List<別添> list)
        throws COSVisitorException, IOException {
        File outFile = new File(outDir, b.pdfFileName);
        if (!outFile.getParentFile().exists())
            outFile.getParentFile().mkdirs();
        Splitter sp = new Splitter();
        sp.setStartPage(startPage);
        sp.setEndPage(endPage);
        sp.setSplitAtPage(doc.getNumberOfPages());
        info("write page %d-%d to %s header=%s title=%s", startPage, endPage, outFile, b.header, b.title);
        list.add(b);
        List<PDDocument> splits = sp.split(doc);
        try {
            splits.get(0).save(outFile.getAbsolutePath());
        } finally {
            for (PDDocument e : splits)
                e.close();
        }
    }

	public static List<別添> split(String fileName, boolean horizontal, float lineHeight, float fontSize,
	    String outDir, 別添関数 matcher) throws IOException, COSVisitorException {
	    Pdf pdf = new Pdf(fileName, horizontal);
	    List<List<String>> pages = pdf.toStringList(lineHeight, fontSize, 0.0F, Skip.NONE, new StringFunction() {
            @Override public String eval(String line) { return line; }
	    });
	    List<別添> list = new ArrayList<>();
	    PDDocument doc = PDDocument.load(fileName);
	    try {
            int prevPage = -1;
            // String outFile = null;
            // String header = null;
            // String title = null;
            別添 prev = null;
            別添 betten = null;
            for (int i = 0, size = pages.size(); i < size; ++i) {
                List<String> page = pages.get(i);
                if (page.size() <= 0)
                    continue;
                int g = 0;
                String line;
                // 医科の「別紙３６」の直前にある「【別添３】」をスキップする。
                for (line = page.get(g); line.trim().length() == 0
                    || line.trim().equals("【別添３】"); ++g) {
                    line = page.get(g);
                }
                String next = "";
                for (int j = g + 1, n = page.size(); j < n; ++j) {
                    String t = page.get(j).trim();
                    if (t.length() > 0) {
                            next = t.replaceAll("[\\s　]", "");
                            break;
                    }
                }
                betten = matcher.eval(line, next);
                if (betten != null) {
                    if (prev != null)
                        writePdf(doc, prevPage, i, new File(outDir), prev, list);
                    prev = betten;
                    prevPage = i + 1;
                }
            }
            if (prev != null)
                writePdf(doc, prevPage, doc.getNumberOfPages(), new File(outDir), prev, list);
	    } finally {
	        doc.close();
	    }
	    return list;
	}

}
