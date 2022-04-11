package saka1029.tensuhyo.dictionary;


/**
 * 用語にアクセスするためのインタフェースです。
 *
 * @param <V>　用語の型を指定します。
 */
public interface WordAccessor<T> {
	
	/**
	 * 用語の種類を取得します。
	 * @param bean
	 * @return
	 */
	String getType(T bean);
	
	/**
	 * 用語の文字列を取得します。
	 * @param bean
	 * @return
	 */
	String getName(T bean);
	

}
