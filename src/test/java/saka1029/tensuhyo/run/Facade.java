package saka1029.tensuhyo.run;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.exceptions.COSVisitorException;

import saka1029.tensuhyo.generator.Renderer;
import saka1029.tensuhyo.generator.RendererOption;
import saka1029.tensuhyo.generator.Sitemap;
import saka1029.tensuhyo.generator.別添Renderer;
import saka1029.tensuhyo.generator.別添RendererCallback;
import saka1029.tensuhyo.generator.施設基準Renderer;
import saka1029.tensuhyo.generator.施設基準RendererCallback;
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
import saka1029.tensuhyo.parser.施設基準通知読込;
import saka1029.tensuhyo.parser.調剤告示読込;
import saka1029.tensuhyo.parser.調剤通知読込;
import saka1029.tensuhyo.pdf.Pdf;
import saka1029.tensuhyo.pdf.StringFunction;
import saka1029.tensuhyo.pdf.別添;
import saka1029.tensuhyo.pdf.別添関数;
import saka1029.tensuhyo.util.Common;
import saka1029.tensuhyo.util.StringConverter;
import saka1029.tensuhyo.util.TextIO;
import saka1029.tensuhyo.util.TextWriter;

public class Facade {
    public String 元号;
    public String 年度;
    public String 旧元号;
    public String 旧年度;
    // PDFファイルはパスなしのファイル名のみで指定する。
    public String[] 医科告示PDF;
    public String[] 医科通知PDF;
    public String[] 医科様式PDF;
    public String[] 歯科告示PDF;
    public String[] 歯科通知PDF;
    public String[] 歯科様式PDF;
    public String[] 調剤告示PDF;
    public String[] 調剤通知PDF;
    public String[] 調剤様式PDF;
    public String[] 施設基準告示PDF;
    public String[] 施設基準通知PDF;
    public String[] 施設基準基本様式PDF;
    public String[] 施設基準特掲様式PDF;

    static final String ENCODING = "UTF-8";

	static { Common.config(); }

    String[] PDFパス(String 点数表, String[] pdfs) {
        return Arrays.stream(pdfs)
            .map(pdf -> "data/in/%s/%s/pdf/%s".formatted(年度, 点数表, pdf))
            .toArray(String[]::new);
    }

    String テキストパス(String 年度, String 点数表, String テキストファイル) {
        return "data/in/%s/%s/txt/%s.txt".formatted(年度, 点数表, テキストファイル);
    }

    String テキストパス(String 点数表, String テキストファイル) {
        return テキストパス(年度, 点数表, テキストファイル);
    }

    String HTMLパス(String 点数表) {
        return "data/web/%s/%s".formatted(年度, 点数表);
    }

    String ベースURL(String 点数表) {
        return "http://tensuhyo.html.xdomain.jp/%s/%s".formatted(年度, 点数表);
    }

