package jp.pgw.kosoado.validations;


/**
 * 禁則文字を列挙
 */
public enum ForbiddenChars {
	
	/** バックスラッシュ */
	BACK_SLASH("\\"),
	/** スラッシュ */
	SLASH("/"),
	/** コロン */
	CORON(":"),
	/** アスタリスク */
	ASTERISK("*"),
	/** びっくりマーク */
	EXCLAMATION("!"),
	/** はてなマーク */
	QUESTION("?"),
	/** シングルクォート */
	SINGLE_QUOTE("'"),
	/** ダブルクォート */
	DOUBLE_QUOTE("\""),
	/** バッククォート */
	BACK_QUOTE("`"),
	/** バーティカルバー */
	VERTICAL_BAR("|"),
	/** スペース */
	SPACE(" "),
	/** カンマ */
	COMMA(","),
	/** ピリオド */
	PERIOD("."),
	/** ハイフン */
	HYPHEN("-"),
	/** アンパサンド */
	AMPERSAND("&"),
	/** パーセント */
	PERCENT("%"),
	/** ハッシュマーク */
	HASH("#"),
	/** 左かっこ */
	LEFT_BRACKET("("),
	/** 右かっこ */
	RIGHT_BRACKET(")"),
	/** 左角かっこ */
	LEFT_SQUARE_BRACKET("["),
	/** 右角かっこ */
	RIGHT_SQUARE_BRACKET("]"),
	/** 左波かっこ */
	LEFT_CURLY_BRACKET("("),
	/** 右波かっこ */
	RIGHT_CURLY_BRACKET(")"),
	/** 左山かっこ */
	LEFT_ANGLE_BRACKET("<"),
	/** 大なり */
	RIGHT_ANGLE_BRACKET(">");
	
	
	/** 禁則文字 */
	private final String forbiddenChars;
	
	
	/**
	 * コンストラクタ。
	 */
	private ForbiddenChars(String chars) {
		this.forbiddenChars = chars;
	}
	
	
	/**
	 * 禁則文字が含まれているかチェックする<br>
	 * 含まれていれば、trueを返す
	 */
	public static boolean containsForbiddenChars(String s) {
		
		for(ForbiddenChars fc : values()) {
			
			if(s.contains(fc.forbiddenChars)) {
				return true;
			}
		}
		return false;
	}
}