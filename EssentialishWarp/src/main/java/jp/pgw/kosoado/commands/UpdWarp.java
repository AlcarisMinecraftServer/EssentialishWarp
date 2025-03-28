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
 * updwarpコマンド<br>
 * 指定したワープの位置やワープ音を変更する
 */
public class UpdWarp extends EWCommand implements CommandExecutor, TabCompleter {

	/**
	 * コンストラクタ。
	 */
	public UpdWarp(EssentialishWarp ew) {
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
        		sender.sendMessage("§cワープ地点変更は「warp」、ワープ音変更は「sound」を入力します。\n" + cmd.getUsage());
        		return true;
        	}
        	if(args.length >= 2 && !isOperation(args[1])) {
        		sender.sendMessage("§cワープ地点変更は「warp」、ワープ音変更は「sound」を入力します。\n" + cmd.getUsage());
            	return true;
        	}
        	if(args.length > 3) {
        		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
        		return true;
        	}
        	
        	FileConfiguration warplistYaml = ew.getWarplistYaml();
        	String warpName = args[0].toLowerCase();
        	
        	if(!warplistYaml.contains(warpName)) {
        		sender.sendMessage("§a" + warpName + " §cは存在しないワープです。");
        		return true;
        	}
        	
        	if(args.length == 2) {
        		
        		if(args[1].equals("sound")) {
        			sender.sendMessage("§cサウンド名か、サウンド無設定の「OFF」が必須です。\n" + cmd.getUsage());
            		return true;
        		}
        		
        		String warpGroup = YamlUtil.getYamlString(warplistYaml, warpName);
        		String yamlPath = createPathString(warpName, warpGroup);
        		
        		Player player = (Player)sender;
        		Location loc = player.getLocation();
        		
        		String world = loc.getWorld().getName();
        		double x = loc.getX();
        		double y = loc.getY();
        		double z = loc.getZ();
        		float yaw = loc.getYaw();
        		float pitch = loc.getPitch();

        		try {
        			File warpYamlFile = new File(ew.getDataFolder(), yamlPath);
        			FileConfiguration warpYaml = YamlConfiguration.loadConfiguration(warpYamlFile);
        			
        			warpYaml.set(KEY_WARP_WORLD, world);
            		warpYaml.set(KEY_WARP_X, x);
            		warpYaml.set(KEY_WARP_Y, y);
            		warpYaml.set(KEY_WARP_Z, z);
            		warpYaml.set(KEY_WARP_YAW, yaw);
            		warpYaml.set(KEY_WARP_PITCH, pitch);
            		
            		warplistYaml.set(warpName, warpGroup);
            		
            		warpYaml.save(warpYamlFile);
            		warplistYaml.save(ew.WARPLIST_YAML_FILE);
    				sender.sendMessage("§a" + warpName + " §6のワープ地点を変更しました。");
        		}
        		catch(IllegalArgumentException e) {
        			sender.sendMessage("§a" + warpName + " §cのワープデータが存在しないか、データが不足しています。");
            		return true;
        		}
        		catch(IOException e) {
        			e.printStackTrace();
        			sender.sendMessage("§a" + warpName + " §c予期せぬエラーが発生しました。");
            		return true;
        		}

        	}
        	else if(args.length == 3) {
        		
        		if(args[1].equals("warp")) {
        			sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
            		return true;
        		}
        		
        		// サウンドチェック
            	if(!isSound(args[2]) && !args[2].equalsIgnoreCase("off")) {
            		sender.sendMessage("§c正しいサウンド名か、サウンド無設定の「OFF」を入力してください。");
            		return true;
            	}
        		
        		String warpGroup = YamlUtil.getYamlString(warplistYaml, warpName);
        		String yamlPath = createPathString(warpName, warpGroup);
        		
        		try {
        			File warpYamlFile = new File(ew.getDataFolder(), yamlPath);
        			FileConfiguration warpYaml = YamlConfiguration.loadConfiguration(warpYamlFile);
        			
            		warpYaml.set(KEY_SOUND_ID, args[2].toUpperCase());
            		warpYaml.set(KEY_SOUND_VOLUME, 1.0);
            		warpYaml.set(KEY_SOUND_PITCH, 1.0);
            		
            		warplistYaml.set(warpName, warpGroup);
            		
            		warpYaml.save(warpYamlFile);
            		warplistYaml.save(ew.WARPLIST_YAML_FILE);
    				sender.sendMessage("§a" + warpName + " §6のワープ音を変更しました。");
        		}
        		catch(IllegalArgumentException e) {
        			sender.sendMessage("§a" + warpName + " §cのワープデータが存在しないか、データが不足しています。");
            		return true;
        		}
        		catch(IOException e) {
        			e.printStackTrace();
        			sender.sendMessage("§a" + warpName + " §c予期せぬエラーが発生しました。");
            		return true;
        		}
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
		 * updwarp <warp_name> <warp | sound> [<sound_name | off>]
		 */
		if(args.length == 1) return suggestWarps(args[0]);
		
		List<String> tabComplete = new ArrayList<>();
		if(args.length == 2) {
			tabComplete.add("sound");
			tabComplete.add("warp");
			return tabComplete;
		}
		if(args.length == 3 && !args[1].equals("warp")) return suggestSounds(args[2]);
		return tabComplete;
	}
	
	
	/**
	 * updwarpコマンドの第2引数がwarpかsoundであるかどうかチェックする<br>
	 * warpかsoundであれば、trueを返す
	 */
	private boolean isOperation(String arg) {
		return arg.equals("warp") || arg.equals("sound");
	}
}