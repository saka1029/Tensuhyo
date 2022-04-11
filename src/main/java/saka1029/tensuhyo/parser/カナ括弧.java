package saka1029.tensuhyo.parser;

public class カナ括弧 extends カナ {

	public static final カナ括弧 value = new カナ括弧();
	
	private カナ括弧() {
		super("括弧カナ", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "[（(](?<" + ORDER + ">[" + イロハ + カナ誤記 + "])[）)])" +
			SEPARATOR_PAT + HEADER_PAT +"$");
	}

}
