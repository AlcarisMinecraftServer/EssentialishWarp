package jp.pgw.kosoado;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import jp.pgw.kosoado.commands.DelWarp;
import jp.pgw.kosoado.commands.RenWarp;
import jp.pgw.kosoado.commands.SetWarp;
import jp.pgw.kosoado.commands.UpdWarp;
import jp.pgw.kosoado.commands.Warp;
import jp.pgw.kosoado.commands.WarpGroup;
import jp.pgw.kosoado.commands.WarpList;
import jp.pgw.kosoado.utils.YamlUtil;

public class EssentialishWarp extends JavaPlugin {
	
	/** ワープ一覧yamlファイル名 */
	public final String WARPLIST_YML = "warplist.yml";
	
	/** ワープ一覧yamlのFileオブジェクト */
	public final File WARPLIST_YAML_FILE;
	
	/** ワープ一覧yamlオブジェクト */
	private FileConfiguration warplistYaml;
	
	
	/**
	 * コンストラクタ。<br>
	 * 初期データを設定する。
	 */
	public EssentialishWarp() {
		
		// データフォルダがなければ作成
		File dataFolder = getDataFolder();
    	if(!dataFolder.exists()) {
    		dataFolder.mkdirs();
    	}
    	
    	File warplistYamlFile = null;
    	try {
    		warplistYamlFile = YamlUtil.createYaml(dataFolder, WARPLIST_YML);
		}
    	catch(IOException e) {
			getLogger().severe("ワープリストを生成できませんでした。");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}
    	finally {
    		WARPLIST_YAML_FILE = warplistYamlFile;
    	}
	}
	
	
    /**
     * 起動時処理
     */
    @Override
    public void onEnable() {
    	
    	// ワープ一覧生成に失敗した場合、プラグインを無効化する
    	if(WARPLIST_YAML_FILE == null) {
    		getLogger().severe("ワープ一覧を生成できませんでした。寝ます。");
			getServer().getPluginManager().disablePlugin(this);
			return;
    	}
 
    	loadYaml();
    	
    	// コマンドクラス
    	Warp warp = new Warp(this);
    	SetWarp setwarp = new SetWarp(this);
    	UpdWarp updwarp = new UpdWarp(this);
    	RenWarp renwarp = new RenWarp(this);
    	DelWarp delwarp = new DelWarp(this);
    	WarpList warplist = new WarpList(this);
    	WarpGroup warpgroup = new WarpGroup(this);
    	
    	// コマンド実行
    	getCommand("warp").setExecutor(warp);
    	getCommand("silwarp").setExecutor(warp);
    	getCommand("setwarp").setExecutor(setwarp);
    	getCommand("updwarp").setExecutor(updwarp); // TODO updwarp
    	getCommand("renwarp").setExecutor(renwarp); // TODO renwarp
    	getCommand("delwarp").setExecutor(delwarp);
    	getCommand("warplist").setExecutor(warplist);
    	getCommand("warpgroup").setExecutor(warpgroup); // TODO warpgroup
    	getCommand("relwarp").setExecutor(warplist);
    	
    	// タブコンプリート
    	getCommand("warp").setTabCompleter(warp);
    	getCommand("silwarp").setTabCompleter(warp);
    	getCommand("setwarp").setTabCompleter(setwarp);
    	// getCommand("updwarp").setTabCompleter(updwarp);
    	// getCommand("renwarp").setTabCompleter(renwarp);
    	getCommand("delwarp").setTabCompleter(delwarp);
    	getCommand("warplist").setTabCompleter(warplist);
    	// getCommand("warpgroup").setTabCompleter(warpgroup);

         
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
     * warplist.ymlを読み込む
     */
    private void loadYaml() {
    	this.warplistYaml = YamlConfiguration.loadConfiguration(WARPLIST_YAML_FILE);
    	getLogger().info("ワープ一覧をロードしました。");
    }
    
    /**
     * warplist yamlを取得する
     */
    public FileConfiguration getWarplistYaml() {
    	return this.warplistYaml;
    }
    
    /**
     * warplist yamlを設定する
     */
    public void setWarplistYaml(FileConfiguration warplistYaml) {
    	this.warplistYaml = warplistYaml;
    }
}