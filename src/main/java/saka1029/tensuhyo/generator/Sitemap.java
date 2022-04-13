package saka1029.tensuhyo.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;

import saka1029.tensuhyo.util.TextWriter;

public class Sitemap {

	// private static final String HIKAKU_PAGE = "hikaku.html";

	private static void create(File file, String url, TextWriter w) {
	    String name = file.getName();
		if (file.isDirectory()) {
			// WindowsとUnixで同じ順番にするため、ファイル名でソートする。
			File[] children = file.listFiles();
			Arrays.sort(children, Comparator.comparing(f -> f.getName()));
			for (File child : children)
				create(child, url + "/" + child.getName(), w);
		} else if (!name.equals("sitemap.xml")) {
			w.printf("<url><loc>%s</loc></url>\n", url);
			// 比較ページはsitemapに載せない
			// if (hikakuUrl != null && !name.equals(HIKAKU_PAGE) && name.endsWith(".html"))
            //     w.printf("<url><loc>%s?%s</loc></url>%n", hikakuUrl, name.replaceFirst("\\.html", ""));
		}
	}
	
	public static void create(File dir, String baseUrl) throws FileNotFoundException {
	    // String hikakuUrl = new File(dir, HIKAKU_PAGE).exists() ? baseUrl + "/" + HIKAKU_PAGE : null;
		try (TextWriter w = new TextWriter(new File(dir, "sitemap.xml"))) {
			w.printf("<?xml version='1.0' encoding='UTF-8'?>\n");
			w.printf("<urlset xmlns='http://www.sitemaps.org/schemas/sitemap/0.9'>\n");
			create(dir, baseUrl, w);
			w.printf("</urlset>\n");
		}
	}
	
}
