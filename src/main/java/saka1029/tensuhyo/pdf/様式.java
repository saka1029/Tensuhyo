package saka1029.tensuhyo.pdf;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〇施設基準の様式に関わる文書構成(令和4年度)
 * <p>
 * 
 * <pre>
 * 施設基準告示
 *      基本診療料(000907989.pdf)
 *          別添1 初・再診料の施設基準                          本文
 *          別添2 入院基本料等の施設基準                        本文
 *          別添3 入院基本料等加算の施設基準                    本文
 *          別添4 特定入院料の施設基準                          本文
 *          別添5 短期滞在手術等基本料の施設基準                本文
 *          別添6 診療等に要する書面等                          本文
 *              別紙1, 2, ...                                   診療等に要する様式
 *          別添7 基本診療料の施設基準等に係る届出書            届出書様式
 *          別添7の2 基本診療料の施設基準等に係る届出書         届出書様式
 *              様式1, 1の2, ...                                届出書様式
 *      特掲診療料(000907862.pdf)
 *          別添1 特掲診療料等の施設基準                        本文
 *          別添2 特掲診療料の施設基準に係る届出書              届出書様式
 *              様式1, 1の2, ...                                届出書様式
 *          別添2の2 特掲診療料の施設基準等に係る届出書         届出書様式
 * </pre>
 * 
 * 基本診療料の様式は別紙nと様式nの2種類がある。 特掲診療料の様式は様式nのみである。 別添nは基本的に本文の見出しである。
 * ただし、基本診療料の別添7および別添7の2、 特掲診療料の別添2および別添2の2はそれぞれ独立した様式である。
 * <p>
 * 〇PDFファイル名
 * <p>
 * 
 * <pre>
 * 別紙様式aのbのc → SYa_b_c.pdf
 * 別添aのbのc → Ta_b_c.pdf
 * 別紙aのbのc → Sa_b_c.pdf
 * 様式aのbのc → Ya_b_c.pdf
 * 別添6の別紙aのbのc → * T6Sa_b_c.pdf
 * 別添7の様式aのbのc → T7Ya_b_c.pdf
 * 別添2の様式aのbのc → T2Ya_b_c.pdf
 * </pre>
 */
public record 様式(String name, String id, int startPage, int endPage, String title) {


	static final int 様式名出現最大行 = 3;

	static String id(String name) {
		return name.replaceAll("\\s+", "").replaceAll("別添", "T").replaceAll("別紙", "S").replaceAll("様式", "Y")
				.replaceAll("の", "_");
	}

	static final Pattern 施設基準様式名パターン = Pattern.compile("\\s*[(（]?\\s*(" // group1
			+ "\\s*(?:別\\s*添|別\\s*紙|様\\s*式)" + "\\s*(?:(?:\\d|\\s+)+)" + "\\s*(?:の\\s*(?:(?:\\d|\\s+)+))?"
			+ "\\s*(?:の\\s*(?:(?:\\d|\\s+)+))?" + ")\\s*[)）]?(?:\\s+(.+))?"); // group2

	public static void 施設基準様式一覧変換(String outTxtFile, String... inPdfFiles) throws IOException {
		try (PrintWriter out = new PrintWriter(outTxtFile, StandardCharsets.UTF_8)) {
			for (String inPdfFile : inPdfFiles) {
				out.printf("#file %s\n", inPdfFile);
				List<List<String>> pageLines = new PDFBox(true).read(inPdfFile);
				String betten = "";
				int startPage = -1;
				String name = null, id = null, title = null;
				int i = 0;
				for (int pageCount = pageLines.size(); i < pageCount; ++i) {
					List<String> lines = pageLines.get(i);
					for (int j = 0, lineCount = Math.min(様式名出現最大行, lines.size()); j < lineCount; ++j) {
						String norm = Normalizer.normalize(lines.get(j), Form.NFKD);
						Matcher m = 施設基準様式名パターン.matcher(norm);
						if (m.matches()) {
							if (name != null)
								out.printf("%s,%s,%d,%d,%s\n", name, id, startPage, i, title);
							startPage = i + 1;
							name = m.group(1).replaceAll("\\s+", "");
							if (name.matches("別添\\d+"))
								betten = name;
							title = m.group(2);
							if (title == null && j + 1 < lines.size())
								title = lines.get(j + 1).replaceAll("\\s+", "");
							id = id(name.startsWith("別添") ? name : betten + name);
						}
					}
				}
				if (name != null)
					out.printf("%s,%s,%d,%d,%s\n", name, id, startPage, i, title);
			}
		}
	}
}
