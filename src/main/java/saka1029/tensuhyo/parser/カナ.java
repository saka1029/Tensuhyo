package saka1029.tensuhyo.parser;

import java.util.List;

import saka1029.tensuhyo.util.StringConverter;

public class カナ extends StencilOrdered {

	public static final カナ value = new カナ();
	
	protected static final String アイウ = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヰヱヲン";
	protected static final String イロハ = "イロハニホヘトチリヌルヲワカヨタレソツネナラムウヰノオクヤマケフコエテアサキユメミシヱヒモセスン";
	protected static final String カナ誤記 = "へり";
	
	private カナ() {
		super("カナ", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">[" + イロハ + カナ誤記 + "]))" +
			SEPARATOR_PAT + HEADER_PAT +"$");
	}
	
	protected カナ(String nodeName, String regex) {
		super(nodeName, regex);
	}

	@Override
	protected int no(Node node, Node prev) {
		char k = StringConverter.toKatakana(node.order()).charAt(0);
		int no1 = イロハ.indexOf(k) + 1;
		int no2 = アイウ.indexOf(k) + 1;
		if (prev == null || prev.stencil() != this)
			return no1 == 1 || no2 == 1 ? 1 : -1;
		int next = prev.no() + 1;
		if (no1 == next) return no1;
		if (no2 == next) return no2;
		return -1;
	}
	
	@Override
	protected ParentPrevNo selectParent(Node node, Node last,
			List<ParentPrevNo> feasibles) throws ParseException {
		switch (feasibles.size()) {
		case 0: return null;
		case 1: return feasibles.get(0);
		default:
			// 先頭以外（イ、ア以外）があればそれを選択します。
			for (ParentPrevNo pp : feasibles)
				if (pp.no > 1)
					return pp;
			// それ以外の場合は最後のノードを親とします。
			for (ParentPrevNo pp : feasibles)
				if (pp.prev == null)
					return pp;
			return null;
		}
	}
	
}
