package saka1029.tensuhyo.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import saka1029.tensuhyo.dictionary.WordFound;
import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.util.StringConverter;
import saka1029.tensuhyo.util.StringEditor;
import saka1029.tensuhyo.util.TextWriter;

public class Renderer {

	private static final Logger logger = Logger.getLogger(Renderer.class.getName());

	public static final String HTML_ENCODING = "UTF-8";
	public static final String CSS_FILE = "../../css/all.css";
//	public static final String ALL_JS = "../../script/all.js";
//	private static final String IMAGE_HTML = "_image.html";
//	private static final String IMAGE_JS = "_image.js";
	public static final String DOCTYPE = "<!DOCTYPE html>";
//	private static final String CSS_VERSION = "?" + System.currentTimeMillis();
	private static final String HIKAKU_PAGE = "hikaku.html";


	private RendererOption callback = null;

	static String GOOGLE_ANALYTICS_TRACKING_CODE_FILE = "GoogleAnalyticsTrackingCode.txt";
	static String GOOGLE_ANALYTICS_TRACKING_CODE;
	static {
		StringBuilder sb = new StringBuilder();
		try (InputStream is = Renderer.class.getResourceAsStream(GOOGLE_ANALYTICS_TRACKING_CODE_FILE);
			InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(ir);) {
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				sb.append(line).append("\n");
			}
			GOOGLE_ANALYTICS_TRACKING_CODE = sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void googleAnalytics(TextWriter w) {
		w.printf("%s", GOOGLE_ANALYTICS_TRACKING_CODE);
// 		w.printf("<script type='text/javascript'>\n");
// 		w.printf("\n");
// 		w.printf("  var _gaq = _gaq || [];\n");
// 		w.printf("  _gaq.push(['_setAccount', 'UA-37161278-1']);\n");
// 		w.printf("  _gaq.push(['_trackPageview']);\n");
// 		w.printf("\n");
// 		w.printf("  (function() {\n");
// 		w.printf("    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n");
// 		// 標準のGoogleアナリティクスのコード
// 		w.printf("    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n");
// 		// ユーザー属性とインタレスト カテゴリに関するレポート
// //		w.printf("    ga.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'stats.g.doubleclick.net/dc.js';\n");
// 		w.printf("    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n");
// 		w.printf("  })();\n");
// 		w.printf("\n");
// 		w.printf("</script>\n");
	}
	public static void writeMeta(TextWriter w, String description) {
		w.printf("<meta charset='%s'>\n", HTML_ENCODING);
		w.printf("<meta name='viewport' content='initial-scale=1.0'>\n");
		w.printf("<meta name='description' content='%s'>\n", description);
		googleAnalytics(w);
	}

	private void パン屑リスト(Node node, TextWriter w) {
		if (node == null) {
			w.printf("<a href='../../index.html'>診療報酬点数表</a>\n");
			return;
		}
		パン屑リスト(node.parent(), w);
		if (node.nodeName().equals("区分"))
			w.printf(" &gt; 区分\n");
		else
			w.printf("&gt; <a href='%s.html'>%s %s</a>\n",
				node.fileName(), node.number(), node.simpleHeader());
	}

//	private static void 目次リスト(Node node, TextWriter w, StringBuilder endTags) {
//		if (node == null) return;
//		目次リスト(node.parent(), w, endTags);
//		w.printf("<ul>\n");
//		w.printf("<li><a href='%s.html'>%s %s</a></li>\n",
//			ファイル名(node),
//			node.number(), node.simpleHeader());
//		endTags.append("</ul>")
//			.append(System.getProperty("line.separator"));
//	}

//	public void サイト内検索(TextWriter w) {
//        w.printf(" <script>\n");
//        w.printf("  (function() {\n");
//        w.printf("    var cx = '008433866737420098736:-h2hjiatj8i';\n");
//        w.printf("    var gcse = document.createElement('script');\n");
//        w.printf("    gcse.type = 'text/javascript';\n");
//        w.printf("    gcse.async = true;\n");
//        w.printf("    gcse.src = (document.location.protocol == 'https:' ? 'https:' : 'http:') +\n");
//        w.printf("        '//cse.google.com/cse.js?cx=' + cx;\n");
//        w.printf("        var s = document.getElementsByTagName('script')[0];\n");
//        w.printf("        s.parentNode.insertBefore(gcse, s);\n");
//        w.printf("    })();\n");
//        w.printf(" </script>\n");
//        w.printf(" <gcse:search></gcse:search>\n");
//        w.printf(" <gcse:searchresults linkTarget='_self' resultSetSize='large'>\n");
//        w.printf(" </gcse:searchresults>\n");
//	}

	private void 目次(Node node, TextWriter w) {
		if (node == null) return;
		w.printf("<div id='breadcrumb'>\n");
		w.printf("<b>\n");
		パン屑リスト(node.parent(), w);
		w.printf("</b>\n");
		if (callback != null) callback.目次(node, w);
		w.printf("</div>\n");
	}

	public static String spaces(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; ++i)
			sb.append("　");
		return sb.toString();
	}

