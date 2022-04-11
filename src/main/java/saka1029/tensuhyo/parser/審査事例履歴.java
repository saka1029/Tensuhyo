package saka1029.tensuhyo.parser;

public class 審査事例履歴 extends StencilUnordered {

	public static final 審査事例履歴 value = new 審査事例履歴();
	
	private 審査事例履歴() {
		super("履歴", MARGIN_PAT +
			"(?<" + NUMBER + ">)" + "(?<" + ORDER + ">)" +
			"(?<" + SEPARATOR + ">)" + "《" + HEADER_PAT + "》" + 空白0 + "$");
		addCandidateParent(審査事例数字.value);
	}

}
