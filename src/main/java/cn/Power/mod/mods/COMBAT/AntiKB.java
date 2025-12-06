package cn.Power.mod.mods.COMBAT;

import java.util.Random;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventMove;
import cn.Power.events.EventRender;
import cn.Power.events.EventTick;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.util.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.potion.Potion;

public class AntiKB extends Mod {
	public Value<String> mode = new Value("AntiKB", "Legit Mode", 0);
	public Value<Double> Horizontal = new Value<Double>("AntiKB_Horizontal", 100d, 0.0d, 100d, 1d);
	public Value<Double> Vertical = new Value<Double>("AntiKB_Vertical", 90d, 0.0d, 100d, 1d);
	public Value<Double> Chance = new Value<Double>("AntiKB_Chance", 100d, 0.0d, 100d, 1d);
	public Value<Boolean> WaterCheck = new Value("AntiKB_WaterCheck", true);
	public Value<Boolean> Fire = new Value("AntiKB_FireCheck", true);
	public Value<Boolean> Poison = new Value("AntiKB_PoisonCheck", true);
	public Value<Boolean> Gui = new Value("AntiKB_GuiCheck", true);
	public Value<Boolean> Fight = new Value("AntiKB_Fight", false);

	public AntiKB() {
		super("AntiKB", Category.COMBAT);
		this.mode.mode.add("Tick");
		this.mode.mode.add("Hurt");
		this.mode.mode.add("AB");
		this.mode.mode.add("Move");
	}

	public float getV() {
		Random Rand = new Random();
		float random = Rand.nextInt(100);
		float V = Vertical.getValueState().floatValue() == 100.0d ? 0 : random / 1000f;
		return (float) Vertical.getValueState().floatValue() + V;
	}

	public float getH() {
		Random Rand = new Random();
		float random = Rand.nextInt(100);
		float H = Horizontal.getValueState().floatValue() == 100.0d ? 0 : random / 1000f;
		return (float) Horizontal.getValueState().floatValue() + H;
	}

	public float getChance() {
		return (float) Chance.getValueState().floatValue();
	}

	public boolean Ycheck() {
		return mc.thePlayer.posY < 0;
	}

	@EventTarget
	public void onPre(EventUpdate event) {
		this.setDisplayName(this.mode.getModeAt(this.mode.getCurrentMode()));
	}

	@EventTarget
	public void onPre(EventTick event) {
		if (this.mode.isCurrentMode("Tick")) {
			Random Rand = new Random();
			int chance = Rand.nextInt(100);
			if (ModManager.getModByClass(Velocity.class).isEnabled())
				return;
			if (ModManager.getModByClass(Speed.class).isEnabled())
				return;
			if (WaterCheck.getValueState()) {
				if (mc.thePlayer.isInWater() || PlayerUtil.isOnLiquid()) {
					return;
				}
			}
			if (Gui.getValueState().booleanValue() && (mc.currentScreen != null
					|| mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
				return;
			}
			if (Fire.getValueState().booleanValue() && mc.thePlayer.canRenderOnFire()
					&& !mc.thePlayer.isPotionActive(Potion.fireResistance)) {
				return;
			}
			if (Poison.getValueState().booleanValue() && mc.thePlayer.isPotionActive(Potion.poison)) {
				return;
			}
			if (Fight.getValueState().booleanValue() && !mc.thePlayer.isSwingInProgress) {
				return;
			}
			if (mc.thePlayer == null || mc.theWorld == null || Ycheck()) {
				return;
			}
			if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
				if ((double) chance > 100.0D - this.Chance.getValueState().doubleValue()) {
					EntityPlayerSP thePlayer = mc.thePlayer;
					thePlayer.motionX *= getH() / 100.0D;
					EntityPlayerSP thePlayer2 = mc.thePlayer;
					if (getV() != 100)
						thePlayer2.motionY *= getV() / 100.0D;
					EntityPlayerSP thePlayer3 = mc.thePlayer;
					thePlayer3.motionZ *= getH() / 100.0D;
				}
			}
		}
	}

