package jp.pgw.kosoado.utils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Yaml操作ユーティリティ
 */
public class YamlUtil {
	
	/**
	 * yamlファイルから文字列を取得する
	 * 
	 */
	public static String getYamlString(FileConfiguration warpYaml, String keyName) {
		return warpYaml.getString(keyName);
	}
	
	/**
	 * yamlファイルからDouble型の数値を取得する
	 */
	public static double getYamlDouble(FileConfiguration warpYaml, String keyName) {	
			return warpYaml.getDouble(keyName);
	}
	
	
	/**
	 * yamlファイルからワープ名一覧を取得する
	 */
	public static Set<String> getWarpNames(FileConfiguration warpYaml) {
		return warpYaml.getKeys(false);
	}
	
	
	/**
	 * yamlファイルがないとき、作成する<br>
	 * yamlのFileオブジェクトを生成して返す
	 */
	public static File createYaml(File dataFolder) {
		
		File yaml = new File(dataFolder, "warps.yml");
		
		if(!yaml.exists()) {
    		try {
    			yaml.createNewFile();
    			
    		}catch(IOException e) {
    			e.printStackTrace();
    		}
    	}
		
		return yaml;
		
	}
}