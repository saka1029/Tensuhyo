package saka1029.tensuhyo.dictionary;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saka1029.tensuhyo.util.CSVReader;
import saka1029.tensuhyo.util.Fields;

/**
 * 正規化を行う辞書です。
 * 置換パターンを使用して
 * （１）辞書に登録する用語、
 * （２）検索対象文字列
 * を正規化します。
 * 検索結果は正規化前の位置を返します。
 * この辞書は正規化のみを行い、辞書のエンジン自体は他のクラスを利用します。
 * 
 */
public class NormalizedDictionary<T> extends WordDictionaryBase<T> {
	
	/**
	 * 置換パターンを保持するための内部クラスです。
	 */
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
	
	/**
	 * 正規化を行うWordAccessorのラッパーです。
	 * 外部クラスで定義されたメソッドnormalizeを参照するので、
	 * static classではない点に注意します。
	 */
	private class NormWordAccessor<E> implements WordAccessor<E> {

		private WordAccessor<E> origin;

		public NormWordAccessor(WordAccessor<E> origin) {
			this.origin = origin;
		}

		@Override
		public String getName(E bean) {
			return normalize(origin.getName(bean), null);
		}
		
		@Override
		public String getType(E bean) {
			return origin.getType(bean);
		}
	}

	public static final String STANDARD_REPLACEMENT_CSV = "NormalizedDictionaryReplacement.csv";
	
	private Map<Character, List<Rep>> reps
		= new HashMap<Character, List<Rep>>();

	private WordDictionary<T> dict;
	
	private boolean initialized = false;
	
	public NormalizedDictionary(WordDictionary<T> dict) {
		super(null);
		WordAccessor<T> newAccessor
			= new NormWordAccessor<T>(dict.getWordAccessor());
		this.dict = dict;
		// カスケードするDictのKeyAccessorを正規化するものに置換します。
		this.dict.setWordAccessor(newAccessor);
		// 自分自身のKeyAccessorも同じものを設定します。
		this.setWordAccessor(newAccessor);
	}
	
	/**
	 * 置換パターンの中に循環参照がないことを確認します。
	 * @throws IllegalStateException
	 * 循環参照がある場合にスローします。
	 */
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

	/**
	 * 置換パターンの多重置換関係を解決します。
	 * 例えば置換パターン
	 * Ａ→ＢおよびＢ→Ｃがあった場合、前者の置換パターンをＡ→Ｃに変更します。
	 * また置換パターンの循環参照をチェックします。
	 * 循環参照がある場合はIllegalStateExceptionをスローします。
	 * @param rep 置換パターンを指定します。
	 * @param dst ワークで使用するStringBuilderを指定します。
	 * @param history ワークで使用するList&lt;String&gt;を指定します。
	 */
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

	/**
	 * 登録されている置換パターンにおける多重置換関係を解決します。
	 */
	private void initialize() {
		if (initialized) return;
		initialized = true;
		StringBuilder dst = new StringBuilder();
		List<String> history = new ArrayList<String>();
		for (List<Rep> list : reps.values())
			for (Rep rep : list)
				initialize(rep, dst, history);
	}
	
