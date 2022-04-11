package saka1029.tensuhyo.parser;

import java.util.List;
// import java.util.logging.Logger;

public abstract class StencilOrdered extends Stencil {

	// private static final Logger logger = Logger.getLogger(StencilOrdered.class.getName());
	// private static void log(String format, Object... args) { logger.info(String.format(format, args)); }

	protected StencilOrdered(String nodeName, String regex) {
		super(nodeName, regex);
	}

	protected int no(String order) {
		return Integer.parseInt(order);
	}
	
	protected int no(Node node, Node prev) {
		int no = no(node.order());
		int prevNo = 0;
		if (prev != null && prev.stencil() == this)
			prevNo = prev.no();
		if (prevNo + 1 == no) return no;
		return -1;
	}

	@Override
	protected ParentPrevNo selectParent(Node node, Node last, List<ParentPrevNo> feasibles)
			throws ParseException {
		// この時点でnode.no()は0となっている。noの候補はfeasiblesの中のnoを参照する必要がある。
		int c = 0;
		for (ParentPrevNo p: feasibles)
			if (p.no != 1)
				++c;
		// 候補が複数あって自分自身のnoの候補が1でない場合
		// prevのインデントが自分自身に最も近いものを選択する。
		if (feasibles.size() >= 2 && c > 0) {
//            log("selectParent: size=%d%n", feasibles.size());
//            log("selectParent: node: %s%n", node);
            int min = Integer.MAX_VALUE;
            ParentPrevNo select = null;
            for (ParentPrevNo p : feasibles) {
//                log("selectParent: feasible: %s%n", p);
                int diff = Math.abs(node.indent() - p.prev.indent());
                if (select == null || diff < min) {
                	min = diff;
                	select = p;
                }
//                log("selectParent: selected: %s%n", p);
            }
            return select;
		}
		ParentPrevNo pp = super.selectParent(node, last, feasibles);
		if (pp == null)
			throw new ParseException(
				feasibles.size() <= 0 ? "親の候補がありません" : "親の候補(*)が複数あります",
				node, last, feasibles);
		return pp;
	}

}
