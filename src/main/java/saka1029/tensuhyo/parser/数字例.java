package saka1029.tensuhyo.parser;

public class 数字例 extends StencilOrdered {

	public static final 数字例 value = new 数字例();
	
	private 数字例() {
		super("数字例", MARGIN_PAT +
			"(?<" + NUMBER + ">例" + ORDER_PAT + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

}
