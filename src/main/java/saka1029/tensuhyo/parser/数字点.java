package saka1029.tensuhyo.parser;

public class 数字点 extends StencilOrdered {

	public static final 数字点 value = new 数字点();
	
	private 数字点() {
		super("数字点", MARGIN_PAT +
			"(?<" + NUMBER + ">" + ORDER_PAT + ")[．.]" +
			"(?<" + SEPARATOR + ">" + 空白0 + ")" + HEADER_PAT + "$");
	}

}
