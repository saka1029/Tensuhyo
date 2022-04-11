package saka1029.tensuhyo.util;

import java.io.IOException;

/**
 * Fieldsの列を保持するオブジェクトからFieldsを読み出すインタフェースです。
 * Fieldsの列を保持するオブジェクトの例としては
 * CSVファイル、
 * RDBのテーブル、
 * Excelファイルのシートなどが考えられます。
 *
 */
public interface FieldsReader extends Cloneable {

	Fields readFields() throws IOException;
	
	public void close() throws IOException;

}
