package jp.pgw.kosoado.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.exceptions.SoundNotFoundException;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * ①warpコマンド<br>
 * 自分または指定したプレイヤーをワープする<br>
 * ワープ音が設定されていれば、ワープ対象者に再生する<br>
 * ②silwarpコマンド<br>
 * 自分または指定したプレイヤーをワープ音なしでワープする<br>
 */
public class Warp extends EWCommand implements CommandExecutor, TabCompleter {

	/**
	 * コンストラクタ。
	 */
	public Warp(EssentialishWarp ew) {
		super(ew);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	/* arg[0]がなければエラー
    	 * yamlを読み込んでlocationを取得
    	 * - warplistからグループを取得
    	 * - グループ未設定or存在しないグループなら、データフォルダ直下を見る
    	 * 指定したワープがなければ、エラー
    	 * arg[1]があれば、そのプレイヤーをtp
    	 * arg[1]のプレイヤーがいなければ、エラー
    	 * arg[1]がなければ、実行者をtp
    	 * 実行者がプレイヤーでなければ、エラー
    	 * ワープ音を再生する
    	 */
    	
    	if(args.length == 0) {
    		sender.sendMessage("§cワープ名は必須です。\n" + cmd.getUsage());
    		return true;
    	}
    	
    	if(args.length > 2) {
    		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return true;
    	}
    	
    	FileConfiguration warplistYaml = ew.getWarplistYaml();
    	String warpName = args[0];
    	String warpGroup = warplistYaml.getString(warpName);
    	String yamlPath = createPathString(warpName, warpGroup);
    	File warpYamlFile = new File(ew.getDataFolder(), yamlPath);
    	
    	try {
    		FileConfiguration warpYaml = YamlConfiguration.loadConfiguration(warpYamlFile);
    		
    		World world = Bukkit.getWorld(YamlUtil.getYamlString(warpYaml, KEY_WARP_WORLD));
        	double x = YamlUtil.getYamlDouble(warpYaml, KEY_WARP_X);
        	double y = YamlUtil.getYamlDouble(warpYaml, KEY_WARP_Y);
        	double z = YamlUtil.getYamlDouble(warpYaml, KEY_WARP_Z);
        	float yaw = (float)( YamlUtil.getYamlDouble(warpYaml, KEY_WARP_YAW) );
        	float pitch = (float)( YamlUtil.getYamlDouble(warpYaml, KEY_WARP_PITCH) );
        	
        	Location warpLoc = new Location(world, x, y, z, yaw, pitch);
        	
        	Player warpPlayer = null;
        	if(args.length > 1) {
        		warpPlayer = Bukkit.getPlayer(args[1]);
        		
        		if(warpPlayer == null) {
        			sender.sendMessage("§c指定したプレイヤーが見つかりませんでした。");
        			return true;
        		}
        	}
        	
        	// soundをyamlから取り出す
        	ConfigurationSection sound = warpYaml.getConfigurationSection("sound");
        	
        	if(warpPlayer == null) {
        		
        		if(sender instanceof Player) {
        			Player player = (Player)sender;
        			player.teleport(warpLoc);
        			player.sendMessage("§a" + warpName + " §6へワープしました。");
        			if(cmd.getName().equals("warp")) {
        				playWarpSound(sender, sound);
        			}
        		}else {
        			sender.sendMessage("§cプレイヤー名は必須です。");
        		}
        	
        	}else {
        		warpPlayer.teleport(warpLoc);
        		if(cmd.getName().equals("warp")) {
        			playWarpSound(warpPlayer, sound);
        		}
        		sender.sendMessage("§6" + warpPlayer.getName() + " を §a " + warpName + " §6へワープしました。");
        	}
        	
    	} catch(NullPointerException | IllegalArgumentException e) {
    		sender.sendMessage("§a" + warpName + " §cのワープデータが存在しないか、データが不足しています。");
    		e.printStackTrace();
    		return true;
    	} catch(SoundNotFoundException e) {
    		sender.sendMessage("§a指定したサウンドは存在しません。\n単語の区切りはすべてアンダーバー(_)です。");
    	}
    	return true;
    }

    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * warp <warp_name> [<player>]
		 */
		if(args.length == 1) return suggestWarps(args[0]);
		if(args.length == 2) return suggestPlayers(args[1]);
		return new ArrayList<>();
	}
}