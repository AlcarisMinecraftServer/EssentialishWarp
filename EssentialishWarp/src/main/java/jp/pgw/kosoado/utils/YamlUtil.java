package jp.pgw.kosoado.utils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Yaml操作ユーティリティ
 */
public class YamlUtil {
	
	/**
	 * yamlファイルから文字列を取得する<br>
	 * 文字列が取得できない場合、nullが返却される
	 * 
	 */
	public static String getYamlString(ConfigurationSection warpYaml, String keyName) {
		return warpYaml.getString(keyName);
	}
	
	/**
	 * yamlファイルからDouble型の数値を取得する<br>
	 * 数値が取得できない場合、0が返却される
	 */
	public static double getYamlDouble(ConfigurationSection warpYaml, String keyName) {
			return warpYaml.getDouble(keyName);
	}
	
	
	/**
	 * yamlファイルからワープ名一覧を取得する
	 */
	public static Set<String> getWarpNames(ConfigurationSection warpYaml) {
		return warpYaml.getKeys(false);
	}
	
	
	/**
	 * グループフォルダがなければ、作成する<br>
	 * yamlファイルがないとき、作成する<br>
	 * yamlのFileオブジェクトを生成して返す
	 * @param dataFolder プラグインデータフォルダのFileオブジェクト
	 * @param fileName 作成するファイル名(ワープ名)
	 * 
	 * @throws IOException 呼び出し側でエラーメッセージを出力する
	 */
	public static File createYaml(File dataFolder, String fileName) throws IOException {

		if(fileName.contains("/")) {
			String group = fileName.split("/")[0];
			File groupDir = new File(dataFolder, group);
			if(!groupDir.exists()) {
				groupDir.mkdir();
			}
		}
		
		File yaml = new File(dataFolder, fileName);
		
		if(!yaml.exists()) {
    		try {
    			yaml.createNewFile();
    			
    		}catch(IOException e) {
    			// 呼び出し側でエラーメッセージを出す
    			throw e;
    		}
    	}
		
		return yaml;
		
	}
}