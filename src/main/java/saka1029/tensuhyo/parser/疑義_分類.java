package saka1029.tensuhyo.parser;

public class 疑義_分類 extends StencilUnordered {

	public static final 疑義_分類 value = new 疑義_分類();

	protected 疑義_分類() {
		super("分類", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)【[^】]+】)" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(疑義_点数表.value);
	}

}