	private static boolean isAscendant(Node child, Node parent) {
		if (child == null) return false;
		if (child == parent) return true;
		return isAscendant(child.parent(), parent);
	}

	private static final Pattern タグ編集正規表現 = Pattern.compile(
		"(?<TENSU>[0-9,.０-９，．]+点)|" +
		"(?<BESI>別(紙|添)様式[0-9０-９]+(?:の[0-9０-９]+)?)");

	private static String タグ編集(final Node node, String s, final File dir) {
		final File images = new File(dir, "image");
		return StringEditor.replace(s, タグ編集正規表現, new StringEditor() {
			@Override
			public String replace(Matcher m) {
				if (m.group("TENSU") != null)
					return String.format("<span class='tensu'>%s</span>", m.group("TENSU"));
				else if (m.group("BESI") != null) {
					logger.info(String.format("イメージリンク %s %s", m.group("BESI"), node.path() ));
					String name = m.group("BESI").replaceFirst("別(紙|添)様式", "BESI").replaceFirst("の", "_");
					name = StringConverter.toNormalWidthANS(name);
//					StringBuilder hash = new StringBuilder();
					String first = name + ".pdf";
					if (new File(images, first).exists()) {
						return String.format("<a href='image/%s'>%s</a>", first, m.group("BESI"));
//						hash.append("../image/").append(first);
//						for (int i = 1; i < 100; ++i) {
//							String next = name + "-" + i + ".png";
//							if (!new File(images, next).exists()) break;
//							hash.append(",").append("../image/").append(next);
//						}
					} else {
						logger.log(Level.SEVERE, String.format("%s(%s)は存在しません。", m.group("BESI"), new File(images, first)));
						return null;
					}
//					return String.format("<a href='_image.html#%s'>%s</a>",
//						hash, m.group("BESI"));
				}
				return null;
			}
		});
	}

	private static String updateLink(Node node, Node kubun, String s, 索引 dict, File dir) {
		if (s.equals("")) return s;
		s = タグ編集(node, s, dir);
		List<WordFound<NodeWord>> list = dict.encode(s);
		TextWriter w = new TextWriter(new StringWriter());
		try {
			int p = 0;
			for (int i = 0, size = list.size(); i < size; ) {
				WordFound<NodeWord>	e = list.get(i);
				if (isAscendant(node, e.getBean().node()) || isAscendant(kubun, e.getBean().node())) {
					++i;
					continue;
				}
				int ep = e.getPosition();
				NodeWord word = e.getBean();
				String title = "";
				if ("章部節款".indexOf(word.node().nodeName()) >= 0)
					title = word.node().simpleHeader();
				else if (word.type().equals(索引.施設基準TYPE)) {
					title = word.node().simpleHeader();
					if (title.equals("")) title = word.name();
				} else {
					title = word.href();
					title = title.replace(".html", "");
					title = title.replace("#", "の");
					title = title.replace("/", "の");
					title = title.replace("注の", "注");
					title += " " + word.node().simpleHeader();
				}
				w.printf("%s<a href='%s' title='%s' >%s</a>",
					s.substring(p, ep),
					word.href(), title,
					s.substring(ep, ep + e.getLength()));
				logger.fine(String.format("採用された用語: %s %s", node.path(), e));
				p = ep + e.getLength();
				++i;
				while (i < size && list.get(i).getPosition() < p) {
					logger.fine(String.format("捨てられた用語: %s %s", node.path(), list.get(i)));
					++i;
				}
			}
			w.printf("%s", s.substring(p));
			return w.toString();
		} finally {
			w.close();
		}
	}

