package saka1029.tensuhyo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.junit.Test;
import saka1029.tensuhyo.generator.Renderer;
import saka1029.tensuhyo.generator.RendererOption;
import saka1029.tensuhyo.generator.索引;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.parser.ParseException;
import saka1029.tensuhyo.parser.医科告示読込;
import saka1029.tensuhyo.util.TextWriter;

public class TestRenderer {

    static final Logger logger = Logger.getLogger(TestRenderer.class.getName());
    static final String KOKUJI =
        "           第１章  基本診療料\r\n"
        + "             第１部  初・再診料\r\n"
        + "   通則\r\n"
        + "     １  健康保険法第63条第１項第１号及び\r\n"
        + "         高齢者医療確保法第64条第１項第１号の規定による初診\r\n"
        + "         及び再診の費用は、第１節又は第２節の各区分の所定点数\r\n"
        + "         により算定する。\r\n";

//    @Test
    public void testRendererGoogleAnalyticsTrackingCode() throws IOException, ParseException {
        Document doc = new 医科告示読込().parse(KOKUJI, "令和04年医科");
        Path outPath = Files.createTempDirectory("tensuhyo-TestRenderer");
        logger.info("outPath=" + outPath);
        new Renderer().HTML出力(doc, outPath.toFile(), new 索引(),
			new RendererOption() {
				@Override public String 共通タイトル() { return "令和04年年医科診療報酬点数表"; }
				@Override public String baseUrl() { return "http://test.org/04/i"; }
				@Override public String 比較タイトル() { return "dummy"; }
				@Override
				public void 目次(Node node, TextWriter w) {
					w.printf("<hr>\n<div id='menu'></div>\n");
 				}
            }
        );
        logger.info("index.html=\n"
            + Files.readString(outPath.resolve("index.html"), StandardCharsets.UTF_8));
    }
}
