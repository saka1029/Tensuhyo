package saka1029.tensuhyo.parser;

public class 数字米 extends StencilOrdered {

	public static final 数字米 value = new 数字米();
	
	private 数字米() {
		super("数字米", MARGIN_PAT +
			"(?<" + NUMBER + ">※" + ORDER_PAT + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

}
