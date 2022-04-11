package saka1029.tensuhyo.parser;

public class StencilRoot extends Stencil {

	public static final StencilRoot value = new StencilRoot();

	private StencilRoot() {
		super("root",
			MARGIN_PAT + 
			"(?<" + NUMBER + ">.*)" + "(?<" + ORDER + ">)" +
			"(?<" + SEPARATOR + ">)" + "(?<" + HEADER + ">)"); 
	}
	
	@Override public String fileName(Node node) { return "0"; }
}
