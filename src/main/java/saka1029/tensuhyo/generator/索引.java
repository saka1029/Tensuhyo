package saka1029.tensuhyo.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import saka1029.tensuhyo.dictionary.NormalizedDictionary;
import saka1029.tensuhyo.dictionary.TrieDictionary;
import saka1029.tensuhyo.dictionary.WordFound;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.parser.StencilRoot;

public class 索引 {

	private static final Logger logger = Logger.getLogger(索引.class.getName());
	
	private static final String 区分番号置換 = "NormalizedDictionaryReplacement.csv";
	private static final String ENCODING = "utf-8";
	
	public static final String 区分番号TYPE = "100";
	public static final String 章部節款TYPE = "150";
	public static final String 項番TYPE = "200";
	public static final String 区分内参照TYPE = "300";
	public static final String 施設基準TYPE = "400";

	private 索引Option option;
	private NormalizedDictionary<NodeWord> kubunDict;
	private NormalizedDictionary<NodeWord> naibuDict;
	
	private Coco索引 cocoIndex;
	
	private NormalizedDictionary<NodeWord> newDict() throws IOException {
		String resourceName = getClass().getPackage().getName().replace('.', '/');
		resourceName += "/" + 区分番号置換;
		NormalizedDictionary<NodeWord> dict = new NormalizedDictionary<>(
				new TrieDictionary<>(new NodeWordAccessor()));
		dict.addReplacementResource(resourceName, ENCODING);
		return dict;
	}
	
	private void 辞書追加(String name, Node node, String type, String href) {
	    cocoIndex.add(name, node, type, href);
		kubunDict.add(new NodeWord(name, node, type, href));
	}
	
	private void 区分内辞書追加(String name, Node node, String type, String href) {
		if (name.startsWith("別に厚生労働大臣が定める")) return;
		naibuDict.add(new NodeWord(name, node, type, href));
	}
	
	/**
	 * 区分番号参照のリンクパターン
	 * 以下のパターンを対象とする。
	 * (名称の後に点数が続く場合は対象外とする）
	 * <ol>
	 * <li>A100</li>
	 * <li>一般病棟入院基本料（１日につき）</li>
	 * <li>A100一般病棟入院基本料（１日につき）</li>
	 * <li>A100に掲げる一般病棟入院基本料（１日につき）</li>
	 * </ol>
	 * 末尾にカッコ書きがある場合はそれを除いたものも対象とする
	 * <ol>
	 * <li>一般病棟入院基本料</li>
	 * <li>A100一般病棟入院基本料</li>
	 * <li>A100に掲げる一般病棟入院基本料</li>
	 * </ol>
	 */
	private void 区分番号辞書追加(Node node) {
		String kubun = option.区分番号接頭語() + option.区分番号表示(node.fileName());
		String href = node.fileName() + ".html";
		String simple = node.simpleHeader();
		if (simple.equals("削除")) return;
		辞書追加(kubun, node, 区分番号TYPE, href);
		辞書追加(simple, node, 区分番号TYPE, href);
		辞書追加(kubun + simple, node, 区分番号TYPE, href);
		辞書追加(kubun + "に掲げる" + simple, node, 区分番号TYPE, href);
		辞書追加(kubun + "の" + simple, node, 区分番号TYPE, href);
		// 最後の括弧を除いた名前を登録します。
		String trimParen = simple.replaceFirst("(?:[（(][^）)]*[）)])+$", "");
		if (!trimParen.equals(simple)) {
			辞書追加(trimParen, node, 区分番号TYPE, href);
			辞書追加(kubun + trimParen, node, 区分番号TYPE, href);
			辞書追加(kubun + "に掲げる" + trimParen, node, 区分番号TYPE, href);
			辞書追加(kubun + "の" + trimParen, node, 区分番号TYPE, href);
		}
	}
	
	private void 章部節款辞書追加(Node node, StringBuilder sb) {
		if (node.stencil() instanceof StencilRoot) return;
		章部節款辞書追加(node.parent(), sb);
		sb.append(node.number());
	}
	
	private void 章部節款辞書追加(Node node) {
		StringBuilder sb = new StringBuilder();
		章部節款辞書追加(node, sb);
//		logger.log(Level.INFO, "*****" + sb.toString());
		String fname = node.fileName();
		辞書追加(sb.toString(), node, 章部節款TYPE, fname + ".html");
	}
	
