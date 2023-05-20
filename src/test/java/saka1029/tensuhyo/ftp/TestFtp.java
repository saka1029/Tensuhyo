package saka1029.tensuhyo.ftp;

import static org.junit.Assert.*;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class TestFtp {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    static void getPngFiles(FTPClient client, String path) throws IOException {
        OUT.println("getPngFiles: " + path);
        FTPFile[] files = client.listFiles(path, f -> f.getName().endsWith(".png"));
        for (FTPFile f : files) {
            OUT.printf("  restore %s (%d)%n", "data/web/" + path + "/" + f.getName(), f.getSize());
            try (OutputStream os = new FileOutputStream("data/web/" + path + "/" + f.getName())) {
                client.retrieveFile(path + "/" + f.getName(), os);
            }
        }
    }

//    @Test
    public void testRecoverPngFiles() throws IOException {
        String[] ftpConfig = System.getenv("FTP_CONFIG").split("\\s+");
        OUT.println(Arrays.toString(ftpConfig));
        FTPClient client = new FTPClient();
        client.connect(ftpConfig[0]);
        client.login(ftpConfig[1], ftpConfig[2]);
        try (Closeable c = () -> client.disconnect()) {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
//            getPngFiles(client, "30/k/image");
            getPngFiles(client, "30/i/image");
//            getPngFiles(client, "30/s/image");
//            getPngFiles(client, "30/t/image");
//            getPngFiles(client, "01/k/image");
//            getPngFiles(client, "01/i/image");
//            getPngFiles(client, "01/s/image");
//            getPngFiles(client, "01/t/image");
//            getPngFiles(client, "02/k/image");
//            getPngFiles(client, "02/i/image");
//            getPngFiles(client, "02/s/image");
//            getPngFiles(client, "02/t/image");
//            getPngFiles(client, "04/k/image");
//            getPngFiles(client, "04/i/image");
            getPngFiles(client, "04/s/image");
//            getPngFiles(client, "04/t/image");
        }
    }

}
