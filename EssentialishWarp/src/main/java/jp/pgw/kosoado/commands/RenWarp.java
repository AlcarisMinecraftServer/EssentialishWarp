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
 * renwarpコマンド<br>
 * ワープ名を変更する
 */
public class RenWarp extends EWCommand implements CommandExecutor, TabCompleter {
	
	/**
	 * コンストラクタ。
	 */
	public RenWarp(EssentialishWarp ew) {
		super(ew);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		/* 
		 * ymlに指定したワープがない場合、エラー
		 * ymlに指定したワープがあれば、該当ワープのファイル名・warplistを更新
    	 */
		
		if(args.length == 0) {
    		sender.sendMessage("§cワープ名は必須です。\n" + cmd.getUsage());
    		return true;
    	}
		if(args.length == 1) {
			sender.sendMessage("§c新しく設定するワープ名を入力してください。\n" + cmd.getUsage());
    		return true;
		}
    	
    	if(args.length > 2) {
    		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return true;
    	}
    	
    	FileConfiguration warplistYaml = ew.getWarplistYaml();
    	String warpName = args[0];
    	
    	if(!warplistYaml.contains(warpName)) {
    		sender.sendMessage("§a" + warpName + " §cは存在しないワープです。");
    		return true;
    	}

    	String warpGroup = YamlUtil.getYamlString(ew.getWarplistYaml(), warpName);
    	
    	String yamlPath = createPathString(warpName, warpGroup);
    	File warpYamlFile = new File(ew.getDataFolder(), yamlPath);
    	if(!warpYamlFile.exists()) {
    		sender.sendMessage("§a" + warpName + " §cのデータファイルが見つかりません。");
    		return true;
    	}
    	
    	String newWarpName = args[1];
    	// 不正文字チェック
    	if(!validate(newWarpName)) {
    		sender.sendMessage("§cワープ名に使えない文字・単語が含まれています。");
    		return true;
    	}
    	// 既存ワープチェック
    	if(warplistYaml.contains(newWarpName)) {
    		sender.sendMessage("§a" + newWarpName + " §cはすでに存在しています。");
    		return true;
    	}
    	String newYamlPath = createPathString(newWarpName, warpGroup);
    	File newWarpYamlFile = new File(ew.getDataFolder(), newYamlPath);
    	
		warpYamlFile.renameTo(newWarpYamlFile);
		
		warplistYaml.set(warpName, null); // 旧名を削除
		warplistYaml.set(newWarpName, warpGroup); // 新名を挿入
		try {
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			sender.sendMessage("§a" + warpName + " §6の名前を§a " + newWarpName + " §6に変更しました。");
		} catch(IOException e) {
			e.printStackTrace();
			sender.sendMessage(" §c予期せぬエラーが発生しました。");
		}
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * renwarp <warp_name> <new_warp_name>
		 */
		if(args.length == 1) return suggestWarps(args[0]);
		return new ArrayList<>();
	}
}