	private void write(Node node, Node kubun, TextWriter w, int level,
			索引 dict, String idPrefix, File dir) {
		if (node.name().equals("通則") && node.paragraphSize() <= 0 && node.childrenSize() <= 0)
			return;
		String separator = spaces((Node.indent(node.separator()) + 1) / 2);
		String header = node.number() + separator + node.header();
		String para = node.paragrahText();
		String text;
		if (node.nodeName().equals("区分番号")) {
			text = タグ編集(node, header, dir);
			if (para.length() > 0)
				text += updateLink(node, kubun, para, dict, dir);
		} else if (node.nodeName().equals("通則") && node.paragraphSize() > 0)
			text = updateLink(node, kubun, para, dict, dir);
		else
			text = updateLink(node, kubun, header + para, dict, dir);
		String spaces = spaces(level);
		int indent = Node.indent(spaces + node.number() + separator);
		int index = node.path().indexOf(kubun.name());
		if (index == -1)
		    index = 0;
		String id = node.path().substring(index);
		id = id.replaceFirst("[^/]*/", "");
		if (node.nodeName().equals("通則") && node.paragraphSize() > 0) {
			w.printf("<p id='%s%s' class='m%d'>%s%s</p>\n",
				idPrefix, id, indent, spaces, header);
			String ps = spaces(level + 1);
			w.printf("<p class='m%d'>%s%s</p>\n",
				Node.indent(ps), ps, text);
		} else
			w.printf("<p id='%s%s' class='m%d'>%s%s</p>\n",
				idPrefix, id, indent, spaces, text);
		for (Node child : node.children())
			write(child, kubun, w, level + 1, dict, idPrefix, dir);
	}

	public static void writeShare(TextWriter w, String title, String url) throws IOException {
//	    w.printf("<div id='social-network'>\n");
//	    w.printf("<!-- Twitter -->\n");
//	    w.printf("<p>\n");
//	    w.printf("<a href=\"https://twitter.com/share\"\n");
//	    w.printf("  class=\"twitter-share-button\"\n");
//	    w.printf("  data-via=\"tensuhyo\"\n");
//	    w.printf("  data-lang=\"ja\">ツイート</a>\n");
//	    w.printf("</p>\n");
//	    w.printf("<script>\n");
//	    w.printf("  !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';\n");
//	    w.printf("  if(!d.getElementById(id)){js=d.createElement(s);\n");
//	    w.printf("  js.id=id;js.src=p+'://platform.twitter.com/widgets.js';\n");
//	    w.printf("  fjs.parentNode.insertBefore(js,fjs);\n");
//	    w.printf("  }}(document, 'script', 'twitter-wjs');\n");
//	    w.printf("</script>\n");
//	    w.printf("<!-- はてなブックマーク -->\n");
//	    w.printf("<p>\n");
//	    w.printf("<a href=\"%s\"", url);
//	    w.printf("  class=\"hatena-bookmark-button\" data-hatena-bookmark-title=\"%s\"", title);
//	    w.printf("  data-hatena-bookmark-layout=\"standard-balloon\"\n");
//	    w.printf("  data-hatena-bookmark-lang=\"ja\"\n");
//	    w.printf("  title=\"このエントリーをはてなブックマークに追加\">\n");
//	    w.printf("<img src=\"http://b.st-hatena.com/images/entry-button/button-only@2x.png\"\n");
//	    w.printf("  alt=\"このエントリーをはてなブックマークに追加\"\n");
//	    w.printf("  width=\"20\" height=\"20\" style=\"border: none; vertical-align: bottom;\" /></a>\n");
//	    w.printf("</p>\n");
//	    w.printf("<script type=\"text/javascript\"\n");
//	    w.printf("  src=\"http://b.st-hatena.com/js/bookmark_button.js\"\n");
//	    w.printf("  charset=\"utf-8\" async=\"async\"></script>\n");
////	    w.printf("<!-- Facebook -->\n");
////	    w.printf("<div id=\"fb-root\"></div>\n");
////	    w.printf("<script>(function(d, s, id) {\n");
////	    w.printf("  var js, fjs = d.getElementsByTagName(s)[0];\n");
////	    w.printf("  if (d.getElementById(id)) return;\n");
////	    w.printf("  js = d.createElement(s); js.id = id;\n");
////	    w.printf("  js.src = \"//connect.facebook.net/ja_JP/sdk.js#xfbml=1&version=v2.0\";\n");
////	    w.printf("  fjs.parentNode.insertBefore(js, fjs);\n");
////	    w.printf("}(document, 'script', 'facebook-jssdk'));</script>\n");
////	    w.printf("<p>\n");
////	    w.printf("<div class=\"fb-like\"\n");
////	    w.printf("  data-href=\"%s\"", url);
////	    w.printf("  data-layout=\"button_count\"\n");
////	    w.printf("  data-action=\"like\"\n");
////	    w.printf("  data-show-faces=\"true\"\n");
////	    w.printf("  data-share=\"false\"></div>\n");
////	    w.printf("</p>\n");
//	    w.printf("</div>\n");
	}

