package saka1029.tensuhyo.parser;

/**
 * インデントに依存します。
 * 直前の行に対して大きくインデントしている場合はマッチしません。
 * 閾値はMAX_INDENTです。
 */
public class 区分分類 extends StencilUnordered {

	public static final 区分分類 value = new 区分分類();
	
	private static final int MAX_INDENT = 20;

	private 区分分類() {
		super("区分分類", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)[(（][^)）]+[)）])" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
		addCandidateParent(区分.value);
	}
	
	@Override
	protected Node getParent(Node node, Node last) throws ParseException {
		Node parent = super.getParent(node, last);
		if (parent != last && node.indent() > last.indent() + MAX_INDENT)
			return null;
		return parent;
	}
	
	@Override
	String fileName(Node node) {
		return parentFileName(node) + "." + node.childNo();
	}

}
