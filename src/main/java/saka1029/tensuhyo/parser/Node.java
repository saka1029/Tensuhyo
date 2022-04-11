package saka1029.tensuhyo.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;

import saka1029.tensuhyo.util.StringConverter;

public class Node {

	public static final String NEWLINE = "\r\n";
	
	private Node parent;
	private List<Node> children = new ArrayList<>();
	private String line;
	private String margin;
	private String number;
	private String order;
	private int no;
	private String separator;
	private String header;
	private String nodeName;
	private List<Paragraph> paragraphs = new ArrayList<>();
	private int lineNo;
	private Stencil stencil;
	private Node tuti = null;
	
	public Node(Matcher match, Stencil stencil, String nodeName, String line, int lineNo) {
		if (stencil == null) throw new IllegalArgumentException("stencil");
		this.margin = match.group(Stencil.MARGIN);
		this.number = match.group(Stencil.NUMBER);
		this.order = match.group(Stencil.ORDER);
		this.separator = match.group(Stencil.SEPARATOR);
		this.header = match.group(Stencil.HEADER);
		this.stencil = stencil;
		this.nodeName = nodeName;
		this.line = line;
		this.lineNo = lineNo;
	}
	
	public Node parent() { return parent; }
//	void parent(Node parent) { this.parent = parent; }
	public Iterable<Node> children() { return children; }
	public int childrenSize() { return children.size(); }
	public Node children(int index) { return children.get(index); }
	public int childNo() { return parent.children.indexOf(this); }
	public String line() { return line; }
	public String margin() { return margin; }
	public String number() { return number; }
	void number(String number) { this.number = number; }
	public String order() { return order; }
	public int no() { return no; }
	void no(int no) {
		if (no < 0) throw new IllegalArgumentException("no");
		this.no = no;
	}
	public String separator() { return separator; }
	public String header() { return header; }
	public String simpleHeader() { 	return header.replaceFirst("([ \t　]+.*|[0-9,０-９，]+点)$", ""); 	}
	public String nodeName() { return nodeName; }
	
	public Iterable<Paragraph> paragraphs() { return paragraphs; }
	public void addParagraph(Paragraph p) { paragraphs.add(p); }
	public int paragraphSize() { return paragraphs.size(); }
	public Paragraph paragraphs(int index) { return paragraphs.get(index); }
	public String paragrahText() {
		StringBuilder sb = new StringBuilder();
		for (Paragraph p : paragraphs)
			sb.append(p.text());
		return sb.toString();
	}
	
	public int lineNo() { return lineNo; }
	public Stencil stencil() { return stencil; }
//	public NodeType type() { return stencil.type(); }
	public Node tuti() { return tuti; }
	public void tuti(Node tuti) { this.tuti = tuti; }
	
	public String name() {
		String r = StringConverter.toNormalWidthANS(number);
		r = StringConverter.toEmSizeKatakana(r);
		return r;
	}
	
	public String fileName() { return stencil.fileName(this); }

	public String path() {
		if (parent == null) return "";
		return parent.path() + "/" + name();
	}
	
	public static int indent(String s) {
		int r = 0;
		for (int i = 0, size = s.length(); i < size; ++i)
			r += s.charAt(i) <= '\u00ff' ? 1 : 2;
		return r;
	}

	public int indent() {
		if (margin == null) return 0;
		return indent(margin);
	}

	void add(Node child) {
		children.add(child);
		child.parent = this;
	}

	void addFirst(Node child) {
		children.add(0, child);
		child.parent = this;
	}

	void moveChild(Node to, int index) {
		Node move = children.remove(index);
		to.add(move);
	}
	
	void delegateChild(Node child) {
		child.paragraphs.addAll(paragraphs);
		paragraphs.clear();
		children.add(0, child);
		child.parent = this;
	}
	
	/**
	 * デバッグ用の文字列表現を返します。
	 */
	@Override
	public String toString() {
		return String.format(
			"%7d:%s : nodeName=%s name=%s no=%d path=%s stencil=%s indent=%d fileName=%s 段落数=%d",
			lineNo, line, nodeName, name(), no(), path(),
			stencil.getClass().getSimpleName(), indent(), fileName(), paragraphs.size());
	}

	/**
	 * デバッグ用の段落を含む長い文字列表現を返します。
	 */
	public String toLongString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(
			"%7d:%s : nodeName=%s name=%s no=%d path=%s stencil=%s indent=%d fileName=%s 段落数=%d%n",
			lineNo, line, nodeName, name(), no(), path(),
			stencil.getClass().getSimpleName(), indent(), fileName(), paragraphs.size()));
		for (Paragraph para : paragraphs)
			sb.append(String.format(
				"%7d:%s : 段落%n", para.lineNo(), para.line()));
		return sb.toString();
	}

	/**
	 * テキスト表現を返します。
	 */
	public String text() {
		StringBuilder sb = new StringBuilder();
		if (!line.equals(""))
			sb.append(line).append(NEWLINE);
		for (Paragraph para : paragraphs)
			sb.append(para.line()).append(NEWLINE);
		return sb.toString();
	}
}
