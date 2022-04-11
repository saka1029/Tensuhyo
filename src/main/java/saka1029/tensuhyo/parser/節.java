package saka1029.tensuhyo.parser;

public class 節 extends StencilOrdered {

	public static final 節 value = new 節();
	
	private 節() {
		super("節", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "第" + ORDER_PAT + "節" + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(部.value);
	}

}
