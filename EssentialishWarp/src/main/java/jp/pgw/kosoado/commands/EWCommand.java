package jp.pgw.kosoado.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.exceptions.SoundNotFoundException;
import jp.pgw.kosoado.utils.StringUtil;
import jp.pgw.kosoado.utils.YamlUtil;
import jp.pgw.kosoado.validations.ForbiddenChars;
import jp.pgw.kosoado.validations.ReservedChars;

/**
 * コマンドの基本クラス
 */
public class EWCommand {
	
	/** プラグインオブジェクト */
	protected final EssentialishWarp ew;
	
	/** コマンド送信者 */
	private CommandSender sender;
	
	/** yamlのキー(ワープするワールド) */
	protected final String KEY_WARP_WORLD = "warp.world";
	/** yamlのキー(ワープするX座標) */
	protected final String KEY_WARP_X = "warp.x";
	/** yamlのキー(ワープするX座標) */
	protected final String KEY_WARP_Y = "warp.y";
	/** yamlのキー(ワープするX座標) */
	protected final String KEY_WARP_Z = "warp.z";
	/** yamlのキー(ワープしたときの垂直方向の角度) */
	protected final String KEY_WARP_YAW = "warp.yaw";
	/** yamlのキー(ワープしたときの水平方向の角度) */
	protected final String KEY_WARP_PITCH = "warp.pitch";
	/** yamlのキー(ワープ時に鳴らす音のID) */
	protected final String KEY_SOUND_ID = "sound.id";
	/** yamlのキー(ワープ時に鳴らす音のID) */
	protected final String KEY_SOUND_VOLUME = "sound.volume";
	/** yamlのキー(ワープ時に鳴らす音のID) */
	protected final String KEY_SOUND_PITCH = "sound.pitch";
	
	
	/**
	 * コンストラクタ。
	 */
	public EWCommand(EssentialishWarp ew) {
		this.ew = ew;
	}
	
	
	/**
	 * 処理対象者にワープ音を鳴らす
	 * @param target 処理対象者
	 * @param soundConf 再生する音のデータ
	 */
	protected void playWarpSound(CommandSender target, ConfigurationSection soundConf) 
			throws SoundNotFoundException {
		
		if(soundConf == null) return;
		
		String soundStr = YamlUtil.getYamlString(soundConf, "id");
		float volume = (float)( YamlUtil.getYamlDouble(soundConf, "volume") );
		float  pitch = (float)( YamlUtil.getYamlDouble(soundConf, "pitch") );
		
		if(target instanceof Player && !isNullOrOff(soundStr) && volume > 0) {
			
			try {
				Player player = (Player)target;
				Sound sound = Sound.valueOf(soundStr.toUpperCase());
				player.playSound(player.getLocation(), sound, volume, pitch);
			}catch(IllegalArgumentException e) {
				throw new SoundNotFoundException();
			}
			
		}
	}
	
	/**
	 * サウンドID(文字列)がnullか"off"かどうか判定する
	 */
	private boolean isNullOrOff(String soundStr) {
		if(soundStr == null || soundStr.toLowerCase().equals("off")) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 文字列に禁則文字・予約文字チェックを行う<br>
	 * 文字列に問題がなければ、trueを返す
	 */
	protected boolean validate(String input) {
		return !ForbiddenChars.containsForbiddenChars(input) && !ReservedChars.isReservedChar(input);
	}
	
	
	/**
	 * グループフォルダが存在するかどうかチェックを行う
	 */
	protected boolean existsGroup(String group) {
		File groupFile = new File(ew.getDataFolder(), group);
		return groupFile.exists();
	}
	
	
	/**
	 * ワープファイルが存在するかどうかチェックを行う
	 */
	protected boolean existsWarpFile(String yamlPath) {
		File warpFile = new File(ew.getDataFolder(), yamlPath);
		return warpFile.exists();
	}
	
	
	/**
	 * 文字列がSoundかどうかチェックする<br>
	 * Soundであれば、trueを返す
	 */
	protected boolean isSound(String input) {
		
		for(Sound sound : Sound.values()) {
			
			if(sound.toString().equalsIgnoreCase(input)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * ワープ名のタブコンプリート候補を生成する
	 */
	protected List<String> suggestWarps(String input) {
		Set<String> warpNameSet = YamlUtil.getWarpNames(ew.getWarplistYaml());
		return warpNameSet.stream()
				.filter(name -> name.startsWith(input.toLowerCase()))
				.sorted().toList();
	}
	
	
	/**
	 * プレイヤー名のタブコンプリート候補を生成する
	 */
	protected List<String> suggestPlayers(String input) {
		
		List<String> playerList = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			playerList.add(player.getName());
		}
		return playerList.stream()
				.filter(name -> name.startsWith(input))
				.sorted().toList();
	}
	
	
	/**
	 * サウンド名のタブコンプリート候補を生成する
	 */
	protected List<String> suggestSounds(String input) {
		
		Sound[] sounds = Sound.values();
		return Stream.concat(
				Arrays.stream(sounds).map(value -> value.toString()), Stream.of("OFF")
				).filter(name -> name.startsWith(input.toUpperCase())).sorted().toList();
	}
	
	
	/**
	 * グループ名のタブコンプリート候補を生成する
	 */
	protected List<String> suggestGroups(String input) {
		File[] groupFolders = ew.getDataFolder().listFiles(file -> file.isDirectory());
		return Arrays.stream(groupFolders)
				.map(file -> file.getName())
				.filter(name -> name.startsWith(input.toLowerCase()))
				.sorted().toList();
	}
	
	
	/**
	 * ワープファイルのパス文字列を作成する<br>
	 */
	protected String createPathString(String warp, String group) {

		StringBuilder warpPath = new StringBuilder();
    	if(!StringUtil.isNullOrEmpty(group)) {
    		warpPath.append(group);
    		warpPath.append("/");
    	}
		warpPath.append(warp);
		warpPath.append(".yml");

		return warpPath.toString();
	}
	
	
	/**
	 * コマンド送信者を取得
	 */
	public CommandSender getSender() {
		return this.sender;
	}
	
	/**
	 * コマンド送信者を設定
	 */
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
}