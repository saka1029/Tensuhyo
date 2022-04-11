package saka1029.tensuhyo.parser;

import java.util.List;

/**
 * インデントに依存します。
 * 自分自身よりも小さいインデントを持つNodeを親とします。
 */
public class 注 extends StencilUnordered {

	public static final 注 value = new 注();

	private 注() {
		super("注", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)注)" +
			SEPARATOR_PAT + HEADER_PAT + "$");
	}

	@Override
	protected ParentPrevNo selectParent(Node node, Node last,
			List<ParentPrevNo> feasibles) throws ParseException {
		for (ParentPrevNo pp : feasibles)
			if (pp.parent.indent() < node.indent())
				return pp;
		throw new ParseException("注に対応する親がありません", node, last, feasibles);
	}

}
