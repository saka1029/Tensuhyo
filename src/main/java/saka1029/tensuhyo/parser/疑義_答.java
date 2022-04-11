package saka1029.tensuhyo.parser;

public class 疑義_答 extends StencilUnordered {

	public static final 疑義_答 value = new 疑義_答();

	protected 疑義_答() {
		super("答", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)[(（]答[)）])" +
			SEPARATOR_0_PAT + HEADER_PAT + "$");
		addCandidateParent(疑義_問.value);
	}

}
