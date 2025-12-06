package cn.Power.mod.mods.PLAYER;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import cn.Power.events.EventPacket;
import cn.Power.events.EventRender;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.FlatColors;
import cn.Power.util.RenderUtil;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Blink extends Mod {
	TimeHelper time = new TimeHelper();
	private ArrayList packets = new ArrayList();
	private ArrayList positions = new ArrayList();

	public Blink() {
		super("Blink", Category.PLAYER);
	}

	public void onEnable() {
		if (this.mc.thePlayer != null && this.mc.theWorld != null) {
			double x = this.mc.thePlayer.posX;
			double y = this.mc.thePlayer.posY;
			double z = this.mc.thePlayer.posZ;
			float yaw = this.mc.thePlayer.rotationYaw;
			float pitch = this.mc.thePlayer.rotationPitch;
			EntityPlayer player = null;
			EntityOtherPlayerMP ent = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
			ent.inventory = this.mc.thePlayer.inventory;
			ent.inventoryContainer = this.mc.thePlayer.inventoryContainer;
			ent.setPositionAndRotation(x, y, z, yaw, pitch);
			ent.rotationYawHead = this.mc.thePlayer.rotationYawHead;
			this.mc.theWorld.addEntityToWorld(-1, ent);
		}

		this.packets.clear();
		this.positions.clear();
		super.onEnable();
	}

	public void onDisable() {
		this.mc.theWorld.removeEntityFromWorld(-1);
		Iterator var2 = this.packets.iterator();
		try {
			while (var2.hasNext()) {
				Packet packet = (Packet) var2.next();
				this.mc.thePlayer.sendQueue.addToSendQueue(packet);
				this.time.reset();
			}
		} catch (Exception e) {
		}
		

		this.packets.clear();
		super.onDisable();
	}

	@EventTarget
	public void onSendPacket(EventPacket event) {
		if(event.getEventType() == EventType.SEND) {
		if (event.getPacket() instanceof C03PacketPlayer) {
			this.packets.add(event.getPacket());
			event.setCancelled(true);
		} else if (event.getPacket() instanceof C08PacketPlayerBlockPlacement
				|| event.getPacket() instanceof C07PacketPlayerDigging
				|| event.getPacket() instanceof C09PacketHeldItemChange
				|| event.getPacket() instanceof C02PacketUseEntity) {
			this.packets.add(event.getPacket());
			event.setCancelled(true);
		}
		}
	}

	@EventTarget
	public void onReceive(EventPacket event) {

	}

	@EventTarget
	public void onRender(EventRender event) {
		this.addPosition();
		GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glEnable(2848);
		GL11.glDisable(2929);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		RenderUtil.color(FlatColors.BLUE.c);
		GL11.glLoadIdentity();
		boolean bobbing = this.mc.gameSettings.viewBobbing;
		this.mc.gameSettings.viewBobbing = false;
		this.mc.entityRenderer.orientCamera(this.mc.timer.renderPartialTicks);
		GL11.glBegin(3);
		Iterator var4 = this.positions.iterator();

		while (var4.hasNext()) {
			Vector3f pos = (Vector3f) var4.next();
			this.mc.getRenderManager();
			this.mc.getRenderManager();
			this.mc.getRenderManager();
			GL11.glVertex3d((double) pos.x - mc.getRenderManager().renderPosX, (double) pos.y - mc.getRenderManager().renderPosY,
					(double) pos.z - mc.getRenderManager().renderPosZ);
		}

		GL11.glEnd();
		this.mc.gameSettings.viewBobbing = bobbing;
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	private void addPosition() {
		double x = this.mc.thePlayer.posX;
		double y = this.mc.thePlayer.posY;
		double z = this.mc.thePlayer.posZ;
		Vector3f vec = new Vector3f((float) x, (float) y, (float) z);
		if (this.mc.thePlayer.movementInput.moveForward != 0.0F || this.mc.gameSettings.keyBindJump.pressed
				|| this.mc.thePlayer.movementInput.moveStrafe != 0.0F) {
			this.positions.add(vec);
		}

	}
}