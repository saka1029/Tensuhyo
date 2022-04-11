package saka1029.tensuhyo.run.h3004;

import static saka1029.tensuhyo.run.h3004.Commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.generator.Renderer;
import saka1029.tensuhyo.generator.RendererOption;
import saka1029.tensuhyo.generator.Sitemap;
import saka1029.tensuhyo.generator.別添Renderer;
import saka1029.tensuhyo.generator.別添RendererCallback;
import saka1029.tensuhyo.generator.索引;
import saka1029.tensuhyo.generator.索引Option;
import saka1029.tensuhyo.parser.Converter;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.parser.ParseException;
import saka1029.tensuhyo.parser.Parser;
import saka1029.tensuhyo.parser.医科告示読込;
import saka1029.tensuhyo.parser.医科通知読込;
import saka1029.tensuhyo.parser.施設基準告示読込;
import saka1029.tensuhyo.pdf.Pdf;
import saka1029.tensuhyo.pdf.StringFunction;
import saka1029.tensuhyo.pdf.別添;
import saka1029.tensuhyo.pdf.別添関数;
import saka1029.tensuhyo.util.LogFormatter;
import saka1029.tensuhyo.util.StringConverter;
import saka1029.tensuhyo.util.TextIO;
import saka1029.tensuhyo.util.TextWriter;

/**
 * 2018/3/09修正: M017 通知(7)  イメージ化                   ポンティック（１歯につき）
 * 2018/3/09修正: M017 通知(12)  spanタグ化(4箇所) ポンティック（１歯につき）
 *
 */
public class TestS {

	static { LogFormatter.init(); }

	private static final String TEN = "s";
	private static final String TENSU = "歯科";

	private static final String BASE_DIR = "data/in/" + YEAR + "/" + TEN + "/";
	private static final String PDF_DIR = BASE_DIR + "pdf/";
	private static final String TEXT_DIR = BASE_DIR + "txt/";

	private static final String KOKUJI_TEXT = TEXT_DIR + "kokuji";
	private static final String TUTI_TEXT = TEXT_DIR + "tuti";

	private static final String KOKUJI_EDITED_TEXT = TEXT_DIR + "告示";
	private static final String TUTI_EDITED_TEXT = TEXT_DIR + "通知";

	private static final String KOKUJI_ROOT_NAME = GENGO + YEAR + "年" + TENSU;
	private static final String TUTI_ROOT_NAME = GENGO + YEAR + "年" + TENSU + "通知";

	private static final String HTML_DIR = "data/web/" + YEAR + "/" + TEN;
	private static final String BASE_URL = "http://tensuhyo.html.xdomain.jp/" + YEAR + "/" + TEN;

	private static final String COMMON_TITLE = GENGO + YEAR + "年" + TENSU + "診療報酬点数表";

	private static final String SISETU_KIZYUN_TEXT = "data/in/" + YEAR + "/k/txt/" + "告示";
	private static final String SISETU_KIZYUN_ROOT_NAME = GENGO + YEAR + "年" + "施設基準(告示)";

	private static final String[] KOKUJI_PDF = {
		PDF_DIR + "0000196303.pdf",	// 別表第2 （歯科点数表）
	};

	private static final String[] TUTI_PDF = {
		PDF_DIR + "0000196309.pdf",	// 別添2（歯科点数表）
	};

	private static final String[] YOSHIKI_PDF = {
	    PDF_DIR + "0000196310.pdf",
	};

    private static String PAGE_NO_PAT = "^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$";

	@Test
	public void kokuji() throws IOException {
//		PdfExtractor extractor = new PdfExtractor();
//		extract(extractor, KOKUJI_PDF, KOKUJI_TEXT + ".txt");
	    Pdf.toText(KOKUJI_PDF, KOKUJI_TEXT + ".txt", true, 5F, 10F, 0.5F, Pdf.Skip.LINE, new StringFunction() {
            @Override public String eval(String line) { return line.matches(PAGE_NO_PAT) ? "#" + line : line; }
	    });
	}

	@Test
	public void tuti() throws IOException {
//		PdfExtractor extractor = new PdfExtractor();
//		extract(extractor, TUTI_PDF, TUTI_TEXT + ".txt");
	    Pdf.toText(TUTI_PDF, TUTI_TEXT + ".txt", true, 5F, 10F, 0.5F, Pdf.Skip.LINE, new StringFunction() {
            @Override public String eval(String line) { return line.matches(PAGE_NO_PAT) ? "#" + line : line; }
	    });
	}

