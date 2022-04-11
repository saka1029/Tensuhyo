package saka1029.tensuhyo.generator;

import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.util.TextWriter;

public interface 施設基準RendererCallback {

    String 共通タイトル();
    String baseUrl();
    void index(Node node, TextWriter w);
    String imageDir();

}
