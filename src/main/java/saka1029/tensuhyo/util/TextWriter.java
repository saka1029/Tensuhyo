package saka1029.tensuhyo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class TextWriter extends PrintWriter {

	public TextWriter(Writer out) {
		super(out, true);	// auto flush
	}
	
	public TextWriter(OutputStream os, String encoding)
		throws UnsupportedEncodingException {
		this(new OutputStreamWriter(os, encoding));
	}
	
	public TextWriter(String path)
		throws IOException {
		this(new FileWriter(path));
	}
	
	public TextWriter(String path, String encoding)
		throws UnsupportedEncodingException,
			   FileNotFoundException {
		this(new FileOutputStream(path), encoding);
	}
	
	public TextWriter(File file)
		throws FileNotFoundException {
		super(file);
	}
	
	public TextWriter(File file, String encoding)
		throws UnsupportedEncodingException, FileNotFoundException {
		this(new FileOutputStream(file), encoding);
	}
	
	@Override
	public String toString() {
		flush();
		return out.toString();
	}
	
}
