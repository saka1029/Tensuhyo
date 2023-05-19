package saka1029.tensuhyo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;
import com.google.gson.Gson;
import org.junit.Test;
import saka1029.tensuhyo.main.Facade;

public class TestProperty {

    static Logger logger = Logger.getLogger(TestProperty.class.getName());

//    @Test
    public void testMultiline() throws IOException {
        String in = "# これはコメント行\n"
            + "a=a0,\\\n"
            + "  a1,\\\n"
            + "  a2";
        logger.info(in);
        Properties prop = new Properties();
        prop.load(new StringReader(in));
        assertEquals("a0,a1,a2", prop.getProperty("a"));
    }

//    @Test
    public void testGson() {
        String json = "{"
            + "    \"元号\" : \"令和\",\n"
            + "    \"年度\" : \"04\",\n"
            + "    \"旧元号\" : \"令和\",\n"
            + "    \"旧年度\" : \"02\",\n"
            + "    \"医科告示PDF\" : [\n"
            + "        \"000907834.pdf\"\n"
            + "    ],\n"
            + "    \"医科通知PDF\" : [\n"
            + "        \"000907838.pdf\"\n"
            + "    ],\n"
            + "    \"医科様式PDF\" : [\n"
            + "        \"000907839.pdf\"\n"
            + "    ],\n"
            + "    \"歯科告示PDF\" : [\n"
            + "        \"000907835.pdf\"\n"
            + "    ],\n"
            + "    \"歯科通知PDF\" : [\n"
            + "        \"000907840.pdf\"\n"
            + "    ],\n"
            + "    \"歯科様式PDF\" : [\n"
            + "        \"000907841.pdf\"\n"
            + "    ],\n"
            + "    \"調剤告示PDF\" : [\n"
            + "        \"000907836.pdf\"\n"
            + "    ],\n"
            + "    \"調剤通知PDF\" : [\n"
            + "        \"000907842.pdf\"\n"
            + "    ],\n"
            + "    \"調剤様式PDF\" : [\n"
            + "        \"000907843.pdf\"\n"
            + "    ],\n"
            + "    \"施設基準告示PDF\" : [\n"
            + "        \"000907845.pdf\",\n"
            + "        \"000908781.pdf\"\n"
            + "    ],\n"
            + "    \"施設基準通知PDF\" : [\n"
            + "        \"000907989.pdf\",\n"
            + "        \"000907862.pdf\"\n"
            + "    ],\n"
            + "    \"施設基準基本様式PDF\" : [\n"
            + "        \"betten/000907989-y.pdf\"\n"
            + "    ],\n"
            + "    \"施設基準特掲様式PDF\" : [\n"
            + "        \"betten/000907862-y.pdf\"\n"
            + "    ]\n"
            + "}\n";
        Gson gson = new Gson();
        Facade facade = gson.fromJson(json, Facade.class);
        assertEquals("令和", facade.元号);
        assertArrayEquals(new String[] {"000907845.pdf", "000908781.pdf"}, facade.施設基準告示PDF);
    }

//    @Test
    public void testReadFile() throws IOException {
        Gson gson = new Gson();
        String json = Files.readString(Paths.get("r0404.json"));
        Facade facade = gson.fromJson(json, Facade.class);
        assertEquals("令和", facade.元号);
        assertEquals("04", facade.年度);
        assertEquals("令和", facade.旧元号);
        assertEquals("02", facade.旧年度);
        assertArrayEquals(new String[] {"000907845.pdf", "000908781.pdf"}, facade.施設基準告示PDF);
    }

}
