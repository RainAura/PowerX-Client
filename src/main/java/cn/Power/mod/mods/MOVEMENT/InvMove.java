package cn.Power.mod.mods.MOVEMENT;

import java.util.Objects;

import org.lwjgl.input.Keyboard;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

public class InvMove extends Mod {
	
	public static Value<Boolean> CheckMove = new Value<Boolean>("InvMove_CheckMove", true);
	
	
	public static long cooldown;

	public InvMove() {
		super("InvMove", Category.MOVEMENT);
	}

	@EventTarget
	public void onUpdate(EventUpdate event) {
		
		
			mc.thePlayer.movementInput.moveStrafe = 0;
			mc.thePlayer.movementInput.moveForward = 0;
			
		
			KeyBinding[] key = { Mod.mc.gameSettings.keyBindForward, Mod.mc.gameSettings.keyBindBack,
					Mod.mc.gameSettings.keyBindLeft, Mod.mc.gameSettings.keyBindRight,
					Mod.mc.gameSettings.keyBindSprint, Mod.mc.gameSettings.keyBindJump };
			KeyBinding[] array;
			
			
			if (Mod.mc.currentScreen != null && !(Mod.mc.currentScreen instanceof GuiChat)) {
				for (int length = (array = key).length, i = 0; i < length; ++i) {
					KeyBinding b = array[i];
					KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
				}
			} else if (Objects.isNull(mc.currentScreen)) {
				for (int length = (array = key).length, i = 0; i < length; ++i) {
					KeyBinding b = array[i];
					if (!Keyboard.isKeyDown(b.getKeyCode())) {
						KeyBinding.setKeyBindState(b.getKeyCode(), false);
					}
				}
			}
	}
}
