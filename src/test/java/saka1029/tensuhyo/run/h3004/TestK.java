package saka1029.tensuhyo.run.h3004;

import static saka1029.tensuhyo.run.h3004.Commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;

import saka1029.tensuhyo.generator.Sitemap;
import saka1029.tensuhyo.generator.施設基準Renderer;
import saka1029.tensuhyo.generator.施設基準RendererCallback;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.parser.ParseException;
import saka1029.tensuhyo.parser.Parser;
import saka1029.tensuhyo.parser.施設基準告示読込;
import saka1029.tensuhyo.parser.施設基準通知読込;
import saka1029.tensuhyo.pdf.Pdf;
import saka1029.tensuhyo.pdf.StringFunction;
import saka1029.tensuhyo.util.LogFormatter;
import saka1029.tensuhyo.util.TextIO;
import saka1029.tensuhyo.util.TextWriter;

public class TestK {

	static { LogFormatter.init(); }
	
	private static final String TEN = "k";
	private static final String TENSU = "施設基準";

	private static final String BASE_DIR = "data/in/" + YEAR + "/" + TEN + "/";
	private static final String PDF_DIR = BASE_DIR + "pdf/";
	private static final String TEXT_DIR = BASE_DIR + "txt/";
//	private static final String CSV_DIR = BASE_DIR + "csv/";

	private static final String KOKUJI_TEXT = TEXT_DIR + "kokuji";
	private static final String TUTI_TEXT = TEXT_DIR + "tuti";

	private static final String KOKUJI_EDITED_TEXT = TEXT_DIR + "告示";
	private static final String TUTI_EDITED_TEXT = TEXT_DIR + "通知";

	private static final String KOKUJI_ROOT_NAME = GENGO + YEAR + "年" + TENSU;
	private static final String TUTI_ROOT_NAME = GENGO + YEAR + "年" + TENSU + "通知";

	private static final String HTML_DIR = "data/web/" + YEAR + "/" + TEN;
	private static final String IMAGE_DIR = HTML_DIR  + "/image";
	private static final String BASE_URL = "http://tensuhyo.html.xdomain.jp/" + YEAR + "/" + TEN;

	private static final String COMMON_TITLE = GENGO + YEAR + "年" + TENSU;

	private static final String[] KOKUJI_PDF = {
		PDF_DIR + "0000196314.pdf",
		PDF_DIR + "0000196317.pdf",
	};

	private static final String[] TUTI_PDF = {
		PDF_DIR + "0000196315.pdf",
		PDF_DIR + "0000196318.pdf",
	};

    private static String PAGE_NO_PAT = "^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$";
    private static String PAGE_NO_PAT_VERTICAL = "^\\s*[〇一二三四五六七八九]+頁\\s*$";

	@Test
	public void kokuji() throws IOException {
	    Pdf.toText(KOKUJI_PDF, KOKUJI_TEXT + ".txt", false, 22F, 14F, 0.5F, Pdf.Skip.TEXT, new StringFunction() {
            @Override public String eval(String line) {
                return line.matches(PAGE_NO_PAT_VERTICAL) ? "#" + line : line.replaceAll("\\s－", "－");
            }
	    });
	}

	@Test
	public void tuti() throws IOException {
	    Pdf.toText(TUTI_PDF, TUTI_TEXT + ".txt", true, 5F, 10F, 0.5F, Pdf.Skip.LINE, new StringFunction() {
            @Override public String eval(String line) { return line.matches(PAGE_NO_PAT) ? "#" + line : line; }
	    });
	}

	static void traverse(Node node, Consumer<Node> visitor) {
	    visitor.accept(node);
	    for (Node e : node.children())
	        traverse(e, visitor);
	}

	@Test
	public void html() throws IOException, ParseException {
		String kokujiIn = TextIO.ReadFrom(KOKUJI_EDITED_TEXT + ".txt", ENCODING);
		Parser kokujiParser = new 施設基準告示読込();
		Document kokujiDoc = kokujiParser.parse(kokujiIn, KOKUJI_ROOT_NAME);
		TextIO.WriteTo(kokujiDoc.toLongString(), KOKUJI_EDITED_TEXT + "debug.txt", ENCODING);

		String tutiIn = TextIO.ReadFrom(TUTI_EDITED_TEXT + ".txt", ENCODING);
		Parser tutiParser = new 施設基準通知読込();
		Document tutiDoc = tutiParser.parse(tutiIn, TUTI_ROOT_NAME);
		TextIO.WriteTo(tutiDoc.toLongString(), TUTI_EDITED_TEXT + "debug.txt", ENCODING);

		施設基準Renderer renderer = new 施設基準Renderer();
		renderer.HTML出力(kokujiDoc, tutiDoc, new File(HTML_DIR),
		    new 施設基準RendererCallback() {
                @Override public String 共通タイトル() { return COMMON_TITLE; }
				@Override public String baseUrl() { return BASE_URL; }
                @Override public void index(Node node, TextWriter w) { }
                @Override public String imageDir() { return IMAGE_DIR; }
            });
		Sitemap.create(new File(HTML_DIR), BASE_URL);
	}

	@Test
	public void sitemap() throws FileNotFoundException {
		Sitemap.create(new File(HTML_DIR), BASE_URL);
	}

}
