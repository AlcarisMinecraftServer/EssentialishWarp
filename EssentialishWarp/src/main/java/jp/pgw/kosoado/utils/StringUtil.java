package jp.pgw.kosoado.utils;


/**
 * 文字列に関するユーティリティ
 */
public class StringUtil {
	
	/**
	 * 値がnullか空文字か判定する
	 * @param s 文字列
	 */
	public static boolean isNullOrEmpty(String s) {
		
		if(s == null || s.isEmpty()) {
			return true;
		}
		return false;
	}
}