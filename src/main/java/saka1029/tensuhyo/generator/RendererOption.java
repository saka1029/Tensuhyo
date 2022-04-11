package saka1029.tensuhyo.generator;

import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.util.TextWriter;

public interface RendererOption {
	String 共通タイトル();
	String 比較タイトル();
	void 目次(Node node, TextWriter w);
	String baseUrl();
}