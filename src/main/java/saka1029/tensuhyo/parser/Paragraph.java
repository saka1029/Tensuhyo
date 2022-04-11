package saka1029.tensuhyo.parser;

public class Paragraph {

	private String line;
	private int lineNo;
	private String margin;
	
	public Paragraph(String line, int lineNo, String margin) {
		this.line = line;
		this.lineNo = lineNo;
		this.margin = margin;
	}
	
	public String line() { return line; }
	public int lineNo() { return lineNo; }
	public String margin() { return margin; }
	public String text() { return line.substring(margin.length()); }
	
	@Override
	public String toString() {
		return line + Node.NEWLINE;
	}
}
