package jp.pgw.kosoado;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jp.pgw.kosoado.commands.DelWarp;
import jp.pgw.kosoado.commands.RelWarp;
import jp.pgw.kosoado.commands.SetWarp;
import jp.pgw.kosoado.commands.Warp;
import jp.pgw.kosoado.utils.YamlUtil;

public class EssentialishWarp extends JavaPlugin {
	
	/** yamlのFileオブジェクト */
	private File yamlFile;
	
	/** yamlから読み込んだデータ */
	private FileConfiguration warpYaml;
	
    /**
     * 起動時処理
     */
    @Override
    public void onEnable() {
    	
    	// データフォルダがなければ作成
    	if(!getDataFolder().exists()) {
    		getDataFolder().mkdirs();
    	}
    	
    	loadYaml(warpYaml);
        
        //ワープコマンド
        getCommand("warp").setExecutor(new Warp(this));
        getCommand("setwarp").setExecutor(new SetWarp(this));
        getCommand("delwarp").setExecutor(new DelWarp(this));
        getCommand("relwarp").setExecutor(new RelWarp(this));
         
        getLogger().info("こんちくわーぷ(◎∀◎)");
    }

    /**
     * 終了時処理
     */
    @Override
    public void onDisable() {
        getLogger().info("おやすみかん( ˘ω˘ )");
    }
    
    /**
     * ymlを読み込む
     */
    private void loadYaml(FileConfiguration warpYaml) {
    	this.yamlFile = YamlUtil.createYaml(getDataFolder());
    	this.warpYaml = YamlConfiguration.loadConfiguration(yamlFile);
    }
    
    /**
     * warp yamlを取得する
     */
    public FileConfiguration getWarpYaml() {
    	return this.warpYaml;
    }
    
    /**
     * warp yamlを設定する
     */
    public void setWarpYaml(FileConfiguration warpYaml) {
    	this.warpYaml = warpYaml;
    }
    
    /**
     * yamlファイルオブジェクトを取得する
     */
    public File getYamlFile() {
    	return this.yamlFile;
    }
    
    /**
     * yamlファイルオブジェクトを設定する
     */
    public void setYamlFile(File yamlFile) {
    	this.yamlFile = yamlFile;
    }
}