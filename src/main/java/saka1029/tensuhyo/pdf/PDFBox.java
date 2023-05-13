package saka1029.tensuhyo.pdf;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

public class PDFBox {
	
	static final Logger lobber = Logger.getLogger(PDFBox.class.getName());

	/**
	 * A4のポイントサイズ 横約8.27 × 縦約11.69 インチ 595.44 x 841.68 ポイント
	 */
	public static final float PAGE_WIDTH = 596F, PAGE_HEIGHT = 842F;
	
	public final boolean horizontal;
	
	public PDFBox(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public record Element(float x, float y, float fontSize, String text) {
	}

	float round(float value) {
		return Math.round(value);
	}

	public List<List<String>> read(String inPdfPath) throws IOException {
		List<List<String>> result = new ArrayList<>();
		PDDocument doc = PDDocument.load(inPdfPath);
		int numberOfPages = doc.getNumberOfPages();
		List<Element> elements = new ArrayList<>();
		try (Closeable c = () -> doc.close()) {
			for (int i = 0; i < numberOfPages; ++i) {
				int p = i + 1;
				new PDFTextStripper() {
					{
						setStartPage(p);
						setEndPage(p);
						getText(doc);
					}
					
					@Override
					protected void processTextPosition(TextPosition text) {
						Element element = new Element(
							round(horizontal ? text.getX() : PAGE_HEIGHT - text.getY()),
							round(horizontal ? PAGE_HEIGHT - text.getY() : PAGE_WIDTH - text.getX()),
							round(text.getXScale()),
							text.getCharacter());
						elements.add(element);
					}
				};
			}
		}
		return result;
	}
}
