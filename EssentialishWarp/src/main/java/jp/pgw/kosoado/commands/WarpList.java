package jp.pgw.kosoado.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.StringUtil;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * warplistコマンド<br>
 * ワープ一覧やグループ一覧を表示する
 */
public class WarpList extends EWCommand implements CommandExecutor, TabCompleter {

	/**
	 * コンストラクタ。
	 */
	public WarpList(EssentialishWarp ew) {
		super(ew);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * コマンドがrelwarpの場合、リロード
		 * コマンドがwarplistの場合
		 * 引数なしならwarplistを表示
		 * 引数がreloadならリロード
		 * 引数がそれ以外ならグループ絞り
		 */
		
		// relwarpコマンド
		if(cmd.getName().equals("relwarp")) {
			reload(sender);
			return true;
		}
		
		// warplistコマンド
		List<String> warplist = null;
		String group = null;
		
		if(args.length == 0) {
			Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
			warplist = warpNameSet.stream().sorted().toList();
		}
		else if(args.length == 1) {
			
			if(args[0].equals("reload")) {
				reload(sender);
				return true;
			}
			
			Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
			group = args[0];
			File[] groupFolders = ew.getDataFolder().listFiles(file -> file.isDirectory());
			List<String> groupList = Arrays.stream(groupFolders).map(file -> file.getName()).toList();
			if(!groupList.contains(group)) {
	    		sender.sendMessage("§a" + group + " §cは存在しないグループです。");
	    		return true;
	    	}
			warplist = warpNameSet.stream()
					.filter(warp -> groupList.contains(YamlUtil.getYamlString(ew.getWarplistYaml(), warp)))
					.sorted().toList();
		}
		
		sender.sendMessage(createWarplistString(warplist, group));
		return true;
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * warplist [<reload | group_name>]
		 * グループ名を取得して表示
		 */
		
		List<String> tabComplete = null;
		
		if(args.length == 1) {
			File[] groupFolders = ew.getDataFolder().listFiles(file -> file.isDirectory());
			tabComplete = Stream.concat(
					Arrays.stream(groupFolders).map(file -> file.getName()), Stream.of("reload")
					).filter(name -> name.startsWith(args[0].toUpperCase())).sorted().toList();
		}
		return tabComplete;
	}
	
	
	/**
	 * warplist.ymlをリロードする
	 * @param sender コマンド送信者(メッセージ送信用)
	 */
	private void reload(CommandSender sender) {
		ew.setWarplistYaml(YamlConfiguration.loadConfiguration(ew.WARPLIST_YAML_FILE));
		sender.sendMessage("§6ワープリストをリロードしました。");
	}

	
	/**
	 * ワープ一覧の表示する文字列を作成する
	 */
	private String createWarplistString(List<String> warplist, String group) {
		
		int warpCount = warplist.size();
		
		StringBuilder msg = new StringBuilder();
		
		if(!StringUtil.isNullOrEmpty(group)) {
			msg.append("§6グループ「§a");
			msg.append(group);
			msg.append("§6」の");
		}
		msg.append("§6warp一覧(");
		msg.append(warpCount);
		msg.append("): ");
		for(int i = 0; i < warpCount; i++) {
			
			if(i > 0) {
				msg.append("§6, ");
			}
			msg.append("§a");
			msg.append(warplist.get(i));
		}
		return msg.toString();
	}
}