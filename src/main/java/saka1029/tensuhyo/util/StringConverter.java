/*
 * @(#)StringConverter.java	1.00 10/16/2002
 *
 * Copyright 2002 Kosuke Yamamoto, All rights reserved.
 * http://www.bystonwell.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package saka1029.tensuhyo.util;

/**
 * 文字列変換操作を行うクラスです。
 * 平仮名-カタカナの変換、および、英数記号カナの全角-半角の変換操作が可能です。
 *
 * @author  Kosuke Yamamoto
 * @version 1.00, 10/16/2002
 * @since   JDK1.2
 */
public class StringConverter {

	/** デフォルトコンストラクタ */
	private StringConverter() {
	}

	/** 半角カタカナテーブル */
	protected static final String NORMAL_WIDTH_KATAKANA_TABLE =
			"\uff61\uff62\uff63\uff64\uff65"	// 。「」、・
			+ "\uff67\uff68\uff69\uff6a\uff6b"	// ァィゥェォ
			+ "\uff6c\uff6d\uff6e\uff6f\uff70"	// ャュョッー
			+ "\uff71\uff72\uff73\uff74\uff75"	// アイウエオ
			+ "\uff76\uff77\uff78\uff79\uff7a"	// カキクケコ
			+ "\uff7b\uff7c\uff7d\uff7e\uff7f"	// サシスセソ
			+ "\uff80\uff81\uff82\uff83\uff84"	// タチツテト
			+ "\uff85\uff86\uff87\uff88\uff89"	// ナニヌネノ
			+ "\uff8a\uff8b\uff8c\uff8d\uff8e"	// ハヒフヘホ
			+ "\uff8f\uff90\uff91\uff92\uff93"	// マミムメモ
			+ "\uff94\uff95\uff96"				// ヤユヨ
			+ "\uff97\uff98\uff99\uff9a\uff9b"	// ラリルレロ
			+ "\uff9c\uff66\uff9d\uff9e\uff9f";	// ワヲン゛゜

	/** 濁音半角カタカナテーブル(濁音になりうる半角カタカナのテーブル) */
	protected static final String NORMAL_WIDTH_DAKUON_KATAKANA_TABLE =
			"\uff76\uff77\uff78\uff79\uff7a"	// カキクケコ(ガギグゲゴ)
			+ "\uff7b\uff7c\uff7d\uff7e\uff7f"	// サシスセソ(ザジズゼゾ)
			+ "\uff80\uff81\uff82\uff83\uff84"	// タチツテト(ダヂヅデド)
			+ "\uff8a\uff8b\uff8c\uff8d\uff8e"	// ハヒフヘホ(バビブベボ)
			+ "\uff73";							// ウ(ヴ)

	/** 半濁音半角カタカナテーブル(半濁音になりうる半角カタカナのテーブル) */
	protected static final String NORMAL_WIDTH_HANDAKUON_KATAKANA_TABLE =
			"\uff8a\uff8b\uff8c\uff8d\uff8e";	// ハヒフヘホ(パピプペポ)

	/** 全角カタカナテーブル */
	protected static final String EM_SIZE_KATAKANA_TABLE =
			"\u3002\u300c\u300d\u3001\u30fb"	// 。「」、・
			+ "\u30a1\u30a3\u30a5\u30a7\u30a9"	// ァィゥェォ
			+ "\u30e3\u30e5\u30e7\u30c3\u30fc"	// ャュョッー
			+ "\u30a2\u30a4\u30a6\u30a8\u30aa"	// アイウエオ
			+ "\u30ab\u30ad\u30af\u30b1\u30b3"	// カキクケコ
			+ "\u30b5\u30b7\u30b9\u30bb\u30bd"	// サシスセソ
			+ "\u30bf\u30c1\u30c4\u30c6\u30c8"	// タチツテト
			+ "\u30ca\u30cb\u30cc\u30cd\u30ce"	// ナニヌネノ
			+ "\u30cf\u30d2\u30d5\u30d8\u30db"	// ハヒフヘホ
			+ "\u30de\u30df\u30e0\u30e1\u30e2"	// マミムメモ
			+ "\u30e4\u30e6\u30e8"				// ヤユヨ
			+ "\u30e9\u30ea\u30eb\u30ec\u30ed"	// ラリルレロ
			+ "\u30ef\u30f2\u30f3\u309b\u309c";	// ワヲン゛゜

