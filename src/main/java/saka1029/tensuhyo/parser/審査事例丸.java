package saka1029.tensuhyo.parser;

public class 審査事例丸 extends StencilUnordered {

	public static final 審査事例丸 value = new 審査事例丸();
	
	private 審査事例丸() {
		super("丸", MARGIN_PAT +
			"(?<" + NUMBER + ">○)" + "(?<" + ORDER + ">)" +
			SEPARATOR_PAT + HEADER_PAT + "$");
		addCandidateParent(審査事例数字.value);
	}

}
