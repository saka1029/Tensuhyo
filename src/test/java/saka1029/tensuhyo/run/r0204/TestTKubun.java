package saka1029.tensuhyo.run.r0204;

import static saka1029.tensuhyo.run.r0204.Commons.ENCODING;
import static saka1029.tensuhyo.run.r0204.Commons.GENGO;
import static saka1029.tensuhyo.run.r0204.Commons.OLD_GENGO;
import static saka1029.tensuhyo.run.r0204.Commons.OLD_YEAR;
import static saka1029.tensuhyo.run.r0204.Commons.YEAR;
import static saka1029.tensuhyo.run.r0204.Commons.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.tensuhyo.generator.Renderer;
import saka1029.tensuhyo.generator.RendererOption;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.parser.ParseException;
import saka1029.tensuhyo.parser.Parser;
import saka1029.tensuhyo.parser.調剤告示読込;
import saka1029.tensuhyo.util.TextIO;
import saka1029.tensuhyo.util.TextWriter;

public class TestTKubun {

	static final Logger logger = getLogger(TestTKubun.class.getName());

	static void log(String format, Object... args) {
		logger.log(Level.INFO, String.format(format, args));
	}

	static final String BASE_DIR = "data/in/";
	static final String TEN = "t";
	static final String TENSU = "調剤";

	static final String OLD_KOKUJI = BASE_DIR + YEAR + "/" + TEN + "/txt/告示";
	static final String OLD_ROOT_NAME = GENGO + YEAR + "年" + TENSU;
	static final String NEW_KOKUJI = BASE_DIR + YEAR + "/" + TEN + "/txt/告示";
	static final String NEW_ROOT_NAME = GENGO + YEAR + "年" + TENSU;

	static final String HTML_DIR = "data/web/" + YEAR + "/" + TEN ;
	static final String BASE_URL = "http://tensuhyo.html.xdomain.jp/" + YEAR + "/" + TEN;
	static final String HTML_FILE = HTML_DIR + "/kubun";
	static final String OLD_URL = "../../" + OLD_YEAR + "/" + TEN ;

	static final String COMMON_TITLE = GENGO + YEAR + "年" + TENSU + "報酬点数表";
	static final String COMPARE_TITLE = OLD_GENGO + OLD_YEAR + "," + YEAR + "年" + TENSU + "報酬点数表";

	@Test
	public void kubun() throws IOException, ParseException {
		String oldText = TextIO.ReadFrom(OLD_KOKUJI + ".txt", ENCODING);
		Parser oldParser = new 調剤告示読込();
		Document oldDoc = oldParser.parse(oldText, OLD_ROOT_NAME);

		String newText = TextIO.ReadFrom(NEW_KOKUJI + ".txt", ENCODING);
		Parser newParser = new 調剤告示読込();
		Document newDoc = newParser.parse(newText, NEW_ROOT_NAME);

		Renderer renderer = new Renderer();
//		renderer.commonTitle = COMMON_TITLE;
		renderer.区分一覧出力(oldDoc, newDoc, new File(HTML_FILE + ".html"), OLD_URL,
			new RendererOption() {
				@Override public String 共通タイトル() { return COMMON_TITLE; }
				@Override public String baseUrl() { return BASE_URL; }
				@Override public String 比較タイトル() { return COMPARE_TITLE; }
                @Override
                public void 目次(Node node, TextWriter w) {
                    w.printf("<hr>%n<div id='menu'></div>%n");
                }
			});
	}

}
