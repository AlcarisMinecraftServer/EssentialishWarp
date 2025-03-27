package jp.pgw.kosoado.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

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
		 * 引数が「-」ならグループなしのワープ表示
		 * 引数がそれ以外ならグループ絞り
		 */
		
		// relwarpコマンド
		if(cmd.getName().equals("relwarp")) {
			reload(sender);
			return true;
		}
		
		// warplistコマンド
		if(args.length > 1) {
    		sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return true;
    	}
		
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
			
			group = args[0];
			File[] groupFolders = ew.getDataFolder().listFiles(file -> file.isDirectory());
			List<String> groupList = Arrays.stream(groupFolders).map(file -> file.getName()).toList();
			if(!group.equals("-") && !groupList.contains(group)) {
			   	sender.sendMessage("§a" + group + " §cは存在しないグループです。");
			   	return true;
			   }
			warplist = createWarplist(group);
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
			String[] subCmds = {"reload", "-"};
			tabComplete = Stream.concat(
					Arrays.stream(groupFolders).map(file -> file.getName()), Arrays.stream(subCmds)
					).filter(name -> name.startsWith(args[0].toUpperCase())).sorted().toList();
		}
		return tabComplete;
	}
	
	
	/**
	 * warplist.ymlをリロードする
	 * @param sender コマンド送信者(メッセージ送信用)
	 */
	private void reload(@Nullable CommandSender sender) {
		ew.setWarplistYaml(YamlConfiguration.loadConfiguration(ew.WARPLIST_YAML_FILE));
		sender.sendMessage("§6ワープ一覧をリロードしました。");
	}
	

	/**
	 * ワープ一覧を作成する<br>
	 * 入力されたグループで絞り込みを行う<br>
	 * groupが「-」で指定されたら、グループ未指定の一覧を作成する
	 */
	private List<String> createWarplist(String group) {
		
		Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
		if(group.equals("-")) {
			return warpNameSet.stream()
					.filter(warp -> {
						String groupName = YamlUtil.getYamlString(ew.getWarplistYaml(), warp);
						return groupName.isEmpty();
					})
					.sorted().toList();
		}
		return warpNameSet.stream()
				.filter(warp -> {
					String groupName = YamlUtil.getYamlString(ew.getWarplistYaml(), warp);
					return groupName.equals(group);
				})
				.sorted().toList();
	}
	
	
	/**
	 * ワープ一覧の表示する文字列を作成する
	 */
	private String createWarplistString(List<String> warplist, @Nullable String group) {
		
		int warpCount = warplist.size();
		
		StringBuilder msg = new StringBuilder();
		
		if(!StringUtil.isNullOrEmpty(group)) {
			
			if(group.equals("-")) {
				msg.append("§6グループ無設定の");
			}
			else {
				msg.append("§6グループ「§a");
				msg.append(group);
				msg.append("§6」の");
			}
			
		}
		msg.append("§6warp一覧(§b");
		msg.append(warpCount);
		msg.append("§6): \n");
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