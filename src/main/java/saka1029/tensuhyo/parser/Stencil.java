package saka1029.tensuhyo.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Stencil {

	protected static final String MARGIN = "margin";
	protected static final String NUMBER = "number";
	protected static final String ORDER = "order";
	protected static final String SEPARATOR = "separator";
	protected static final String HEADER = "header";
	
	protected static final String 空白1 = "[ \t　]";
	protected static final String 空白0 = 空白1 + "*";
	protected static final String 空白n = 空白1 + "+";
	protected static final String 数字1 = "[0-9０-９]";
	protected static final String 数字n = 数字1 + "+";
	protected static final String 任意0 = ".*";
	protected static final String MARGIN_PAT = "^(?<" + MARGIN + ">" + 空白0 + ")";
	protected static final String SEPARATOR_PAT = "(?<" + SEPARATOR + ">" + 空白n + ")";
	protected static final String SEPARATOR_0_PAT = "(?<" + SEPARATOR + ">" + 空白0 + ")";
	protected static final String ORDER_PAT = "(?<" + ORDER + ">" + 数字n + ")";
	protected static final String HEADER_PAT = "(?<" + HEADER + ">" + 任意0 + ")";
	
	private Pattern pattern;
	private String nodeName;
	protected List<Stencil> candidateParents = new ArrayList<>();
	
	protected Stencil(String nodeName, String regex) {
		this.nodeName = nodeName;
		this.pattern = Pattern.compile(regex);
	}
	
	protected void addCandidateParent(Stencil stencil) {
		candidateParents.add(stencil);
	}
	
	protected boolean isCandidateParent(Node parent) {
		if (candidateParents.size() <= 0)
			return true;
		else if (parent == null)
			return false;
		else
			return candidateParents.contains(parent.stencil());
	}
	
	public Node match(String line, int lineNo) throws ParseException {
		Matcher match = pattern.matcher(line);
		if (!match.matches()) return null;
		return new Node(match, this, nodeName, line, lineNo);
	}
	
	protected int no(Node node, Node prev) {
		return 0;
	}
	
	protected ParentPrevNo selectParent(Node node, Node last, List<ParentPrevNo> feasibles)
			throws ParseException {
		if (feasibles.size() <= 0) return null;
		return feasibles.get(0);
	}

	protected Node getParent(Node node, Node last) throws ParseException {
		List<ParentPrevNo> feasibles = new ArrayList<>();
		Node prev = null;
		for (Node parent = last; parent != null; prev = parent, parent = parent.parent())
			if (isCandidateParent(parent)) {
				int no = no(node, prev);
				if (no >= 0)
					feasibles.add(new ParentPrevNo(parent, prev, no));
			}
		ParentPrevNo pp = selectParent(node, last, feasibles);
		if (pp == null) return null;
		node.no(pp.no);
		return pp.parent;
	}

	protected Node addNode(Node node, Node parent) throws ParseException {
		parent.add(node);
		return node;
	}

	public Pattern pattern() { return pattern; }
	public String nodeName() { return nodeName; }

	protected String parentFileName(Node node) {
		return node.parent() == null ? "" : node.parent().fileName();
	}

	String fileName(Node node) {
		return parentFileName(node) + "." + node.no();
	}
}
