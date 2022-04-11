package saka1029.tensuhyo.parser;

public class 事項 extends StencilUnordered {

	public static final 事項 value = new 事項();
	
	private 事項() {
		super("通則", MARGIN_PAT +
				"(?<" + NUMBER + ">" + "(?<" + ORDER + ">))" +
				"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
	}
	
	@Override
	public Node match(String line, int lineNo) throws ParseException {
		Node node = super.match("", lineNo);
		node.number("通則");
		return node;
	}
	
	@Override
	String fileName(Node node) {
		return parentFileName(node) + ".T";
	}
}
