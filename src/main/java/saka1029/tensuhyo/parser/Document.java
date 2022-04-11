package saka1029.tensuhyo.parser;

/**
 * HTMLのid属性で使用可能な文字。
 * <ul>
 * <li>半角英字で始まる。（"xml"で始まる文字列は不可）
 * <li>半角英数字または_、-、:、.のみからなる文字列。（":"はXHTMLでは不可）
 * </ul>
 */
public class Document {

	private Node root;
	
	public Document(Node root) {
		this.root = root;
	}
	
	public Node root() { return root; }

	private void visit(Node node, NodeVisitor visitor) {
		visitor.visit(node);
		for (Node child : node.children())
			visit(child, visitor);
	}

	public void visit(NodeVisitor visitor) {
		visit(root, visitor);
	}
	
	private Node find(Node parent, String[] paths, int index) {
		if (index >= paths.length)
			return parent;
		for (Node child : parent.children())
			if (child.name().equals(paths[index]))
				return find(child, paths, index + 1);
		return null;
	}
	
	public Node find(String path) {
		if (path.equals("") || path.equals("/"))
			return root;
		String[] paths = path.split("/");
		if (paths.length <= 0 || !paths[0].equals(""))
			throw new IllegalArgumentException("pathは'/'で始まる必要があります。");
		return find(root, paths, 1);
	}
	
	@Override
	public String toString() {
		class Visitor implements NodeVisitor {
			StringBuilder sb = new StringBuilder();
			@Override
			public void visit(Node node) {
				sb.append(node).append(Node.NEWLINE);
			}
		};
		Visitor visitor = new Visitor();
		visit(visitor);
		return visitor.sb.toString();
	}

	public String toLongString() {
		class Visitor implements NodeVisitor {
			StringBuilder sb = new StringBuilder();
			@Override
			public void visit(Node node) {
				sb.append(node.toLongString());
			}
		};
		Visitor visitor = new Visitor();
		visit(visitor);
		return visitor.sb.toString();
	}
	
	public String text() {
		class Visitor implements NodeVisitor {
			StringBuilder sb = new StringBuilder();
			@Override
			public void visit(Node node) {
				sb.append(node.text());
			}
		};
		Visitor visitor = new Visitor();
		visit(visitor);
		return visitor.sb.toString();
	}
}