	/**
	 * 項番等参照のリンクパターン
	 * <ol>
	 * <li>区分番号A100に掲げる一般病棟入院基本料（１日につき）の1</li>
	 * <li>区分番号A100の1に掲げる７対１入院基本料</li>
	 * <li>区分番号A100の1</li>
	 * <li>区分番号A100に掲げる７対１入院基本料</li>
	 * <li>区分番号A100に掲げる一般病棟入院基本料の1</li>
	 * </ol>
	 */
	private void 項番辞書追加(Node node, Node kubun) {
//		if (!node.header().matches(".*[0-9,０-９，]+点.*")) return;
		String rpath = node.path().substring(kubun.path().length() + 1);
		String href = kubun.fileName() + ".html#" + rpath;
		String rname = rpath.replaceAll("/", "の");
		rname = rname.replaceAll("注の", "注");
		String simple = kubun.simpleHeader();
		辞書追加(kubun.name() + "に掲げる" + simple + "の" + rname, node, 項番TYPE, href);
		辞書追加(kubun.name() + simple + "の" + rname, node, 項番TYPE, href);
		if (!node.simpleHeader().equals("")) {
			辞書追加(kubun.name() + "の" + rname + "に掲げる" + node.simpleHeader(), node, 項番TYPE, href);
			辞書追加(kubun.name() + "の" + rname + node.simpleHeader(), node, 項番TYPE, href);
		}
		辞書追加(kubun.name() + "の" + rname, node, 項番TYPE, href);
		if (!node.simpleHeader().equals("")) {
			辞書追加(kubun.name() + "に掲げる" + node.simpleHeader(), node, 項番TYPE, href);
			辞書追加(kubun.name() + node.simpleHeader(), node, 項番TYPE, href);
		}
		// 最後の括弧を除いた名前を登録します。
		String trimParen = simple.replaceFirst("(?:[（(][^）)]*[）)])+$", "");
		if (!trimParen.equals(simple)) {
			辞書追加(kubun.name() + "に掲げる" + trimParen + "の" + rname, node, 項番TYPE, href);
			辞書追加(kubun.name() + trimParen + "の" + rname, node, 項番TYPE, href);
		}
	}

	private void 項番辞書作成(Node node, Node kubun) {
		項番辞書追加(node, kubun);
		for (Node child : node.children())
			項番辞書作成(child, kubun);
	}
	
	private void 区分番号辞書作成(Node node) {
		if (node.nodeName().equals("区分番号")) {
			区分番号辞書追加(node);
			for (Node child : node.children())
				項番辞書作成(child, node);
			return;
		} else if (node.nodeName().equals("注"))
			return;
		else if (node.nodeName().equals("章") ||
			node.nodeName().equals("部") ||
			node.nodeName().equals("節") ||
			node.nodeName().equals("款"))
			章部節款辞書追加(node);
		for (Node child : node.children())
			区分番号辞書作成(child);
	}
	
	public void 区分番号辞書作成(Document doc, 索引Option option) throws IOException {
		kubunDict = newDict();
		cocoIndex = new Coco索引();
		this.option = option;
		区分番号辞書作成(doc.root());
//		List<NodeWord> list = kubunDict.toList();
//		for (NodeWord e : list)
//			logger.log(Level.INFO, String.format(
//				"%s:%s", e.name(), e.href()));
//		logger.log(Level.INFO, "用語数=" + list.size());
	}

	/**
	 * 区分内参照のリンクパターン
	 * <ol>
	 * <li>１</li>
	 * <li>注１</li>
	 * <li>療養病棟入院基本料１</li>
	 * </ol>
	 */
	private void 区分内参照追加(Node node, Node kubun) throws IOException {
		String rpath = node.path().substring(kubun.path().length() + 1);
		String href = kubun.name() + ".html#" + rpath;
		String rname = rpath.replaceAll("/", "の");
		rname = rname.replaceAll("注の", "注");
		/* この条件を外すと辞書検索時にStringIndexOutOfFoundsExceptionとなる */
		/* "注"のみで番号がない場合は対象外とする。 */
		if (!rname.matches("[0-9]+") && !rname.equals("注"))
			区分内辞書追加(rname, node, 区分内参照TYPE, href);
		String simple = node.simpleHeader();
		if (!simple.equals("")) {
			区分内辞書追加(simple, node, 区分内参照TYPE, href);
			String trimed = simple.replaceFirst("(?:[（(][^）)]*[）)])+$", "");
			if (!trimed.equals("") && !trimed.equals(simple))
				区分内辞書追加(trimed, node, 区分内参照TYPE, href);
		}
		for (Node child : node.children())
			区分内参照追加(child, kubun);
	}
	
