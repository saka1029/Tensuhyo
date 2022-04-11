package saka1029.tensuhyo.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import saka1029.tensuhyo.parser.Document;
import saka1029.tensuhyo.parser.Node;
import saka1029.tensuhyo.util.StringConverter;
import saka1029.tensuhyo.util.StringEditor;
import saka1029.tensuhyo.util.TextWriter;

public class 施設基準Renderer {

	private static final Logger logger = Logger.getLogger(Renderer.class.getName());

    // private static final int MAX_HEADER_SIZE = 10;

    private 施設基準RendererCallback callback;

    private void breadCrumb(Node node, TextWriter w) {
        if (node == null) {
            w.printf("<a href='../../index.html'>診療報酬点数表</a>%n");
            return;
        }
        breadCrumb(node.parent(), w);
        if (node.nodeName().equals("施設基準等"))
            w.printf("");
        else if (node.nodeName().equals("施設基準別表"))
            w.printf("");
        else
            w.printf("&gt; <a href='%s.html'>%s %s</a>%n", node.fileName(), node.number(), node.header());
    }

    private void header(Node node, TextWriter w) {
        if (node == null)
            return;
        w.printf("<div id='breadcrumb'>%n");
        w.printf("<b>%n");
        breadCrumb(node.parent(), w);
        w.printf("</b>%n");
        if (callback != null)
            callback.index(node, w);
        w.printf("</div>%n");
    }

    private void index(Node node, TextWriter w) throws IOException {
        String name = node.nodeName();
        if (name.equals("第漢数字") || name.equals("別表第漢数字")) {
            // String spaces = Renderer.spaces(MAX_HEADER_SIZE -
            // node.number().length() + 2);
            // int indent = (MAX_HEADER_SIZE + 2) * 2;
            int indent = node.number().length() * 2 + 5;
            w.printf("<p id='%s' class='m%d'><a href='%s.html'>　　%s %s</a></p>%n", node.fileName(), indent,
                node.fileName(), node.number(), node.header());
        } else if (name.equals("施設基準等")) {
            String id = node.number().contains("基本診療料") ? "1" : "2";
            w.printf("<p id='%s' class='m2'>　%s</p>%n", id, node.number());
            // w.printf("<ul>%n");
            for (Node child : node.children())
                index(child, w);
            // w.printf("</ul>%n");
        } else
            for (Node child : node.children())
                index(child, w);
    }

    private void index(Document doc, TextWriter w) throws IOException {
        String url = String.format("%s/0.html", callback.baseUrl());
        w.printf("%s%n", Renderer.DOCTYPE);
        w.printf("<html>%n");
        w.printf("<head>%n");
        String title = String.format("施設基準目次 - %s", callback.共通タイトル());
        Renderer.writeMeta(w, title);
        w.printf("<link rel='stylesheet' type='text/css' href='%s' />%n", Renderer.CSS_FILE);
        // w.printf("<script src='%s' type='text/javascript'></script>%n",
        // Renderer.ALL_JS);
        w.printf("<title>%s</title>%n", title);
        w.printf("</head>%n");
        w.printf("<body>%n");
        w.printf("<div id='all'>%n");
        header(doc.root(), w);
        w.printf("<p>%s</p>%n", callback.共通タイトル());
        w.printf("<div id='content'>%n");
        // w.printf("<ul>%n");
        index(doc.root(), w);
        // w.printf("</ul>%n");
        w.printf("</div>%n");
        Renderer.writeShare(w, title, url);
        w.printf("</div>%n");
        w.printf("</body>%n");
        w.printf("</html>%n");
    }

    private void index(Document doc, File dir) throws IOException {
        try (TextWriter w = new TextWriter(new File(dir, "0.html"))) {
            index(doc, w);
        }
    }

    private static String NUM = "[0-9０-９]+";
    private static Pattern YOSHIKI_PAT = Pattern.compile("別添(?<B>" + NUM + ")(の様式(?<M>" + NUM + ")"
        + "(の(?<N>" + NUM + ")(の(?<E>" + NUM + "))?)?"
        + ")?");
    private static String num(String s) {
        if (s == null) return "";
        return StringConverter.toNormalWidthANS(s);
    }

    private String imageLink(String s, Node node) {
        String repl = StringEditor.replace(s, YOSHIKI_PAT,
            m -> {
                String betten = num(m.group("B"));
                String yoshiki = num(m.group("M"));
                String eda = num(m.group("N"));
                String oi = num(m.group("E"));
                String f = "KIHON-BETTEN" + betten;
//                if (betten.equals("2") && yoshiki.equals("12"))
//                    System.out.println(node);
                if (!yoshiki.equals("")) f += "-BESI" + yoshiki;
                if (!eda.equals("")) f += "_" + eda;
                if (!oi.equals("")) f += "_" + oi;
                f += ".pdf";
                boolean exists = new File(callback.imageDir(), f).exists();
                if (!exists) {
                    f = f.replaceFirst("^KIHON-", "TOKKEI_");
                    exists = new File(callback.imageDir(), f).exists();
                }
                if (exists)
                    return String.format("<a href='image/%s'>%s</a>",
                        f, m.group(0));
                else {
                    logger.severe("imageLink : " + f + " " + node.fileName());
                    return null;
                }
            });
        return repl;
    }

