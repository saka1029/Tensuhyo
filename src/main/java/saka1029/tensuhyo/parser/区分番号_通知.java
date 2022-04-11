package saka1029.tensuhyo.parser;

public class 区分番号_通知 extends StencilUnordered {

	public static final 区分番号_通知 value = new 区分番号_通知();

	private 区分番号_通知() {
		super("区分番号", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)" +
				区分番号_告示.区分番号 + "(?:及び" + 区分番号_告示.区分番号 + ")?" +
			")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(款.value);
		addCandidateParent(節.value);
		addCandidateParent(部.value);
		addCandidateParent(章.value);
	}

	@Override
	String fileName(Node node) {
		return node.name().replaceFirst("^(" + 区分番号_告示.区分番号 + ").*$", "$1");
	}
}
