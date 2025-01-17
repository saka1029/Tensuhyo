package saka1029.tensuhyo.pdf;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;
import org.apache.pdfbox.util.TextPosition;

public class PDFBox {

    static final Logger LOGGER = Logger.getLogger(PDFBox.class.getName());
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    static final Charset 出力文字セット = StandardCharsets.UTF_8;
    static final String 改行文字 = "\n";

    /**
     * A4のポイントサイズ 横約8.27 × 縦約11.69 インチ 595.44 x 841.68 ポイント
     */
    public static final float PAGE_WIDTH = 596F, PAGE_HEIGHT = 842F;

    public final boolean horizontal;

    public float 行併合範囲割合 = 0.6F;
    public float ルビ割合 = 0.6F;
    public float 行高さ規定値 = 10F;
    public float 行間隔規定値 = 14F;
    public Pattern ルビパターン = Pattern.compile("\\p{IsHiragana}*");
    public Pattern ページ番号パターン = Pattern.compile("^\\s*\\S*\\s*-\\s*\\d+\\s*-\\s*$");
    public DebugElement debugElement = null;

    public PDFBox(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public record Element(float x, float y, float fontSize, String text) {
        @Override
        public String toString() {
            return "%sx%s:%s:%s".formatted(x, y, fontSize, text);
        }
    }

    public interface DebugElement {
        void element(String path, int pageNo, int lineNo, 文書属性 attr, TreeSet<Element> elements);
    }

    static final Comparator<Element> 行内ソート = Comparator.comparing(Element::x)
        .thenComparing(Comparator.comparing(Element::y).reversed());

    List<TreeMap<Float, List<Element>>> 行分割(List<List<Element>> fileElements) {
        List<TreeMap<Float, List<Element>>> result = new ArrayList<>();
        for (List<Element> pageElements : fileElements) {
            TreeMap<Float, List<Element>> lineElements = new TreeMap<>();
            result.add(lineElements);
            for (Element e : pageElements)
                lineElements.computeIfAbsent(e.y, k -> new ArrayList<>()).add(e);
        }
        return result;
    }

    public record 文書属性(boolean 横書き, float 左余白, float 行間隔, float 行高さ, float 行併合範囲, float ルビ高さ) {
    }

    文書属性 文書属性(List<TreeMap<Float, List<Element>>> fileLines) {
        float 左余白 = Float.MAX_VALUE;
        Map<Float, Integer> 行間隔度数分布 = new HashMap<>();
        Map<Float, Integer> 行高さ度数分布 = new HashMap<>();
        for (TreeMap<Float, List<Element>> pageElements : fileLines) {
            float prevY = Float.MIN_VALUE;
            for (Entry<Float, List<Element>> line : pageElements.entrySet()) {
                左余白 = Math.min(左余白, line.getValue().get(0).x);
                float y = line.getKey();
                if (prevY != Float.MIN_VALUE)
                    行間隔度数分布.compute(y - prevY, (k, v) -> v == null ? 1 : v + 1);
                prevY = y;
                for (Element e : line.getValue())
                    行高さ度数分布.compute(e.fontSize, (k, v) -> (v == null ? 0 : v) + e.text.length());
            }
        }
        if (左余白 == Float.MAX_VALUE)
            左余白 = 0;
        float 行間隔 = 行間隔度数分布.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey)
            .orElse(行間隔規定値);
        float 行高さ = 行高さ度数分布.entrySet().stream()
            .max(Entry.comparingByValue())
            .map(Entry::getKey)
            .orElse(行高さ規定値);
        float 行併合範囲 = 行間隔 * 行併合範囲割合;
        float ルビ高さ = 行高さ * ルビ割合;
        return new 文書属性(horizontal, 左余白, 行間隔, 行高さ, 行併合範囲, ルビ高さ);
    }

    float round(float value) {
        return Math.round(value);
    }

