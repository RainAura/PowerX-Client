package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class AntiFall extends Mod {

	private TimeHelper timer = new TimeHelper();
	private boolean saveMe;
	public static Value<Boolean> VOID = new Value("AntiFall_Void", true);
	public Value<Double> DISTANCE = new Value<Double>("AntiFall_distance", 5.0, 1.0, 20.0, 1.0);
	public Value<String> mode = new Value("AntiFall", "Mode", 0);

	public AntiFall() {
		super("AntiFall", Category.MOVEMENT);
		this.mode.mode.add("Hypixel");

	}

	@EventTarget
	public void onPre(EventPreMotion e) {
		this.setDisplayName("Hypixel");
		if (this.isBlockUnder() && !this.saveMe) {
			if (ModManager.getModByClass(NoFall.class).isEnabled())
				return;
		}
		if (mc.thePlayer.capabilities.isFlying) {
			return;
		}
		if (this.saveMe && this.timer.delay(350.0f) || mc.thePlayer.isCollidedVertically) {
			this.saveMe = false;
			this.timer.reset();
			return;
		}
		int dist = this.DISTANCE.getValueState().intValue();
		if (!(mc.thePlayer.fallDistance < (float) dist || ModManager.getModByClass(Fly.class).isEnabled()
				|| ModManager.getModByClass(ZoomFly.class).isEnabled()
				|| mc.theWorld
						.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ))
						.getBlock() != Blocks.air
				|| ((Boolean) this.VOID.getValueState().booleanValue() && this.isBlockUnder()))
				&& this.timer.delay(900.0f)) {
			if (this.timer.delay(400.0f))
				e.setY(e.getY() + mc.thePlayer.fallDistance);
			timer.reset();
		}

	}

	@EventTarget
	public void onPre(EventPacket ep) {
		if (!(ep.getPacket() instanceof S08PacketPlayerPosLook))
			return;

		mc.thePlayer.motionZ = 0.0;
		mc.thePlayer.motionX = 0.0;
		this.saveMe = false;
		this.timer.reset();
	}

	private boolean isBlockUnder() {
		for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
			AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

			if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty()) {
				return true;
			}
		}

		return false;
	}

}
