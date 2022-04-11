package saka1029.tensuhyo.parser;

public class StencilParagraph extends Stencil {

	public static final StencilParagraph value = new StencilParagraph();
	
	private StencilParagraph() {
		super("段落", MARGIN_PAT +
			"(?<" + NUMBER + ">)" + "(?<" + ORDER + ">)" +
			"(?<" + SEPARATOR + ">)" + HEADER_PAT);
	}
	
	@Override
	protected Node addNode(Node node, Node parent) {
		Paragraph para = new Paragraph(node.line(), node.lineNo(), node.margin());
		parent.addParagraph(para);
		return parent;
	}

}
