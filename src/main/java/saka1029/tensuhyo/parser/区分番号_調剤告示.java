package saka1029.tensuhyo.parser;

public class 区分番号_調剤告示 extends StencilUnordered {

	public static final 区分番号_調剤告示 value = new 区分番号_調剤告示();

//	public static final String 区分番号 = "[0-9０-９]{2}(?:の[0-9０-９]{1,2})?";
	public static final String 区分番号 = "[０-９]{2}(?:の[０-９]{1,2})?";

	private 区分番号_調剤告示() {
		super("区分番号", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)" +
				区分番号 +
				"(?:及び" + 区分番号 + "|から" + 区分番号 + "まで)?" +
			")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(区分_調剤.value);
	}

	@Override
	String fileName(Node node) {
		String r = node.name().replaceFirst("^(" + 区分番号 + ").*$", "$1");
		return r.replaceAll("の", "-");
	}

}
