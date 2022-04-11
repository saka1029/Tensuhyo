package saka1029.tensuhyo.parser;

public class 医科告示読込 extends BaseParser {

	public 医科告示読込() {
		add(章.value);
		add(部.value);
		add(節.value);
		add(款.value);
		add(通則_告示.value);
		add(区分.value);
		add(区分分類.value);
		add(区分番号_告示.value);
		add(数字.value);
		add(数字括弧.value);
		add(数字丸.value);
		add(カナ.value);
		add(注.value);
		add(注１.value);
		add(StencilParagraph.value);
	}
}
