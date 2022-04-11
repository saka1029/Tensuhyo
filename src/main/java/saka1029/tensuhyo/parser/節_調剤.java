package saka1029.tensuhyo.parser;

public class 節_調剤 extends StencilOrdered {

	public static final 節_調剤 value = new 節_調剤();
	
	private 節_調剤() {
		super("節", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "第" + ORDER_PAT + "節" + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(StencilRoot.value);
	}

}
