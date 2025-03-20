package jp.pgw.kosoado.commands;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import jp.pgw.kosoado.EssentialishWarp;
import jp.pgw.kosoado.exceptions.SoundNotFoundException;
import jp.pgw.kosoado.utils.YamlUtil;

/**
 * コマンドの基本クラス
 */
public class EWCommand {
	
	/** プラグインオブジェクト */
	protected final EssentialishWarp ew;
	
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
}