    void parse(PDDocument doc, List<List<Element>> fileElements, int pageNo) throws IOException {
        List<Element> pageElements = new ArrayList<>();
        fileElements.add(pageElements);
        new PDFTextStripper() {
            {
                setStartPage(pageNo);
                setEndPage(pageNo);
                getText(doc);
            }

            @Override
            protected void processTextPosition(TextPosition text) {
                if (text.getCharacter().isBlank())
                    return;
                Element element = new Element(
                    round(horizontal ? text.getX() : text.getY()),
                    round(horizontal ? text.getY() : PAGE_WIDTH - text.getX()),
                    round(text.getXScale()),
                    text.getCharacter());
                pageElements.add(element);
            }
        };
    }

    String toString(TreeSet<Element> line, float leftMargin, float charWidth) {
        StringBuilder sb = new StringBuilder();
        float halfWidth = charWidth / 2;
        float start = leftMargin;
        for (Element e : line) {
            int spaces = Math.round((e.x - start) / halfWidth);
            for (int i = 0; i < spaces; ++i)
                sb.append(" ");
            sb.append(e.text);
            start = e.x + (float) e.text.codePoints().mapToDouble(c -> c < 256 ? halfWidth : charWidth).sum();
        }
        return sb.toString();
    }

    void addLine(List<String> list, TreeSet<Element> sortedLine, String path, int pageNo, int lineNo, 文書属性 文書属性) {
        if (!sortedLine.isEmpty())
            list.add(toString(sortedLine, 文書属性.左余白, 文書属性.行高さ));
        if (debugElement != null)
            debugElement.element(path, pageNo, lineNo, 文書属性, sortedLine);
        sortedLine.clear();
    }

    public List<List<String>> read(String inPdfPath) throws IOException {
        List<List<String>> result = new ArrayList<>();
        PDDocument doc = PDDocument.load(inPdfPath);
        int numberOfPages = doc.getNumberOfPages();
        List<List<Element>> fileElements = new ArrayList<>();
        try (Closeable c = () -> doc.close()) {
            for (int i = 0; i < numberOfPages; ++i)
                parse(doc, fileElements, i + 1);
        }
        List<TreeMap<Float, List<Element>>> fileLines = 行分割(fileElements);
        文書属性 文書属性 = 文書属性(fileLines);
        LOGGER.info(inPdfPath + ":" + 文書属性);
        int pageNo = 0;
        for (TreeMap<Float, List<Element>> lines : fileLines) {
            ++pageNo;
            List<String> linesString = new ArrayList<>();
            result.add(linesString);
//			float y = Float.MIN_VALUE;
            TreeSet<Element> sortedLine = new TreeSet<>(行内ソート);
            int lineNo = 0;
            for (Entry<Float, List<Element>> line : lines.entrySet()) {
                List<Element> lineElements = line.getValue();
                if (lineElements.stream().allMatch(e -> e.fontSize <= 文書属性.ルビ高さ && ルビパターン.matcher(e.text).matches()))
                    continue;
//				if (y != Float.MIN_VALUE && line.getKey() > y + 文書属性.行併合範囲)
                if (!sortedLine.isEmpty()
                    && line.getKey() > sortedLine.stream().mapToDouble(Element::y).average().getAsDouble() + 文書属性.行併合範囲)
                    addLine(linesString, sortedLine, inPdfPath, pageNo, ++lineNo, 文書属性);
                sortedLine.addAll(lineElements);
            }
            addLine(linesString, sortedLine, inPdfPath, pageNo, ++lineNo, 文書属性);
        }
        return result;
    }

    public void テキスト変換(String outTxtFile, String... inPdfFiles) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter writer = new PrintWriter(new FileWriter(outTxtFile, 出力文字セット))) {
            for (String path : inPdfFiles) {
                List<List<String>> pages = read(path);
                for (int i = 0, pageSize = pages.size(); i < pageSize; ++i) {
                    writer.printf("# file: %s page: %d%s", Path.of(path).getFileName(), i + 1, 改行文字);
                    for (String line : pages.get(i))
                        writer.printf("%s%s", ページ番号パターン.matcher(line).replaceFirst("#$0"), 改行文字);
                }
            }
        }
    }

