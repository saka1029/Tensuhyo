package saka1029.tensuhyo.main;

import java.io.File;

/**
 * usage: java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...
 */
public class Main {

    static String USAGE = "usage java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...%n"
        + "  TOP_DIRECTORY: 先頭ディレクトリ%n"
        + "    TOP_DIRECTORY%n"
        + "      +---data%n"
        + "          +---in%n"
        + "          |   +---04 (令和4年)%n"
        + "          |       +---k (施設基準)%n"
        + "          |       |   +---pdf%n"
        + "          |       |   +---txt%n"
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
        + "    k1 施設基準HTML生成%n"
        + "    i0 医科PDF変換%n"
        + "    i1 医科HTML生成%n"
        + "    s0 歯科PDF変換%n"
        + "    s1 歯科HTML生成%n"
        + "    t0 調剤PDF変換%n"
        + "    i1 調剤HTML生成%n"
        ;

    static void usage() {
        System.err.printf(USAGE);
        System.exit(1);
    }

    public static void main(String[] args) {
        File top = new File(".");
        int max = args.length, i = 0;
        for ( ; i < max; ++i) {
            String arg = args[i];
            if (arg.startsWith("-")) {

            } else
                break;
        }
        if (max - i < 2)
            usage();
    }
}