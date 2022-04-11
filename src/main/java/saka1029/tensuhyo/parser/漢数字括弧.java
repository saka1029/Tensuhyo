package saka1029.tensuhyo.parser;

public class 漢数字括弧 extends StencilOrdered {

	public static final 漢数字括弧 value = new 漢数字括弧();
	
	private 漢数字括弧() {
		super("漢数字括弧", MARGIN_PAT +
			"(?<" + NUMBER + ">[(（]" + 漢数字.漢数_ORDER_PAT + "[)）])" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(数字.value);
	}

	@Override
	protected int no(Node node, Node prev) {
	    return 漢数字.numberize(node.order());
	}
}
