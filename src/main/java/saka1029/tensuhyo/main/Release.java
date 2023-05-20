package saka1029.tensuhyo.main;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;

public class Release {

    static final Logger logger = Logger.getLogger(Release.class.getName());
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    static final PrintStream ERR = new PrintStream(System.err, true, StandardCharsets.UTF_8);

    static Path ROOT = Path.of("data/web");

    interface IOConsumer<T> {
        void accept(T e) throws IOException;
    }
    
    static <T> Consumer<T> wrap(IOConsumer<T> a) {
        return e -> {
            try {
                a.accept(e);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        };
    }

    static void release(FTPClient client, String path) throws IOException {
        Path root = ROOT.resolve(path);
        Files.walk(root)
            .forEach(wrap(f -> {
                Path remotePath = f.subpath(ROOT.getNameCount(), f.getNameCount());
                String remote = "/" + remotePath.toString().replaceAll("\\\\", "/");
                if (Files.isDirectory(f)) {
                    logger.info("mkdir " + remote);
                    client.makeDirectory(remote);
                } else {
                    logger.info("upload " + f + " to " + remote);
                    try (InputStream is = Files.newInputStream(f)) {
                        client.storeFile(remote, is);
                    }
                }
            }));
    }
    static void release(String server, String user, String password, String... paths) throws IOException {
        FTPClient client = new FTPClient();
        client.connect(server);
        logger.info("connect");
        client.login(user, password);
        logger.info("login");
        try (Closeable c = () -> client.disconnect()) {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            for (String path : paths)
                release(client, path);
        }
        logger.info("quit");
    }

    static void usage() throws IOException {
        ERR.println("usage: java " + Release.class.getName() + " PATH...");
        ERR.println("    PATH : アップロードするファイルのトップディレクトリ");
        ERR.println("           (令和4年医科の場合「04/i」)");
        ERR.println("    起動前に環境変数FTP_CONFIGに「サーバ名 ユーザ名 パスワード」を設定する必要があります。");
        throw new IOException("パラメータを修正してください");
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            usage();
        String config = System.getenv("FTP_CONFIG");
        if (config == null)
            usage();
        String[] ftpConfig = config.split("\\s+");
        release(ftpConfig[0], ftpConfig[1], ftpConfig[2], args);
    }

}
