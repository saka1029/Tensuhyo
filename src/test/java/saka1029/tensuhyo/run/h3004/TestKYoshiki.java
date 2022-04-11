package saka1029.tensuhyo.run.h3004;

import static saka1029.tensuhyo.run.h3004.Commons.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.generator.別添Renderer;
import saka1029.tensuhyo.generator.別添RendererCallback;
import saka1029.tensuhyo.pdf.Pdf;
import saka1029.tensuhyo.pdf.別添;
import saka1029.tensuhyo.pdf.別添関数;
import saka1029.tensuhyo.util.StringConverter;

public class TestKYoshiki {

	private static final String TEN = "k";

	private static final String BASE_DIR = "data/in/" + YEAR + "/" + TEN + "/";
	private static final String PDF_DIR = BASE_DIR + "pdf/";
	private static final String PDF_BETTEN_DIR = PDF_DIR + "betten/";
//	private static final String PDF_KIHON_DIR = PDF_TUTI_DIR + "基本診療料/";
//	private static final String PDF_TOKKEI_DIR = PDF_TUTI_DIR + "特掲診療料/";

	private static final String HTML_DIR = "data/web/" + YEAR + "/" + TEN + "/";
	private static final String BASE_URL = "http://tensuhyo.html.xdomain.jp/" + YEAR + "/" + TEN + "/";

	private static final String OUTPUT_PDF_DIR = HTML_DIR + "image/";

	private static final String SP = "[\\s　]*";
	private static final Pattern BETTEN_PAT = Pattern.compile(
	    "^" + SP + "(?<H>別" + SP + "添(?<N>[0-9０-９]+))" + SP + "$"
    );
	private static final Pattern HEADER_PAT = Pattern.compile(
	    "^" + SP + "(?<H>[(（]?(別" + SP + "紙" + SP + ")?様" + SP + "式"
        + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+)"
        + "(" + SP + "[の-ー‐－―]" + SP + "(?<E>[0-9０-９]+))?)?"
            + "[)）]?)" + SP + "$"
    );

	private static final String[] PDF_KIHON_YOSIKI = {
	    PDF_BETTEN_DIR + "0000196315.pdf",	// 別添７
	    PDF_BETTEN_DIR + "2018-03-30-改訂-基本診療料.pdf",
	    PDF_BETTEN_DIR + "2018-05-01-改訂-基本診療料.pdf",
	};
	// private static final String PDF_KIHON_FORMAT = "KIHON-BETTEN7-BESI%s.pdf";

	private static final String[] PDF_TOKKEI_YOSIKI = {
	    PDF_BETTEN_DIR + "0000196318.pdf",	// 別添２
	    PDF_BETTEN_DIR + "2018-03-30-改訂-特掲診療料.pdf",
	    PDF_BETTEN_DIR + "2018-05-01-改訂-特掲診療料.pdf",
	};
	// private static final String PDF_TOKKEI_FORMAT = "TOKKEI-BETTEN2-BESI%s.pdf";

	static String han(String s) { return StringConverter.toNormalWidthANS(s); }

	static class 基本診療別添関数 implements 別添関数 {
        String betten = "";
        @Override
        public 別添 eval(String line, String next) {
            Matcher t = BETTEN_PAT.matcher(line);
            if (t.matches())
                return new 別添(String.format("KIHON-BETTEN%s.pdf", betten = han(t.group("N"))),
                    t.group("H"), next.replaceAll("\\s", ""));
            Matcher m = HEADER_PAT.matcher(line);
            if (m.matches()) {
                String no = han(m.group("N"));
                String d = m.group("D");
                if (d != null) no += "_" + han(d);
                return new 別添(String.format("KIHON-BETTEN%s-BESI%s.pdf", betten, no),
                    m.group("H"), next.replaceAll("\\s", ""));
            }
            return null;
        }
    }

	static class 基本診療料別添RendererCallback implements 別添RendererCallback {
        @Override public String 共通タイトル() { return "平成" + YEAR + "年 基本診療料の施設基準に係る届出書"; }
        @Override public String ファイル名() { return "1.betten"; }
        @Override public String baseUrl() { return BASE_URL; }
	}

	@Test
	public void kihon() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDF_KIHON_YOSIKI) {
	        System.out.println("**** " + pdfFile);
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, OUTPUT_PDF_DIR, new 基本診療別添関数());
            for (別添 b : list) {
                System.out.println(b + " -> exists=" + map.containsKey(b.pdfFileName));
                map.put(b.pdfFileName, b);
            }
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTML_DIR), new 基本診療料別添RendererCallback());
	}

	static class 特掲診療料別添関数 implements 別添関数 {
        String betten = "";
        @Override
        public 別添 eval(String line, String next) {
            Matcher t = BETTEN_PAT.matcher(line);
            if (t.matches())
                return new 別添(String.format("TOKKEI-BETTEN%s.pdf", betten = han(t.group("N"))),
                    t.group("H"), next.replaceAll("\\s", ""));
            Matcher m = HEADER_PAT.matcher(line);
            if (m.matches()) {
                String no = han(m.group("N"));
                String d = m.group("D");
                String e = m.group("E");
                if (d != null) no += "_" + han(d);
                if (e != null) no += "_" + han(e);
                return new 別添(String.format("TOKKEI_BETTEN%s-BESI%s.pdf", betten, no),
                    m.group("H"), next.replaceAll("\\s", ""));
            }
            return null;
        }
	}

	static class 特掲診療料別添RendererCallback implements 別添RendererCallback {
        @Override public String 共通タイトル() { return "平成" + YEAR + "年 特掲診療料の施設基準に係る届出書"; }
        @Override public String ファイル名() { return "2.betten"; }
        @Override public String baseUrl() { return BASE_URL; }
	}

	@Test
	public void tokkei() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDF_TOKKEI_YOSIKI) {
	        System.out.println("**** " + pdfFile);
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, OUTPUT_PDF_DIR, new 特掲診療料別添関数());
            for (別添 b : list) {
                System.out.println(b + " -> exists=" + map.containsKey(b.pdfFileName));
                map.put(b.pdfFileName, b);
            }
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTML_DIR), new 特掲診療料別添RendererCallback());
	}

}
