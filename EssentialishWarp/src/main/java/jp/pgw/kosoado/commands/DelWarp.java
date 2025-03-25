package jp.pgw.kosoado.commands;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.StringUtil;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * delwarpコマンド<br>
 * 指定したワープを削除する
 */
public class DelWarp extends EWCommand implements CommandExecutor, TabCompleter {
	
	/**
	 * コンストラクタ。
	 */
	public DelWarp(EssentialishWarp ew) {
		super(ew);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		/* 
		 * ymlに指定したワープがない場合、エラー
		 * ymlに指定したワープがあれば、該当ワープのデータを削除
    	 */
		
		if(args.length == 0) {
    		sender.sendMessage("§cワープ名は必須です。\n" + cmd.getUsage());
    		return false;
    	}
    	
    	if(args.length > 1) {
    		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return false;
    	}
    	
    	FileConfiguration warplistYaml = ew.getWarplistYaml();
    	String warpName = args[0];
    	String yamlPath = warpName + ".yml";
    	String warpGroup = YamlUtil.getYamlString(warplistYaml, warpName);
    	
    	if(!warplistYaml.contains(warpName)) {
    		sender.sendMessage("§a" + warpName + " §cは存在しないワープです。");
    		return true;
    	}
    	
    	File warpYamlFile = null; // new File(ew.getDataFolder(), "");
    	if(!StringUtil.isNullOrEmpty(warpGroup)) {
    		yamlPath = warpGroup + "/" + yamlPath;
    	}
    	warpYamlFile = new File(ew.getDataFolder(), yamlPath);
		warpYamlFile.delete();
		
		sender.sendMessage("§a" + warpName + " §6を削除しました。");
		
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * delwarp <warp_name>
		 * ワープ名をyamlから取得して表示
		 */
		
		List<String> tabComplete = null;
		
		if(args.length == 1) {
			Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
			tabComplete = warpNameSet.stream()
					.sorted()
					.filter(name -> name.startsWith(args[0].toLowerCase())).toList();
		}
		return tabComplete;
	}
}