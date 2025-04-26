package jp.pgw.kosoado.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * warpgroupコマンド<br>
 * ワープグループに関する操作をする
 */
public class WarpGroup extends EWCommand implements CommandExecutor, TabCompleter {
	
	/** サブコマンド create */
	private final String SUB_COMMAND_CREATE = "create";
	/** サブコマンド delete */
	private final String SUB_COMMAND_DELETE = "delete";
	/** サブコマンド list */
	private final String SUB_COMMAND_LIST = "list";
	/** サブコマンド remove */
	private final String SUB_COMMAND_REMOVE = "remove";
	/** サブコマンド rename */
	private final String SUB_COMMAND_RENAME = "rename";
	/** サブコマンド set */
	private final String SUB_COMMAND_SET = "set";
	
	/** サブコマンド delete オプション whole */
	private final String OPTION_WHOLE = "whole";
	

	/**
	 * コンストラクタ。
	 */
	public WarpGroup(EssentialishWarp ew) {
		super(ew);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 *  warpgroup <operation> <name> [<value>]
		 *  
		 */
		if(args.length == 0) {
    		sender.sendMessage("§c操作種別を入力してください。\n" + cmd.getUsage());
    		return true;
    	}
		if(args.length > 3) {
			sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
    		return true;
		}
		
		if(args.length > 0) {
			
			boolean isList = args[0].equalsIgnoreCase(SUB_COMMAND_LIST);

			if(args.length < 2 && !isList) {
				sender.sendMessage("§cワープグループ名は必須です。\n" + cmd.getUsage());
				return true;
			}
			
			setSender(sender); // setする行のセンスがよくない気がする。。
			
			// SUB_COMMAND_LIST
			if(isList) {
				
				if(args.length > 1) {
					sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
		    		return true;
				}
				listGroup();
			}
			else {
				String group = args[1];
				if(!validate(group)) {
					sender.sendMessage("§cグループ名に使えない文字・単語が含まれています。");
		    		return true;
				}
				if(!existsGroup(group) && !args[0].equalsIgnoreCase(SUB_COMMAND_CREATE)) {
					sender.sendMessage("§a" + group + " §cは存在しないグループです。");
		    		return true;
				}
				
				switch(args[0].toLowerCase()) {
				
				case SUB_COMMAND_CREATE: 
					if(args.length > 2) {
						sender.sendMessage("§c引数が不正です。\n" + cmd.getUsage());
			    		return true;
					}
					createGroup(group);
					break;
					
				case SUB_COMMAND_DELETE:
					boolean isWhole = false;
					if(args.length == 3) {
						
						if(!args[2].equalsIgnoreCase(OPTION_WHOLE)) {
							sender.sendMessage("§cグループ内ワープごと削除する場合、「whole」を入力してください。\n" + cmd.getUsage());
				    		return true;
						}
						isWhole = true;
					}
					deleteGroup(group, isWhole);
					break;
					
				case SUB_COMMAND_REMOVE:
					if(args.length < 3) {
						sender.sendMessage("§cグループから除去するワープ名を入力してください。\n" + cmd.getUsage());
			    		return true;
					}
					removeGroup(group, args[2]);
					break;
					
				case SUB_COMMAND_RENAME:
					if(args.length < 3) {
						sender.sendMessage("§c新しく設定するワープグループ名を入力してください。\n" + cmd.getUsage());
			    		return true;
					}
					renameGroup(group, args[2]);
					break;
					
				case SUB_COMMAND_SET:
					if(args.length < 3) {
						sender.sendMessage("§cグループを設定するワープ名を入力してください。\n" + cmd.getUsage());
			    		return true;
					}
					setGroup(group, args[2]);
					break;
					
				default:
					sender.sendMessage("§c正しい操作種別を入力してください。\n" + cmd.getUsage());
				}
			}
			
		}
		return true;
	}
	
	
	/**
	 * グループを作成する
	 */
	private void createGroup(String group) {
		
		File groupFolder = new File(ew.getDataFolder(), group);
		
		if(groupFolder.exists()) {
			getSender().sendMessage("§a" + group + " §cはすでに存在しています。");
			return;
		}
		groupFolder.mkdirs();
		getSender().sendMessage("§a" + group + " §6を作成しました。");
	}
	
	
	/**
	 * グループを削除する<br>
	 * 第3引数によって、処理が変わる<br>
	 * -「whole」を指定：削除するグループ内のワープ地点もすべて削除<br>
	 * - 引数指定なし：削除するグループ内のワープ地点をグループなしに変更
	 */
	private void deleteGroup(String group, boolean isWhole) {
		
		/*
		 * ■isWhole = true
		 * グループ内のワープを削除(ファイル物理削除、warplistから削除)
		 * グループを削除
		 * ■isWhole = false
		 * グループ内のワープのグループを未設定とする(ファイル移動、warplist書き換え)
		 * グループを削除
		 */
		
		File dataFolder = ew.getDataFolder();
		File groupFolder = new File(dataFolder, group);
		
		FileConfiguration warplistYaml = ew.getWarplistYaml();
		Set<String> warpNameSet = YamlUtil.getWarpNames(warplistYaml);
		// グループに属するワープのリスト
		List<String> warpList = warpNameSet.stream()
				.filter(warp -> {
				String groupName = YamlUtil.getYamlString(warplistYaml, warp);
				return groupName.equals(group);
				})
				.toList();
		
		try {
			// wholeの指定がなければワープファイルを移動
			if(!isWhole) moveWarpFile(groupFolder, dataFolder);
			
			// グループフォルダ(と中身のワープファイル)を削除
			deleteFile(groupFolder);
			
			// warplistから削除
			for(String warp : warpList) {
				if(isWhole) {
					warplistYaml.set(warp, null);
				}
				else {
					warplistYaml.set(warp, "");
				}
			}
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			getSender().sendMessage("§a" + group + " §6を削除しました。");
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
			return;
		}
	}
	
	
	/**
	 * ファイルを削除する
	 */
	private void deleteFile(File file) {
		
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			if(files.length > 0) {
				
				for(File f : files) {
					deleteFile(f);
				}
			}
		}
		file.delete();
	}
	
	
	/**
	 * グループ一覧を表示する
	 */
	private void listGroup() {
		
		File[] groupFolders = ew.getDataFolder().listFiles(file -> file.isDirectory());
		List<String> groupList = Arrays.stream(groupFolders).map(file -> file.getName()).toList();
		
		getSender().sendMessage(createGroupListString(groupList));
	}
	
	/**
	 * グループ一覧の表示する文字列を作成する
	 */
	private String createGroupListString(List<String> groupList) {
		
		StringBuilder msg = new StringBuilder();
		
		msg.append("§6ワープグループ");
		
		if(groupList.isEmpty()) {
			msg.append("は存在しません。");
			return msg.toString();
		}
		
		msg.append("一覧(§b");
		
		int groupCount = groupList.size();
		
		msg.append(groupCount);
		msg.append("§6): \n");

		for(int i = 0; i < groupCount; i++) {
			
			if(i > 0) {
				msg.append("§6, ");
			}
			msg.append("§a");
			msg.append(groupList.get(i));
		}
		return msg.toString();
	}
	
	
	/**
	 * グループからワープを除去する
	 */
	private void removeGroup(String group, String warp) {
		
		/*
		 * ・ワープファイルをデータフォルダ直下に移動
		 * ・warplistを書き換え
		 */
		
		File dataFolder = ew.getDataFolder();
		String yamlPath = createPathString(warp, group);
		File warpYamlFile = new File(dataFolder, yamlPath);
		File targetFile = new File(dataFolder, warp + ".yml");
		
		try {
			moveWarpFile(warpYamlFile, targetFile);
			
			FileConfiguration warplistYaml = ew.getWarplistYaml();
			warplistYaml.set(warp, "");
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			getSender().sendMessage("§a" + group + " §6から " + warp + " を除去しました。");
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
			return;
		}
	}
	
	
	/**
	 * グループ名をリネームする
	 */
	private void renameGroup(String group, String newName) {
		
		/*
		 * フォルダのリネーム
		 * warpyaml書き換え
		 * - リネーム前のグループ名のワープを抽出して反復処理でyaml書き換え
		 */
		
		File dataFolder = ew.getDataFolder();
		File groupFolder = new File(dataFolder, group);
		File renamedFolder = new File(dataFolder, newName);
		try {
			move(groupFolder, renamedFolder);
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
			return;
		}
		
		FileConfiguration warplistYaml = ew.getWarplistYaml();
		Set<String> warpNameSet = YamlUtil.getWarpNames(warplistYaml);
		// グループに属するワープのリスト
		List<String> warpList = warpNameSet.stream()
				.filter(warp -> {
				String groupName = YamlUtil.getYamlString(warplistYaml, warp);
				return groupName.equals(group);
				})
				.toList();
		
		for(String warp : warpList) {
				warplistYaml.set(warp, newName);
		}
		
		try {
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			getSender().sendMessage("§a" + group + " §6のグループ名を§a " + newName + " §6に変更しました。");
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
			return;
		}
	}
	
	
	/**
	 * 既存のワープ地点のグループを設定する
	 */
	private void setGroup(String group, String warp) {
		
		/*
		 * warpyamlから今のグループを読み込む
		 * Files.moveで現在のグループから指定グループに移動
		 * warpyamlの書き換え
		 * - groupで上書き
		 */
		
		FileConfiguration warplistYaml = ew.getWarplistYaml();
		if(!warplistYaml.contains(warp)) {
    		getSender().sendMessage("§a" + warp + " §cは存在しないワープです。");
    		return;
    	}
		File dataFolder = ew.getDataFolder();
		String currentGroup = YamlUtil.getYamlString(warplistYaml, warp);
		// ワープファイル移動の用意
		File source = new File(dataFolder, createPathString(warp, currentGroup));
		File target = new File(dataFolder, createPathString(warp, group));
		// ワープリストを更新
		warplistYaml.set(warp, group);
		
		try {
			move(source, target);
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
		}
		
		warplistYaml.set(warp, group);
		try {
			warplistYaml.save(ew.WARPLIST_YAML_FILE);
			getSender().sendMessage("§6" + warp + " のグループを§a " + group + " §6に設定しました。");
		}
		catch(IOException e) {
			e.printStackTrace();
			getSender().sendMessage(" §c予期せぬエラーが発生しました。");
			return;
		}
	}
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		/*
		 * warpgroup <operation> <name> [<value>]
		 */
		if(args.length == 1) {
			List<String> operations = new ArrayList<>();
			operations.add(SUB_COMMAND_CREATE);
			operations.add(SUB_COMMAND_DELETE);
			operations.add(SUB_COMMAND_LIST);
			operations.add(SUB_COMMAND_REMOVE);
			operations.add(SUB_COMMAND_RENAME);
			operations.add(SUB_COMMAND_SET);
			return operations.stream()
					.filter(operation -> operation.startsWith(args[0].toLowerCase()))
					.sorted().toList();
		}
		if(args.length == 2) {
			
			if(!args[0].equalsIgnoreCase(SUB_COMMAND_CREATE) && !args[0].equalsIgnoreCase(SUB_COMMAND_LIST)) {
				return suggestGroups(args[1]);
			}
		}
		if(args.length == 3) {
			
			if(args[0].equalsIgnoreCase(SUB_COMMAND_DELETE)) {
				List<String> whole = new ArrayList<>();
				whole.add(OPTION_WHOLE);
				return whole;
			}
			if(args[0].equalsIgnoreCase(SUB_COMMAND_REMOVE)) {
				Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
				return warpNameSet.stream()
						.filter(name -> {
							String groupName = YamlUtil.getYamlString(ew.getWarplistYaml(), name);
							if(groupName.equals(args[1])) {
								return groupName.startsWith(args[2]);
							}
							return false;
						})
						.sorted().toList();
			}
			if(args[0].equalsIgnoreCase(SUB_COMMAND_SET)) return suggestWarps(args[2]);
		}
			
		return new ArrayList<>();
	}
	
	
	/**
	 * ワープファイルを指定ディレクトリ(tgt)に移動する<br>
	 * @param src 移動するファイル・フォルダ
	 * @param tgtDir 移動先ディレクトリ
	 */
	private void moveWarpFile(File src, File tgt) throws IOException {
		
		if(src.isDirectory()) {
			File[] files = src.listFiles();
			if(files.length < 1) return;
			if(files.length > 0) {
				
				for(File f : files) {
					File target = new File(tgt, f.getName());
					moveWarpFile(f, target);
					return;
				}
			}
		}
		try {
			move(src, tgt);
		}
		catch(IOException e) {
			throw e;
		}
	}
	
	
	/**
	 * ファイル・フォルダを移動またはリネームする<br>
	 * Files.move()を実行する
	 * @param src 移動するファイル・フォルダ
	 * @param tgt 移動先
	 */
	private void move(File src, File tgt) throws IOException {
		Path source = src.toPath();
		Path target = tgt.toPath();
		try {
			Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
		}
		catch (IOException e) {
			throw e;
		}
	}
}