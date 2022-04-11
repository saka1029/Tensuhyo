package saka1029.tensuhyo.run.r0204;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Ignore;
import org.junit.Test;

public class TestUpload {

    static Logger logger = Commons.getLogger(TestUpload.class.getName());

     static final String HOST = "tensuhyo.html.xdomain.jp";
//    static final String HOST = "localhost";
    static final String USER = "tensuhyo.html.xdomain.jp";
    static final String PASS = "javar00t";
    static final String LOCAL_BASE = "data/web/";

    static void session(FTPClient client) throws IOException {
        put(client, "css/all.css");
        put(client, "index.html");
        put(client, "30/i/A000.html");
//        dir(client, "/");
    }

    static void dir(FTPClient client, String dir) throws IOException {
        if (!dir.endsWith("/")) dir += "/";
        for (FTPFile f : client.listFiles(dir))
            System.out.println(f);
    }

    static void put(FTPClient client, String file) throws IOException {
        System.out.println("put " + file);
        try (InputStream is = new FileInputStream(new File(LOCAL_BASE, file))) {
            client.storeFile(file, is);
//            check(client);
        }
    }

    static void del(FTPClient client, String path) throws IOException {
        client.dele(path);
        check(client);
    }

    static void check(FTPClient client) throws IOException {
        int code = client.getReplyCode();
        String s = client.getReplyString();
        System.out.println(s);
        if (!FTPReply.isPositiveCompletion(code))
            throw new IOException(s);
    }

    public void upload(String... paths) throws SocketException, IOException {
        FTPClient client = new FTPClient();
        client.connect(HOST);
        try (Closeable c = () -> client.disconnect()) {
            client.login(USER, PASS);
            check(client);
            client.enterLocalPassiveMode();
            check(client);
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            check(client);
            for (String path : paths) {
                put(client, path);
                check(client);
            }
        }

    }

    @Test
    @Ignore
    public void testConnect() throws SocketException, IOException {
        FTPClient client = new FTPClient();
        client.connect(HOST);
        try (Closeable c = () -> client.disconnect()) {
            client.login(USER, PASS);
            check(client);
            client.enterLocalPassiveMode();
            check(client);
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            check(client);
            session(client);
            client.quit();
            check(client);
        }
    }

    @Test
    public void upload20200306() throws SocketException, IOException {
        // アップロードエラーとなったファイルを再試行
        upload(
            "02/i/D245.html",
            "02/i/K075.html",
            "02/i/K454.html",
            "02/i/K502-2.html",
            "02/i/K769-2.html",
            "02/i/K803-3.html",
            "02/s/B004.html",
            "02/k/image/KIHON-BETTEN7-BESI50_3.pdf",
            "02/k/image/KIHON-BETTEN7-BESI56.pdf"
            );
    }

    @Test
    public void upload20200418() throws SocketException, IOException {
        upload(
            "02/i/0.2.10.T.html",
            "02/i/0.2.4.3.T.html",
            "02/i/A001.html",
            "02/i/A245.html",
            "02/i/A301.html",
            "02/i/A400.html",
            "02/i/B001.html",
            "02/i/B004.html",
            "02/i/B005-10.html",
            "02/i/B005-9.html",
            "02/i/C002.html",
            "02/i/C110-2.html",
            "02/i/J118.html",
            "02/i/K053.html",
            "02/i/K936.html",
            "02/i/N005-2.html",

            "02/k/1.8.html",
            "02/k/2.10.html",
            "02/k/2.3.html",
            "02/k/2.5.html",
            "02/k/2.9.html"
        );
    }

    @Test
    public void upload20200513() throws SocketException, IOException {
        upload(
            "02/i/C200.html",
            "02/i/D003.html",
            "02/i/D313.html"
        );
    }

    @Test
    public void upload20200517() throws SocketException, IOException {
        upload(
            "02/i/D012.html",
            "02/i/D023.html"
        );
    }

    @Test
    public void upload20200616() throws SocketException, IOException {
        upload(
            "02/i/0.2.3.T.html",
            "02/i/0.2.7.T.html",
            "02/i/B001-9.html",
            "02/i/M001-4.html",
            "02/i/M002.html",
            "02/i/M003.html",
            "02/i/M004.html",
            "02/s/B014.html"
        );
    }

    @Test
    public void upload20200708() throws SocketException, IOException {
        upload(
            "02/i/D012.html"
        );
    }

    @Test
    public void upload20200709() throws SocketException, IOException {
        upload(
            "02/i/D007.html",
            "02/i/D014.html",
            "02/i/D023.html",
            "02/i/D004-2.html"
        );
    }

    @Test
    public void upload20200802() throws SocketException, IOException {
        upload(
            "02/i/image/BESI36.pdf"
        );
    }

    @Test
    public void upload20200823() throws SocketException, IOException {
        upload(
            "02/i/D004-2.html",
            "02/i/D006-4.html",
            "02/i/D023.html"
        );
    }

    @Test
    public void upload20200904() throws SocketException, IOException {
        upload(
            "02/i/B001.html",
            "02/i/0.2.10.T.html",
            "02/i/K599-3.html",
            "02/i/K616-4.html",
            "02/i/K725.html",
            "02/i/K725-2.html"
        );
    }

