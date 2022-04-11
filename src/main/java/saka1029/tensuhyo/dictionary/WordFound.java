package saka1029.tensuhyo.dictionary;


/**
 * 検索結果の単語情報を保持するクラス。
 *
 * @param <T> 単語情報を格納するBeanの型を指定します。
 */
public class WordFound<T> {

	private T bean;
	private int length;
	
	/**
	 * 出現した単語の長さを取得します。
	 * 長さは文字列上での長さです。
	 * 単語の長さとは必ずしも一致しないことがあります。
	 * @return
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * 単語情報を取得します。
	 * @return
	 */
	public T getBean() {
		return bean;
	}

	/**
	 * 単語の出現位置を保持します。
	 */
	private int position;

	/**
	 * 単語の出現位置を取得します。
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 単語とその出現位置を指定してインスタンスを作成します。
	 * @param bean
	 * @param position
	 */
	public WordFound(T bean, int position, int length) {
		this.bean = bean;
		this.position = position;
		this.length = length;
	}
	
	@Override
	public String toString() {
		return "WordFound(" + position + ":" + length + ", " + bean + ")";
	}

}
