package saka1029.tensuhyo.parser;

public class 施設基準告示読込 extends BaseParser {

	public 施設基準告示読込() {
	    add(施設基準等.value);
		add(漢数字第.value);
		add(施設基準別表.value);
		add(漢数字別表第.value);
		add(漢数字.value);
		add(数字括弧.value);
		add(カナ.value);
		add(数字丸.value);
		add(数字.value);
		add(漢数字括弧.value);
		add(StencilParagraph.value);
	}
}
