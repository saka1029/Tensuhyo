package saka1029.tensuhyo.pdf;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gson.Gson;

import saka1029.tensuhyo.pdf.PDFBox.DebugElement;

public class TestTensuhyo {

	static final String TENSUHYO_DATA_DIR = "data/in/";
	static final String OUT_DIR = "test-data/tensuhyo";

	static final String[] PARAMS = {
//		"h3004.json",   平成30年度と令和元年度はPDF自体は同一なので除外する。
		"r0110.json",
		"r0204.json",
		"r0404.json"
	};

	static PrintWriter OUT = new PrintWriter(System.out, true, StandardCharsets.UTF_8);

	static class Param {
		String 元号, 年度;
		String[] 医科告示PDF, 医科通知PDF, 医科様式PDF;
		String[] 歯科告示PDF, 歯科通知PDF, 歯科様式PDF;
		String[] 調剤告示PDF, 調剤通知PDF, 調剤様式PDF;
		String[] 施設基準告示PDF, 施設基準通知PDF, 施設基準基本様式PDF, 施設基準特掲様式PDF;
	}

	static final CopyOption OW = StandardCopyOption.REPLACE_EXISTING;

	static Param param(String jsonFile) throws IOException {
		try (Reader reader = new FileReader(jsonFile)) {
			return new Gson().fromJson(reader, Param.class);
		}
	}
	
	static String[] pdfPath(String nendo, String tensuhyo, String[] names) {
		int length = names.length;
		String[] t = new String[length];
		for (int i = 0; i < length; ++i)
			t[i] = TENSUHYO_DATA_DIR + nendo + "/" + tensuhyo + "/pdf/" + names[i];
		return t;
	}

	static void copy(String[] srcs, Path dst) throws IOException {
		for (String src : srcs) {
			Path srcPath = Path.of(src);
			Files.copy(srcPath, dst.resolve(srcPath.getFileName()), OW);
		}
	}

	static void copyOldPdf() throws IOException {
		for (String paramFile : PARAMS) {
			Param param = param(paramFile);
			String n = param.年度;
			Path dst = Path.of(OUT_DIR, "pdf");
			Files.createDirectories(dst);
			copy(pdfPath(n, "i", param.医科告示PDF), dst);
			copy(pdfPath(n, "i", param.医科通知PDF), dst);
			copy(pdfPath(n, "s", param.歯科告示PDF), dst);
			copy(pdfPath(n, "s", param.歯科通知PDF), dst);
			copy(pdfPath(n, "t", param.調剤告示PDF), dst);
			copy(pdfPath(n, "t", param.調剤通知PDF), dst);
			copy(pdfPath(n, "k", param.施設基準告示PDF), dst);
			copy(pdfPath(n, "k", param.施設基準通知PDF), dst);
		}
	}

	static void copyOldTxt() throws IOException {
		for (String paramFile : PARAMS) {
			Param param = param(paramFile);
			String n = param.年度;
			Path src = Path.of(TENSUHYO_DATA_DIR, n);
			Path dst = Path.of(OUT_DIR, "old");
			Files.createDirectories(dst);
			Files.copy(src.resolve("i/txt/kokuji.txt"), dst.resolve(n + "-i-kokuji.txt"), OW);
			Files.copy(src.resolve("i/txt/tuti.txt"), dst.resolve(n + "-i-tuti.txt"), OW);
			Files.copy(src.resolve("s/txt/kokuji.txt"), dst.resolve(n + "-s-kokuji.txt"), OW);
			Files.copy(src.resolve("s/txt/tuti.txt"), dst.resolve(n + "-s-tuti.txt"), OW);
			Files.copy(src.resolve("t/txt/kokuji.txt"), dst.resolve(n + "-t-kokuji.txt"), OW);
			Files.copy(src.resolve("t/txt/tuti.txt"), dst.resolve(n + "-t-tuti.txt"), OW);
			Files.copy(src.resolve("k/txt/kokuji.txt"), dst.resolve(n + "-k-kokuji.txt"), OW);
			Files.copy(src.resolve("k/txt/tuti.txt"), dst.resolve(n + "-k-tuti.txt"), OW);
		}
	}
	
