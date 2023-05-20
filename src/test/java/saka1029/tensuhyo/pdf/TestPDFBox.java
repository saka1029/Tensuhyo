package saka1029.tensuhyo.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
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
	
//	@Test
	public void testテキスト変換() throws IOException {
		Files.createDirectories(Path.of("test-data"));
		new PDFBox(false).テキスト変換("test-data/04kokuji.txt", "data/in/04/k/pdf/000907845.pdf");
		new PDFBox(true).テキスト変換("test-data/04tuti.txt", "data/in/04/k/pdf/000907862.pdf");
	}
	
//	@Test
	public void testページ分割() throws IOException, COSVisitorException {
		Files.createDirectories(Path.of("test-data"));
		String yoshikiList = "test-data/yoshiki-list.txt";
		try (PrintWriter w = new PrintWriter(yoshikiList, StandardCharsets.UTF_8)) {
		    w.println("data/in/04/i/pdf/000907839.pdf,別紙様式1,SY1,1,1,退院証明書");
		    w.println("data/in/04/i/pdf/000907839.pdf,別紙様式2,SY2,2,4,医療区分・ＡＤＬ区分等に係る評価票");
		}
		List<様式> result = PDFBox.ページ分割(yoshikiList, "test-data"/*, "BESI"*/);
		assertTrue(new File("test-data/SY1.pdf").exists());
		assertTrue(new File("test-data/SY2.pdf").exists());
		assertEquals(2, result.size());
		assertEquals(List.of(new 様式("data/in/04/i/pdf/000907839.pdf", "別紙様式1", "SY1", 1, 1, "退院証明書"),
		    new 様式("data/in/04/i/pdf/000907839.pdf", "別紙様式2", "SY2", 2, 4, "医療区分・ＡＤＬ区分等に係る評価票")), result);
	}
}
