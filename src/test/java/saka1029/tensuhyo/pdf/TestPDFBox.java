package saka1029.tensuhyo.pdf;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import org.junit.Test;

public class TestPDFBox {

	static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

	static void strip(String outText, String inPdf) throws IOException {
		PDDocument doc = PDDocument.load(inPdf);
		try (Closeable c = () -> doc.close(); PrintWriter out = new PrintWriter(outText, StandardCharsets.UTF_8)) {
			for (int i = 0, maxPage = doc.getNumberOfPages(); i < maxPage; ++i) {
				int pageNo = i + 1;
				new PDFTextStripper() {
					{
						setStartPage(pageNo);
						setEndPage(pageNo);
						getText(doc);
					}

					@Override
					protected void processTextPosition(TextPosition text) {
						out.printf("%d:%sx%s:%s(%s)%s:%s%n",
							pageNo, text.getX(), text.getY(),
							text.getFontSize(),
							text.getFontSizeInPt(), text.getXScale(), text.getCharacter());
					}
				};
			}
		}
	}

//	@Test
	public void testSimpleStripper() throws IOException {
//		Path inPdf = Path.of("data/in/04/k/pdf/000907845.pdf");	// 施設基準告示
		Path inPdf = Path.of("data/in/04/k/pdf/000907862.pdf"); // 施設基準通知
		Path outText = Path.of("test-data", inPdf.getFileName() + ".txt");
		Files.createDirectories(outText.getParent());
		strip(outText.toString(), inPdf.toString());
	}
	
	@Test
	public void testRead() throws IOException {
		Files.createDirectories(Path.of("test-data"));
		new PDFBox(false).テキスト変換("test-data/04kokuji.txt", "data/in/04/k/pdf/000907845.pdf");
		new PDFBox(true).テキスト変換("test-data/04tuti.txt", "data/in/04/k/pdf/000907862.pdf");
	}
}
