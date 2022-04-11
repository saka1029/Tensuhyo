package saka1029.tensuhyo.generator;

import saka1029.tensuhyo.parser.Node;

public class NodeWord {

	private String name;
	private Node node;
	private String type;
	private String href;
	
	public NodeWord(String name, Node node, String type, String href) {
		this.name = name;
		this.node = node;
		this.type = type;
		this.href = href;
	}
	
	public String name() { return name; }
	public Node node() { return node; }
	public String type() { return type; }
	public String href() { return href; }
	
	@Override
	public String toString() {
		return "NodeWord(name=" + name + " type=" + type + " href=" + href + ")";
	}
}
