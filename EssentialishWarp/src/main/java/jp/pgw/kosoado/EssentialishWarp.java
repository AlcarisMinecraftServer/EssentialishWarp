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
    	
    	// コマンド
    	Warp warpCmd = new Warp(this);
    	getCommand("warp").setExecutor(warpCmd);
    	getCommand("silwarp").setExecutor(warpCmd);
    	getCommand("setwarp").setExecutor(new SetWarp(this));
    	getCommand("updwarp").setExecutor(new UpdWarp(this));
    	getCommand("renwarp").setExecutor(new RenWarp(this));
    	getCommand("delwarp").setExecutor(new DelWarp(this));
    	getCommand("warplist").setExecutor(new WarpList(this));
    	getCommand("warpgroup").setExecutor(new WarpGroup(this));

         
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