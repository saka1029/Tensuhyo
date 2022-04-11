package saka1029.tensuhyo.parser;

public class 施設基準別表 extends StencilUnordered {

	public static final 施設基準別表 value = new 施設基準別表();
	
	private 施設基準別表() {
		super("施設基準別表", MARGIN_PAT +
			"(?<" + NUMBER + ">" + "別表" + "(?<" + ORDER + ">)" + ")" +
			"(?<" + SEPARATOR +">)" + "(?<" + HEADER +">)" + "$");
		addCandidateParent(施設基準等.value);
	}

}
