package saka1029.tensuhyo.pdf;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import saka1029.tensuhyo.generator.様式一覧Renderer;

public class Test様式 {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void test施設基準様式一覧変換() throws IOException {
        String[] inPdfFiles = {
            "data/in/04/k/pdf/000907989.pdf",
            "data/in/04/k/pdf/000907862.pdf"
        };
        Files.createDirectories(Path.of("test-data/04/k"));
        String outTxtFile = "test-data/04/k/yosiki.txt";
        様式.施設基準様式一覧変換(outTxtFile, inPdfFiles);
    }
    
    @Test
    public void test施設基準様式一覧出力() throws IOException, COSVisitorException {
        String base = "test-data/04/k/";
        String inTxtFile = base + "様式.txt";
        Files.createDirectories(Path.of(base, "image"));
        List<様式> list = PDFBox.ページ分割(inTxtFile, base + "image");
        new 様式一覧Renderer().HTML出力(list, base + "yoshiki.html", "令和04年度 施設基準様式一覧");
    }

    @Test
    public void test医科様式一覧変換() throws IOException {
        String[] inPdfFiles = {
            "data/in/04/i/pdf/000907839.pdf"
        };
        String outTxtFile = "test-data/04-i-yosiki-list.txt";
        様式.様式一覧変換(outTxtFile, inPdfFiles);
    }

}
