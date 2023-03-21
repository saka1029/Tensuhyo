package saka1029.tensuhyo;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.tensuhyo.pdf.Pdf;
import saka1029.tensuhyo.pdf.StringFunction;

public class TestSisetukizyun  {
    
    static final PrintWriter out = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
    static final Path KOKUJI = Path.of("data/in/04/k/txt/kokuji.txt");
    static final Path TUTI = Path.of("data/in/04/k/txt/tuti.txt");
    static final Pattern TITLE = Pattern.compile("^\\s*(\\S+)\\s+(\\S+の施設基準)\\s*");

    static List<String> titles(Path file) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                Matcher matcher = TITLE.matcher(line);
                if (matcher.matches())
                    result.add(matcher.group(2));
            }
        }
        return result;
    }
    
    static String norm(String title) {
        return title
            .replace("二十五対一", "25対１")
            .replace("七十五対一", "75対１")
            .replace("十三対一", "13対１")
            .replace("十五対一", "15対１")
            .replace("十六対一", "16対１")
            .replace("十八対一", "18対１")
            .replace("二十対一", "20対１")
            .replace("三十対一", "30対１")
            .replace("四十対一", "40対１")
            .replace("五十対一", "50対１")
            .replace("百対一", "100対１")
            .replace("十対一", "10対１");
    }

    @Test
    public void testTitles() throws IOException {
        List<String> ks = titles(KOKUJI);
        List<String> ts = titles(TUTI);
        Map<String, String> map = new TreeMap<>();
        for (String t : ks) {
            t = norm(t);
            map.put(t, "告示");
        }
        for (String t : ts) {
            t = norm(t);
            if (map.containsKey(t))
                map.put(t, "両方");
            else
                map.put(t, "通知");
        }
        for (Entry<String, String> e : map.entrySet())
            out.printf("%s %s%n", e.getValue(), e.getKey());
    }
    
    static final String マスタファイル仕様PDF = "data/in/04/k/pdf/master_2_20220930.pdf";
    static final String マスタファイル仕様TXT = "data/in/04/k/txt/master_spec.txt";

//    @Test
    public void test施設基準コード() throws IOException {
        Pdf pdf = new Pdf(マスタファイル仕様PDF, true);
        List<List<String>> pages = pdf.toStringList(5.0F, 10.0F, 0.5F, Pdf.Skip.LINE,
            line -> line.matches("^\\s*-\\s*[0-9０-９]+\\s*-\\s*$") ? null : line);
        for (String line : pages.get(30))
            out.println(line);
    }
    
//    @Test
    public void testNum() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of("data/in/04/k/txt/施設基準コード.txt"));
            FileOutputStream fos = new FileOutputStream("data/in/04/k/txt/施設基準コード.csv");
            PrintWriter writer = new PrintWriter(fos, false, StandardCharsets.UTF_8)) {
            for (;;) {
                String line = reader.readLine();
                if (line == null)
                    break;
                String[] fs = line.split(",", 2);
                writer.printf("\"%04d\",\"%s\"\r\n", Integer.parseInt(fs[0]), fs[1]);
            }
        }
    }

    static String pdfPath = "data/in/04/k/pdf/";
    static String[] kokujiPDF = {
//        pdfPath + "000907845.pdf",
        pdfPath + "000908781.pdf"
    };

	public static void 施設基準告示変換() throws IOException {
//	    Pdf.toText(kokujiPDF, "data/in/04/k/txt/kokujiText.txt", false, 22F, 14F, 0.5F, Pdf.Skip.TEXT,
	    Pdf.toText(kokujiPDF, "data/in/04/k/txt/kokujiText.txt", false, 22F, 14F, 0.5F, Pdf.Skip.TEXT,
        new StringFunction() {
            @Override public String eval(String line) {
                return line.matches("^\\s*[〇一二三四五六七八九]+頁\\s*$") ? "#" + line : line.replaceAll("\\s－", "－");
            }
	    });
	}
	
	@Test
	public void test施設基準告示変換() throws IOException {
	    施設基準告示変換();
	}
	
	@Test
	public void testPDF() throws IOException {
	    Pdf pdf = new Pdf(pdfPath + "000908781.pdf", false);
	    pdf.addDebugLine(96, 13);
	    pdf.addDebugLine(96, 14);
	    pdf.addDebugLine(96, 15);
	    pdf.addDebugLine(96, 16);
	    pdf.addDebugLine(96, 17);
	    List<List<String>> s = pdf.toStringList(22F, 14F, 0.5F, Pdf.Skip.TEXT, x -> x);
	    out.println(s);
	}
	
	
}
