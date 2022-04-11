package saka1029.tensuhyo.dictionary;


import java.util.ArrayList;
import java.util.List;

/**
 * トライ木を使用した用語辞書の実装です。
 */
public class TrieDictionary<T> extends WordDictionaryBase<T> {

	/**
	 * ノードクラスです。
	 */
	private static class Node<T> {
		
		/**
		 * ノードのキーです。
		 */
		private char key;
		
		/**
		 * 用語を格納するリストです。
		 * メモリを節約するため、追加時にリストを作成します。
		 */
		private List<T> words = null;
		
		/**
		 * 子ノードのリストです。
		 * 子ノードを検索するとき二分探索を行うため
		 * 子ノードはフィールドkeyの上昇順に並んでいる必要があります。
		 * メモリを節約するため、追加時にリストを作成します。
		 */
		private List<Node<T>> children = null;
		
		private Node(char key) {
			this.key = key;
		}
		
		/**
		 * 子ノードを追加します。
		 * 二分探索できるように、
		 * フィールドｃｈの上昇順となるように追加します。
		 * @param node 親ノードを指定します。
		 * @param child 追加する子ノードを指定します。
		 */
		private void add(Node<T> child) {
			if (children == null)
				children = new ArrayList<Node<T>>();
			for (int i = children.size() - 1; i >= 0; --i) {
				Node<T> e = children.get(i);
				if (child.key >= e.key) {
					children.add(i + 1, child);
					return;
				}
			}
			children.add(0, child);
		}
		
		/**
		 * ノードに用語を追加します。
		 * @param node 追加先のノードを指定します。
		 * @param word 追加する用語を指定します。
		 */
		private void add(T word) {
			if (words == null)
				words = new ArrayList<T>();
			words.add(word);
		}
	}
	
	/**
	 * ルートノードです。
	 */
	private Node<T> root = new Node<T>('\0');

	/**
	 * 空の辞書を作成します。
	 */
	public TrieDictionary(WordAccessor<T> wordAccessor) {
		super(wordAccessor);
	}

