package saka1029.tensuhyo;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.tensuhyo.pdf.PDFBox;

public class Test施設基準様式読込 {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    static final int HEAD_LINES = 3;
    static final Pattern YOSHIKI_NAME = Pattern.compile(
        "\\s*[(（]?\\s*("                       // group1
        + "\\s*(?:別\\s*添|別\\s*紙|様\\s*式)"
        + "\\s*((?:\\d|\\s+)+)"                 // group2
        + "\\s*(?:の\\s*((?:\\d|\\s+)+))?"      // group3
        + "\\s*(?:の\\s*((?:\\d|\\s+)+))?"      // group4
        + ")\\s*[)）]?(?:\\s+(.+))?");          // group5

    static void 様式パーサ(String... inPdfFiles) throws IOException {
        for (String inPdfFile : inPdfFiles) {
            List<List<String>> pageLines = new PDFBox(true).read(inPdfFile);
            for (int i = 0, pageCount = pageLines.size(); i < pageCount; ++i) {
                List<String> lines = pageLines.get(i);
                for (int j = 0, lineCount = Math.min(HEAD_LINES, lines.size()); j < lineCount; ++j) {
                    String norm = Normalizer.normalize(lines.get(j), Form.NFKD);
                    Matcher m = YOSHIKI_NAME.matcher(norm);
                    if (m.matches())
                        OUT.printf("%d:%d:%s%n", i + 1, j + 1, m.group(1).replaceAll("\\s+", ""));
                }
            }
        }
    }

    @Test
    public void test() throws IOException {
        String[] inPdfFiles = {
            "data/in/04/k/pdf/000907989.pdf",
            "data/in/04/k/pdf/000907862.pdf"
        };
        様式パーサ(inPdfFiles);
    }

}
