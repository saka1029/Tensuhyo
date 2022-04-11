package saka1029.tensuhyo.run.h3004;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class Test404_20181222 {

    String base = "http://tensuhyo.html.xdomain.jp/";

    void get(String path) {
        try {
            URL url = new URL(base + path);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            try (Closeable c = () -> con.disconnect()) {
                con.connect();
                System.out.println(path + " -> " + con.getResponseCode());
            }
        } catch (IOException e) {
            System.out.println(path + ":");
            System.out.println(e);
        }
    }

    @Test
    public void test() {
        get("26/s/M032.html");
        get("24/i/J070-4.html");
        get("30/s/A221-2.html");
        get("28/i/D305.html");
        get("28/i/0.2.3.2.T.html");
        get("28/i/B005-3.html");
        get("28/i/K604.html");
        get("24/i/0.2.9.1.K.189.html");
        get("24/i/K154-3.html");
        get("24/i/K183-2.html");
        get("24/i/B001-2-9.html");
        get("26/s/I011-2-2.html");
        get("24/i/A207-4.html");
        get("24/i/K755-2.html");
        get("24/i/K802-4.html");
        get("26/i/K356.html");
        get("24/i/K804-2.html");
        get("24/i/K356-2.html");
        get("26/i/H007-4.html");
        get("28/i/0.2.9.1.K.189.html");
        get("24/i/K525-3.html");
        get("28/i/N005-3.html");
        get("26/i/0.2.3.3.K.84.T.html");
        get("28/i/A238-9.html");
        get("26/i/D211-4.html");
        get("26/i/0.2.3.3.K.84.html");
        get("26/i/K530-3.html");
        get("26/i/A248.html");
        get("28/i/K716-3.html");
        get("28/i/B005-2.html");
        get("26/s/0.2.11.2.html");
        get("30/t/15%E3%81%AE6.html");
        get("26/i/yoshiki.html");
        get("26/i/A308-2.html");
        get("24/i/K182-3.html");
        get("26/i/0.2.12.1.html");
        get("26/i/K528-3.html");
        get("26/i/H003-4.html");
        get("28/t/13.html");
        get("26/i/0.2.12.2.html");
        get("26/t/yoshiki.html");
        get("28/i/A238-5.html");
        get("28/i/K684-2.html");
        get("26/i/K560-2.html");
        get("28/i/K735-5.html");
        get("24/i/H003-3.html");
        get("26/i/C171-2.html");
        get("26/i/K939-6.html");
    }

}