	@EventTarget
	public void onrender(EventRender event) {
		if (this.mode.isCurrentMode("AB")) {
			Random Rand = new Random();
			int chance = Rand.nextInt(100);
			if (ModManager.getModByClass(Velocity.class).isEnabled())
				return;
			if (ModManager.getModByClass(Speed.class).isEnabled())
				return;
			if (WaterCheck.getValueState()) {
				if (mc.thePlayer.isInWater() || PlayerUtil.isOnLiquid()) {
					return;
				}
			}
			if (Gui.getValueState().booleanValue() && (mc.currentScreen != null
					|| mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
				return;
			}
			if (Fire.getValueState().booleanValue() && mc.thePlayer.canRenderOnFire()
					&& !mc.thePlayer.isPotionActive(Potion.fireResistance)) {
				return;
			}
			if (Poison.getValueState().booleanValue() && mc.thePlayer.isPotionActive(Potion.poison)) {
				return;
			}
			if (Fight.getValueState().booleanValue() && !mc.thePlayer.isSwingInProgress) {
				return;
			}
			if (mc.thePlayer == null || mc.theWorld == null || Ycheck()) {
				return;
			}
			if (mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime && mc.thePlayer.maxHurtTime > 0) {
				if ((double) chance > 100.0D - this.Chance.getValueState().doubleValue()) {
					EntityPlayerSP thePlayer = mc.thePlayer;
					thePlayer.motionX *= getH() / 100.0D;
					EntityPlayerSP thePlayer2 = mc.thePlayer;
					if (getV() != 100)
						thePlayer2.motionY *= getV() / 100.0D;
					EntityPlayerSP thePlayer3 = mc.thePlayer;
					thePlayer3.motionZ *= getH() / 100.0D;
				}
			}
		}
	}

	@EventTarget
	public void onUpdate(EventUpdate eventUpdate) {
		if (this.mode.isCurrentMode("Hurt")) {
			Random Rand = new Random();
			int chance = Rand.nextInt(100);
			if (ModManager.getModByClass(Velocity.class).isEnabled())
				return;
			if (ModManager.getModByClass(Speed.class).isEnabled())
				return;
			if (WaterCheck.getValueState()) {
				if (mc.thePlayer.isInWater() || PlayerUtil.isOnLiquid()) {
					return;
				}
			}
			if (Gui.getValueState().booleanValue() && (mc.currentScreen != null
					|| mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
				return;
			}
			if (Fire.getValueState().booleanValue() && mc.thePlayer.canRenderOnFire()
					&& !mc.thePlayer.isPotionActive(Potion.fireResistance)) {
				return;
			}
			if (Poison.getValueState().booleanValue() && mc.thePlayer.isPotionActive(Potion.poison)) {
				return;
			}
			if (Fight.getValueState().booleanValue() && !mc.thePlayer.isSwingInProgress) {
				return;
			}
			if (mc.thePlayer == null || mc.theWorld == null || Ycheck()) {
				return;
			}
			if (mc.thePlayer.maxHurtResistantTime != mc.thePlayer.hurtResistantTime
					|| mc.thePlayer.maxHurtResistantTime == 0) {
				return;
			}
			if ((double) chance > 100.0D - this.Chance.getValueState().doubleValue()) {
				final float n = (float) this.getH() / 100.0f;
				final float n2 = (float) this.getV() / 100.0f;
				final EntityPlayerSP thePlayer = mc.thePlayer;
				thePlayer.motionX *= n;
				final EntityPlayerSP thePlayer2 = mc.thePlayer;
				thePlayer2.motionZ *= n;
				final EntityPlayerSP thePlayer3 = mc.thePlayer;
				thePlayer3.motionY *= n2;
			}
		}
	}

	@EventTarget
	public void onmove(EventMove e) {
		if (this.mode.isCurrentMode("Move")) {
			Random Rand = new Random();
			int chance = Rand.nextInt(100);
			/*
			 * if (mc.thePlayer.maxHurtResistantTime != mc.thePlayer.hurtResistantTime ||
			 * mc.thePlayer.maxHurtResistantTime == 0) { return; }
			 */
			if (ModManager.getModByClass(Velocity.class).isEnabled())
				return;
			if (ModManager.getModByClass(Speed.class).isEnabled())
				return;
			if (WaterCheck.getValueState()) {
				if (mc.thePlayer.isInWater() || PlayerUtil.isOnLiquid()) {
					return;
				}
			}
			if (Gui.getValueState().booleanValue() && (mc.currentScreen != null
					|| mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
				return;
			}
			if (Fire.getValueState().booleanValue() && mc.thePlayer.canRenderOnFire()
					&& !mc.thePlayer.isPotionActive(Potion.fireResistance)) {
				return;
			}
			if (Poison.getValueState().booleanValue() && mc.thePlayer.isPotionActive(Potion.poison)) {
				return;
			}
			if (Fight.getValueState().booleanValue() && !mc.thePlayer.isSwingInProgress) {
				return;
			}
			if (mc.thePlayer == null || mc.theWorld == null || Ycheck()) {
				return;
			}
			if (mc.thePlayer.hurtResistantTime >= 19 && mc.thePlayer.maxHurtTime > 0) {
				if ((double) chance > 100.0D - this.Chance.getValueState().doubleValue()) {
					final float xz = (float) this.getH() / 100.0f;
					final float y = (float) this.getV() / 100.0f;
					final EntityPlayerSP thePlayer = mc.thePlayer;
					e.setX(thePlayer.motionX *= xz);
					final EntityPlayerSP thePlayer2 = mc.thePlayer;
					e.setZ(thePlayer2.motionZ *= xz);
					final EntityPlayerSP thePlayer3 = mc.thePlayer;
					e.setY(thePlayer3.motionY *= y);
				}
			}
		}
	}
}
