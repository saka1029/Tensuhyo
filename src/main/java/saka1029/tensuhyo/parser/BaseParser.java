package saka1029.tensuhyo.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BaseParser implements Parser {

	private static final String UTF8_BOM = "\ufeff";
	private List<Stencil> stencils = new ArrayList<>();
	private BufferedReader reader;
	private int lineNo;
	
	protected int lineNo() { return lineNo; }
	
	protected String read() throws IOException {
		while (true) {
			String line = reader.readLine();
			// UTF-8のBOMがあればスキップします。
			if (lineNo == 0 && line.startsWith(UTF8_BOM))
				line = line.substring(1);
			if (line != null) {
				++lineNo;
				if (line.startsWith("#")) continue;
			}
			return line;
		}
	}

	protected void add(Stencil stencil) {
		stencils.add(stencil);
	}

	@Override
	public Document parse(BufferedReader reader, String rootName)
			throws IOException, ParseException {
		this.reader = reader;
		this.lineNo = 0;
		Node prev = StencilRoot.value.match(rootName, -1);
		Document document = new Document(prev);
		while (true) {
			String line = read();
			if (line == null) break;
			Node node = null;
			Node parent = null;
			for (Stencil stencil : stencils) {
				node = stencil.match(line, lineNo);
				if (node != null) {
					parent = stencil.getParent(node, prev);
					if (parent != null)
						break;
				}
			}
			if (node == null || parent == null)
				throw new ParseException(
					"マッチするステンシルが見つかりません", line, lineNo, prev);
			prev = node.stencil().addNode(node, parent);
		}
		return document;
	}
	
	@Override
	public Document parse(String text, String rootName) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		try {
			return parse(reader, rootName);
		} finally {
			reader.close();
		}
	}

}
