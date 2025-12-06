package cn.Power.mod.mods.COMBAT;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Fly;
import cn.Power.mod.mods.MOVEMENT.Jesus;
import cn.Power.mod.mods.MOVEMENT.LongJump;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.MOVEMENT.ZoomFly;
import cn.Power.mod.mods.PLAYER.Step;
import cn.Power.util.BlockUtils;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

public class Criticals extends Mod {
	public static Value<Double> Hurttime = new Value<Double>("Criticals_Hurttime", 8.0, 1.0, 20.0, 1.0);
	public static Value<Double> dadly = new Value<Double>("Criticals_Delay", 350.0, 0.0, 1000.0, 10.0);

	public TimeHelper timer = new TimeHelper();

	public Criticals() {
		super("Criticals", Category.COMBAT);
	}

	public static double random(double d, double d2) {
		return d + (double) ThreadLocalRandom.current().nextFloat() * (d2 - d);
	}

	public boolean canCri() {
		return ShouldCritPacket() && Critcheck();
	}

	@EventTarget
	public void openPacket(EventPacket e) {
		if (Minecraft.thePlayer == null)
			return;

		if (!((Step) ModManager.getModByClass(Step.class)).getNoJumping() && canCri()) {

			if (e.getPacket() instanceof C0APacketAnimation) {
				KillAura KillAura = (KillAura) ModManager.getModByClass(KillAura.class);

				if (!KillAura.isEnabled()) {
					return;
				}
				

				final double x = Minecraft.thePlayer.posX;
				final double y = Minecraft.thePlayer.posY;
				final double z = Minecraft.thePlayer.posZ;

				double[] cy = new double[] {0.06 + ThreadLocalRandom.current().nextDouble(0.008),(new Random().nextBoolean() ? 0.00925 : 0.006725) * (new Random().nextBoolean() ? 0.98 : 0.99)};
				
				double[] p = new double[] { 0.045f, 0.0010000275, 0.0436f, 0.0010000275 };

				int i = 0;

				if (ThreadLocalRandom.current().nextBoolean())
					while (i < p.length) {
						double oY = p[i];

						if (oY + 2.0E-4 <= 0.0625 && oY != 0.0) {
							oY += random(-2.0E-4, 2.0E-4);
						} else if (oY >= 0.0625) {
							oY += random(-2.0E-4f, 0.0f);
						} else
							oY += random(0.0, 2.0E-4);

						Minecraft.thePlayer.sendQueue.getNetworkManager()
								.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + oY, z, false));

						++i;

					}

				timer.reset();
			}

		}

		return;

	}

	private boolean Critcheck() {
		return cn.Power.mod.mods.COMBAT.KillAura.Target != null
				&& (cn.Power.mod.mods.COMBAT.KillAura.Target.hurtTime <= 3 ? true
						: cn.Power.mod.mods.COMBAT.KillAura.Target.hurtTime <= Hurttime.getValueState().intValue());
	}

	public boolean ShouldCritPacket() {
		Speed speed = (Speed) ModManager.getModByClass(Speed.class);
		ZoomFly zoom = (ZoomFly) ModManager.getModByClass(ZoomFly.class);
		LongJump LongJump = (LongJump) ModManager.getModByClass(LongJump.class);
		Fly fly = (Fly) ModManager.getModByClass(Fly.class);
		Scaffold scaf = (Scaffold) ModManager.getModByClass(Scaffold.class);

		return mc.thePlayer.fallDistance < 2 && !Jesus.isOnLiquid() && !scaf.isEnabled() && !speed.isEnabled() && !fly.isEnabled() && !zoom.isEnabled() && !LongJump.isEnabled()
				&& ModManager.getModByClass(KillAura.class).isEnabled() && (mc.thePlayer.onGround)
				&& !BlockUtils.isOnLiquid()
				&& mc.getNetHandler().S08count == 0
				&& (dadly.getValueState().doubleValue() == 0 || timer.delay(dadly.getValueState().doubleValue()))
				&& !Minecraft.thePlayer.isInWater() && !Minecraft.thePlayer.isInLava() && !Minecraft.thePlayer.isInWeb
				&& !Minecraft.thePlayer.isOnLadder() && !ModManager.getModByClass(Fly.class).isEnabled();
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {
	}

}
