package jp.pgw.kosoado.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.pgw.kosoado.EssentialishWarp;

/**
 * relwarpコマンド<br>
 * warplist.yamlファイルをリロードする<br>
 */
public class RelWarp extends EWCommand implements CommandExecutor {

	/**
	 * コンストラクタ。
	 */
	public RelWarp(EssentialishWarp ew) {
		super(ew);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ew.setWarplistYaml(YamlConfiguration.loadConfiguration(ew.WARPLIST_YAML_FILE));
    	return true;
    }
}