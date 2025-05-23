package jp.pgw.kosoado.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * setwarpコマンド<br>
 * ワープ地点を設定する
 */
public class SetWarp extends EWCommand implements CommandExecutor, TabCompleter {
	
	/**
	 * コンストラクタ。
	 */
	public SetWarp(EssentialishWarp ew) {
		super(ew);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	/* 
    	 * senderがプレイヤーの場合のみ実行
    	 * プレイヤーの位置を取得
    	 * ymlにワープ名とプレイヤーの位置(world, x, y, z, yaw, pitch)を登録
    	 * senderがプレイヤーではない場合、エラー
    	 */
    	
    	if(sender instanceof Player) {
    		
    		if(args.length == 0) {
        		sender.sendMessage("§cワープ名は必須です。\n" + cmd.getUsage());
        		return true;
        	}
        	if(args.length < 2) {
        		sender.sendMessage("§cサウンド名か、サウンド無設定の「OFF」が必須です。\n" + cmd.getUsage());
        		return true;
        	}
        	
        	if(args.length > 3) {
        		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
        		return true;
        	}
        	
        	FileConfiguration warplistYaml = ew.getWarplistYaml();
        	String warpName = args[0].toLowerCase();
        	
        	// 不正文字チェック
        	if(!validate(warpName)) {
        		sender.sendMessage("§cワープ名に使えない文字・単語が含まれています。");
        		return true;
        	}
        	
        	// 既存ワープチェック
        	if(warplistYaml.contains(warpName)) {
        		sender.sendMessage("§a" + warpName + " §cはすでに存在しています。");
        		return true;
        	}
        	
        	// サウンドチェック
        	if(!isSound(args[1]) && !args[1].equalsIgnoreCase("off")) {
        		sender.sendMessage("§c正しいサウンド名か、サウンド無設定の「OFF」を入力してください。");
        		return true;
        	}
    		
    		Player player = (Player)sender;
    		Location loc = player.getLocation();
    		
    		String world = loc.getWorld().getName();
    		double x = loc.getX();
    		double y = loc.getY();
    		double z = loc.getZ();
    		float yaw = loc.getYaw();
    		float pitch = loc.getPitch();
    		
    		File warpYamlFile = null;
    		String warpGroup = "";
    		if(args.length == 3) {
    			warpGroup = args[2].toLowerCase();
    			
    			// 不正文字チェック
    			if(!validate(warpGroup)) {
    				sender.sendMessage("§cグループ名に使えない文字・単語が含まれています。");
    	    		return true;
    			}
    		}
    		String yamlPath = createPathString(warpName, warpGroup);
    		
    		try {
    			warpYamlFile = YamlUtil.createYaml(ew.getDataFolder(), yamlPath);
    		}
    		catch(IOException e) {
    			e.printStackTrace();
    			sender.sendMessage("§cワープが正常に登録されませんでした。");
    			return true;
    		}
    		FileConfiguration warpYaml = YamlConfiguration.loadConfiguration(warpYamlFile);
    		
    		warpYaml.set(KEY_WARP_WORLD, world);
    		warpYaml.set(KEY_WARP_X, x);
    		warpYaml.set(KEY_WARP_Y, y);
    		warpYaml.set(KEY_WARP_Z, z);
    		warpYaml.set(KEY_WARP_YAW, yaw);
    		warpYaml.set(KEY_WARP_PITCH, pitch);
    		warpYaml.set(KEY_SOUND_ID, args[1].toUpperCase());
    		warpYaml.set(KEY_SOUND_VOLUME, 1.0);
    		warpYaml.set(KEY_SOUND_PITCH, 1.0);
    		
    		ew.getWarplistYaml().set(warpName, warpGroup);
    		try {
				warpYaml.save(warpYamlFile);
				ew.getWarplistYaml().save(ew.WARPLIST_YAML_FILE);
				sender.sendMessage("§a" + warpName + " §6をワープ地点に登録しました。");
    		} catch(IOException e) {
				e.printStackTrace();
				sender.sendMessage("§c予期せぬエラーが発生しました。");
    			return true;
    		}
    	}
    	else {
    		sender.sendMessage("§6プレイヤー以外の実行はできません");
    	}
    	return true;
    }
    
    
    @Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * setwarp <warp_name> <sound_name | off> [<group_name>]
		 */
		if(args.length == 2) return suggestSounds(args[1]);
		if(args.length == 3) return suggestGroups(args[2]);
		return new ArrayList<>();
	}
}