package saka1029.tensuhyo.ftp;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
//            getPngFiles(client, "30/i/image");
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
//            getPngFiles(client, "04/s/image");
//            getPngFiles(client, "04/t/image");
        }
    }
    
    static void deleteOldPdf(FTPClient client, String dir) throws IOException {
        OUT.printf("%s:%n", dir);
        FTPFile[] files = client.listFiles(dir,
            f -> f.getName().contains("BESI") || f.getName().contains("BETTEN"));
        for (var f : files) {
            boolean deleted = client.deleteFile(dir + "/" + f.getName());
            OUT.printf("delete %s (%d) -> %s%n", f.getName(), f.getSize(), deleted);
        }
    }

//    @Test
    public void testDeleteOldPdf() throws IOException {
        String[] ftpConfig = System.getenv("FTP_CONFIG").split("\\s+");
        FTPClient client = new FTPClient();
        client.connect(ftpConfig[0]);
        client.login(ftpConfig[1], ftpConfig[2]);
        try (Closeable c = () -> client.disconnect()) {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            for (String n : new String[] {"30", "01", "02", "04"}) {
//				deleteOldPdf(client, n + "/k/image");
//				deleteOldPdf(client, n + "/i/image");
//				deleteOldPdf(client, n + "/s/image");
//				deleteOldPdf(client, n + "/t/image");
            }
        }
    }

}
