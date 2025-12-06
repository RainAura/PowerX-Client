package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.MOVEMENT.ZoomFly;
import cn.Power.util.SkyBlockUtils;

public class Timer extends Mod {
	private boolean tick;

	public Value<Double> UHCTimer = new Value<Double>("Timer_UHCTimer", 1.07d, 1.0d, 1.1d, 0.01d);
	public Value<Double> Timer = new Value<Double>("Timer_Timer", 1.07d, 0.1d, 7d, 0.01d);
	public Value<Boolean> gostTimer = new Value<Boolean>("Timer_UHCTimer", true);

	public Timer() {
		super("Timer", Category.WORLD);
	}

	@EventTarget
	private void pub(EventRender e) {
		
		if(mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
			mc.timer.timerSpeed = 1.0f;
			
			EventManager.unregister(this);
			this.set(false);
			
			return;
		}
	
		if(mc.thePlayer == null) {
			mc.timer.timerSpeed = 1.0f;
		
			return;
		}
		
		ZoomFly fly = (ZoomFly) ModManager.getModByClass(ZoomFly.class);
		Speed speed = (Speed) ModManager.getModByClass(Speed.class);
		KillAura killAura = (KillAura) ModManager.getModByClass(KillAura.class);
		this.tick = !this.tick;

		if (gostTimer.getValueState().booleanValue()) {
			if (!fly.isEnabled() && !speed.isEnabled() && !killAura.isEnabled() && SkyBlockUtils.isUHCgame())
				mc.timer.timerSpeed = this.tick ? 1.0F : UHCTimer.getValueState().floatValue();
			this.setDisplayName(SkyBlockUtils.isUHCgame() ? UHCTimer.getValueState().toString() : "");
		} else {
			this.setDisplayName(Timer.getValueState().toString());
			mc.timer.timerSpeed = this.Timer.getValueState().floatValue();

		}
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		super.onDisable();
	}

}
