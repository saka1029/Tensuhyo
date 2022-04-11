package saka1029.tensuhyo.parser;

public class 調剤告示読込 extends BaseParser {

	public 調剤告示読込() {
		add(節_調剤.value);
		add(通則_告示.value);
		add(区分_調剤.value);
		add(区分番号_調剤告示.value);
		add(数字.value);
		add(数字括弧.value);
		add(数字丸.value);
		add(カナ.value);
		add(注.value);
		add(注１.value);
		add(StencilParagraph.value);
	}
}
