package saka1029.tensuhyo.util;

/**
 * 読み取り専用の文字列の配列です。
 *
 */
public interface Fields extends Iterable<String> {

	int size();
	
	String get(int index);
	
}
