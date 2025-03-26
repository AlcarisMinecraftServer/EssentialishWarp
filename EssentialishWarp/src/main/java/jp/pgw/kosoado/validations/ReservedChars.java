package jp.pgw.kosoado.validations;


/**
 * 予約文字を列挙
 */
public enum ReservedChars {
	
	// システムの予約文字
	CON("con"),
	PRN("prn"),
	AUX("aux"),
	NUL("nul"),
	COM1("com1"),
	COM2("com2"),
	COM3("com3"),
	COM4("com4"),
	COM5("com5"),
	COM6("com6"),
	COM7("com7"),
	COM8("com8"),
	COM9("com9"),
	LPT1("lpt1"),
	LPT2("lpt2"),
	LPT3("lpt3"),
	LPT4("lpt4"),
	LPT5("lpt5"),
	LPT6("lpt6"),
	LPT7("lpt7"),
	LPT8("lpt8"),
	LPT9("lpt9"),
	
	// プラグインの予約文字
	RELOAD("reload"),
	WARPLIST("warplist");
	
	
	
	/** 予約文字 */
	private final String reservedChars;
	
	
	/**
	 * コンストラクタ。
	 */
	private ReservedChars(String chars) {
		this.reservedChars = chars;
	}
	
	
	/**
	 * 予約文字かどうかチェックする<br>
	 * 含まれていれば、trueを返す
	 */
	public static boolean isReservedChar(String s) {
		
		for(ReservedChars rc : values()) {
			
			if(s.equals(rc.reservedChars)) {
				return true;
			}
		}
		return false;
	}
}