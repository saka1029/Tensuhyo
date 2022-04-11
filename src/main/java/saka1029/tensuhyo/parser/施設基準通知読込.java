package saka1029.tensuhyo.parser;

/**
 * 第１、第２は元のテキストにない。
 */
public class 施設基準通知読込 extends BaseParser {

	public 施設基準通知読込() {
		add(章.value);
		add(数字第.value);
		add(数字の.value);
		add(数字括弧.value);
		add(カナ.value);
		add(カナ括弧.value);
//		add(英小文字.value);
		add(StencilParagraph.value);
	}
}
