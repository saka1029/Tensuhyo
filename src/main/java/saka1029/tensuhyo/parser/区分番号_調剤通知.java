package saka1029.tensuhyo.parser;

public class 区分番号_調剤通知 extends StencilUnordered {

	public static final 区分番号_調剤通知 value = new 区分番号_調剤通知();

//	public static final String 区分番号 = "[0-9０-９]{2}(?:の[0-9０-９]{1,2})?";
	
	private 区分番号_調剤通知() {
		super("区分番号", MARGIN_PAT +
			"区分" +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)" +
				区分番号_調剤告示.区分番号 +
				"(?:及び" + 区分番号_調剤告示.区分番号 + "|から" + 区分番号_調剤告示.区分番号 + "まで)?" +
			")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(節_調剤.value);
	}

	@Override
	String fileName(Node node) {
		String r = node.name().replaceFirst("^(" + 区分番号_調剤告示.区分番号 + ").*$", "$1");
		return r.replaceAll("の", "-");
	}

}