	@Test
	public void html() throws IOException, ParseException {
		String inText = TextIO.ReadFrom(KOKUJI_EDITED_TEXT + ".txt", ENCODING);
		Parser parser = new 医科告示読込();
		Document doc = parser.parse(inText, KOKUJI_ROOT_NAME);
		TextIO.WriteTo(doc.toLongString(), KOKUJI_EDITED_TEXT + "debug.txt", ENCODING);
		String outText = doc.text().replaceAll("(\r\n[ \t　]*注)[ \t　]*\r\n[ \t　]*", "$1");
		TextIO.WriteTo(outText, KOKUJI_EDITED_TEXT + "out.txt", ENCODING);
//		assertEquals(String.format("%s%n%s", KOKUJI_ROOT_NAME, inText), outText);
//		Converter.目次チェック(doc);
		Converter.事項追加(doc);
		Converter.目次チェック(doc);
//		String convertedText = doc.text().replaceAll("(\r\n[ \t　]*注)[ \t　]*\r\n[ \t　]*", "$1");
//		assertEquals(String.format("%s%n%s", KOKUJI_ROOT_NAME, inText), convertedText);
		TextIO.WriteTo(doc.toLongString(), KOKUJI_EDITED_TEXT + "converted.txt", ENCODING);
//		Map<String, Integer> ct = new HashMap<>();
//		count(doc.root(), ct);
//		for (Entry<String, Integer> e : ct.entrySet())
//			logger.log(Level.INFO, String.format("%s : %d", e.getKey(), e.getValue()));
		索引 dict =  new 索引();
		dict.区分番号辞書作成(doc, new 索引Option() {
			@Override public String 区分番号表示(String fileName) { return fileName; }
			@Override public String 区分番号接頭語() { return ""; }
		});		String tutiText = TextIO.ReadFrom(TUTI_EDITED_TEXT + ".txt", ENCODING);
		Parser tutiParser = new 医科通知読込();
		Document tutiDoc = tutiParser.parse(tutiText, TUTI_ROOT_NAME);
//		Converter.目次チェック(tutiDoc);
		Converter.事項追加(tutiDoc);
		Converter.目次チェック(tutiDoc);
		// String tutiConvertedText = tutiDoc.text().replaceAll("(\r\n[ \t　]*注)[ \t　]*\r\n[ \t　]*", "$1");
//		assertEquals(String.format("%s%n%s", TUTI_ROOT_NAME, tutiText), tutiConvertedText);
		TextIO.WriteTo(tutiDoc.toLongString(), TUTI_EDITED_TEXT + "converted.txt", ENCODING);
		Converter.通知マージ(doc, tutiDoc);
		Renderer renderer = new Renderer();
//		renderer.oldUrl = OLD_URL;
//		renderer.oldName = OLD_NAME;
//		renderer.commonTitle = COMMON_TITLE;

	    // 施設基準辞書追加
        String sisetuText = TextIO.ReadFrom(SISETU_KIZYUN_TEXT + ".txt", ENCODING);
        Parser sisetuParser = new 施設基準告示読込();
        Document sisetuDoc = sisetuParser.parse(sisetuText, SISETU_KIZYUN_ROOT_NAME);
        dict.施設基準追加(sisetuDoc);

		renderer.HTML出力(doc, new File(HTML_DIR), dict,
			new RendererOption() {
				@Override public String 共通タイトル() { return COMMON_TITLE; }
				@Override public String baseUrl() { return BASE_URL; }
				@Override public String 比較タイトル() { return "dummy"; }
				@Override
				public void 目次(Node node, TextWriter w) {
					w.printf("<hr>%n<div id='menu'></div>%n");
				}
		});
		Sitemap.create(new File(HTML_DIR), BASE_URL);
//		testKubunMap(doc);
	}

	@Test
	public void sitemap() throws FileNotFoundException {
		Sitemap.create(new File(HTML_DIR), BASE_URL);
	}

	private static final String OUTPUT_PDF_DIR = HTML_DIR + "/image";
	private static final String SP = "[\\s　]*";
	private static final Pattern HEADER_PAT = Pattern.compile(
	    "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
        + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)" + SP + "$"
    );
	private static final Pattern HEADER_TITLE_PAT = Pattern.compile(
	    "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
        + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)"
	    + SP + "(?<T>.*)" + "$"
    );

	static String han(String s) { return StringConverter.toNormalWidthANS(s); }
	static String norm(String s) { return s.replaceAll("[()（）\\s]", ""); }

	static class 歯科別添関数 implements 別添関数 {
        @Override
        public 別添 eval(String line, String next) {
            Matcher m = HEADER_PAT.matcher(line);
            if (m.matches()) {
                String no = han(m.group("N"));
                String d = m.group("D");
                if (d != null) no += "_" + han(d);
                return new 別添(String.format("BESI%s.pdf", no),
                    norm(m.group("H")), next.replaceAll("\\s", ""));
            }
            m = HEADER_TITLE_PAT.matcher(line);
            if (m.matches()) {
                String no = han(m.group("N"));
                String d = m.group("D");
                String t = m.group("T");
                if (d != null) no += "_" + han(d);
                return new 別添(String.format("BESI%s.pdf", no),
                    norm(m.group("H")), t.replaceAll("\\s", ""));
            }
            return null;
        }
	}

	static class 歯科別添RendererCallback implements 別添RendererCallback {
        @Override public String 共通タイトル() { return "平成" + YEAR + "年 別紙様式一覧"; }
        @Override public String ファイル名() { return "yoshiki"; }
        @Override public String baseUrl() { return BASE_URL; }
	}

	@Test
	public void 別添様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new HashMap<>();
	    for (String pdfFile : YOSHIKI_PDF) {
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, OUTPUT_PDF_DIR, new 歯科別添関数());
            for (別添 b : list)
                map.put(b.pdfFileName, b);
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTML_DIR), new 歯科別添RendererCallback());
	}
}
