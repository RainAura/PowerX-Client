package cn.Power.mod.mods.RENDER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class Weather extends Mod {
	
	public String backup = null;

	public static Value<Double> TIME = new Value<Double>("Weather_Time", 11000.0, 0.0, 24000.0, 500.0);

	public Weather() {
		super("Weather", Category.RENDER);
	}
	
	
	@Override
	public void onEnable() {
	
	}
	
	@EventTarget
	public void onRespawn (EventRespawn re) {
		
	
	}
	

	@EventTarget
	public void onpacket(EventPacket eventPacket) {
		if (eventPacket.getPacket() instanceof S03PacketTimeUpdate) {
			eventPacket.setCancelled(true);
		}
	}

	@EventTarget
	public void ontick(EventTick e) {
		
		if(mc.theWorld != null)
			mc.theWorld.setWorldTime((long) TIME.getValueState().intValue());

	}

}
