package saka1029.tensuhyo.parser;

import java.util.List;

/**
 * インデントに依存します。
 */
public class 注１ extends StencilOrdered {

	public static final 注１ value = new 注１();
	
	private 注１() {
		super("注１", MARGIN_PAT +
				"(?<" + NUMBER + ">" + "注(?<" + ORDER + ">１))" +
				SEPARATOR_PAT + HEADER_PAT + "$");
	}

	@Override
	public Node match(String line, int lineNo) throws ParseException {
		return super.match(line, lineNo);
	}

	/**
	 * 最後に追加されたノードから順に自分よりインデントの小さいものを選択します。
	 */
	@Override
	protected ParentPrevNo selectParent(Node node, Node last,
			List<ParentPrevNo> feasibles) throws ParseException {
		for (ParentPrevNo pp : feasibles)
			if (pp.parent.indent() < node.indent())
				return pp;
		throw new ParseException("注１に対応する親がありません", node, last, feasibles);
	}
	
	@Override
	protected Node addNode(Node node, Node parent) throws ParseException {
		String tyuLine = node.margin() + "注　";
		Node tyu = 注.value.match(tyuLine, node.lineNo());
		parent.add(tyu);
		String numLine = node.line().replaceFirst("注", "  ");
		Node num = 数字.value.match(numLine, node.lineNo());
		num.no(1);
		tyu.add(num);
		return num;
	}

}
