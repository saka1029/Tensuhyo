package saka1029.tensuhyo.parser;

public class 審査事例読込 extends BaseParser {

	public 審査事例読込() {
		add(審査事例数字.value);
		add(審査事例履歴.value);
		add(審査事例丸.value);
		add(StencilParagraph.value);
	}
}
