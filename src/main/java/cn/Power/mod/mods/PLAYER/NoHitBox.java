package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.notification.Notification.Type;
import cn.Power.util.misc.ChatUtil;

public class NoHitBox  extends Mod {
	public static boolean status;
	public static Value<Boolean> teamMate_only = new Value("NoHitBox_TeamMateOnly", true);
	
	public NoHitBox() {
		super("NoHitBox", Category.PLAYER);
	}

	@Override
	public void onEnable() {
		ChatUtil.printChat("[Warnning] If u turn this on, u can't hit any entity (include Players) !");
		status = true;
	}
	
	@Override
	public void onDisable() {
		status = false;
	}
}
