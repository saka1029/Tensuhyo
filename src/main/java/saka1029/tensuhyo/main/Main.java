package saka1029.tensuhyo.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.pdfbox.exceptions.COSVisitorException;
import saka1029.tensuhyo.parser.ParseException;

/**
 * usage: java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...
 */
public class Main {

    static final Logger logger = Logger.getLogger(Main.class.getName());

    static String USAGE = "usage java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...%n"
        + "  TOP_DIRECTORY: 先頭ディレクトリ%n"
        + "    TOP_DIRECTORY%n"
        + "      +---data%n"
        + "          +---in%n"
        + "          |   +---04 (令和4年)%n"
        + "          |       +---k (施設基準)%n"
        + "          |       |   +---pdf%n"
        + "          |       |       +---告示.pdf%n"
        + "          |       |       +---通知.pdf%n"
        + "          |       |   +---txt%n"
        + "          |       |       +---kokuji.txt%n"
        + "          |       |       +---tuti.txt%n"
        + "          |       |       +---告示.txt%n"
        + "          |       |       +---通知.txt%n"
        + "          |       +---i (医科)%n"
        + "          |       |   +---pdf%n"
        + "          |       |   +---txt%n"
        + "          |       +---s (歯科)%n"
        + "          |       |   +---pdf%n"
        + "          |       |   +---txt%n"
        + "          |       +---s (調剤)%n"
        + "          |           +---pdf%n"
        + "          |           +---txt%n"
        + "          +---web%n"
        + "              +---04 (令和4年)%n"
        + "  PDF_CONFIG: PDFの場所を格納する定義ファイル%n"
        + "  OPERATION:%n"
        + "    k0 施設基準PDF変換%n"
        + "    k1 施設基準様式一覧生成%n"
        + "    k2 施設基準HTML生成%n"
        + "    i0 医科PDF変換%n"
        + "    i1 医科様式一覧生成%n"
        + "    i2 医科HTML生成%n"
        + "    s0 歯科PDF変換%n"
        + "    s1 歯科様式一覧生成%n"
        + "    s2 歯科HTML生成%n"
        + "    t0 調剤PDF変換%n"
        + "    t1 調剤様式一覧生成%n"
        + "    t2 調剤HTML生成%n"
        ;

    static final Map<String, String> STEP_NAME = Map.ofEntries(
        Map.entry("k0", "施設基準PDF変換"),
        Map.entry("k1", "施設基準様式一覧生成"),
        Map.entry("k2", "施設基準HTML生成"),
        Map.entry("i0", "医科PDF変換"),
        Map.entry("i1", "医科様式一覧生成"),
        Map.entry("i2", "医科HTML生成"),
        Map.entry("s0", "歯科PDF変換"),
        Map.entry("s1", "歯科様式一覧生成"),
        Map.entry("s2", "歯科HTML生成"),
        Map.entry("t0", "調剤PDF変換"),
        Map.entry("t1", "調剤様式一覧生成"),
        Map.entry("t2", "調剤HTML生成"));

    static void usage(String message) {
        System.err.println(message);
        System.err.printf(USAGE);
        System.exit(1);
    }

    public static void run(Path config, String... operations)
        throws JsonSyntaxException, IOException, ParseException, COSVisitorException {
        Gson gson = new Gson();
        Facade facade = gson.fromJson(Files.readString(config), Facade.class);
        for (String op : operations) {
            logger.info("running " + op + ":" + STEP_NAME.get(op) + " ...");
            switch (op) {
            case "k0":
                facade.施設基準告示変換();
                facade.施設基準通知変換();
                facade.施設基準様式一覧変換();
                break;
            case "k1":
                facade.施設基準様式一覧生成();
                break;
            case "k2":
                facade.施設基準HTML生成();
//                facade.施設基準基本様式();
//                facade.施設基準特掲様式();
                break;
            case "i0":
                facade.医科告示変換();
                facade.医科通知変換();
                facade.医科様式一覧変換();
                break;
            case "i1":
                facade.医科様式一覧生成();
                break;
            case "i2":
                facade.医科HTML生成();
                facade.医科区分一覧生成();
//                facade.医科様式();
                break;
            case "s0":
                facade.歯科告示変換();
                facade.歯科通知変換();
                facade.歯科様式一覧変換();
                break;
            case "s1":
                facade.歯科様式一覧生成();
                break;
            case "s2":
                facade.歯科HTML生成();
                facade.歯科区分一覧生成();
//                facade.歯科様式();
                break;
            case "t0":
                facade.調剤告示変換();
                facade.調剤通知変換();
                facade.調剤様式一覧変換();
                break;
            case "t1":
                facade.調剤様式一覧生成();
                break;
            case "t2":
                facade.調剤HTML生成();
                facade.調剤区分一覧生成();
//                facade.調剤様式();
                break;
            default:
                System.err.println("unknown option " + op);
                System.exit(2);
                break;
            }
        }
    }

    public static void main(String[] args) throws JsonSyntaxException, IOException, ParseException, COSVisitorException {
        Path top = Paths.get(".");
        int max = args.length, i = 0;
        for ( ; i < max; ++i) {
            String arg = args[i];
            if (arg.startsWith("-") && arg.length() >= 2)
                switch (arg.charAt(1)) {
                case 'd':
                    if (++i >= max)
                        usage("missing after '-d'");
                    top = Paths.get(args[i]);
                    break;
                default:
                    usage("unknown option");
                }
            else
                break;
        }
        if (max - i < 1)
            usage("no config");
        run(top.resolve(args[i++]), Arrays.copyOfRange(args, i, max));
    }
}