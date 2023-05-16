package saka1029.tensuhyo;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import saka1029.tensuhyo.parser.様式読込;
import saka1029.tensuhyo.pdf.様式;

public class Test様式読込 {

    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void test施設基準() throws IOException {
        String[] inPdfFiles = {
            "data/in/04/k/pdf/000907989.pdf",
            "data/in/04/k/pdf/000907862.pdf"
        };
        String outTxtFile = "test-data/04-k-yosiki-list.txt";
        List<様式> result = 様式読込.施設基準(outTxtFile, inPdfFiles);
        Set<String> set = new HashSet<>();
        for (var y : result)
        	if (set.contains(y.id()))
        		OUT.println("duplicate " + y.id());
        	else
        		set.add(y.id());
    }

}
