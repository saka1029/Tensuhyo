package saka1029.tensuhyo.parser;

public class 数字丸 extends StencilOrdered {

	public static final 数字丸 value = new 数字丸();
	
	private static final String 丸数字 = "①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳";
	
	private 数字丸() {
		super("丸数字", MARGIN_PAT +
			"(?<" + NUMBER + ">(?<" + ORDER + ">[" + 丸数字 + "]))" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

	@Override
	protected int no(String order) {
		char k = order.charAt(0);
		return 丸数字.indexOf(k) + 1;
	}

}
