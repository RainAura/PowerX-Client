package cn.Power.mod.mods.COMBAT;

import java.text.DecimalFormat;
import java.util.Random;

import org.lwjgl.input.Mouse;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.PlayerUtil;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoClicker extends Mod {

	public Value<Double> StartDelay = new Value<Double>("AutoClicker_StartDelay", 3d, 0d, 10d, 1d);

	public Value<Double> maxcps = new Value<Double>("AutoClicker_MaxCPS", 12d, 1d, 20d, 1d);
	public Value<Double> mincps = new Value<Double>("AutoClicker_MinCPS", 8d, 1d, 20d, 1d);

	public Value<Boolean> FakeAutoBlock = new Value("AutoClicker_FakeAutoBlock", true);
	private double tick;
	private double startDelay;
	private double delay = 0.0;
	private TimeHelper time = new TimeHelper();
	private TimeHelper time2 = new TimeHelper();
	Random random = new Random();

	public AutoClicker() {
		super("AutoClicker", Category.COMBAT);
	}

	@EventTarget
	private void startDelay(EventTick event) {

		if (this.time2.delay(100.0) && Mouse.isButtonDown((int) 0)) {
			this.startDelay += 1.0;
			this.time2.reset();
			return;
		}
		if (Mouse.isButtonDown((int) 0))
			return;
		this.startDelay = 0.0;
	}

	@EventTarget
	public void onUpdate(EventTick event) {
		DecimalFormat df = new DecimalFormat("0.00");
		BlockPos bp = this.mc.thePlayer.rayTrace(4.0, 0.0f).getBlockPos();
		boolean isblock = this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
				&& this.mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != Blocks.air
				&& this.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY;
		this.setDisplayName(
				"CPS:" + (int) mincps.getValueState().intValue() + "-" + (int) maxcps.getValueState().intValue());
		if (!this.mc.gameSettings.keyBindAttack.isKeyDown())
			return;
		if (!this.time.delay(this.delay))
			return;
		if (this.mc.currentScreen != null)
			return;
		if (this.startDelay < StartDelay.getValueState())
			return;
		if (isblock)
			return;
		PlayerUtil.blockHit(this.mc.objectMouseOver.entityHit, FakeAutoBlock.getValueState());
		mc.clickMouse();
		mc.leftClickCounter = 0;
		this.delay();
		this.time.reset();

	}

	private void delay() {
		float minCps = (float) mincps.getValueState().floatValue();
		float maxCps = (float) maxcps.getValueState().floatValue();
		float minDelay = 1000.0f / minCps;
		float maxDelay = 1000.0f / maxCps;
		this.delay = (double) maxDelay + this.random.nextDouble() * (double) (minDelay - maxDelay);
	}
}