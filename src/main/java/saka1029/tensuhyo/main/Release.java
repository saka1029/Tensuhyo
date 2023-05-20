package saka1029.tensuhyo.main;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;

public class Release {

    static final Logger logger = Logger.getLogger(Release.class.getName());
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    static final PrintStream ERR = new PrintStream(System.err, true, StandardCharsets.UTF_8);
    static final int MAX_RETRY_COUNT = 5;
    static final int RETRY_INTERVAL = 5000;

    static Path ROOT = Path.of("data/web");
    
    static String server, user, password;

    static void release(List<Path> files, int[] done) throws IOException {
        FTPClient client = new FTPClient();
        client.connect(server);
        logger.info("接続しました。");
        client.login(user, password);
        logger.info("ログインしました。");
        try (Closeable c = () -> client.disconnect()) {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            int max = files.size();
            for (int i = done[0] + 1; i < max; done[0] = i++) {
                Path f = files.get(i);
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
            }
            done[0] = max;
        }
        logger.info("切断しました。");
    }

    static void release(String... paths) throws IOException {
        for (String path : paths) {
            Path p = ROOT.resolve(path);
            List<Path> files = Files.walk(p).toList();
            int max = files.size();
            int[] done = {-1};
            while (done[0] < max)
                try {
                    release(files, done);
                } catch (IOException e) {
                    logger.warning("失敗しました。");
                }
        }
    }

    static void usage(String message) throws IOException {
        ERR.println("usage: java " + Release.class.getName() + " PATH...");
        ERR.println("    PATH : アップロードするファイルのトップディレクトリ");
        ERR.println("           (令和4年医科の場合「04/i」)");
        ERR.println("    起動前に環境変数FTP_CONFIGに「サーバ名 ユーザ名 パスワード」を設定する必要があります。");
        throw new IOException(message);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            usage("パラメータがありません。");
        String config = System.getenv("FTP_CONFIG");
        if (config == null)
            usage("環境変数FTP_CONFIGがありません。");
        String[] ftpConfig = config.split("\\s+");
        server = ftpConfig[0];
        user = ftpConfig[1];
        password = ftpConfig[2];
        release(args);
    }

}
