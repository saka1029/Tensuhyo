package saka1029.tensuhyo.parser;

public class 漢数字別表第 extends StencilOrdered {

	public static final 漢数字別表第 value = new 漢数字別表第();
	
	private 漢数字別表第() {
		super("別表第漢数字", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "別表第" + 漢数字.漢数の漢数の漢数_ORDER_PAT + ")" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(施設基準別表.value);
	}

	@Override protected int no(Node node, Node prev) { return 漢数字.mainSub(node, prev); }
	
	@Override
	String fileName(Node node) {
	    String syo = node.path().startsWith("/基本診療料") ? "1" : "2";
	    return syo + ".b." + 漢数字.fileNo(node);
	}
}
