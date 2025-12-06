package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.PlayerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;

public class Strafe extends Mod {
	public static Value<String> mode = new Value("Strafe", "Mode", 0);

	public Strafe() {
		super("Strafe", Category.MOVEMENT);
		Strafe.mode.mode.add("NCP");
		Strafe.mode.mode.add("AAC");
		this.showValue = mode;
	}


}