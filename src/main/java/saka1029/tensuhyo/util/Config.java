package saka1029.tensuhyo.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * [SYNTAX]
 * config  = {define | comment}
 * define  = value '=' {value}
 * value   = symbol | string
 * symbol  = SYMCHAR {SYMCHAR}
 * comment + '#' (改行以外) ['\r'] '\n'
 * string  = '"' STRCHAR '"'
 * SYMCHAR = ('=', 空白以外)
 * STRCHAR = ('"'以外、ただしエスケープ文字'\n', '\r', '\t', '\"'を許容)
 * </pre>
 */
public class Config {

    Map<String, List<String>> map = new HashMap<>();

    public static Config load(String file) throws IOException {
        return of(Files.readString(Paths.get(file), StandardCharsets.UTF_8));
    }
    
    public static Config of(String text) {
    }
}
