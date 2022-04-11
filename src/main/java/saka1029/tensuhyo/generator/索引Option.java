package saka1029.tensuhyo.generator;

public interface 索引Option {

	/**
	 * 区分番号のリンクとなる語の接頭語を指定します。
	 * 医科、歯科の場合は区分番号自体が英字で始まる数字３桁で
	 * 識別率が高いため""を返すべきです。
	 * 調剤は単なる数字２桁で誤認識する可能性が高いため
	 * "区分番号"を指定すべきです。
	 */
	String 区分番号接頭語();
	
	/**
	 * Node.fileName()を文章上の区分番号表記に変換します。
	 * 医科、歯科の場合はfileName()をそのまま返すべきです。
	 * 調剤の場合はfileName() == "14-2"を"14の2"に変換して返すべきです。
	 */
	String 区分番号表示(String fileName);
}
