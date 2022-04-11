package saka1029.tensuhyo.parser;

public class 数字括弧 extends StencilOrdered {

	public static final 数字括弧 value = new 数字括弧();
	
	private 数字括弧() {
		super("数字括弧", MARGIN_PAT +
			"(?<" + NUMBER + ">[(（]" + ORDER_PAT + "[)）])" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

}
