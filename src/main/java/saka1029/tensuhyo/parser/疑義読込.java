package saka1029.tensuhyo.parser;

public class 疑義読込 extends BaseParser {

	public 疑義読込() {
		add(疑義_点数表.value);
		add(疑義_分類.value);
		add(疑義_問.value);
		add(疑義_答.value);
		add(StencilParagraph.value);
	}
}
