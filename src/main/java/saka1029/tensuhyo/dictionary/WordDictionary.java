package saka1029.tensuhyo.dictionary;


import java.util.List;

/**
 * 単語の辞書です。
 * 157,000語の辞書を読みこんで、20,000回のencodeを実行したときの処理性能は以下の通りでした。
 * 
 * KakashiDictionary        : read=  2125ms encode= 63922ms total= 66047ms memory= 499167664
 * HashDictionary           : read=   969ms encode= 38656ms total= 39625ms memory= 497253320
 * ACDictionary             : read=  6687ms encode=   157ms total=  6844ms memory= 352253528
 * TrieDictionary           : read=  1359ms encode=   235ms total=  1594ms memory= 242401608
 * NormalizedDictionary     : read=  1407ms encode=   359ms total=  1766ms memory= 250641480
 * AhoCorasickDictionary    : read=  1953ms encode=   234ms total=  2187ms memory= 266427936
 * ACLongHashMapDictionary  : read=  2016ms encode=   156ms total=  2172ms memory= 259589800
 * 
 * JUnitテストクラスtest.saka1029.index.dict.TestDictPerformanceで計測しています。
 * NormalizedDictionaryはベースとしてTrieDictionaryを使用しています。
 * 
 * @param <T>　単語を格納するBean型を指定します。
 */
public interface WordDictionary<T> {

	WordAccessor<T> getWordAccessor();
	
	void setWordAccessor(WordAccessor<T> accessor);
	
	/**
	 * 辞書に用語を追加します。
	 * @param bean　追加する用語を含むBeanを指定します。
	 */
	void add(T bean);
	
	/**
	 * 追加された用語について内部のインデックスを初期化するメソッドです。
	 * 用語を追加した後、検索系のメソッドを呼び出す前に呼び出すメソッド必要があります。
	 * ただし、このメソッドが呼び出されない場合でも検索系のメソッドは正しく動作する必要があります。
	 */
	void prepare();
	
	/**
	 * 指定した文字列に含まれるすべての用語を検索します。
	 * @param s 検索対象文字列を指定します。
	 * @return 見つかった用語とその出現位置のリストを返します。
	 */
	List<WordFound<T>> encode(String s, String... types);
	
	void encode(String s, List<WordFound<T>> list, String... types);
	
	/**
	 * 指定文字列に一致する用語を検索します。
	 * @param s 検索文字列を指定します。
	 * @param max 検索する最大件数を指定します。
	 * ゼロ以下の値を指定した場合は全件検索します。
	 * @param types 検索対象となる用語の種類を指定します。
	 * 指定した値のいずれかに一致する用語を検索します。
	 * 何も指定しない場合はすべての用語が検索対象となります。
	 * @return
	 */
	List<T> find(String s, int max, String... types);
	
	/**
	 * 指定文字列で始まる用語を検索します。
	 * @param s 検索文字列を指定します。
	 * @param max 検索する最大件数を指定します。
	 * ゼロ以下の値を指定した場合は全件検索します。
	 * @param types 検索対象となる用語の種類を指定します。
	 * 指定した値のいずれかに一致する用語を検索します。
	 * 何も指定しない場合はすべての用語が検索対象となります。
	 * @return 見つかった用語のリストを返します。
	 */
	List<T> findStartsWith(String s, int max, String... types);
	
	/**
	 * 辞書に格納されたすべての用語をリストに出力します。
	 * @return
	 */
	List<T> toList();
	
	/**
	 * 指定した種類の用語をすべて削除します。
	 * @param types 削除する用語の種類を指定します。
	 * 指定した値のいずれかに一致する用語を削除します。
	 * 何も指定しない場合はすべての用語が削除対象となります。
	 */
	void removeType(String... types);
}
