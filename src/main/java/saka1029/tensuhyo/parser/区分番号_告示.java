package saka1029.tensuhyo.parser;

public class 区分番号_告示 extends StencilUnordered {

	public static final 区分番号_告示 value = new 区分番号_告示();

	public static final String 区分番号 = "[A-ZＡ-Ｚ][0-9０-９]{3}(?:[-ー－－‐][0-9０-９]{1,3}){0,2}";
	
	private 区分番号_告示() {
		super("区分番号", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)" +
				区分番号 +
				"(?:及び" + 区分番号 + "|から" + 区分番号 + "まで)?" +
			")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(区分.value);
	}

	@Override
	String fileName(Node node) {
		return node.name().replaceFirst("^(" + 区分番号 + ").*$", "$1");
	}
}
