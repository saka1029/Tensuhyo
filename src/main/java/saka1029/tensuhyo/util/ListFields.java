package saka1029.tensuhyo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 要素を追加できるFieldsの実装です。
 *
 */
public class ListFields implements Fields {

	private List<String> list = new ArrayList<String>();
	
	public ListFields() {
	}
	
	public ListFields(int size) {
		this();
		for (int i = 0; i < size; ++i)
			this.add("");
	}
	
	public String get(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}

	public Iterator<String> iterator() {
		return list.iterator();
	}
	
	public void add(String element) {
		list.add(element);
	}
	
	public void set(int index, String element) {
		list.set(index, element);
	}
	
	public void clear() {
		list.clear();
	}

}