	private void makeMenu(TextWriter w) throws IOException {
	    w.printf("<script type='text/javascript' src='../../js/menu.js'></script>\n");
	}

	private void 区分番号(Node node, File dir, String file, 索引 dict) throws IOException {
		TextWriter w = new TextWriter(new File(dir, file), HTML_ENCODING);
		try {
			w.printf("%s\n", DOCTYPE);
			w.printf("<html>\n");
			w.printf("<head>\n");
			String title = String.format("%s %s - %s",
			    node.name(), node.header().replaceFirst("[ \t　].*$", ""), callback.共通タイトル());
			String url = String.format("%s/%s", callback.baseUrl(), file);
//			String title = node.header().replaceFirst("[ \t　].*$", "");
			writeMeta(w, title);
			w.printf("<link rel='stylesheet' type='text/css' href='%s' />\n", CSS_FILE);
//			w.printf("<script src='%s' type='text/javascript'></script>\n", ALL_JS);
			w.printf("<title>%s</title>\n", title);
			w.printf("</head>\n");
			w.printf("<body>\n");
			w.printf("<div id='all'>\n");
//			w.printf("<p>");
			目次(node, w);
//			w.printf("</p>");
			w.printf("<div id='content'>\n");
			dict.区分内参照追加(node);
			write(node, node, w, 0, dict, "", dir);
			if (node.tuti() != null) {
				w.printf("<div id='tuti'>\n");
				write(node.tuti(), node, w, 0, dict, "t", dir);
				w.printf("</div>\n");
			}
			dict.区分内参照削除();
			w.printf("</div>\n");
			writeShare(w, title, url);
			w.printf("</div>\n");
			makeMenu(w);
			w.printf("</body>\n");
			w.printf("</html>\n");
		} finally {
			w.close();
		}
	}

	private void 区分番号(Node node, File dir, 索引 dict)
			throws IOException {
//		Matcher m = KubunPat.matcher(node.name());
//		while (m.find())
//			区分番号(node, dir, m.group() + ".html", dict);
		区分番号(node, dir, node.fileName() + ".html", dict);
	}

	private void 通則(Node node, File dir, 索引 dict) throws IOException {
		String fileName = node.fileName();
		TextWriter w = new TextWriter(new File(dir, fileName + ".html"), HTML_ENCODING);
		try {
			w.printf("%s\n", DOCTYPE);
			w.printf("<html>\n");
			w.printf("<head>\n");
			String title = String.format("%s - %s", node.name(), callback.共通タイトル());
			if (node.parent() != null) {
			    Node parent = node.parent();
			    title = String.format("%s %s %s", parent.name(), parent.header().replaceFirst("[ \t　].*$", ""), title);
			}
			String url = String.format("%s/%s.html", callback.baseUrl(), fileName);
//			String title = node.header().replaceFirst("[ \t　].*$", "");
			writeMeta(w, title);
			w.printf("<link rel='stylesheet' type='text/css' href='%s' />\n", CSS_FILE);
//			w.printf("<script src='%s' type='text/javascript'></script>\n", ALL_JS);
			w.printf("<title>%s</title>\n", title);
			w.printf("</head>\n");
			w.printf("<body>\n");
			w.printf("<div id='all'>\n");
//			w.printf("<p>");
			目次(node, w);
//			w.printf("</p>");
			w.printf("<div id='content'>\n");
			dict.区分内参照削除();
			write(node, node, w, 0, dict, "", dir);
			if (node.tuti() != null) {
				w.printf("<div id='tuti'>\n");
				write(node.tuti(), node, w, 0, dict, "t", dir);
				w.printf("</div>\n");
			}
			w.printf("</div>\n");
			writeShare(w, title, url);
			w.printf("</div>\n");
			makeMenu(w);
			w.printf("</body>\n");
			w.printf("</html>\n");
		} finally {
			w.close();
		}
	}

