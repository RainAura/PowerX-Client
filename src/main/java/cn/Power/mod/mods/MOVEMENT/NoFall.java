package cn.Power.mod.mods.MOVEMENT;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRender2D;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.AxisAlignedBB;

/*
 * 
 * NoFall.java PowerX
 * 
 */

public class NoFall extends Mod {

	int Packets;

	long milles = System.currentTimeMillis();

	public NoFall() {
		super("NoFall", Category.PLAYER);
	}

	@EventTarget
	public void onDraw(EventRender2D is) {
//		mc.fontRendererObj.drawStringWithShadow(Packets + "" , 88.0f, 88.0f, 0);
	}

	@EventTarget(1)
	public final void onPreMotion(EventPreMotion e) {

		if (Minecraft.thePlayer.capabilities.isFlying || Minecraft.thePlayer.capabilities.disableDamage
				|| Minecraft.thePlayer.motionY >= 0.0d)
			return;

		if (Minecraft.thePlayer.fallDistance > 3.0f) {
			if (mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0)
				if (Minecraft.thePlayer.fallDistance < 11.0f || this.isBlockUnder()) {

					if (Minecraft.thePlayer.motionY < -2.0) {
						Minecraft.thePlayer.motionY += 1.000000001E-4 * Minecraft.thePlayer.fallDistance;
					}
					Minecraft.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
				}

		}
	}

	@EventTarget(4)
	public final void onPacket(EventPacket event) {
		if (event.getPacket() instanceof C03PacketPlayer && event.getEventType() == EventType.SEND) {
			
			C03PacketPlayer e = (C03PacketPlayer) event.getPacket();

			if (mc.getNetHandler().S08count != 0) {
				if (Minecraft.getMinecraft().ingameGUI != null && !Minecraft.getMinecraft().ingameGUI.displayedTitle.isEmpty()
						&& Minecraft.getMinecraft().ingameGUI.displayedTitle.contains("SYNCING WORLD")) {
					((EventPacket) event).setCancelled(true);
					
					return;
					
					
				} else {
					mc.getNetHandler().S08count = 0;
				}


			}

			if (Minecraft.thePlayer.capabilities.isFlying || Minecraft.thePlayer.capabilities.disableDamage
					|| Minecraft.thePlayer.motionY > 0.0d)
				return;


			

			if (e.isMoving() && (Minecraft.thePlayer.fallDistance > 2.0f || mc.currentScreen instanceof GuiInventory)) {
				if (mc.thePlayer.lastTickPosY - mc.thePlayer.posY > 0)
					if (Minecraft.thePlayer.fallDistance < 11.0f || this.isBlockUnder()
							|| mc.currentScreen instanceof GuiInventory) {

						((EventPacket) event).setCancelled(true);

						Minecraft.getNetHandler().getNetworkManager().sendPacketNoEvent(
								new C03PacketPlayer.C04PacketPlayerPosition(e.x, e.y, e.z, e.onGround));

						// Do not send Pos Look Packet

					}

			}
		}
	}

	private boolean isBlockUnder() {
		for (int offset = 0; offset < Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(); offset += 2) {
			AxisAlignedBB boundingBox = Minecraft.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

			if (!Minecraft.theWorld.getCollidingBoundingBoxes(Minecraft.thePlayer, boundingBox).isEmpty()) {
				return true;
			}
		}

		return false;
	}

}
