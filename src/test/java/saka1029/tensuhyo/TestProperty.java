package saka1029.tensuhyo;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import org.junit.Test;

public class TestProperty {

    @Test
    public void testMultiline() throws IOException {
        String in = "# これはコメント行\n"
            + "a=a0,\\\n"
            + "  a1,\\\n"
            + "  a2";
        System.out.println(in);
        Properties prop = new Properties();
        prop.load(new StringReader(in));
        assertEquals("a0,a1,a2", prop.getProperty("a"));
    }
}