	/**
	 * 子ノードを検索します。
	 * 子ノードはフィールドchの上昇順に並んでいるので、
	 * 二分探索で検索します。
	 * @param node 親ノードを指定します。
	 * @param ch 検索キーを指定します。
	 * @return 見つかった子ノードを返します。
	 * 見つからなかった場合はnullを返します。
	 */
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
		String name = getName(word);
		if (name == null || name.length() < 1)
			throw new IllegalArgumentException("name is null");
		Node<T> last = root;
		for (int i = 0, len = name.length(); i < len; ++i) {
			char ch = name.charAt(i);
			Node<T> child = find(last, ch);
			if (child == null)
				last.add(child = new Node<T>(ch));
			last = child;
		}
		last.add(word);
	}

	private void addWord(Node<T> node, int pos, int len,
			List<WordFound<T>> list, String... types) {
		if (node.words == null) return;
		for (T word : node.words)
			if (inType(word, types))
				list.add(new WordFound<T>(word, pos, len));
	}

	@Override
	public void encode(String s, List<WordFound<T>> found, String... types) {
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
	
	private void scan(String s, int pos, int i,
			Node<T> node, int tolerance,
			List<WordFound<T>> list, String... types) {
		int len = s.length();
		if (i >= len) return;
		char ch = s.charAt(i);
		Node<T> found = find(node, ch);
		if (found != null) {
			addWord(found, pos, i - pos + 1, list, types);
			scan(s, pos, i + 1, found, tolerance, list, types);
			return;
		}
		if (i <= pos) return;	// 先頭の脱字は許容しません。
		if (tolerance <= 0) return;
		if (node.children == null) return;
		// 脱字チェック（nodeの子について文字の出現をチェックします）
		for (Node<T> child : node.children) {
			found = find(child, ch);
			if (found == null) continue;
			addWord(found, pos, i - pos + 1, list, types);
			scan(s, pos, i + 1, found, tolerance - 1, list, types);
			return;
		}
		if (i + 1 >= len) return;
		// 誤字チェック（nodeの子について次の文字の出現をチェックします）
		char nch = s.charAt(i + 1);
		for (Node<T> child : node.children) {
			found = find(child, nch);
			if (found == null) continue;
			addWord(found, pos, i - pos + 2, list, types);
			scan(s, pos, i + 2, found, tolerance - 1, list, types);
			return;
		}
		// 冗字チェック（nodeについて次の文字の出現をチェックします）
		found = find(node, nch);
		if (found == null) return;
		addWord(found, pos, i - pos + 2, list, types);
		scan(s, pos, i + 2, found, tolerance - 1, list, types);
	}

	/**
	 * 文字列sの中から用語を抽出しリストで返します。
	 * 誤字、脱字、冗字を許容します。
	 * 許容する誤字、脱字、冗字は以下に限られます。
	 * （１）先頭および末尾の誤字、脱字、冗字は許容されません。
	 * （２）許容値で２以上を指定した場合でも、連続する誤字、脱字、冗字は許容されません。
	 * 許容値がゼロの場合は誤字、脱字、冗字を許容しないencodeと同じ動作になります。
	 * 許容値に１以上を指定すると３倍程度処理時間が多くかかることがあります。
	 * 以下は性能テストの結果です。
	 *   許容値＝０ : read=  1453ms encode=   672ms total=  2125ms memory= 228677016
	 *   許容値＝１ : read=  1375ms encode=  2063ms total=  3438ms memory= 244297184
	 * @param s 対象となる文字列を指定します。
	 * @param tolerance １用語中で許容する誤字、脱字、冗字の最大数を指定します。
	 * @param types 対象となる用語の種類を指定します。
	 * @return 抽出した用語を返します。
	 * 	ひとつも抽出できなかった場合は空のリストを返します。
	 */
	public List<WordFound<T>> encode(String s, int tolerance, String... types) {
		if (s == null || s.length() < 1)
			throw new IllegalArgumentException("s");
		List<WordFound<T>> list = new ArrayList<WordFound<T>>();
		for (int pos = 0, len = s.length(); pos < len; ++pos)
			scan(s, pos, pos, root, tolerance, list, types);
		return list;
	}
	
	@Override
	public List<T> find(String s, int max, String... types) {
		List<T> list = new ArrayList<T>();
		Node<T> last = root;
		for (int i = 0, size = s.length(); i < size; ++i) {
			last = find(last, s.charAt(i));
			if (last == null) return list;
		}
		if (last.words != null)
			for (T word : last.words)
				if (inType(word, types))
					list.add(word);
		return list;
	}
	
	/**
	 * 指定したノードおよびそのすべての子孫ノードに格納されている用語を
	 * リストに追加します。
	 * @param node 最上位ノードを指定します。
	 * @param list 追加先のリストを指定します。
	 * @param max リストに格納する用語の最大件数を指定します。
	 * ゼロ以下が指定された場合はすべての用語を格納します。
	 * @param types 格納する用語の種類を指定します。
	 */
	private void allDescendant(Node<T> node, List<T> list, int max, String... types) {
		if (node.words != null)
			for (T word : node.words)
				if (inType(word, types)) {
					if (max > 0 && list.size() >= max) return;
					list.add(word);
				}
		if (node.children != null)
			for (Node<T> child : node.children)
				allDescendant(child, list, max, types);
	}

	@Override
	public List<T> findStartsWith(String s, int max, String... types) {
		List<T> list = new ArrayList<T>();
		Node<T> last = root;
		for (int i = 0, size = s.length(); i < size; ++i) {
			last = find(last, s.charAt(i));
			if (last == null) return list;
		}
		allDescendant(last, list, max, types);
		return list;
	}
	
	@Override
	public List<T> toList() {
		List<T> result = new ArrayList<T>();
		allDescendant(root, result, -1);
		return result;
	}
	
	private void removeType(Node<T> node, String... types) {
		if (node.words != null) {
			for (int i = 0; i < node.words.size();)
				if (inType(node.words.get(i), types))
					node.words.remove(i);
				else
					++i;
//			if (node.words.size() == 0)
//				node.words = null;
		}
		if (node.children != null)
			for (Node<T> child : node.children)
				removeType(child, types);
	}

	/**
	 * Nodeにぶら下がっている用語のみ削除します。
	 * oldTODO: すべての用語が削除された場合はNode（必要なら親ノードも）削除すべき。
	 */
	@Override
	public void removeType(String... types) {
		removeType(root, types);
	}

}
