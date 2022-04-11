package saka1029.tensuhyo.parser;

public class 数字の extends StencilOrdered {

	public static final 数字の value = new 数字の();
	
	private 数字の() {
		super("数字の", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "(?<" + ORDER + ">" + 数字n + "(の" + 数字n + ")?" + "))" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(数字第.value);
	}
	
    @Override protected int no(Node node, Node prev) {
        int no = 数字第.mainSub(node, prev);
        return no;
    }
    
    @Override
    String fileName(Node node) {
	    return node.parent().fileName() + "." + 数字第.fileNo(node);
    }

}
