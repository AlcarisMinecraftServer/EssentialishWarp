package jp.pgw.kosoado.commands;

import java.io.IOException;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * setwarpコマンド
 */
public class SetWarp implements CommandExecutor ,Listener {
	
	private final EssentialishWarp ew ;
	
	//Constructor
	public SetWarp(EssentialishWarp _ew) {
		
		ew = _ew;
	}
	
    @Override
    public boolean onCommand(CommandSender sender , Command cmd ,
            String label , String[] args) {
    	
    	/* 
    	 * senderがプレイヤーの場合のみ実行
    	 * プレイヤーの位置を取得
    	 * ymlにワープ名とプレイヤーの位置(world, x, y, z, yaw, pitch)を登録
    	 * senderがプレイヤーではない場合、エラー
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
    	
    	if(warpNameSet.contains(warpName)) {
    		sender.sendMessage("§a" + warpName + " §cはすでに存在しています。");
    		return true;
    	}
    	
    	if(sender instanceof Player) {
    		
    		Player player = (Player)sender;
    		Location loc = player.getLocation();
    		String world = loc.getWorld().getName();
    		double x = loc.getX();
    		double y = loc.getY();
    		double z = loc.getZ();
    		float yaw = loc.getYaw();
    		float pitch = loc.getPitch();
    		
    		warpYaml.set(warpName + ".world", world);
    		warpYaml.set(warpName + ".x", x);
    		warpYaml.set(warpName + ".y", y);
    		warpYaml.set(warpName + ".z", z);
    		warpYaml.set(warpName + ".yaw", yaw);
    		warpYaml.set(warpName + ".pitch", pitch);
    		try {
				warpYaml.save(ew.getYamlFile());
				player.sendMessage("§a" + warpName + " §6をワープ地点に登録しました。");
    		} catch (IOException e) {
				e.printStackTrace();
			}
    		
    		
    	} else {
    		sender.sendMessage("§6プレイヤー以外の実行はできません");
    	}
    	return true ;
    }
}