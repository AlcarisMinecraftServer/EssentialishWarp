package jp.pgw.kosoado.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * warpコマンド
 */
public class Warp implements CommandExecutor ,Listener {
	
	private final EssentialishWarp ew ;
	
	//Constructor
	public Warp(EssentialishWarp _ew) {
		
		ew = _ew;
	}
	
    @Override
    public boolean onCommand(CommandSender sender , Command cmd ,
            String label , String[] args) {
    	
    	/* arg[0]がなければエラー
    	 * ymlを読み込んでlocationを取得
    	 * 指定したワープがなければ、エラー
    	 * arg[1]があれば、そのプレイヤーをtp
    	 * arg[1]のプレイヤーがいなければ、エラー
    	 * arg[1]がなければ、実行者をtp
    	 * 実行者がプレイヤーでなければ、エラー
    	 */
    	
    	if(args.length == 0) {
    		sender.sendMessage("§cワープ名は必須です。");
    		return false;
    	}
    	
    	if(args.length > 2) {
    		sender.sendMessage("§c引数が不正です。");
    		return false;
    	}
    	
    	FileConfiguration warpYaml = ew.getWarpYaml();
    	String warpName = args[0];
    	// String warpPlayer = args[1];
    	
    	try {
    		
    		World world = Bukkit.getWorld(YamlUtil.getYamlString(warpYaml, warpName + ".world"));
        	double x = YamlUtil.getYamlDouble(warpYaml, warpName + ".x");
        	double y = YamlUtil.getYamlDouble(warpYaml, warpName + ".y");
        	double z = YamlUtil.getYamlDouble(warpYaml, warpName + ".z");
        	float yaw = (float)( YamlUtil.getYamlDouble(warpYaml, warpName + ".yaw") );
        	float pitch = (float)( YamlUtil.getYamlDouble(warpYaml, warpName + ".pitch") );
        	
        	Location warpLoc = new Location(world, x, y, z, yaw, pitch);
        	
        	Player warpPlayer = null;
        	if(args.length > 1) {
        		warpPlayer = Bukkit.getPlayer(args[1]);
        		
        		if(warpPlayer == null) {
        			sender.sendMessage("§c指定したプレイヤーが見つかりませんでした。");
        			return false;
        		}
        	}
        	
        	if(warpPlayer == null) {
        		
        		if(sender instanceof Player) {
        			Player player = (Player)sender;
        			player.teleport(warpLoc);
        			player.sendMessage("§a" + warpName + " §6へワープしました。");
        		
        		}else {
        			sender.sendMessage("§cプレイヤー名は必須です。");
        		}
        	
        	}else {
        		warpPlayer.teleport(warpLoc);
        		warpPlayer.sendMessage("§a" + warpName + " §6へワープしました。");
        		sender.sendMessage("§6" + warpPlayer + " を §a " + warpName + " §6へワープしました。");
        	}
        	
    	} catch(NullPointerException e) {
    		sender.sendMessage("§a" + warpName + " §cのワープデータが存在しないか、データが不足しています。");
    		return false;
    	} catch(IllegalArgumentException e) {
    		sender.sendMessage("§a" + warpName + " §cのワープデータが存在しないか、データが不足しています。");
    	}
    	return true ;
    }
}