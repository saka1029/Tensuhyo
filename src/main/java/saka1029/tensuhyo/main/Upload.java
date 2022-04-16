package saka1029.tensuhyo.main;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Upload {

    static final String FTP_CONFIG_NAME = "FTP_CONFIG";
    static final String USAGE = "java saka1029.tensuhyo.main.Upload UPLOAD_FILE_NAMES";
    static final Path BASE_DIR = Paths.get("data/web/");

    static void usage(String message) {
        if (message != null)
            System.err.println(message);
        System.err.println(USAGE);
    }

    static void check(FTPClient client) throws IOException {
        int code = client.getReplyCode();
        String s = client.getReplyString();
        System.out.println(s);
        if (!FTPReply.isPositiveCompletion(code))
            throw new IOException(s);
    }

    static void put(FTPClient client, String file) throws IOException {
        System.out.println("put " + file);
        try (InputStream is = Files.newInputStream(BASE_DIR.resolve(file))) {
            client.storeFile(file, is);
        }
    }

    static void upload(String host, String user, String pass, String path) throws IOException {
        List<String> files = Files.readAllLines(Paths.get(path));
        FTPClient client = new FTPClient();
        client.connect(host);
        try (Closeable c = () -> client.disconnect()) {
            client.login(user, pass);
            if (!FTPReply.isPositiveCompletion(client.getReplyCode()))
                throw new IOException(client.getReplyString());
            client.enterLocalPassiveMode();
            check(client);
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            check(client);
            for (String file : files) {
                put(client, file);
                check(client);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage(null);
        String ftpParam = System.getenv(FTP_CONFIG_NAME);
        if (ftpParam == null)
            usage("環境変数'" + FTP_CONFIG_NAME + "'(HOST USER PASS)を指定してください。");
        String[] ftpParams = ftpParam.split("\\s+");
        upload(ftpParams[0], ftpParams[1], ftpParams[2], args[0]);
    }
}
