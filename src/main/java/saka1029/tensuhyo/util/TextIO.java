package saka1029.tensuhyo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class TextIO {
	
	private static final int BUFFER_SIZE = 4096;

	private TextIO() {
	}
	
	public static String ReadFrom(String path, String encoding)
			throws IOException {
		Reader reader = new InputStreamReader(
			new FileInputStream(path), encoding);
		try {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[BUFFER_SIZE];
			while (true) {
				int size = reader.read(buffer);
				if (size == -1) break;
				int start = 0;
				if (sb.length() == 0 && size > 0 && buffer[0] == '\ufeff') {
					start = 1;
					--size;
				}
				sb.append(buffer, start, size);
			}
			return sb.toString();
		} finally {
			reader.close();
		}
	}
	
	public static void WriteTo(String text, String path, String encoding)
			throws IOException {
		Writer writer = new OutputStreamWriter(
			new FileOutputStream(path), encoding);
		try {
			writer.write(text);
		} finally {
			writer.close();
		}
	}

}
