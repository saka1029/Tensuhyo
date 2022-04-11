package saka1029.tensuhyo.parser;

public class 通則_告示 extends StencilUnordered {

	public static final 通則_告示 value = new 通則_告示();

	private 通則_告示() {
		super("通則", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">)通則)" +
			"(?<" + SEPARATOR + ">)(?<" + HEADER + ">)$");
	}
	
	@Override
	String fileName(Node node) {
		return parentFileName(node) + ".T";
	}
}
