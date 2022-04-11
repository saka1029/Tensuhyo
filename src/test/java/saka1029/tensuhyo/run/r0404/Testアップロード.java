package saka1029.tensuhyo.run.r0404;

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

    /**
     * 区分番号一覧から比較ページへのリンク先を
     * hikaku.htmlから../../hikaku.htmlに変更した。
     */
    @Test
    public void upload20220328() throws SocketException, IOException {
        upload(
            "02/i/kubun.html",
            "02/s/kubun.html",
            "02/t/kubun.html",
            "04/i/kubun.html",
            "04/s/kubun.html",
            "04/t/kubun.html"
        );
    }

    @Test
    public void upload20220406() throws SocketException, IOException {
        upload(
            "index.html",
            "04/i/0.1.2.T.html",
            "04/i/0.2.7.T.html",
            "04/i/B001.html",
            "04/i/B005-11.html",
            "04/i/B009.html",
            "04/i/B011-5.html",
            "04/i/B015.html",
            "04/i/C112-2.html",
            "04/i/C112.html",
            "04/i/C152-2.html",
            "04/i/D206.html",
            "04/i/D239-4.html",
            "04/i/I002.html",
            "04/i/I006-2.html",
            "04/i/J041-2.html",
            "04/k/1.8.html",
            "04/s/K004.html",
            "04/t/01.html",
            "04/t/10-3.html",
            "04/t/15-3.html",
            "04/t/15.html"
         );
    }}
