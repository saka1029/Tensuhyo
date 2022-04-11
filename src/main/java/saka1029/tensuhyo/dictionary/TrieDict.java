package saka1029.tensuhyo.dictionary;

import java.util.ArrayList;
import java.util.List;

public class TrieDict<T> implements Dict<T> {

	private static class Node<T> {
		
		private char key;
		private List<T> words = null;
		private List<Node<T>> children = null;
		
		private Node(char key) { this.key = key; }
		
		private void add(Node<T> child) {
			if (children == null) children = new ArrayList<Node<T>>();
			for (int i = children.size() - 1; i >= 0; --i) {
				Node<T> e = children.get(i);
				if (child.key >= e.key) {
					children.add(i + 1, child);
					return;
				}
			}
			children.add(0, child);
		}
		
		private void add(T word) {
			if (words == null) words = new ArrayList<T>();
			words.add(word);
		}
	}
	
	private Acc<T> acc;
	private TrieDict<T> parent;
	private Node<T> root = new Node<T>('\0');

	public TrieDict(Acc<T> acc) {
		this.acc = acc; 	
	}

	public TrieDict(TrieDict<T> parent) {
		this.acc = parent.acc;
		this.parent = parent;
	}
	
	@Override public Acc<T> acc() { return this.acc; }
	@Override public void acc(Acc<T> acc) { this.acc = acc; }
	public TrieDict<T> parent() { return this.parent; }

	private Node<T> find(Node<T> node, char ch) {
		if (node.children == null) return null;
		int i = 0;
		int j = node.children.size() - 1;
		while (i <= j) {
			int m = (i + j) / 2;
			Node<T> e = node.children.get(m);
			char ekey = e.key;
			if (ch == ekey)
				return e;
			else if (ch < ekey)
				j = m - 1;
			else
				i = m + 1;
		}
		return null;
	}

	@Override
	public void add(T word) {
		String name = acc.name(word);
		if (name == null || name.length() < 1)
			throw new IllegalArgumentException("name is null");
		Node<T> last = root;
		for (int i = 0, len = name.length(); i < len; ++i) {
			char ch = name.charAt(i);
			Node<T> child = find(last, ch);
			if (child == null) 	last.add(child = new Node<T>(ch));
			last = child;
		}
		last.add(word);
	}
	
	private boolean inType(T word, String... types) {
		if (types.length <= 0) return true;
		String type = acc.type(word);
		for (String e : types)
			if (type.equals(e))
				return true;
		return false;
	}

	private void addWord(Node<T> node, int pos, int len, 	List<Found<T>> list, String... types) {
		if (node.words == null) return;
		for (T word : node.words)
			if (inType(word, types))
				list.add(new Found<T>(word, pos, len));
	}

	public void encode(String s, List<Found<T>> found, String... types) {
		for (int pos = 0, len = s.length(); pos < len; ++pos) {
			Node<T> last = root;
			for (int i = pos; i < len; ++i) {
				char ch = s.charAt(i);
				last = find(last, ch);
				if (last == null) break;
				addWord(last, pos, i - pos + 1, found, types);
			}
		}
	}

	@Override
	public List<Found<T>> encode(String s, String... types) {
		List<Found<T>> found = new ArrayList<>();
		if (parent != null) parent.encode(s, found, types);
		encode(s, found, types);
		return found;
	}

}

