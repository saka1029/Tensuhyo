package saka1029.tensuhyo.dictionary;

public class Found<T> implements Comparable<Found<T>> {

	private int position;
	private int length;
	private T word;
	
	public Found(T word, int position, int length) {
		this.word = word;
		this.position = position;
		this.length = length;
	}
	public int position() { return this.position; }
	public int length() { return this.length; }
	public T word() { return this.word; }
	
	/**
	 * オブジェクトの大小を比較します。
	 * positionの昇順
	 * lenngthの降順
	 */
	@Override
	public int compareTo(Found<T> o) {
		int r = this.position - o.position;
		if (r == 0) r = o.length - this.length;
		return r;
	}
	
}
