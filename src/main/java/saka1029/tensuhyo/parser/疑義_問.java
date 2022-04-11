package saka1029.tensuhyo.parser;

public class 疑義_問 extends StencilUnordered {

	public static final 疑義_問 value = new 疑義_問();

	protected 疑義_問() {
		super("問", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)[(（]問[^)）]+[)）])" +
			SEPARATOR_0_PAT + HEADER_PAT + "$");
		addCandidateParent(疑義_分類.value);
	}

}