	static final Map<String, Set<Integer>> DEBUG_ELEMENTS_MAP = Map.of(
		"0000196314.pdf", Set.of(10),
		"0000196291.pdf", Set.of(4)
	);

	static final DebugElement DEBUG_ELEMENTS = (path, p, l, attr, elements) -> {
		Set<Integer> pages = DEBUG_ELEMENTS_MAP.get(Path.of(path).getFileName().toString());
		if (pages == null || !pages.contains(p))
			return;
		OUT.printf("%d:%d:%s%n", p, l, elements);
	};

	static void copyNewTxt(boolean horizontal, String outTxtFile, String... inPdfFiles) throws IOException {
		PDFBox pdfBox = new PDFBox(horizontal);
		pdfBox.debugElement = DEBUG_ELEMENTS;
		pdfBox.テキスト変換(outTxtFile, inPdfFiles);
	}

	static void copyNewTxt() throws IOException {
		for (String paramFile : PARAMS) {
			Param param = param(paramFile);
			String n = param.年度;
			Path dst = Path.of(OUT_DIR, "new");
			Files.createDirectories(dst);
			copyNewTxt(true, dst.resolve(n + "-i-kokuji.txt").toString(), pdfPath(n, "i", param.医科告示PDF));
			copyNewTxt(true, dst.resolve(n + "-i-tuti.txt").toString(), pdfPath(n, "i", param.医科通知PDF));
			copyNewTxt(true, dst.resolve(n + "-s-kokuji.txt").toString(), pdfPath(n, "s", param.歯科告示PDF));
			copyNewTxt(true, dst.resolve(n + "-s-tuti.txt").toString(), pdfPath(n, "s", param.歯科通知PDF));
			copyNewTxt(true, dst.resolve(n + "-t-kokuji.txt").toString(), pdfPath(n, "t", param.調剤告示PDF));
			copyNewTxt(true, dst.resolve(n + "-t-tuti.txt").toString(), pdfPath(n, "t", param.調剤通知PDF));
			copyNewTxt(false, dst.resolve(n + "-k-kokuji.txt").toString(), pdfPath(n, "k", param.施設基準告示PDF));
			copyNewTxt(true, dst.resolve(n + "-k-tuti.txt").toString(), pdfPath(n, "k", param.施設基準通知PDF));
		}
	}
	
	static void copyYoshikiIchiran() throws IOException {
		for (String paramFile : PARAMS) {
			Param param = param(paramFile);
			String n = param.年度;
			Path dst = Path.of(OUT_DIR, "yoshiki");
			Files.createDirectories(dst);
			new PDFBox(true).様式一覧変換(dst.resolve(n + "-i.txt").toString(), pdfPath(n, "i", param.医科様式PDF));
			new PDFBox(true).様式一覧変換(dst.resolve(n + "-s.txt").toString(), pdfPath(n, "s", param.歯科様式PDF));
			new PDFBox(true).様式一覧変換(dst.resolve(n + "-t.txt").toString(), pdfPath(n, "t", param.調剤様式PDF));
			new PDFBox(true).様式一覧変換(dst.resolve(n + "-k-kihon.txt").toString(), pdfPath(n, "k", param.施設基準基本様式PDF));
			new PDFBox(true).様式一覧変換(dst.resolve(n + "-k-tokkei.txt").toString(), pdfPath(n, "k", param.施設基準特掲様式PDF));
		}
	}
	
	@Test
	public void testCopyOld() throws IOException {
		copyOldPdf();
		copyOldTxt();
	}
	
	@Test
	public void testCopyNew() throws IOException {
		copyNewTxt();
	}
	
	@Test
	public void testCopyYoshikiIchiran() throws IOException {
	    copyYoshikiIchiran();
	}
}
