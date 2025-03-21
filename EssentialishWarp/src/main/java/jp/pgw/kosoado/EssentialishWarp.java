package jp.pgw.kosoado;

import java.io.File;

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
	public final File WARPLIST_YAML_FILE = YamlUtil.createYaml(getDataFolder(), WARPLIST_YML);
	
	/** ワープ一覧yamlオブジェクト */
	private FileConfiguration warplistYaml;
	
    /**
     * 起動時処理
     */
    @Override
    public void onEnable() {
    	
    	// データフォルダがなければ作成
    	if(!getDataFolder().exists()) {
    		getDataFolder().mkdirs();
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
    	getCommand("updwarp").setExecutor(updwarp);
    	getCommand("renwarp").setExecutor(renwarp);
    	getCommand("delwarp").setExecutor(delwarp);
    	getCommand("warplist").setExecutor(warplist);
    	getCommand("warpgroup").setExecutor(warpgroup);
    	
    	// タブコンプリート
    	getCommand("warp").setTabCompleter(warp);
    	getCommand("silwarp").setTabCompleter(warp);
    	// getCommand("setwarp").setTabCompleter(setwarp);
    	// getCommand("updwarp").setTabCompleter(updwarp);
    	// getCommand("renwarp").setTabCompleter(renwarp);
    	// getCommand("delwarp").setTabCompleter(delwarp);
    	// getCommand("warplist").setTabCompleter(warplist);
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
     * ymlを読み込む<br>
     * ①commands.yml -> jar内デフォルト値(コマンド)<br>
     * ②warplist.yml -> ワープ一覧<br>
     * TODO
     */
    private void loadYaml() {
    	this.warplistYaml = YamlConfiguration.loadConfiguration(WARPLIST_YAML_FILE);
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