	private static final Pattern 点数 = Pattern.compile("[0-9,.０-９，．]+点");

	private boolean hasInfo(Node child) {
		if (child.paragraphSize() > 0) return true;
		if (child.childrenSize() > 0) return true;
		if (child.tuti() != null) return true;
		if (点数.matcher(child.header()).find()) return true;
		return false;
	}

	private boolean writeLink(Node child, TextWriter w) {
		if (hasInfo(child)) {
			w.printf("<li class='m%d'><a href='%s.html'>%s　%s</a></li>\n",
				Node.indent(child.number()) + 2,
				child.fileName(), child.number(), child.simpleHeader());
			return true;
		} else {
			w.printf("<li class='m%d'>%s　%s</li>\n",
				Node.indent(child.number()) + 2,
				child.number(), child.simpleHeader());
			return false;
		}
	}

	private void その他(Node node, File dir, 索引 dict, int level)
			throws IOException {
		String fileName = node.fileName();
		TextWriter w = new TextWriter(new File(dir, fileName + ".html"), HTML_ENCODING);
		try {
			w.printf("%s\n", DOCTYPE);
			w.printf("<html>\n");
			w.printf("<head>\n");
			String title = String.format("%s %s - %s",
			    node.name(), node.header().replaceFirst("[ \t　].*$", ""), callback.共通タイトル());
			String url = String.format("%s/%s.html", callback.baseUrl(), fileName);
//			String title = node.header().replaceFirst("[ \t　].*$", "");
			writeMeta(w, title);
			w.printf("<link rel='stylesheet' type='text/css' href='%s' />\n", CSS_FILE);
//			w.printf("<script src='%s' type='text/javascript'></script>\n", ALL_JS);
			w.printf("<title>%s</title>\n", title);
			w.printf("</head>\n");
			w.printf("<body>\n");
			w.printf("<div id='all'>\n");
			目次(node, w);
			w.printf("<p>%s %s</p>\n", node.number(), node.simpleHeader());
			w.printf("<div id='content'>\n");
			w.printf("<ul>\n");
			for (Node child : node.children()) {
				if (child.nodeName().equals("区分")) {
					w.printf("<li>区分</li>\n");
					w.printf("<ul>\n");
					for (Node grand : child.children()) {
						if (writeLink(grand, w))
							write(grand, dir, dict, level + 1);
					}
					w.printf("</ul>\n");
				} else {
					if (writeLink(child, w))
						write(child, dir, dict, level + 1);
				}
			}
			w.printf("</ul>\n");
			w.printf("</div>\n");
			writeShare(w, title, url);
			w.printf("</div>\n");
			makeMenu(w);
			w.printf("</body>\n");
			w.printf("</html>\n");
		} finally {
			w.close();
		}
	}

	private void write(Node node, File dir, 索引 dict, int level)
			throws IOException {
		String name = node.nodeName();
		if (name.equals("区分番号"))
			区分番号(node, dir, dict);
		else if (name.equals("通則"))
			通則(node, dir, dict);
		else
			その他(node, dir, dict, level);
	}

	public static void writeIndex(File dir) throws IOException {
		File root = new File(dir, "0.html");
		if (!root.exists()) return;
        try (InputStream is = new FileInputStream(root);
            OutputStream os = new FileOutputStream(new File(dir, "index.html"))) {
            byte[] buffer = new byte[2048];
            int size;
            while (true) {
                size = is.read(buffer);
                if (size == -1) break;
                os.write(buffer, 0, size);
            }
        }
	}

