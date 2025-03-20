package jp.pgw.kosoado.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import jp.pgw.kosoado.EssentialishWarp;

/**
 * warplistコマンド<br>
 * ワープ一覧やグループ一覧を表示する
 */
public class WarpList extends EWCommand implements CommandExecutor {

	/**
	 * コンストラクタ。
	 */
	public WarpList(EssentialishWarp ew) {
		super(ew);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("§cこのコマンドは未実装です。。");
		
		// TODO
		
		return true;
	}
}