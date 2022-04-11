package saka1029.tensuhyo.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

	private static Logger logger = Logger.getLogger(Converter.class.getName());
	
	private Converter() {
	}

	private static boolean contains(String key, String[] array) {
		for (String e : array)
			if (key.equals(e))
				return true;
		return false;
	}
	
	private static final String[] TOC_NODES = {
		"root", "章", "部", "節", "款", "区分", "区分分類", 
	};
	
	private static boolean isToc(Node node) {
		return contains(node.nodeName(), TOC_NODES);
	}
	
	private static final String[] FILE_NODES = {
		"区分番号", "通則",
	};
	
	private static boolean isFile(Node node) {
		return contains(node.nodeName(), FILE_NODES);
	}
	
	private static boolean isText(Node node) {
		return !isToc(node) && !isFile(node);
	}
	
	private static void 目次チェック(Node node) {
		if (isToc(node)) {
			String msg = "";
			if (node.paragraphSize() > 0)
				msg += "目次ノードの下に段落あり。";
			int size = node.childrenSize();
			int i = 0;
			while (i < size && isText(node.children(i)))
				++i;
			if (i > 0)
				msg += "目次ノードの下にテキストノードあり。";
			while (i < size && isText(node.children(i)))
				++i;
			if (i < size)
				msg += "目次ノードの下に混在あり。";
			if (!msg.equals(""))
				logger.log(Level.WARNING, msg + node.toLongString());
		} else if (isFile(node))
			return;
		for (Node child : node.children())
			目次チェック(child);
	}

	public static void 目次チェック(Document doc) {
		目次チェック(doc.root());
	}

	private static final Pattern 点数 = Pattern.compile("[0-9０-９,，]+点");
	
	private static void 区分番号項番チェック(Node node, String kubun) {
		Matcher m = 点数.matcher(node.header());
		if (m.find()) {
			int pos = node.path().indexOf(kubun);
			logger.log(Level.INFO, String.format(
					"%d: %s:%s",
					node.lineNo(), node.path().substring(pos), node.header()));
			return;
		}
		for (Node child : node.children())
			区分番号項番チェック(child, kubun);
	}
	
	private static void 区分番号チェック(Node node) {
		if (node.nodeName().equals("区分番号")) {
			logger.log(Level.INFO, String.format(
				"%d: %s:%s", node.lineNo(), node.name(), node.header()));
			for (Node child : node.children())
				区分番号項番チェック(child, node.name());
			return;
		}
		for (Node child : node.children())
			区分番号チェック(child);
	}
	
	public static void 区分番号チェック(Document doc) {
		区分番号チェック(doc.root());
	}
	
	private static void insertChild(Node node, Node to) {
		while (node.childrenSize() > 0 && isText(node.children(0)))
			node.moveChild(to, 0);
		node.delegateChild(to);
	}
	
	private static void 事項追加(Node node) throws ParseException {
		if (isToc(node)) {
			boolean ok = true;
			if (node.paragraphSize() > 0)
				ok = false;
			int size = node.childrenSize();
			int i = 0;
			while (i < size && isText(node.children(i)))
				++i;
			if (i > 0) ok = false;
			while (i < size && isText(node.children(i)))
				++i;
			if (i < size)
				logger.log(Level.SEVERE, "目次ノードの下にテキストの混在あり" + node.toLongString());
			if (!ok) {
				Node child = 事項.value.match("", node.lineNo());
				insertChild(node, child);
			}
		} else if (isFile(node))
			return;
		for (Node child : node.children())
			事項追加(child);
	}
	
	public static void 事項追加(Document doc) throws ParseException {
		事項追加(doc.root());
	}

	private static void 通知参照収集(Node node, Map<String, Node> map) {
		String name = node.nodeName();
		if (name.equals("通則"))
			map.put(node.path().replaceAll("<(通則)>", "$1"), node);
		else if (name.equals("区分番号"))
			map.put(node.name(), node);
		else
			for (Node child : node.children())
				通知参照収集(child, map);
	}
	
	private static void 通知マージ(Node node, Map<String, Node> map, String key) {
		Node tuti = map.get(key);
		if (tuti == null) return;
		node.tuti(tuti);
		map.remove(key);
	}
	
	private static void 通知マージ(Node node, Map<String, Node> map) {
		String name = node.nodeName();
		if (name.equals("通則"))
			通知マージ(node, map, node.path());
		else if (name.equals("区分番号"))
			通知マージ(node, map, node.name());
		else
			for (Node child : node.children())
				通知マージ(child, map);
	}
	
	public static void 通知マージ(Document kokuji, Document tuti)
			throws ParseException {
		Map<String, Node> map = new HashMap<>();
		通知参照収集(tuti.root(), map);
		通知マージ(kokuji.root(), map);
		for (Entry<String, Node> e : map.entrySet()) {
			String path = e.getValue().path();
			int pos = path.lastIndexOf('/');
			if (pos >= 0) path = path.substring(0, pos);
			Node parent = kokuji.find(path);
			if (parent == null)
				logger.log(Level.SEVERE, String.format(
					"参照されない通知%s", e.getValue().toLongString()));
			else {
				logger.warning(String.format(
					"参照された通知%s 告示=%s", e.getValue().toLongString(), parent));
				Node child = 通則_告示.value.match("通則", parent.lineNo());
				parent.addFirst(child);
				child.tuti(e.getValue());
			}

		}
	}
}
