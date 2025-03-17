package jp.pgw.kosoado.commands;

import java.io.IOException;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

public class DelWarp implements CommandExecutor ,Listener {
	
	private final EssentialishWarp ew ;
	
	//Constructor
	public DelWarp(EssentialishWarp _ew) {
		
		ew = _ew;
	}
	
	@Override
	public boolean onCommand(CommandSender sender , Command cmd ,
            String label , String[] args) {
		
		/* 
		 * ymlに指定したワープがない場合、エラー
		 * ymlに指定したワープがあれば、該当ワープのデータを削除
    	 */
		
		if(args.length == 0) {
    		sender.sendMessage("§cワープ名は必須です。");
    		return false;
    	}
    	
    	if(args.length > 1) {
    		sender.sendMessage("§c引数が不正です。");
    		return false;
    	}
    	
    	String warpName = args[0];
    	FileConfiguration warpYaml = ew.getWarpYaml();
    	Set<String> warpNameSet = YamlUtil.getWarpNames(warpYaml);
    	
    	if(!warpNameSet.contains(warpName)) {
    		sender.sendMessage("§a" + warpName + " §cは存在しないワープです。");
    		return true;
    	}
    	
    	warpYaml.set(warpName, null);
		try {
			warpYaml.save(ew.getYamlFile());
			sender.sendMessage("§a" + warpName + " §6を削除しました。");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}