	public void 区分内参照追加(Node node) throws IOException {
		naibuDict = newDict();
		if (!node.nodeName().equals("区分番号"))
			throw new IllegalArgumentException("node");
		for (Node child : node.children())
			区分内参照追加(child, node);
	}

	public void 区分内参照削除() {
		naibuDict = null;
	}
	
	/**
	 * 辞書の検索結果を
	 * <ul>
	 * <li>positionの上昇順
	 * <li>lengthの下降順
	 * <li>beanのtypeの上昇順
	 * </ul>
	 * にソートするためのクラスです。
	 * 同一position, lengthの場合はソート前の順序が維持されます。
	 * （Collections.sort(LIst<T>, Comparator<? super T>)の仕様）
	 */
	static class WordFoundComparator implements Comparator<WordFound<NodeWord>> {

		@Override
		public int compare(WordFound<NodeWord> o1, WordFound<NodeWord> o2) {
			int diff = o1.getPosition() - o2.getPosition();
			if (diff != 0) return diff;
			diff = o2.getLength() - o1.getLength();
			if (diff != 0) return diff;
			diff = o1.getBean().type().compareTo(o2.getBean().type());
			return diff;
		}

	}
	
	private static final Comparator<WordFound<NodeWord>> WCOMP = new WordFoundComparator();
	
	private static List<WordFound<NodeWord>> filter(List<WordFound<NodeWord>> list) {
		if (list.size() <= 1) return list;
		Collections.sort(list, WCOMP);
//		return list;
		List<WordFound<NodeWord>> result = new ArrayList<>();
		WordFound<NodeWord> p = list.get(0);
		boolean single = true;
		for (int i = 1, size = list.size(); i < size; ++i) {
			WordFound<NodeWord> e = list.get(i);
			if (e.getPosition() == p.getPosition() &&
					e.getLength() == p.getLength() &&
					e.getBean().type().equals(p.getBean().type()))
				single = false;
			else {
				if (single) result.add(p);
				p = e;
				single = true;
			}
		}
		if (single) result.add(p);
//		if (list.size() != result.size())
//			logger.log(Level.INFO, "diff");
		return result;
	}

	public List<WordFound<NodeWord>> encode(String s) {
		List<WordFound<NodeWord>> list = new ArrayList<>();
		if (kubunDict != null) {
			kubunDict.encode(s, list);
			if (naibuDict != null)
				naibuDict.encode(s, list);
		}
		list = filter(list);
		return list;
	}

	private Node 施設基準ファイル(Node node) {
        while (node != null) {
            String name = node.nodeName();
            if (name.equals("第漢数字") || name.equals("別表第漢数字"))
                return node;
            node = node.parent();
        }
        throw new IllegalArgumentException("ファイルノードがありません:" + node);
	}

	private void 施設基準辞書追加(Node node) {
	    String base = "../k/";
	    Node fileNode = 施設基準ファイル(node);
	    String hash = fileNode == node ? "" : "#" + node.fileName();
	    String fileName = fileNode.fileName() + ".html" + hash;
	    String header = node.header();
	    String simple = header.replaceFirst("の施設基準$", "");
	    辞書追加(header, node, 施設基準TYPE, base + fileName);
	    辞書追加(simple, node, 施設基準TYPE, base + fileName);
	}

	private void 施設基準追加(Node node) {
	    String header = node.header();
	    if (header.endsWith("の施設基準"))
	        施設基準辞書追加(node);
	    for (Node child : node.children())
	        施設基準追加(child);
	}

	public void 施設基準追加(Document doc) {
	    施設基準追加(doc.root());
	    辞書追加("施設基準", doc.root(), 施設基準TYPE, "../k/0.html");
	    辞書追加("基本診療料の施設基準等", doc.root(), 施設基準TYPE, "../k/0.html#1");
	    辞書追加("基本診療料に係る施設基準", doc.root(), 施設基準TYPE, "../k/0.html#1");
	    辞書追加("特掲診療料の施設基準等", doc.root(), 施設基準TYPE, "../k/0.html#2");
	    辞書追加("特掲診療料に係る施設基準", doc.root(), 施設基準TYPE, "../k/0.html#2");
	}
}
