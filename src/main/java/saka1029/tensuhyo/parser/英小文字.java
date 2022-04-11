package saka1029.tensuhyo.parser;

public class 英小文字 extends StencilOrdered {

	public static final 英小文字 value = new 英小文字();
	
	private static final String 英小文字 = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";
	
	private 英小文字() {
		super("英小文字", MARGIN_PAT +
			"(?<" + NUMBER + ">(?<" + ORDER + ">[" + 英小文字 + "]))" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

	@Override
	protected int no(String order) {
		char k = order.charAt(0);
		return 英小文字.indexOf(k) + 1;
	}

}