	public void HTML出力(Document doc, File dir, 索引 dict, RendererOption callback) throws IOException {
		this.callback = callback;
		if (!dir.exists()) dir.mkdirs();
//		KubunPat = Pattern.compile(KubunRex);
		write(doc.root(), dir, dict, 0);
		writeIndex(dir);
//		サイトマップ作成(dir);
	}

//	public void HTML出力調剤(Document doc, File dir, 索引 dict, String baseUrl) throws IOException {
//		KubunRex = 区分番号_調剤告示.区分番号;
//		HTML出力(doc, dir, dict, baseUrl);
//	}

	private static class Pair {
		Node oldNode, newNode;
		Pair(Node oldNode, Node newNode) { this.oldNode = oldNode; this.newNode = newNode; }
		String name(Node node) {
			if (node == null) return null;
			return node.simpleHeader();
		}
		@Override
		public String toString() {
			String o = name(oldNode);
			String n = name(newNode);
			if (o != null) {
				if (n == null)
					return o + " →";
				else if (o.equals(n))
					return o;
				else
					return o + " → " + n;
			} else
				return " → " + n;
		}
	}

	private void 区分番号抽出(Node node, List<Node> list) {
		if (node.nodeName().equals("区分番号")) list.add(node);
		for (Node child : node.children())
			区分番号抽出(child, list);
	}

	private List<Node> 区分番号抽出(Document doc) {
		List<Node> list = new ArrayList<>();
		区分番号抽出(doc.root(), list);
		return list;
	}

	private TreeMap<String, Pair> map(List<Node> oldKubun, List<Node> newKubun) {
		TreeMap<String, Pair> map = new TreeMap<>();
		for (Node node : oldKubun)
			map.put(node.name(), new Pair(node, null));
		for (Node node : newKubun) {
			String key = node.name();
			Pair p = map.get(key);
			if (p == null)
				map.put(key, p = new Pair(null, node));
			else
				p.newNode = node;
		}
		for (Entry<String, Pair> p : map.entrySet())
			logger.info(p.toString());
		return map;
	}

	private void 区分一覧出力(String oldUrl, String newUrl, String kubun, String title, TextWriter w) throws IOException {
		String id = "";
		if (oldUrl != null || newUrl != null)
			id = String.format("id='%s'", kubun);
		w.printf("<p %s class='m%d'>", id, 6 + kubun.length() + 1);
		if (oldUrl != null)
			w.printf("<a href='../../%s.html'>旧</a>", oldUrl);
		else
			w.printf("－");
		w.printf(" ");
		if (newUrl != null)
			w.printf("<a href='../../%s.html'>新</a>", newUrl);
		else
			w.printf("－");
		w.printf(" ");
		if (oldUrl != null && newUrl != null) {
//			String t = oldUrl.replaceFirst("^.*/", "").replaceFirst("\\..*$", "");
			w.printf("<a href='../../%s?l=%s&r=%s'>比較</a>", HIKAKU_PAGE, oldUrl, newUrl);
		}
		else
			w.printf("－－");
		w.printf(" %s %s</p>\n", kubun, title);
	}

	private Node getNode(Node node) {
		if (node == null) return null;
		if (node.simpleHeader().equals("削除")) return null;
		if (node.paragraphSize() > 0) return node;
		if (node.childrenSize() > 0) return node;
		if (点数.matcher(node.header()).find()) return node;
		return null;
	}

	private void 区分一覧出力(Node root, TreeMap<String, Pair> map, String oldBaseUrl, TextWriter w) throws IOException {
		w.printf("%s\n", DOCTYPE);
		w.printf("<html>\n");
		w.printf("<head>\n");
		String title = String.format("区分番号一覧 - %s", callback.共通タイトル());
		String baseUrl = callback.baseUrl();
		String url = String.format("%s/kubun.html", baseUrl);
		writeMeta(w, title);
		w.printf("<link rel='stylesheet' type='text/css' href='%s' />\n", CSS_FILE);
//        w.printf("<script src='%s' type='text/javascript'></script>\n", ALL_JS);
		w.printf("<title>%s</title>\n", title);
		w.printf("</head>\n");
		w.printf("<body>\n");
		w.printf("<div id='all'>\n");
		目次(root, w);
		makeMenu(w);
		w.printf("<div id='content'>\n");
		String oldPrefix = oldBaseUrl.substring(oldBaseUrl.length() - 4) + "/";
		String newPrefix = baseUrl.substring(baseUrl.length() - 4) + "/";
		for (Entry<String, Pair> e : map.entrySet()) {
			String k = e.getKey();
			Pair p = e.getValue();
			Node o = getNode(p.oldNode);
			Node n = getNode(p.newNode);
			if (o != null)
				if (n != null)
					区分一覧出力(oldPrefix + o.fileName(), newPrefix + n.fileName(), k, n.simpleHeader(), w);
				else
					区分一覧出力(oldPrefix + o.fileName(), null, k, o.simpleHeader(), w);
			else
				if (n != null)
					区分一覧出力(null, newPrefix + n.fileName(), k, n.simpleHeader(), w);
				else {
					Node nn = p.newNode;
					if (nn == null) nn = p.oldNode;
					区分一覧出力(null, null, k, nn.simpleHeader(), w);
				}
		}
		w.printf("</div>\n");
		writeShare(w, title, url);
		w.printf("</div>\n");
		w.printf("</body>\n");
		w.printf("</html>\n");
	}

