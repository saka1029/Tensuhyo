package saka1029.tensuhyo.parser;

public class 区分 extends StencilUnordered {

	public static final 区分 value = new 区分();

	private 区分() {
		super("区分", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)区分)" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(款.value);
		addCandidateParent(節.value);
		addCandidateParent(部.value);
	}

	@Override
	public String fileName(Node node) {
		return parentFileName(node) + ".K";
	}
}