	public void 施設基準告示変換() throws IOException {
	    Pdf.toText(PDFパス("k", 施設基準告示PDF), テキストパス("k", "kokuji"), false, 22F, 14F, 0.5F, Pdf.Skip.TEXT,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*[〇一二三四五六七八九]+頁\\s*$") ? "#" + line : line.replaceAll("\\s－", "－");
            }
	    });
	}

	public void 施設基準通知変換() throws IOException {
	    Pdf.toText(PDFパス("k", 施設基準通知PDF), テキストパス("k", "tuti"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$") ? "#" + line : line;
            }
	    });
	}

	public void 施設基準HTML変換() throws IOException, ParseException {
		String kokujiIn = TextIO.ReadFrom(テキストパス("k", "告示"), ENCODING);
		Parser kokujiParser = new 施設基準告示読込();
		Document kokujiDoc = kokujiParser.parse(kokujiIn, 元号 + 年度 + "年施設基準");
		String tutiIn = TextIO.ReadFrom(テキストパス("k", "通知"), ENCODING);
		Parser tutiParser = new 施設基準通知読込();
		Document tutiDoc = tutiParser.parse(tutiIn, 元号 + 年度 + "年施設基準通知");
		施設基準Renderer renderer = new 施設基準Renderer();
		renderer.HTML出力(kokujiDoc, tutiDoc, new File(HTMLパス("k")),
		    new 施設基準RendererCallback() {
                @Override public String 共通タイトル() { return 元号 + 年度 + "年施設基準"; }
				@Override public String baseUrl() { return ベースURL("k"); }
                @Override public void index(Node node, TextWriter w) { }
                @Override public String imageDir() { return HTMLパス("k") + "/image"; }
            });
		Sitemap.create(new File(HTMLパス("k")), ベースURL("k"));
	}

	static class 基本診療別添関数 implements 別添関数 {
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

	public void 施設基準基本様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDFパス("k", 施設基準基本様式PDF)) {
	        System.out.println("**** " + pdfFile);
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, HTMLパス("k") + "/image", new 基本診療別添関数());
            for (別添 b : list) {
                System.out.println(b + " -> exists=" + map.containsKey(b.pdfFileName));
                map.put(b.pdfFileName, b);
            }
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTMLパス("k")), new 別添RendererCallback() {
            @Override public String 共通タイトル() { return 元号 + 年度 + "年 基本診療料の施設基準に係る届出書"; }
            @Override public String ファイル名() { return "1.betten"; }
            @Override public String baseUrl() { return ベースURL("k"); }
        });
	}

	static class 特掲診療料別添関数 implements 別添関数 {
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

	public void 施設基準特掲様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDFパス("k", 施設基準特掲様式PDF)) {
	        System.out.println("**** " + pdfFile);
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, HTMLパス("k") + "/image", new 特掲診療料別添関数());
            for (別添 b : list) {
                System.out.println(b + " -> exists=" + map.containsKey(b.pdfFileName));
                map.put(b.pdfFileName, b);
            }
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTMLパス("k")), new 別添RendererCallback() {
            @Override public String 共通タイトル() { return 元号 + 年度 + "年 特掲診療料の施設基準に係る届出書"; }
            @Override public String ファイル名() { return "2.betten"; }
            @Override public String baseUrl() { return ベースURL("k"); }
        });
	}

    void 医科告示変換() throws IOException {
	    Pdf.toText(PDFパス("i", 医科告示PDF), テキストパス("i", "kokuji"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*-\\s*[0-9０-９]+\\s*-\\s*$") ? "#" + line : line;
            }
	    });
    }

    void 医科通知変換() throws IOException {
	    Pdf.toText(PDFパス("i", 医科通知PDF), テキストパス("i", "tuti"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*.*\\s*-\\s*[0-9０-９]+\\s*-\\s*$") ? "#" + line : line;
            }
	    });
    }

	public void 医科HTML変換() throws IOException, ParseException {
		String inText = TextIO.ReadFrom(テキストパス("i", "告示"), ENCODING);
		Parser parser = new 医科告示読込();
		Document doc = parser.parse(inText, 元号 + 年度 + "年医科");
		// TextIO.WriteTo(doc.toLongString(), テキストパス("i", "告示debug"), ENCODING);
		Converter.事項追加(doc);
		Converter.目次チェック(doc);
		索引 dict =  new 索引();
		dict.区分番号辞書作成(doc, new 索引Option() {
			@Override public String 区分番号表示(String fileName) { return fileName; }
			@Override public String 区分番号接頭語() { return ""; }
		});
		String tutiText = TextIO.ReadFrom(テキストパス("i", "通知"), ENCODING);
		Parser tutiParser = new 医科通知読込();
		Document tutiDoc = tutiParser.parse(tutiText, 元号 + 年度 + "年医科通知");
		Converter.事項追加(tutiDoc);
		Converter.目次チェック(tutiDoc);
		Converter.通知マージ(doc, tutiDoc);
		Renderer renderer = new Renderer();
		// 施設基準辞書追加
	    String sisetuText = TextIO.ReadFrom(テキストパス("k", "告示"), ENCODING);
	    Parser sisetuParser = new 施設基準告示読込();
	    Document sisetuDoc = sisetuParser.parse(sisetuText, 元号 + 年度 + "年施設基準(告示)");
	    dict.施設基準追加(sisetuDoc);
		renderer.HTML出力(doc, new File(HTMLパス("i")), dict,
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年医科診療報酬点数表"; }
				@Override public String baseUrl() { return ベースURL("i"); }
				@Override public String 比較タイトル() { return "dummy"; }
				@Override
				public void 目次(Node node, TextWriter w) {
					w.printf("<hr>\n<div id='menu'></div>\n");
 				}
		});
		Sitemap.create(new File(HTMLパス("i")), ベースURL("i"));
	}
    
	public void 医科区分一覧() throws IOException, ParseException {
		String oldText = TextIO.ReadFrom(テキストパス(旧年度, "i", "告示"), ENCODING);
		Parser oldParser = new 医科告示読込();
		Document oldDoc = oldParser.parse(oldText, 旧元号 + 旧年度 + "医科");
		String newText = TextIO.ReadFrom(テキストパス(年度, "i", "告示"), ENCODING);
		Parser newParser = new 医科告示読込();
		Document newDoc = newParser.parse(newText, 元号 + 年度 + "医科");
		Renderer renderer = new Renderer();
//		renderer.commonTitle = COMMON_TITLE;
		renderer.区分一覧出力(oldDoc, newDoc, new File(HTMLパス("i") + "/kubun.html"), "../../" + 旧年度 + "/i",
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年医科診療報酬点数表"; }
				@Override public String baseUrl() { return ベースURL("i"); }
				@Override public String 比較タイトル() { return 旧元号 + 旧年度 + "年," + 元号 + 年度 + "年診療報酬点数表"; }
                @Override public void 目次(Node node, TextWriter w) {
                    w.printf("<hr>\n<div id='menu'></div>\n");
                }
			});
	}
	static String han(String s) { return StringConverter.toNormalWidthANS(s); }
	static String norm(String s) { return s.replaceAll("[()（）\\s]", ""); }

	static 別添関数 医科別添関数 = new 別添関数() {
        static final String SP = "[\\s　]*";
        static final Pattern HEADER_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙(\\s*様\\s*式)?|様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)" + SP + "$"
        );
        static final Pattern HEADER_TITLE_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙(\\s*様\\s*式)?|様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)"
            + SP + "(?<T>.*)" + "$"
        );

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
	};

	public void 医科様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDFパス("i", 医科様式PDF)) {
	        List<別添> list = Pdf.split(pdfFile, true, 4, 10, HTMLパス("i") + "/image", 医科別添関数);
	        for (別添 b : list)
	            map.put(b.pdfFileName, b); // ファイル名をキーとしてマージします。
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTMLパス("i")), new 別添RendererCallback() {
            @Override public String 共通タイトル() { return 元号 + 年度 + "年 別紙様式一覧"; }
            @Override public String ファイル名() { return "yoshiki"; }
            @Override public String baseUrl() { return ベースURL("i"); }
        });
	}

	public void 歯科告示変換() throws IOException {
	    Pdf.toText(PDFパス("s", 歯科告示PDF), テキストパス("s", "kokuji"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE, new StringFunction() {
            @Override public String eval(String line) { return line.matches("^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$") ? "#" + line : line; }
	    });
	}

	public void 歯科通知変換() throws IOException {
	    Pdf.toText(PDFパス("s", 歯科通知PDF), テキストパス("s", "tuti"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE, new StringFunction() {
            @Override public String eval(String line) { return line.matches("^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$") ? "#" + line : line; }
	    });
	}

	public void 歯科HTML変換() throws IOException, ParseException {
		String inText = TextIO.ReadFrom(テキストパス("s", "告示"), ENCODING);
		Parser parser = new 医科告示読込();
		Document doc = parser.parse(inText, 元号 + 年度 + "歯科");
		// TextIO.WriteTo(doc.toLongString(), テキストパス("s", "告示debug"), ENCODING);
		Converter.事項追加(doc);
		Converter.目次チェック(doc);
		索引 dict =  new 索引();
		dict.区分番号辞書作成(doc, new 索引Option() {
			@Override public String 区分番号表示(String fileName) { return fileName; }
			@Override public String 区分番号接頭語() { return ""; }
		});		String tutiText = TextIO.ReadFrom(テキストパス("s", "通知"), ENCODING);
		Parser tutiParser = new 医科通知読込();
		Document tutiDoc = tutiParser.parse(tutiText, 元号 + 年度 + "歯科通知");
		Converter.事項追加(tutiDoc);
		Converter.目次チェック(tutiDoc);
		Converter.通知マージ(doc, tutiDoc);
		Renderer renderer = new Renderer();
	    // 施設基準辞書追加
        String sisetuText = TextIO.ReadFrom(テキストパス("k", "告示"), ENCODING);
        Parser sisetuParser = new 施設基準告示読込();
        Document sisetuDoc = sisetuParser.parse(sisetuText, 元号 + 年度 + "施設基準(告示)");
        dict.施設基準追加(sisetuDoc);

		renderer.HTML出力(doc, new File(HTMLパス("s")), dict,
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年歯科診療報酬点数表"; }
				@Override public String baseUrl() { return HTMLパス("s"); }
				@Override public String 比較タイトル() { return "dummy"; }
				@Override
				public void 目次(Node node, TextWriter w) {
					w.printf("<hr>\n<div id='menu'></div>\n");
				}
		});
		Sitemap.create(new File(HTMLパス("s")), ベースURL("s"));
	}

	public void 歯科区分一覧() throws IOException, ParseException {
		String oldText = TextIO.ReadFrom(テキストパス(旧年度, "s", "告示"), ENCODING);
		Parser oldParser = new 医科告示読込();
		Document oldDoc = oldParser.parse(oldText, 旧元号 + 旧年度 + "年歯科");

		String newText = TextIO.ReadFrom(テキストパス(年度, "s", "告示"), ENCODING);
		Parser newParser = new 医科告示読込();
		Document newDoc = newParser.parse(newText, 元号 + 年度 + "年歯科");

		Renderer renderer = new Renderer();
//		renderer.commonTitle = COMMON_TITLE;
		renderer.区分一覧出力(oldDoc, newDoc, new File(HTMLパス("s") + "/kubun.html"), "../../" + 旧年度 + "/s",
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年歯科診療報酬点数表"; }
				@Override public String baseUrl() { return ベースURL("s"); }
				@Override public String 比較タイトル() { return 旧元号 + 旧年度 + "年," + 元号 + 年度 + "年歯科診療報酬点数表"; }
                @Override public void 目次(Node node, TextWriter w) {
                    w.printf("<hr>\n<div id='menu'></div>\n");
                }
            });
	}
	static class 歯科別添関数 implements 別添関数 {
        static final String SP = "[\\s　]*";
        static final Pattern HEADER_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)" + SP + "$"
        );
        static final Pattern HEADER_TITLE_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)"
            + SP + "(?<T>.*)" + "$"
        );
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

	public void 歯科様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDFパス("s", 歯科様式PDF)) {
            List<別添> list = Pdf.split(pdfFile, true, 4, 10, HTMLパス("s") + "/image", new 歯科別添関数());
            for (別添 b : list)
                map.put(b.pdfFileName, b);
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTMLパス("s")), new 別添RendererCallback() {
            @Override public String 共通タイトル() { return 元号 + 年度 + "年 別紙様式一覧"; }
            @Override public String ファイル名() { return "yoshiki"; }
            @Override public String baseUrl() { return ベースURL("s"); }
        });
	}

	public void 調剤告示変換() throws IOException {
	    Pdf.toText(PDFパス("t", 調剤告示PDF), テキストパス("t", "kokuji"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$") ? "#" + line : line;
            }
	    });
	}

	public void 調剤通知変換() throws IOException {
	    Pdf.toText(PDFパス("t", 調剤通知PDF), テキストパス("t", "tuti"), true, 5F, 10F, 0.5F, Pdf.Skip.LINE,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*\\S*\\s*-\\s*[0-9０-９]+\\s*-$") ? "#" + line : line;
            }
	    });
	}

	public void 調剤HTML変換() throws IOException, ParseException {
		String inText = TextIO.ReadFrom(テキストパス("t", "告示"), ENCODING);
		Parser parser = new 調剤告示読込();
		Document doc = parser.parse(inText, 元号 + 年度 + "年調剤");
		Converter.事項追加(doc);
		Converter.目次チェック(doc);
		索引 dict =  new 索引();
		dict.区分番号辞書作成(doc, new 索引Option() {
			@Override public String 区分番号表示(String fileName) { return fileName.replaceAll("-", "の"); }
			@Override public String 区分番号接頭語() { return "区分番号"; }
		});
		String tutiText = TextIO.ReadFrom(テキストパス("t", "通知"), ENCODING);
		Parser tutiParser = new 調剤通知読込();
		Document tutiDoc = tutiParser.parse(tutiText, 元号 + 年度 + "年調剤通知");
		Converter.事項追加(tutiDoc);
		Converter.目次チェック(tutiDoc);
		Converter.通知マージ(doc, tutiDoc);
		Renderer renderer = new Renderer();
		// 施設基準辞書追加
	    String sisetuText = TextIO.ReadFrom(テキストパス("k", "告示"), ENCODING);
	    Parser sisetuParser = new 施設基準告示読込();
	    Document sisetuDoc = sisetuParser.parse(sisetuText, 元号 + 年度 + "年施設基準(告示)");
	    dict.施設基準追加(sisetuDoc);
		renderer.HTML出力(doc, new File(HTMLパス("t")), dict,
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年調剤報酬点数表"; }
				@Override public String baseUrl() { return ベースURL("t"); }
				@Override public String 比較タイトル() { return "dummy"; }
                @Override
                public void 目次(Node node, TextWriter w) {
                    w.printf("<hr>\n<div id='menu'></div>\n");
                }
			});
		Sitemap.create(new File(HTMLパス("t")), ベースURL("t"));
	}
 
	public void 調剤区分一覧() throws IOException, ParseException {
		String oldText = TextIO.ReadFrom(テキストパス(旧年度, "t", "告示"), ENCODING);
		Parser oldParser = new 調剤告示読込();
		Document oldDoc = oldParser.parse(oldText, 旧元号 + 旧年度 + "年調剤");

		String newText = TextIO.ReadFrom(テキストパス(年度, "t", "告示"), ENCODING);
		Parser newParser = new 調剤告示読込();
		Document newDoc = newParser.parse(newText, 元号 + 年度 + "年調剤");

		Renderer renderer = new Renderer();
//		renderer.commonTitle = COMMON_TITLE;
		renderer.区分一覧出力(oldDoc, newDoc, new File(HTMLパス("t") + "/kubun.html"), "../../" + 旧年度 + "/t",
			new RendererOption() {
				@Override public String 共通タイトル() { return 元号 + 年度 + "年調剤診療報酬点数表"; }
				@Override public String baseUrl() { return ベースURL("t"); }
				@Override public String 比較タイトル() { return 旧元号 + 旧年度 + "年," + 元号 + 年度 + "年調剤診療報酬点数表"; }
                @Override
                public void 目次(Node node, TextWriter w) {
                    w.printf("<hr>\n<div id='menu'></div>\n");
                }
			});
	}

    static 別添関数 調剤別添関数 = new 別添関数() {
        static final String SP = "[\\s　]*";
        static final Pattern HEADER_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)" + SP + "$"
        );
        static final Pattern HEADER_TITLE_PAT = Pattern.compile(
            "^" + SP + "(?<H>[(（]?" + SP + "(別\\s*紙\\s*様\\s*式)"
            + SP + "(?<N>[0-9０-９]+)(" + SP + "[の-ー‐－―]" + SP + "(?<D>[0-9０-９]+))?[)）]?)"
            + SP + "(?<T>.*)" + "$"
        );

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
    };

	public void 調剤様式() throws IOException, COSVisitorException {
	    Map<String, 別添> map = new LinkedHashMap<>();
	    for (String pdfFile : PDFパス("t", 調剤様式PDF)) {
	        List<別添> list = Pdf.split(pdfFile, true, 4, 10, HTMLパス("t") + "/image", 調剤別添関数);
	        for (別添 b : list)
	            map.put(b.pdfFileName, b); // ファイル名をキーとしてマージします。
	    }
	    別添Renderer renderer = new 別添Renderer();
	    renderer.HTML出力(map.values(), new File(HTMLパス("t")), new 別添RendererCallback() {
            @Override public String 共通タイトル() { return 元号 + 年度 + "年 別紙様式一覧"; }
            @Override public String ファイル名() { return "yoshiki"; }
            @Override public String baseUrl() { return ベースURL("t"); }
        });
	}
}