    private void detailTuti(Node node, TextWriter w, int level, boolean top) throws IOException {
        if (top) w.printf("<p>[通知]</p>");
        String number = Renderer.spaces(level) + node.number() + "　";
        int indent = Node.indent(number);
        String para = imageLink(node.header() + node.paragrahText(), node);
        w.printf("<p id='%s' class='m%d'>%s%s</p>%n",
            node.fileName(), indent, number, para);
//            node.paragrahText());
        for (Node child : node.children())
            detailTuti(child, w, level + 1, false);
    }

    private void detail(Node node, TextWriter w, int level) throws IOException {
        String number = Renderer.spaces(level) + node.number() + "　";
        int indent = Node.indent(number);
        w.printf("<p id='%s' class='m%d'>%s%s%s</p>%n", node.fileName(), indent, number, node.header(),
            node.paragrahText());
        for (Node child : node.children())
            detail(child, w, level + 1);
        if (node.tuti() != null) {
            w.printf("<div id='tuti'>%n");
            detailTuti(node.tuti(), w, level, true);
            w.printf("</div>%n");
        }
    }

    private void body(Node node, TextWriter w) throws IOException {
        String url = String.format("%s/%s.html", callback.baseUrl(), node.fileName());
        w.printf("%s%n", Renderer.DOCTYPE);
        w.printf("<html>%n");
        w.printf("<head>%n");
        String title = String.format("%s %s - %s", node.number(), node.header(), callback.共通タイトル());
        Renderer.writeMeta(w, title);
        w.printf("<link rel='stylesheet' type='text/css' href='%s' />%n", Renderer.CSS_FILE);
        // w.printf("<script src='%s' type='text/javascript'></script>%n",
        // Renderer.ALL_JS);
        w.printf("<title>%s</title>%n", title);
        w.printf("</head>%n");
        w.printf("<body>%n");
        w.printf("<div id='all'>%n");
        header(node, w);
        String number = node.number() + "　";
        int indent = Node.indent(number);
        w.printf("<p class='m%d'>%s%s%s</p>%n", indent, number, node.header(), node.paragrahText());
        w.printf("<div id='content'>%n");
        w.printf("<ul>%n");
        for (Node child : node.children())
            detail(child, w, 0);
        w.printf("</ul>%n");
        w.printf("</div>%n");
        Renderer.writeShare(w, title, url);
        w.printf("</div>%n");
        w.printf("</body>%n");
        w.printf("</html>%n");
    }

    private void body(Node node, File dir) throws IOException {
        String name = node.nodeName();
        if (!name.equals("第漢数字") && !name.equals("別表第漢数字")) {
            for (Node child : node.children())
                body(child, dir);
            return;
        }
        try (TextWriter w = new TextWriter(new File(dir, node.fileName() + ".html"))) {
            body(node, w);
        }
    }
    
    private void tutiMap(Node node, Map<String, Node> tutiMap) {
        tutiMap.put(node.simpleHeader(), node);
        for (Node child : node.children())
            tutiMap(child, tutiMap);
    }
    
    private void merge(Node node, Map<String, Node> tutiMap) {
        String name = node.simpleHeader();
        if (name.endsWith("の施設基準")) {
            Node tuti = tutiMap.get(name);
            if (tuti == null) {
                name = name.replaceFirst("の施設基準$", "");
                tuti = tutiMap.get(name);
                if (tuti == null) {
                    name = name.replaceFirst("及び.*$", "");
                    tuti = tutiMap.get(name);
                }
            }
            node.tuti(tuti);
            // 子に通知がある場合はその祖先の通知は削除する。
            if (tuti != null)
                for (Node p = node.parent(); p != null; p = p.parent())
                    if (p.tuti() != null)
                        p.tuti(null);
        }
        for (Node child : node.children())
            merge(child, tutiMap);
    }

    public void HTML出力(Document kokuji, Document tuti, File dir, 施設基準RendererCallback callback) throws IOException {
        if (!dir.exists())
            dir.mkdirs();
        this.callback = callback;
        Map<String, Node> tutiMap = new HashMap<>();
        tutiMap(tuti.root(), tutiMap);
        merge(kokuji.root(), tutiMap);
        index(kokuji, dir);
        body(kokuji.root(), dir);
        Renderer.writeIndex(dir);
    }
}
