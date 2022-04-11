package saka1029.tensuhyo.parser;

public class 区分_調剤 extends StencilUnordered {

	public static final 区分_調剤 value = new 区分_調剤();

	private 区分_調剤() {
		super("区分", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)区分)" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(節_調剤.value);
	}

	@Override
	public String fileName(Node node) {
		return parentFileName(node) + ".K";
	}
}