	private void 比較ページ出力(String oldBaseUrl, TextWriter w) throws IOException {
		w.printf("%s\n", DOCTYPE);
		w.printf("<html>\n");
		w.printf("<head>\n");
		w.printf("<meta charset='%s'>\n", HTML_ENCODING);
		w.printf("<meta name='viewport' content='initial-scale=1.0'>\n");
		String title = String.format("比較 - %s", callback.比較タイトル());
		w.printf("<meta name='description' content='%s'>\n", title);
		googleAnalytics(w);
		w.printf("<title>%s</title>\n", title);
		w.printf("<script type='text/javascript'>\n");
		w.printf("    function bodyonload() {\n");
		w.printf("        var kubun = window.location.search.substring(1);\n");
		w.printf("        document.getElementById('left').src = '%s/'+ kubun + '.html';\n", oldBaseUrl);
		w.printf("        document.getElementById('right').src = kubun + '.html';\n");
		w.printf("        document.title = kubun + ' ' + document.title;\n");
		w.printf("    };\n");
		w.printf("</script>\n");
		w.printf("<style type='text/css'>\n");
		w.printf("    html, body { height: 100%%; margin: 0px; padding: 0px; }\n");
		w.printf("    #content { height: 100%%; margin: 0px; padding: 0px; }\n");
		w.printf("    #left { float:left; margin: 0px; padding: 0px; }\n");
		w.printf("    #right { float:right; margin: 0px; padding: 0px; }\n");
		w.printf("</style>\n");
		w.printf("</head>\n");
		w.printf("<body onload='bodyonload()'>\n");
		w.printf("<div id='content'>\n");
		w.printf("<iframe id='left'\n");
		w.printf("    width='50%%' height='100%%'\n");
		w.printf("    border='0' frameborder='0'\n");
		w.printf("    hspace='0' vspace='0'\n");
		w.printf("    marginheight='0' marginwidth='0'\n");
		w.printf("    scrolling='auto' border='0' >iframe対応のブラウザが必要です</iframe>\n");
		w.printf("<iframe id='right'\n");
		w.printf("    width='50%%' height='100%%'\n");
		w.printf("    border='0' frameborder='0'\n");
		w.printf("    hspace='0' vspace='0'\n");
		w.printf("    marginheight='0' marginwidth='0'\n");
		w.printf("    scrolling='auto' border='0' >iframe対応のブラウザが必要です</iframe>\n");
		w.printf("</div>\n");
//		writeShare(w);
		w.printf("</body>\n");
		w.printf("</html>\n");
	}

	public void 区分一覧出力(Document oldDoc, Document newDoc, File outFile, String oldBaseUrl, RendererOption callback) throws IOException {
		this.callback = callback;
		List<Node> oldKubun = 区分番号抽出(oldDoc);
		List<Node> newKubun = 区分番号抽出(newDoc);
		TreeMap<String, Pair> map = map(oldKubun, newKubun);
		TextWriter w = new TextWriter(outFile, HTML_ENCODING);
		try {
			区分一覧出力(newDoc.root().children(0), map, oldBaseUrl, w);
		} finally {
			w.close();
		}
		TextWriter x = new TextWriter(new File(outFile.getParentFile(), HIKAKU_PAGE), HTML_ENCODING);
		try {
			比較ページ出力(oldBaseUrl, x);
		} finally {
			x.close();
		}
	}
}
