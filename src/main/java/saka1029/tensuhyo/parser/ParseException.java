package saka1029.tensuhyo.parser;

import java.util.List;

// import saka1029.tensuhyo.parser.ParentPrevNo;

public class ParseException extends Exception {

	private static final long serialVersionUID = 8385792162233864645L;

	private static void format(Node prev, StringBuilder sb, List<ParentPrevNo> feasibles) {
		if (prev == null) return;
		format(prev.parent(), sb, feasibles);
		String mark = "";
		if (feasibles != null)
			for (ParentPrevNo pp : feasibles)
				if (prev == pp.parent) mark = "*";
		sb.append(mark).append(prev).append(Node.NEWLINE);
	}
	
	private static String format(String message, String line, int lineNo, Node prev, List<ParentPrevNo> feasibles) {
		StringBuilder sb = new StringBuilder();
		sb.append(lineNo).append("行: ");
		sb.append(message).append(Node.NEWLINE);
		sb.append(line).append(Node.NEWLINE);
		format(prev, sb, feasibles);
		return sb.toString();
	}
	
	public ParseException(String message, Node node, Node prev) {
		super(format(message, node.line(), node.lineNo(), prev, null));
	}
	
	public ParseException(String message, String line, int lineNo, Node prev) {
		super(format(message, line, lineNo, prev, null));
	}
	
	public ParseException(String message, Node node, Node prev, List<ParentPrevNo> feasibles) {
		super(format(message, node.line(), node.lineNo(), prev, feasibles));
	}
	
}
