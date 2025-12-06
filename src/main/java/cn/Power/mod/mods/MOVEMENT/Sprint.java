package cn.Power.mod.mods.MOVEMENT;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class Sprint extends Mod {
	public static boolean backward = true;

	public Sprint() {
		super("Sprint", Category.MOVEMENT);
	}

	@EventTarget
	public void onUpdate(EventPreMotion event) {
		
		
		
		   if (!Minecraft.thePlayer.isMovingKeyBindingActive() || Minecraft.thePlayer.isSneaking() ||
	                (!(Minecraft.thePlayer.getFoodStats().getFoodLevel() > 6.0F || Minecraft.thePlayer.capabilities.allowFlying)
	                		|| MovementInput.moveForward <= 0.8 || mc.thePlayer.isCollidedHorizontally
	                		|| mc.thePlayer.isInLava() || mc.thePlayer.isInWater() || mc.thePlayer.isOnLadder()
	                		|| mc.thePlayer.isRiding() || mc.thePlayer.isSneaking()
	                		|| mc.thePlayer.isDead
	                		|| mc.thePlayer.isPotionActive(Potion.moveSlowdown)
	                		|| ModManager.getModByClass(Scaffold.class).isEnabled()
	//                		|| mc.thePlayer.isBlocking()
	                		)
	                ) {
	            Minecraft.thePlayer.setSprinting(false);
	            return;
	        }


		   		Minecraft.thePlayer.setSprinting(true);
	}
}
