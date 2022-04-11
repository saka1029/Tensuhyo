package saka1029.tensuhyo.parser;

public class 審査事例数字 extends StencilUnordered {

	public static final 審査事例数字 value = new 審査事例数字();
	
	private 審査事例数字() {
		super("審査事例数字", MARGIN_PAT +
			"(?<" + NUMBER + ">" + ORDER_PAT + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(StencilRoot.value);
	}

}
