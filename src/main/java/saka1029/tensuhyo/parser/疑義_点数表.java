package saka1029.tensuhyo.parser;

public class 疑義_点数表 extends StencilUnordered {

	public static final 疑義_点数表 value = new 疑義_点数表();

	protected 疑義_点数表() {
		super("点数表", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)" +
			"(医科診療報酬点数表関係|医科診療報酬点数表関係（ＤＰＣ）|歯科診療報酬点数表関係|訪問看護療養費関係)" +
			")" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(StencilRoot.value);
	}

}
