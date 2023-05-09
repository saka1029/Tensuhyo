package saka1029.tensuhyo.generator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import saka1029.tensuhyo.pdf.様式;
import saka1029.tensuhyo.util.TextWriter;

public class 様式一覧Renderer {

	public String 共通タイトル, ファイル名, baseUrl, 様式プレフィックス;

	private void ヘッダ(TextWriter w) {
		w.printf("<div id='breadcrumb'>\n");
		w.printf("<b>\n");
		w.printf("<a href='../../index.html'>診療報酬点数表</a>\n");
		w.printf("&gt; %s\n", 共通タイトル);
		w.printf("<div id='menu'></div>");
		w.printf("</b>\n");
		w.printf("</div>\n");
	}

	private void 本文(Collection<様式> list, TextWriter w) throws IOException {
		String url = String.format("%s/%s.html", baseUrl, ファイル名);
		w.printf("%s\n", Renderer.DOCTYPE);
		w.printf("<html>\n");
		w.printf("<head>\n");
		String title = 共通タイトル;
		Renderer.writeMeta(w, title);
		w.printf("<link rel='stylesheet' type='text/css' href='%s' />\n", Renderer.CSS_FILE);
//		w.printf("<script src='%s' type='text/javascript'></script>\n", Renderer.ALL_JS);
		w.printf("<title>%s</title>\n", title);
		w.printf("</head>\n");
		w.printf("<body>\n");
		w.printf("<div id='all'>\n");
		ヘッダ(w);
		w.printf("<div id='content'>\n");
		w.printf("<h1>%s</h1>\n", title);
		w.printf("<ul>\n");
		for (様式 e : list)
			w.printf("<li><a href='image/%s'>%s %s</a></li>\n", 様式プレフィックス + e.id(), e.name(), e.title());
		w.printf("</ul>\n");
		w.printf("</div>\n");
		Renderer.writeShare(w, title, url);
		w.printf("</div>\n");
		w.printf("<script type='text/javascript' src='../../js/menu.js'></script>\n");
		w.printf("</body>\n");
		w.printf("</html>\n");
	}

	public void HTML出力(Collection<様式> list, File dir, String 共通タイトル, String ファイル名, String baseUrl, String 様式プレフィックス) throws IOException {
		if (!dir.exists())
			dir.mkdirs();
		this.共通タイトル = 共通タイトル;
		this.ファイル名 = ファイル名;
		this.baseUrl = baseUrl;
		this.様式プレフィックス = 様式プレフィックス;
		try (TextWriter w = new TextWriter(new File(dir, ファイル名 + ".html"), Renderer.HTML_ENCODING)) {
			本文(list, w);
		}
	}
}