	/**
	 * 置換パターンを登録します。
	 * @param src 置換前文字列を指定します。１文字以上である必要があります。
	 * @param dst 置換後文字列を指定します。空文字列でも構いません。
	 * 空文字列の場合は置換全文字列を削除することになります。
	 */
	public void addReplacement(String src, String dst) {
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
	
	/**
	 * 置換パターンをCSVReaderから読み取って登録します。
	 * 標準の置換パターンを読み込む方法を以下に示します。
	 * NormalizedDictionary<R> dict = new NormalizedDictionary<R>(
	 * 		new TrieDictionary<R>(R.ACC));
	 * CSVReader reader = new CSVReader(
	 * 		dict.getClass().getResourceAsStream(
	 * 			NormalizedDictionary.STANDARD_REPLACEMENT_CSV));
	 * try {
	 * 		dict.addReplacement(reader);
	 * } finally {
	 * 		reader.close();
	 * }
	 */
	public void addReplacement(CSVReader reader) throws IOException {
		while (true) {
			Fields f = reader.readFields();
			if (f == null) break;
			if (f.size() < 2) continue;
			if (f.get(0).trim().startsWith("#")) continue;
			addReplacement(f.get(0), f.get(1));
		}
	}
	
	public void addReplacementResource(String resourceName, String encoding)
			throws IOException {
		CSVReader reader = new CSVReader(
			ClassLoader.getSystemResourceAsStream(resourceName), encoding);
		try {
			addReplacement(reader);
		} finally {
			reader.close();
		}
	}
	
	/**
	 * 置換前後の文字位置の対応表を更新します。
	 * @param positions 置換前後の文字位置対応表を指定します。
	 * 置換後文字位置iが置換前文字位置jに対応する場合。
	 * nullが指定された場合、このメソッドは何もせずにリターンします。
	 * positions.get(i) == j
	 * となります。
	 * @param index 置換後文字位置を指定します。
	 * @param len 置換後文字位置が継続する長さを指定します。
	 * @param element 置換前文字位置を指定します。
	 */
	private void set(List<Integer> positions, int index, int len, int element) {
		// 対応表がない場合は何もしません。
		if (positions == null) return;
		// index番目まで格納できるように対応表を拡張します。
		int size = index + len;
		while (positions.size() <= size)
			positions.add(-1);
		for (int i = index; i < size; ++i)
			positions.set(i, element);
	}

	/**
	 * 文字列を正規化します。
	 * 引数positionsにnull以外の値を指定すると
	 * 置換前後の文字位置の対応表を格納します。
	 * 置換後文字列における文字位置iに対応する置換前文字列の位置がjの場合、
	 * positions.get(i) == j
	 * となります。
	 * @param s
	 * 正規化する文字列を指定します。
	 * @param positions 
	 * 正規化前後の位置対応表を格納する空のリストを指定します。
	 * 位置対応表が必要ない場合はnullを指定します。
	 * positionsは必要に応じてサイズが拡張されます。
	 * 呼出後positionsの長さは「 正規化後文字列.length() + 1」となります。
	 * @return
	 * 正規化された文字列を返します。
	 */
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
//		System.out.println("置換前:" + s);
//		System.out.println("置換後:" + r);
		return r.toString();
	}

	@Override
	public void prepare() {
		if (isPrepared()) return;
		initialize();
		dict.prepare();
		super.prepare();
	}

	@Override
	public void add(T bean) {
		initialize();
		dict.add(bean);
	}

	@Override
	public void encode(String s, List<WordFound<T>> found, String... types) {
		prepare();
		List<Integer> positions = new ArrayList<Integer>();
		s = normalize(s, positions);
		List<WordFound<T>> founds = dict.encode(s, types);
		// 検索結果を元の文字列に対する検索を行ったようにアレンジします。
		for (WordFound<T> e : founds) {
			int start = positions.get(e.getPosition());
			int end = positions.get(e.getPosition() + e.getLength());
			found.add(new WordFound<T>(e.getBean(), start, end - start));
		}
	}
	
	@Override
	public List<T> find(String s, int max, String... types) {
		prepare();
		s = normalize(s, null);
		return dict.find(s, max, types);
	}
	
	@Override
	public List<T> findStartsWith(String s, int max, String... types) {
		prepare();
		s = normalize(s, null);
		return dict.findStartsWith(s, max, types);
	}

	@Override
	public List<T> toList() {
		prepare();
		return dict.toList();
	}
	
	public List<Rep> replacementToList() {
		List<Rep> r = new ArrayList<NormalizedDictionary.Rep>();
		for (List<Rep> list : reps.values())
			for (Rep rep : list)
				r.add(rep);
		return r;
	}

	@Override
	public void removeType(String... types) {
		dict.removeType(types);
	}

}
