package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;

public class Derp extends Mod {
	private Value<Boolean> spinny = new Value<Boolean>("Derp_spinny", false);
	private Value<Boolean> headless = new Value<Boolean>("Derp_headless", false);
	public static Value<Boolean> Rotary_animation = new Value<Boolean>("Derp_Rotary animation", true);
	private Value<Double> increment = new Value<Double>("Derp_Speed", 40.0, 1.0, 50.0, 1.0);

	public Derp() {
		super("Derp", Category.PLAYER);
	}

	private double serverYaw;

	@EventTarget(Priority.HIGHEST)
	private void onPre(EventPreMotion event) {
		this.serverYaw += this.increment.getValueState().intValue();
		if (this.spinny.getValueState()) {
			event.setYaw((float) this.serverYaw);
		}

		if (this.headless.getValueState()) {
			event.setPitch(150 + (float)(ThreadLocalRandom.current().nextInt(29)));
		} else if (!this.headless.getValueState() && !this.spinny.getValueState()) {
			event.setYaw((float) (Math.random() * 360.0D));
			event.setPitch((float) (Math.random() * 360.0D));
		}
	}

}