    @Test
    public void upload20200918() throws SocketException, IOException {
        upload(
            "02/i/N002.html",
            "02/i/C112.html",
            "02/i/C169.html",
            "02/i/K002.html",
            "02/i/K154-4.html",
            "02/s/M015-2.html"
        );
    }

    @Test
    public void upload20201029() throws SocketException, IOException {
        upload(
            "02/i/D003.html"
        );
    }

    @Test
    public void upload20201101() throws SocketException, IOException {
        upload(
            "02/i/K043.html"
        );
    }

    @Test
    public void upload20201117() throws SocketException, IOException {
        upload(
            "02/i/D001.html"
        );
    }

    @Test
    public void upload20201120() throws SocketException, IOException {
        upload(
            "02/i/D023.html"
        );
    }

    @Test
    public void upload20210106() throws SocketException, IOException {
        upload(
            "02/i/B001-3-2.html",
            "02/i/D004-2.html",
            "02/i/D206.html",
            "02/i/K318.html",
            "02/i/K526-4.html",
            "02/i/K939.html",
            "02/s/J200-5.html"
        );
    }

    @Test
    public void upload20210120() throws SocketException, IOException {
        upload(
            "02/i/D004-2.html",
            "02/i/D006-18.html",
            "02/i/D006-7.html",
            "02/i/D008.html",
            "02/i/D012.html"
        );
    }

    @Test
    public void upload20210302() throws SocketException, IOException {
        upload(
            "02/i/D003.html",
            "02/i/D014.html"
        );
    }

    @Test
    public void upload20210320() throws SocketException, IOException {
        upload(
            "02/i/D217.html",
            "02/i/K000.html",
            "02/i/K555-2.html",
            "02/i/K938.html"
        );
    }

    @Test
    public void upload20210421() throws SocketException, IOException {
        upload(
            "index.html",
            "all.css",
            "css/all.css"
        );
    }

    @Test
    public void upload20210423() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/J201.html",
            "02/i/image/BESI16.pdf",
            "02/i/image/BESI21_6.pdf",
            "02/s/A204-2.html"
        );
    }

    @Test
    public void upload20210527() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D012.html"
        );
    }

    @Test
    public void upload20210627() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/C110-3.html",
            "02/i/C119.html",
            "02/i/C164.html",
            "02/i/C170.html",
            "02/i/D004-2.html",
            "02/i/D012.html",
            "02/i/D014.html",
            "02/i/D310.html",
            "02/i/K054.html",
            "02/i/K057.html",
            "02/i/K181-4.html",
            "02/i/K697-3.html",
            "02/i/K722.html",
            "02/i/K735-2.html"
        );
    }

    @Test
    public void upload20210705() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D015.html"
        );
    }

    @Test
    public void upload20210717() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D008.html",
            "02/i/D012.html"
        );
    }

    @Test
    public void upload20210824() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D004-2.html",
            "02/i/D006-12.html",
            "02/i/D006-19.html"
        );
    }

    @Test
    public void upload20210913() throws SocketException, IOException {
        upload(
            "sitemapindex.xml",
            "index.html",
            "02/t/0.1.html",
            "02/t/0.2.html",
            "02/t/0.2.T.html",
            "02/t/0.3.html",
            "02/t/0.4.html",
            "02/t/0.5.html",
            "02/t/0.5.T.html",
            "02/t/0.html",
            "02/t/0.T.html",
            "02/t/00.html",
            "02/t/01.html",
            "02/t/10.html",
            "02/t/11.html",
            "02/t/13-2.html",
            "02/t/13-3.html",
            "02/t/13.html",
            "02/t/14-2.html",
            "02/t/14-3.html",
            "02/t/15-2.html",
            "02/t/15-3.html",
            "02/t/15-4.html",
            "02/t/15-5.html",
            "02/t/15-6.html",
            "02/t/15-7.html",
            "02/t/15.html",
            "02/t/20.html",
            "02/t/30.html",
            "02/t/hikaku.html",
            "02/t/image/BESI1.pdf",
            "02/t/image/BESI2.pdf",
            "02/t/index.html",
            "02/t/kubun.html",
            "02/t/sitemap.xml",
            "02/t/yoshiki.html"
        );
    }

    @Test
    public void upload20210917() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D004-2.html"
        );
    }

    @Test
    public void upload20210925() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D004-2.html",
            "02/i/D005.html",
            "02/i/D014.html",
            "02/i/K305.html",
            "02/i/K594.html",
            "02/i/K936.html",
            "02/s/M010.html",
            "02/s/M029.html"
        );
    }

    @Test
    public void upload20211015() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D006-4.html",
            "02/i/D014.html"
        );
    }

    @Test
    public void upload20211221() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D003.html",
            "02/i/D012.html",
            "02/i/D023.html",
            "02/i/K259.html",
            "02/i/K594.html",
            "02/i/K939.html"
        );
    }

    @Test
    public void upload20220113() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D004-2.html",
            "02/i/D023.html",
            "02/i/J041-2.html"
        );
    }

    @Test
    public void upload20220301() throws SocketException, IOException {
        upload(
            "index.html",
            "02/i/D004-2.html"
        );
    }

    @Test
    public void upload20220301a() throws SocketException, IOException {
        upload(
            "02/i/sitemap.xml",
            "02/s/sitemap.xml",
            "02/t/sitemap.xml",
            "02/k/sitemap.xml"
        );
    }
}