//    public int 様式名出現最大行 = 3;
//    public Pattern 様式IDパターン = Pattern.compile(
//        "\\s*[（(]?(" // group1:別紙様式3の4の5
//            + "\\s*(?:(?:別\\s*紙)?\\s*様\\s*式|別\\s*添|別\\s*紙)"
//            + "((?:\\s*\\d)+)" // group2:3
//            + "(?:\\s*の((?:\\s*\\d)+))?" // group3:4
//            + "(?:\\s*の((?:\\s*\\d)+))?" // group4:5
//            + ")\\s*[）)]?"
//            + "(?:\\s+(.*))?"); // group5:様式名
//
//    public void 様式一覧変換(String outFile, String... inFiles) throws IOException {
//        try (PrintWriter writer = new PrintWriter(outFile, 出力文字セット)) {
//            for (String inFile : inFiles) {
//                List<List<String>> pages = read(inFile);
//                writer.printf("#file %s%s", inFile, 改行文字);
//                String name = null, id = null, title = null;
//                int startPage = -1;
//                int i = 0;
//                for (int pageSize = pages.size(); i < pageSize; ++i) {
//                    List<String> page = pages.get(i);
//                    for (int j = 0, maxLine = Math.min(様式名出現最大行, page.size()); j < maxLine; ++j) {
//                        String line = page.get(j);
//                        OUT.printf("%d:%d:%s%n", i + 1, j + 1, line);
//                        String normalLine = Normalizer.normalize(line, Form.NFKD);
//                        Matcher m = 様式IDパターン.matcher(normalLine);
//                        if (m.matches()) {
//                            if (name != null)
//                                writer.printf("%s,%s,%d,%d,%s%s", name, id, startPage, i, title, 改行文字);
//                            name = m.group(1).replaceAll("\\s+", "");
//                            id = m.group(2);
//                            startPage = i + 1;
//                            for (int k = 3; k <= 5 && m.group(k) != null; ++k)
//                                id += "_" + m.group(k);
//                            id = id.replaceAll("\\s+", "");
//                            title = m.group(5);
//                            if (title == null && j + 1 < page.size())
//                                title = page.get(j + 1);
//                            title = title.replaceAll("\\s+", "");
//                        }
//                    }
//                }
//                if (name != null)
//                    writer.printf("%s,%s,%d,%d,%s%s", name, id, startPage, i, title, 改行文字);
//            }
//        }
//    }
    
    public static List<様式> ページ分割(String inTextFile, String outDir) throws IOException, COSVisitorException {
        Files.createDirectories(Path.of(outDir));
        List<様式> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(inTextFile), 出力文字セット)) {
            PDDocument doc = null;
            String line, curFile = null;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#"))
                    continue;
                    String[] fields = line.split(",", 6);
                    String file = fields[0];
                    String name = fields[1];
                    String id = fields[2];
                    String outFile = /* outPdfPrefix + */ id + ".pdf";
                    String title = fields[5];
                    int startPage = Integer.parseInt(fields[3]);
                    int endPage = Integer.parseInt(fields[4]);
                    result.add(new 様式(file, name, id, startPage, endPage, title));
                    if (!file.equals(curFile)) {
                        if (doc != null)
                            doc.close();
                        doc = PDDocument.load(file);
                        curFile = file;
                    }
                    Splitter splitter = new Splitter();
                    splitter.setStartPage(startPage);
                    splitter.setEndPage(endPage);
                    splitter.setSplitAtPage(doc.getNumberOfPages());
                    List<PDDocument> splitted = splitter.split(doc);
                    splitted.get(0).save(outDir + "/" + outFile);
                    for (PDDocument d : splitted)
                        d.close();
            }
            if (doc != null)
                doc.close();
        }
        return result;
    }
}
