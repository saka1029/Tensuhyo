package saka1029.tensuhyo.parser;

import java.io.BufferedReader;
import java.io.IOException;

public interface Parser {

	public Document parse(BufferedReader reader, String rootName)
			throws IOException, ParseException;

	public Document parse(String text, String rootName)
			throws IOException, ParseException;
	
}
