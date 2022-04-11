package saka1029.tensuhyo.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saka1029.tensuhyo.util.CSVReader;
import saka1029.tensuhyo.util.Fields;

public class NormDict<T> implements Dict<T> {

	public static class Rep {
		
		public String src, dst;
		
		public Rep(String src, String dst) {
			this.src = src;
			this.dst = dst;
		}
		
		@Override
		public String toString() {
			return String.format("Rep(%s->%s)", src, dst);
		}
	}

	private class NormAcc<E> implements Acc<E> {

		private Acc<E> origin;

		public NormAcc(Acc<E> origin) {
			this.origin = origin;
		}

		@Override
		public String name(E word) {
			return normalize(origin.name(word), null);
		}
		
		@Override
		public String type(E word) {
			return origin.type(word);
		}
	}

	private Dict<T> dict;
	private Map<Character, List<Rep>> reps = new HashMap<Character, List<Rep>>();
	private boolean initialized = false;
	
	public NormDict(Dict<T> dict) {
		NormAcc<T> nacc = new NormAcc<>(dict.acc());
		dict.acc(nacc);
		this.dict = dict;
	}
	
	public NormDict(Dict<T> dict, NormDict<T> org) {
		this(dict);
		this.reps = org.reps;
		this.initialized = org.initialized;
	}
	
	@Override public Acc<T> acc() { return this.dict.acc(); }
	@Override public void acc(Acc<T> acc) { throw new UnsupportedOperationException(); }
	public Dict<T> dict() { return this.dict; }
	
	private void cyclicCheck(Rep r, List<String> history) {
		if (history.contains(r.src)) {
			StringBuilder sb = new StringBuilder();
			for (String e : history)
				sb.append(e).append("->");
			sb.append(r.src);
			throw new IllegalStateException(String.format(
				"置換パターン%sは循環参照しています(%s)",
				r, sb.toString()));
		}
	}
	
	private void initialize(Rep rep, StringBuilder dst, List<String> history) {
		// 置換後文字列自身を置換するためにdstに格納します。
		dst.setLength(0);
		dst.append(rep.dst);
		boolean changed = false;
		// 置換履歴をクリアします。
		history.clear();
		history.add(rep.src);
		int i = 0;
		while (i < dst.length()) {
			boolean repled = false;
			List<Rep> found = reps.get(dst.charAt(i));
			if (found != null)
				for (Rep r : found) {
					if (!dst.substring(i).startsWith(r.src)) continue;
					// 置換の履歴を参照して循環参照のチェックを行います。
					cyclicCheck(r, history);
					// 置換履歴に置換後文字列を追加します。
					history.add(r.src);
					// dstの先頭を置換語の文字列に置き換えます。
					dst.delete(i, i + r.src.length());
					dst.insert(i, r.dst);
					changed = true;
					repled = true;
					break;
				}
			if (!repled) {
				++i;
				history.clear();
				history.add(rep.src);
			}
		}
		if (changed)
			rep.dst = dst.toString();
	}

	private void initialize() {
		if (initialized) return;
		initialized = true;
		StringBuilder dst = new StringBuilder();
		List<String> history = new ArrayList<String>();
		for (List<Rep> list : reps.values())
			for (Rep rep : list)
				initialize(rep, dst, history);
	}

	public void addRep(String src, String dst) {
		// 初期化後に用語が追加された場合は未初期化状態に戻します。
		initialized = false;
		if (src == null || src.length() < 1)
			throw new IllegalArgumentException("src");
		if (dst == null)
			throw new IllegalArgumentException("dst");
		Rep rep = new Rep(src, dst);
		char key = src.charAt(0);
		List<Rep> list = reps.get(key);
		if (list == null)
			reps.put(key, list = new ArrayList<Rep>());
		// srcの長いものから順に並べます。
		for (int i = 0, size = list.size(); i < size; ++i)
			if (src.length() > list.get(i).src.length()) {
				list.add(i, rep);
				return;
			}
		list.add(rep);
	}

	public void addRep(CSVReader reader) throws IOException {
		while (true) {
			Fields f = reader.readFields();
			if (f == null) break;
			if (f.size() < 2) continue;
			if (f.get(0).trim().startsWith("#")) continue;
			addRep(f.get(0), f.get(1));
		}
	}
	
	public void addRepResource(String resourceName, String encoding)
			throws IOException {
		CSVReader reader = new CSVReader(
			ClassLoader.getSystemResourceAsStream(resourceName), encoding);
		try {
			addRep(reader);
		} finally {
			reader.close();
		}
	}
	
	private static void set(List<Integer> positions, int index, int len, int element) {
		// 対応表がない場合は何もしません。
		if (positions == null) return;
		// index番目まで格納できるように対応表を拡張します。
		int size = index + len;
		while (positions.size() <= size)
			positions.add(-1);
		for (int i = index; i < size; ++i)
			positions.set(i, element);
	}

	private String normalize(String s, List<Integer> positions) {
		if (s == null)
			throw new IllegalArgumentException("s");
		StringBuilder r = new StringBuilder();
		L: for (int i = 0, size = s.length(); i < size;) {
			char ch = s.charAt(i);
			List<Rep> list = reps.get(ch);
			if (list != null)
				for (Rep e : list)
					if (s.substring(i).startsWith(e.src)) {
						int len = e.src.length();
						set(positions, r.length(), len, i);
						r.append(e.dst);
						i += len;
						continue L;
					}
			set(positions, r.length(), 1, i);
			r.append(ch);
			++i;
		}
		// 最終文字位置の次の対応関係を対応表に追加します。
		set(positions, r.length(), 1, s.length());
		return r.toString();
	}


	@Override
	public void add(T word) {
		initialize();
		dict.add(word);
	}

	@Override
	public List<Found<T>> encode(String s, String... types) {
		List<Found<T>> found = new ArrayList<>();
		List<Integer> positions = new ArrayList<Integer>();
		s = normalize(s, positions);
		List<Found<T>> founds = dict.encode(s, types);
		// 検索結果を元の文字列に対する検索を行ったようにアレンジします。
		for (Found<T> e : founds) {
			int start = positions.get(e.position());
			int end = positions.get(e.position() + e.length());
			found.add(new Found<T>(e.word(), start, end - start));
		}
		return found;
	}

}
