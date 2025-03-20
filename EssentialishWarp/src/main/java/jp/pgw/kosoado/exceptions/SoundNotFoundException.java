package jp.pgw.kosoado.exceptions;


/**
 * サウンドが一覧(enum)から見つからなかったときにスローされる
 */
public class SoundNotFoundException extends Exception {
	
	/**
	 * コンストラクタ。
	 */
	public SoundNotFoundException() {
		super("Sound is not found.");
	}
}