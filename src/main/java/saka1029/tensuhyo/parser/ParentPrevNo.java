package saka1029.tensuhyo.parser;

class ParentPrevNo {

	public Node parent;
	public Node prev;
	public int no;

	public ParentPrevNo(Node parent, Node prev, int no) {
		if (parent == null) throw new IllegalArgumentException("parent");
		if (no < 0) throw new IllegalArgumentException("no");
		this.parent = parent;
		this.prev = prev;
		this.no = no;
	}

	@Override
	public String toString() {
		return String.format("ParentPrevNo[no=%d%nparent=%s%n prev=%s]", no, parent, prev);
	}
}
