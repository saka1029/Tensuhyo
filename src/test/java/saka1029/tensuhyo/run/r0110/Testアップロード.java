package saka1029.tensuhyo.run.r0110;

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

public class Testアップロード {

    static Logger logger = Commons.getLogger(Testアップロード.class.getName());

     static final String HOST = "tensuhyo.html.xdomain.jp";
//    static final String HOST = "localhost";
    static final String USER = "tensuhyo.html.xdomain.jp";
    static final String PASS = "javar00t";
    static final String LOCAL_BASE = "D:/git/Tensuhyo/data/web/";

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
            check(client);
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

    /**
     * 誤り訂正分のみアップロード
     */
    @Test
    public void upload20190822() throws SocketException, IOException {
        upload(
            "01/i/A104.html",
            "01/i/A301.html",
            "01/i/A317.html",
            "01/i/A400.html",
            "01/i/C001-2.html",
            "01/t/01.html"
            );
    }

    @Test
    public void upload20190912() throws SocketException, IOException {
        upload(
            "01/i/J201.html",
            "01/i/image/BESI25.pdf"
            );
    }

    @Test
    public void upload20191011() throws SocketException, IOException {
        upload("01/i/D007.html");
    }

    @Test
    public void upload20191119() throws SocketException, IOException {
        upload("01/i/D023.html");
    }

    @Test
    public void upload20191121() throws SocketException, IOException {
        upload("01/i/J041-2.html");
    }

    @Test
    public void upload20191126() throws SocketException, IOException {
        upload("01/i/J038.html",
               "01/i/K311.html");
    }

    @Test
    public void upload20191126_2() throws SocketException, IOException {
        upload("sitemapindex.xml");
    }

    @Test
    public void upload20191211() throws SocketException, IOException {
        upload("01/i/D006-4.html",
               "01/i/D413.html",
               "01/i/K574-2.html",
               "01/i/K617-4.html",
               "01/i/K642.html",
               "01/s/I001.html");
    }

    @Test
    public void upload20200115() throws SocketException, IOException {
        upload("01/i/D006-6.html",
               "01/i/K617-4.html");
    }

    @Test
    public void upload20200310() throws SocketException, IOException {
        upload("01/i/D023.html");
    }

}
