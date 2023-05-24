package saka1029.tensuhyo.parser;

/**
 * oldTODO: 通知では区分分類の下に通則が来ることがある。
 *
 */
public class 調剤通知読込 extends BaseParser {

	public 調剤通知読込() {
		add(節_調剤.value);
		add(通則_通知.value);
		add(区分番号_調剤通知.value);
		add(数字.value);
//		add(数字点.value);
//		add(数字例.value);
		add(数字括弧.value);
		add(数字丸.value);
//		add(数字米.value);
		add(カナ.value);
		add(カナ括弧.value);
//		add(英小文字.value);
		add(注.value);
		add(注１.value);
		add(StencilParagraph.value);
	}
}
