package jp.pgw.kosoado.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import jp.pgw.kosoado.EssentialishWarp;

/**
 * warpgroupコマンド<br>
 * ワープグループに関する操作をする
 */
public class WarpGroup extends EWCommand implements CommandExecutor {

	/**
	 * コンストラクタ。
	 */
	public WarpGroup(EssentialishWarp ew) {
		super(ew);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("§cこのコマンドは未実装です。。");
		
		// TODO
		
		return true;
	}
}