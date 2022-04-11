package saka1029.tensuhyo.dictionary;


import java.util.ArrayList;
import java.util.List;

/**
 * Dictインタフェースを実装する抽象クラスです。
 */
public abstract class WordDictionaryBase<T> implements WordDictionary<T> {

	private WordAccessor<T> wordAccessor;
	private boolean prepared = false;
	
	protected WordDictionaryBase(WordAccessor<T> keyAccessor) {
		this.wordAccessor = keyAccessor;
	}
	
	public String getType(T word) {
		return wordAccessor.getType(word);
	}
	
	public String getName(T word) {
		return wordAccessor.getName(word);
	}
	
	@Override
	public WordAccessor<T> getWordAccessor() {
		return wordAccessor;
	}

	@Override
	public void setWordAccessor(WordAccessor<T> wordAccessor) {
		this.wordAccessor = wordAccessor;
	}

	@Override
	public void prepare() {
		prepared = true;
	}
	
	public boolean isPrepared() {
		return prepared;
	}
	
	@Override
	public List<WordFound<T>> encode(String s, String... types) {
		if (s == null || s.length() < 1)
			throw new IllegalArgumentException("s");
		List<WordFound<T>> found = new ArrayList<>();
		encode(s, found, types);
		return found;
	}

	/**
	 * 用語の種類が一致するかどうかを調べます。
	 * @param word 調べる用語を指定します。
	 * @param types 用語種類を指定します。
	 * @return
	 * typesの中に一致するものがあればtrueを、それ以外の場合はfalseを返します。
	 * 用語種類をひとつも指定しなかった場合は常にtrueを返します。
	 */
	public boolean inType(T word, String... types) {
		if (types.length <= 0) return true;
		String type = getType(word);
		for (String e : types)
			if (type.equals(e))
				return true;
		return false;
	}

	@Override
	public void removeType(String... types) {
		throw new UnsupportedOperationException();
	}

}
