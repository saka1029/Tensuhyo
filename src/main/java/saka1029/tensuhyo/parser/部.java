package saka1029.tensuhyo.parser;

public class 部 extends StencilOrdered {

	public static final 部 value = new 部();
	
	private 部() {
		super("部", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "第" + ORDER_PAT + "部" + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(章.value);
	}

}