	/** 濁音全角カタカナテーブル */
	protected static final String EM_SIZE_DAKUON_KATAKANA_TABLE =
			"\u30ac\u30ae\u30b0\u30b2\u30b4"	// ガギグゲゴ
			+ "\u30b6\u30b8\u30ba\u30bc\u30be"	// ザジズゼゾ
			+ "\u30c0\u30c2\u30c5\u30c7\u30c9"	// ダヂヅデド
			+ "\u30d0\u30d3\u30d6\u30d9\u30dc"	// バビブベボ
			+ "\u30f4";							// ヴ

	/** 濁音全角カタカナテーブル */
	protected static final String EM_SIZE_HANDAKUON_KATAKANA_TABLE =
			"\u30d1\u30d4\u30d7\u30da\u30dd";	// パピプペポ

	/** 半角スペース */
	protected static final char NORMAL_WIDTH_SPACE = '\u0020';
	/** 全角スペース */
	protected static final char EM_SIZE_SPACE = '\u3000';
	/** 半角濁点 */
	protected static final char NORMAL_WIDTH_DAKUTEN = '\uff9e';
	/** 半角半濁点 */
	protected static final char NORMAL_WIDTH_HANDAKUTEN = '\uff9f';
	/** 全角濁点 */
	protected static final char EM_SIZE_DAKUTEN = '\u309b';
	/** 全角半濁点 */
	protected static final char EM_SIZE_HANDAKUTEN = '\u309c';

