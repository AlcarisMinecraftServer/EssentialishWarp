package jp.pgw.kosoado.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

public class RelWarp implements CommandExecutor ,Listener {
	
	private final EssentialishWarp ew ;
	
	//Constructor
	public RelWarp(EssentialishWarp ew) {
		
		this.ew = ew;
	}
	
	public boolean onCommand(CommandSender sender , Command cmd ,
            String label , String[] args) {
    	
    	// relwarpコマンド
		ew.setYamlFile(YamlUtil.createYaml(ew.getDataFolder()));
    	ew.setWarpYaml(YamlConfiguration.loadConfiguration(ew.getYamlFile()));
    	sender.sendMessage("§6ワープデータをリロードしました。");
    	
    	return true ;
    }
}