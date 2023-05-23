package saka1029.tensuhyo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.exceptions.COSVisitorException;

import com.google.gson.JsonSyntaxException;

import saka1029.tensuhyo.main.Facade;
import saka1029.tensuhyo.parser.ParseException;
import saka1029.tensuhyo.util.Common;

public class TestFacade {

    static {
        Common.config();
    }

//    @Test
    public void test施設基準() throws JsonSyntaxException, IOException, ParseException, COSVisitorException {
        Facade facade = Facade.load("r0404.json");
        String outDir = "test-data/web";
        Files.createDirectories(Path.of(outDir));
        facade.出力ディレクトリ = outDir;
//        facade.施設基準様式一覧生成();
        facade.施設基準HTML生成();
    }

}
