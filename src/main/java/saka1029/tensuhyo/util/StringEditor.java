package saka1029.tensuhyo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringEditor {
	
	String replace(Matcher m);

	static String replace(String text, Pattern pattern, StringEditor editor) {
		Matcher m = pattern.matcher(text);
		int start = 0;
		StringBuilder newText = new StringBuilder();
		while (m.find()) {
			int mstart = m.start();
			int mend = m.end();
			newText.append(text.substring(start, mstart));
			String r = editor.replace(m);
			if (r != null)
				newText.append(r);
			else	// StringEditorがnullを返した場合は置換しません。
				newText.append(m.group());
			start = mend;
		}
		newText.append(text.substring(start));
		return newText.toString();
	}
	
	static String replace(String text, String pattern, StringEditor editor) {
		return replace(text, Pattern.compile(pattern), editor);
	}

}
