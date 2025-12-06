package cn.Power.mod.mods.COMBAT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPostMotion;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.Fly;
import cn.Power.mod.mods.MOVEMENT.ZoomFly;
import cn.Power.util.RotationUtils;
import cn.Power.util.misc.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.PotionEffect;

public class AutoPot extends Mod {
	private Value<Double> HEALTH = new Value<Double>("AutoPot_Health", 6.0, 1.0, 10.0, 0.5);
	private Value<Boolean> SPEED = new Value<Boolean>("AutoPot_Speed", true);
	private Value<Boolean> REGEN = new Value<Boolean>("AutoPot_Regen", true);
	private Value<Boolean> PREDICT = new Value<Boolean>("AutoPot_Predict", false);
	public static boolean potting;
	Timer timer = new Timer();

	public AutoPot() {
		super("AutoPot", Category.COMBAT);
	} 

	public static boolean isPotting() {
		return potting;
	}

	@EventTarget
	public void onUpdate(EventPostMotion e) {
		final boolean speed = SPEED.getValueState();
		final boolean regen = REGEN.getValueState();
		if (timer.check(200)) {
			if (potting)
				potting = false;
		}
		int spoofSlot = getBestSpoofSlot();
		int pots[] = { 6, -1, -1 };
		if (regen)
			pots[1] = 10;
		if (speed)
			pots[2] = 1;

		for (int i = 0; i < pots.length; i++) {
			if (pots[i] == -1)
				continue;
			if (pots[i] == 6 || pots[i] == 10) {
				if (timer.check(900) && !mc.thePlayer.isPotionActive(pots[i])) {
					if (mc.thePlayer.getHealth() < (HEALTH.getValueState()).doubleValue() * 2) {
						getBestPot(spoofSlot, pots[i]);
					}
				}
			} else if (timer.check(1000) && !mc.thePlayer.isPotionActive(pots[i])) {
				getBestPot(spoofSlot, pots[i]);
			}
		}
	}

	public void swap(int slot1, int hotbarSlot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
	}

	float[] getRotations() {
		double movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 26.0D;
		double movedPosY = mc.thePlayer.boundingBox.minY - 3.6D;
		double movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 26.0D;
		if (PREDICT.getValueState())
			return RotationUtils.getRotationFromPosition(movedPosX, movedPosZ, movedPosY);
		else
			return new float[] {mc.thePlayer.rotationYaw, 90 };
	}

	int getBestSpoofSlot() {
		int spoofSlot = 5;
		for (int i = 36; i < 45; i++) {
			if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				spoofSlot = i - 36;
				break;
			} else if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
				spoofSlot = i - 36;
				break;
			}
		}
		return spoofSlot;
	}

	void getBestPot(int hotbarSlot, int potID) {
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
					&& !ModManager.getModByClass(Fly.class).isEnabled()
					&& !ModManager.getModByClass(ZoomFly.class).isEnabled()
					&& (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemPotion) {
					ItemPotion pot = (ItemPotion) is.getItem();
					if (pot.getEffects(is).isEmpty())
						return;
					PotionEffect effect = (PotionEffect) pot.getEffects(is).get(0);
					int potionID = effect.getPotionID();
					if (potionID == potID)
						if (ItemPotion.isSplash(is.getMetadata()) && isBestPot(pot, is)) {
							if (36 + hotbarSlot != i)
								swap(i, hotbarSlot);
							timer.reset();
							boolean canpot = true;
							int oldSlot = mc.thePlayer.inventory.currentItem;
							mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(hotbarSlot));
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(getRotations()[0], getRotations()[1], mc.thePlayer.onGround));
							mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
							mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook( mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
							break;
						}
				}
			}
		}
	}

	boolean isBestPot(ItemPotion potion, ItemStack stack) {
		if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
			return false;
		PotionEffect effect = (PotionEffect) potion.getEffects(stack).get(0);
		int potionID = effect.getPotionID();
		int amplifier = effect.getAmplifier();
		int duration = effect.getDuration();
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (is.getItem() instanceof ItemPotion) {
					ItemPotion pot = (ItemPotion) is.getItem();
					if (pot.getEffects(is) != null) {
						for (Object o : pot.getEffects(is)) {
							PotionEffect effects = (PotionEffect) o;
							int id = effects.getPotionID();
							int ampl = effects.getAmplifier();
							int dur = effects.getDuration();
							if (id == potionID && ItemPotion.isSplash(is.getMetadata())) {
								if (ampl > amplifier) {
									return false;
								} else if (ampl == amplifier && dur > duration) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

}
