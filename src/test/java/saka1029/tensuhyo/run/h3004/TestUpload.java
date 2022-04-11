package saka1029.tensuhyo.run.h3004;

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

    @Test
    public void upload20180830() throws SocketException, IOException {
        upload("30/i/D004-2.html");
    }

    @Test
    public void upload20180912() throws SocketException, IOException {
        upload(
            "30/i/C106.html",
            "30/i/D006-2.html",
            "30/i/K686.html",
            "30/i/D007.html"
        );
    }

    @Test
    public void upload20180907() throws SocketException, IOException {
        upload("30/i/D014.html");
    }

    @Test
    public void upload20181116() throws SocketException, IOException {
        upload("30/i/D009.html");
        upload("30/i/D014.html");
        upload("30/i/D023.html");
    }

    @Test
    public void upload20181216() throws SocketException, IOException {
        upload("index.html");
    }

    @Test
    public void upload20181228() throws SocketException, IOException {
        upload("30/i/C152-2.html");
        upload("30/i/D006-4.html");
        upload("30/i/E203.html");
        upload("30/s/M000-2.html");
        upload("30/s/M001.html");
        upload("30/s/M016.html");
    }

    /**
     * http://tensuhyo.html.xdomain.jp/30/i/0.html　見出し変更経過措置→経過措置等
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.html　見出し変更経過措置→経過措置等
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.T.html　これは通則なのでなくなる。
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.1.html    これは第１部　経過措置として追加
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.1.T.html    これは第１部　経過措置の通則として追加
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.2.html　これは第２部　算定制限として追加
     * http://tensuhyo.html.xdomain.jp/30/i/0.4.2.T.html　これは第２部　算定制限の通則として追加
     */

    @Test
    public void upload20190113() throws SocketException, IOException {
        upload("30/i/0.html");
        upload("30/i/0.4.html");
//        upload("30/i/0.4.T.html"); 削除
        upload("30/i/0.4.1.html");
        upload("30/i/0.4.1.T.html");
        upload("30/i/0.4.2.html");
        upload("30/i/0.4.2.T.html");
        upload("30/i/D006-3.html");
        upload("30/i/D014.html");
    }

    @Test
    public void upload20190301() throws SocketException, IOException {
        upload("30/i/D006-7.html");
    }

    @Test
    public void upload20190319() throws SocketException, IOException {
        upload("30/i/K920.html");
        upload("30/i/K921.html");
    }

    @Test
    public void upload20190403() throws SocketException, IOException {
        upload("30/i/J003.html");
        upload("30/i/K000.html");
        upload("30/i/K910-2.html");
    }

    @Test
    public void upload20190425() throws SocketException, IOException {
        upload("30/i/D023.html");
    }

    @Test
    public void upload20190603() throws SocketException, IOException {
        upload("30/i/K921.html");
        upload("30/i/K922.html");
    }

    @Test
    public void upload20190604() throws SocketException, IOException {
        upload("30/i/D004-2.html");
        upload("30/i/D006-2.html");
        upload("30/i/D006-4.html");
        upload("30/i/I002.html");
        upload("30/i/M001-2.html");
        upload("30/i/N002.html");
        upload("30/i/N005.html");
    }

    @Test
    public void upload20190708() throws SocketException, IOException {
        upload("30/i/D007.html");
    }

    @Test
    public void upload20190803() throws SocketException, IOException {
        upload("30/i/D006-2.html",
               "30/i/D012.html");
    }

    @Test
    public void upload20190912() throws SocketException, IOException {
        upload("30/i/C107.html",
               "30/i/C167.html",
               "30/i/D239.html",
               "30/i/K537-2.html",
               "30/i/K559-3.html");
    }

}
