package jp.pgw.kosoado.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import jp.pgw.kosoado.EssentialishWarp;

/**
 * renwarpコマンド<br>
 * ワープ名を変更する
 */
public class RenWarp implements CommandExecutor {
	
	private final EssentialishWarp ew ;
	
	/**
	 * コンストラクタ。
	 */
	public RenWarp(EssentialishWarp ew) {
		this.ew = ew;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// TODO
		
		return true;
	}
}