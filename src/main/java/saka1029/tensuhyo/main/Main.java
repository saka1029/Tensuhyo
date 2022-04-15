package saka1029.tensuhyo.main;

import java.io.File;

/**
 * usage: java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...
 */
public class Main {

    static String USAGE = "usage java saka1029.tensuhyo.main.Main [-d TOP_DIRECTORY] PDF_CONFIG OPERATION...";

    static void uasge() {
        System.err.println(USAGE);
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
        
    }
}