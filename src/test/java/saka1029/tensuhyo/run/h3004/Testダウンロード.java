package saka1029.tensuhyo.run.h3004;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

public class Testダウンロード {

    static final String 年度 = Commons.YEAR;
    static final String TOP_URL = "http://www.mhlw.go.jp";
    static final String BASE_URL = TOP_URL + "/stf/seisakunitsuite/bunya/0000188411.html";

    static final String SAVE_TXT_DIR = "data/in/" + 年度 + "/改訂/HomePage/";
    static final String SAVE_PDF_DIR = "c:/temp/";

    static final boolean DOWNLOAD_PDF = false;

    void save(String url) throws FileNotFoundException, IOException {
        if (!DOWNLOAD_PDF) return;
        if (url.startsWith("http")) return;
        HttpURLConnection conn = (HttpURLConnection)new URL(TOP_URL + url).openConnection();
        // String f = url.replaceFirst(".*/", "");
        try (InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(SAVE_PDF_DIR + url.replaceFirst(".*/", ""))) {
            byte[] buffer = new byte[4096];
            int size;
            while ((size = in.read(buffer)) != -1)
                out.write(buffer, 0, size);
        }
    }

    @Test
    public void testDownload() throws IOException {
        File dir = new File(SAVE_TXT_DIR);
        if (!dir.exists())
            dir.mkdirs();
        Calendar d = Calendar.getInstance();
        String f = String.format("%tF.txt", d);
        File file = new File(SAVE_TXT_DIR + f);
        Document doc = Jsoup.connect(BASE_URL).get();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (Element e : doc.getAllElements())
                if (e.tagName().equals("a")
                    && e.hasAttr("href")
                    && e.attr("href").toLowerCase().endsWith(".pdf")) {
                    out.println(e.attr("href"));
                    save(e.attr("href"));
                } else if (e.hasText())
                    out.println(e.text());
        }
    }

}
