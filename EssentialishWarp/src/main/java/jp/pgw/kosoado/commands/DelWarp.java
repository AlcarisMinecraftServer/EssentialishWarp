package jp.pgw.kosoado.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import jp.pgw.kosoado.EssentialishWarp;
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
    		return true;
    	}
    	
    	if(args.length > 1) {
    		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return true;
    	}
    	
    	FileConfiguration warplistYaml = ew.getWarplistYaml();
    	String warpName = args[0];
    	String warpGroup = YamlUtil.getYamlString(warplistYaml, warpName);
    	
    	if(!warplistYaml.contains(warpName)) {
    		sender.sendMessage("§a" + warpName + " §cは存在しないワープです。");
    		return true;
    	}
    	
    	String yamlPath = createPathString(warpName, warpGroup);
    	File warpYamlFile = new File(ew.getDataFolder(), yamlPath);
		warpYamlFile.delete();
		
		warplistYaml.set(warpName, null);
		try {
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			sender.sendMessage("§a" + warpName + " §6を削除しました。");
		} catch(IOException e) {
			e.printStackTrace();
			sender.sendMessage(" §c予期せぬエラーが発生しました。");
		}
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * delwarp <warp_name>
		 */
		if(args.length == 1) return suggestWarps(args[0]);
		
		return new ArrayList<>();
	}
}