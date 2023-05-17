package saka1029.tensuhyo.pdf;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class Test様式 {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void test施設基準様式一覧変換() throws IOException {
        String[] inPdfFiles = {
            "data/in/04/k/pdf/000907989.pdf",
            "data/in/04/k/pdf/000907862.pdf"
        };
        String outTxtFile = "test-data/04-k-yosiki-list.txt";
        様式.施設基準様式一覧変換(outTxtFile, inPdfFiles);
    }

}
