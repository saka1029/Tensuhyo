package saka1029.tensuhyo.parser;

public class 施設基準等 extends StencilUnordered {

	public static final 施設基準等 value = new 施設基準等();

	private 施設基準等() {
		super("施設基準等", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "\\S+の施設基準等(?<" + ORDER + ">))" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(StencilRoot.value);
	}

}
