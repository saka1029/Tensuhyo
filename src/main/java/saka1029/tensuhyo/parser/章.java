package saka1029.tensuhyo.parser;

public class 章 extends StencilOrdered {

	public static final 章 value = new 章();
	
	private 章() {
		super("章", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "第" + ORDER_PAT + "章" + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(StencilRoot.value);
	}

}
