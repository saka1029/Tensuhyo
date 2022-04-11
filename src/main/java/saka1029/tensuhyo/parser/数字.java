package saka1029.tensuhyo.parser;

public class 数字 extends StencilOrdered {

	public static final 数字 value = new 数字();
	
	private 数字() {
		super("数字", MARGIN_PAT +
			"(?<" + NUMBER + ">" + ORDER_PAT + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

}