	/**
	 * 平仮名を全角カタカナに変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	平仮名が全角カタカナに変換された文字列
	 */
	public static String toKatakana(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isHiragana(code)) {
				// 平仮名のとき全角カタカナに変換
				ret.append((char)(code + 0x60));
			} else {
				// 平仮名以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 全角カタカナを平仮名に変換します。
	 * 但し次の文字は、平仮名に対応する文字がないので変換しません。
	 * <ul>
	 *   <li>ヴ ('\u30f4')</li>
	 *   <li>ヵ ('\u30f5')</li>
	 *   <li>ヶ ('\u30f6')</li>
	 * </ul>
	 *
	 * @param	str	変換対象文字列
	 * @return	全角カタカナが平仮名に変換された文字列
	 */
	public static String toHiragana(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isKatakana(code)
					&& (!(code == '\u30f4'
						|| code == '\u30f5'
						|| code == '\u30f6'))) {	// 'ヴ','ヵ','ヶ'は除く
				// 全角カタカナのとき平仮名に変換
				ret.append((char)(code - 0x60));
			} else {
				// 全角カタカナ以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 半角英数記号を全角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	半角英数記号が全角に変換された文字列
	 */
	public static String toEmSizeANS(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isNormalWidthANS(code)) {
				// 半角英数記号のとき全角に変換
				ret.append((char)(code + 0xfee0));
			} else {
				// 半角英数記号以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 全角英数記号を半角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	全角英数記号が半角に変換された文字列
	 */
	public static String toNormalWidthANS(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isEmSizeANS(code)) {
				// 全角英数記号のとき半角に変換
				ret.append((char)(code - 0xfee0));
			} else {
				// 全角英数記号以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 半角スペースを全角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	半角スペースが全角に変換された文字列
	 */
	public static String toEmSizeSpace(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isNormalWidthSpace(code)) {
				// 半角スペースのとき全角に変換
				ret.append(EM_SIZE_SPACE);
			} else {
				// 半角スペース以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 全角スペースを半角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	全角スペースが半角に変換された文字列
	 */
	public static String toNormalWidthSpace(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			if (isEmSizeSpace(code)) {
				// 全角スペースのとき半角に変換
				ret.append(NORMAL_WIDTH_SPACE);
			} else {
				// 全角スペース以外はそのまま
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 半角カタカナを全角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	半角カタカナが全角に変換された文字列
	 */
	public static String toEmSizeKatakana(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		boolean deleted = false;
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			// 全角カタカナに変換可能なら変換する
			if (isConvertibleEmSizeKatakana(code)) {

				// 半角濁点なら全角濁音に極力変換する
				if (code == NORMAL_WIDTH_DAKUTEN) {
					if (i > 0) {
						char before = str.charAt(i-1);
						int beforeIndex = NORMAL_WIDTH_DAKUON_KATAKANA_TABLE.indexOf(before);
						if (beforeIndex >= 0) {
							// 全角濁音に変換出来る場合は変換する(例: ガ -> ガ)
							ret.setCharAt(ret.length() - 1, '\0');
							deleted = true;
							ret.append(EM_SIZE_DAKUON_KATAKANA_TABLE.charAt(beforeIndex));
						} else {
							// 全角濁音に変換出来ない場合はそのまま全角濁点に変換する(例: ア゛ -> ア゛)
							ret.append(EM_SIZE_DAKUTEN);
						}
					} else {
						// (文法的にはありえないが)1文字目が半角濁点の場合はそのまま全角濁点に変換する
						ret.append(EM_SIZE_DAKUTEN);
					}
				}
				// 半角半濁点なら全角半濁音に極力変換する
				else if (code == NORMAL_WIDTH_HANDAKUTEN) {
					if (i > 0) {
						char before = str.charAt(i-1);
						int beforeIndex = NORMAL_WIDTH_HANDAKUON_KATAKANA_TABLE.indexOf(before);
						if (beforeIndex >= 0) {
							// 全角半濁音に変換出来る場合は変換する(例: パ -> パ)
							ret.setCharAt(ret.length() - 1, '\0');
							deleted = true;
							ret.append(EM_SIZE_HANDAKUON_KATAKANA_TABLE.charAt(beforeIndex));
						} else {
							// 全角半濁音に変換出来ない場合はそのまま全角半濁点に変換する(例: ア゜ -> ア゜)
							ret.append(EM_SIZE_HANDAKUTEN);
						}
					} else {
						// (文法的にはありえないが)1文字目が半角半濁点の場合はそのまま全角半濁点に変換する
						ret.append(EM_SIZE_HANDAKUTEN);
					}
				}
				// それ以外は全角カタカナに変換する
				else {
					int index = NORMAL_WIDTH_KATAKANA_TABLE.indexOf(code);
					ret.append(EM_SIZE_KATAKANA_TABLE.charAt(index));
				}

			// 変換不可能ならそのまま
			} else {
				ret.append(code);
			}
		}
		if (deleted) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0, s = ret.length(); i < s; ++i)
				if (ret.charAt(i) != '\0')
					sb.append(ret.charAt(i));
			return sb.toString();
		} else
			return ret.toString();
	}

	/**
	 * 全角カタカナを半角に変換します。
	 *
	 * @param	str	変換対象文字列
	 * @return	全角カタカナが半角に変換された文字列
	 */
	public static String toNormalWidthKatakana(String str) {

		// 変換対象文字列がnullの場合はnullを返す
		if (str == null) return null;

		StringBuffer ret = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char code = str.charAt(i);
			// 半角カタカナに変換可能なら変換する
			if (isConvertibleNormalWidthKatakana(code)) {
				if (EM_SIZE_KATAKANA_TABLE.indexOf(code) >= 0) {
					int index = EM_SIZE_KATAKANA_TABLE.indexOf(code);
					ret.append(NORMAL_WIDTH_KATAKANA_TABLE.charAt(index));
				}
				// 濁音は半角カタカナ＋半角濁点に変換する(例: ガ -> ガ)
				else if (EM_SIZE_DAKUON_KATAKANA_TABLE.indexOf(code) >= 0) {
					int index = EM_SIZE_DAKUON_KATAKANA_TABLE.indexOf(code);
					ret.append(NORMAL_WIDTH_DAKUON_KATAKANA_TABLE.charAt(index)).append(NORMAL_WIDTH_DAKUTEN);
				}
				// 半濁音は半角カタカナ＋半角半濁点に変換する(例: パ -> パ)
				else if (EM_SIZE_HANDAKUON_KATAKANA_TABLE.indexOf(code) >= 0) {
					int index = EM_SIZE_HANDAKUON_KATAKANA_TABLE.indexOf(code);
					ret.append(NORMAL_WIDTH_HANDAKUON_KATAKANA_TABLE.charAt(index)).append(NORMAL_WIDTH_HANDAKUTEN);
				} 
			// 変換不可能ならそのまま
			} else {
				ret.append(code);
			}
		}

		return ret.toString();
	}

	/**
	 * 平仮名か否かを判定します。
	 * 厳密にはUnicodeマップ上の0x3041〜0x3093の範囲に属しているか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	平仮名なら真
	 */
	protected static boolean isHiragana(char c) {
		if (('\u3041' <= c) && (c <= '\u3093')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 全角カタカナか否かを判定します。
	 * 厳密にはUnicodeマップ上の0x30a1〜0x30f6の範囲に属しているか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	全角カタカナなら真
	 */
	public static boolean isKatakana(char c) {
		if (('\u30a1' <= c) && (c <= '\u30f6')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 半角カタカナか否かを判定します。
	 * 厳密にはUnicodeマップ上の0xff61〜0xff9fの範囲に属しているか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	半角カタカナなら真
	 */
	protected static boolean isNormalWidthKatakana(char c) {
		if (('\uff61' <= c) && (c <= '\uff9f')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 半角英数記号か否かを判定します。
	 * 厳密にはUnicodeマップ上の0x21〜0x7eの範囲に属しているか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	半角英数記号なら真
	 */
	protected static boolean isNormalWidthANS(char c) {
		if (('\u0021' <= c) && (c <= '\u007e')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 全角英数記号か否かを判定します。
	 * 厳密にはUnicodeマップ上の0xff01〜0xff4eの範囲に属しているか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	全角英数記号なら真
	 */
	protected static boolean isEmSizeANS(char c) {
		if (('\uff01' <= c) && (c <= '\uff4e')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 半角スペースか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	半角スペースなら真
	 */
	protected static boolean isNormalWidthSpace(char c) {
		if (NORMAL_WIDTH_SPACE == c) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 全角スペースか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	全角スペースなら真
	 */
	protected static boolean isEmSizeSpace(char c) {
		if (EM_SIZE_SPACE == c) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 半角カタカナに変換可能な全角カタカナか否かを判定します。
	 * 厳密には全角テーブル(濁点、半濁点テーブル含む)にあるか否かを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	半角カタカナに変換可能な全角カタカナなら真
	 */
	protected static boolean isConvertibleNormalWidthKatakana(char c) {
		if ((EM_SIZE_KATAKANA_TABLE.indexOf(c) >= 0)
				|| (EM_SIZE_DAKUON_KATAKANA_TABLE.indexOf(c) >= 0)
				|| (EM_SIZE_HANDAKUON_KATAKANA_TABLE.indexOf(c) >= 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 全角カタカナに変換可能な半角カタカナか否かを判定します。
	 * 厳密には半角カタカナテーブルにあるかを判定します。
	 *
	 * @param	c	判定対象文字
	 * @return	全角カタカナに変換可能な半角カタカナなら真
	 */
	protected static boolean isConvertibleEmSizeKatakana(char c) {
		if (NORMAL_WIDTH_KATAKANA_TABLE.indexOf(c) >= 0) {
			return true;
		} else {
			return false;
		}
	}

}
