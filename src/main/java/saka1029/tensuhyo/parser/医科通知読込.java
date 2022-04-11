package saka1029.tensuhyo.parser;

/**
 * oldTODO: 通知では区分分類の下に通則が来ることがある。
 *
 */
public class 医科通知読込 extends BaseParser {

	public 医科通知読込() {
		add(章.value);
		add(部.value);
		add(節.value);
		add(款.value);
		add(通則_通知.value);
		add(区分番号_通知.value);
		add(数字.value);
		add(数字点.value);
		add(数字例.value);
		add(数字括弧.value);
		add(数字丸.value);
		add(数字米.value);
		add(カナ.value);
		add(カナ括弧.value);
		add(英小文字.value);
		add(注.value);
		add(注１.value);
		add(StencilParagraph.value);
	}
}
