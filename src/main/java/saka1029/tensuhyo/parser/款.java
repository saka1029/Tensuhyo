package saka1029.tensuhyo.parser;

public class 款 extends StencilOrdered {

	public static final 款 value = new 款();
	
	private 款() {
		super("款", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "第" + ORDER_PAT + "款" + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(節.value);
	}

}
