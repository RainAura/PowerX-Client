package cn.Power.command.commands;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.events.EventPacket;
import cn.Power.events.EventUpdate;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

public class CommandStp extends Command {
	public final Minecraft mc = Minecraft.getMinecraft();
	public static double x;
	public static double y;
	public static double z;
	public static double blocksPerTeleport = 100;
	public long lastTp;
	public Vec3 lastPos;

	public CommandStp(String[] commands) {
		super(commands);
		this.setArgs("stp x y z");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length < 3) {
			if (args.length >= 2 && args[1].contains("LookTP")) {
				x = (double) LookTP.TragetEntity.posX + Math.random()/100;
				y = (double) (LookTP.TragetEntity.posY + 2 + Math.random() / 100);
				z = (double) (LookTP.TragetEntity.posZ + Math.random()/100);
				
				C03PacketPlayer c2 = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + .00001, mc.thePlayer.posZ, true);

				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c2);

				for (int i = 0; i < 4; i++)
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));


				C03PacketPlayer c211 = new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false);

				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c211);
			} else {
				Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.WARNING);
			}
			
			
			
		} else {

			if (args.length >= 4) {

				x = Double.parseDouble(args[1]);
				y = Double.parseDouble(args[2]);
				z = Double.parseDouble(args[3]);

			} else if (args.length >= 3) {

				x = Double.parseDouble(args[1]);
				y = mc.thePlayer.posY;
				z = Double.parseDouble(args[2]);

			}


			try {

				C03PacketPlayer c2 = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + .00001, mc.thePlayer.posZ, true);

				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c2);

				for (int i = 0; i < 4; i++)
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));

				if (args.length == 5 && args[4].contains("p"))
					EventManager.register(this);

				C03PacketPlayer c211 = new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false);

				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(c211);

			} catch (Throwable c) {
				c.printStackTrace();
			}
			// blocksPerTeleport = 100;

			// EventManager.register(this);
		}
	}

	@EventTarget
	public void onUpdate(EventUpdate e) {
		final Vec3 targetVec = new Vec3(this.x, this.y, this.z);
		final Vec3 tpDirectionVec = targetVec.subtract(mc.thePlayer.getPositionVector()).normalize();
		final int chunkX = (int) Math.floor(mc.thePlayer.getPositionVector().xCoord / 16.0);
		final int chunkZ = (int) Math.floor(mc.thePlayer.getPositionVector().zCoord / 16.0);
		if (mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ).isLoaded()) {
			mc.thePlayer.getPositionVector();
			this.lastPos = mc.thePlayer.getPositionVector();
			if (targetVec.distanceTo(mc.thePlayer.getPositionVector()) < 0.5) {
				EventManager.unregister(this);
				return;
			}
			if (targetVec.distanceTo(mc.thePlayer.getPositionVector()) >= this.blocksPerTeleport) {
				final Vec3 vec = tpDirectionVec.scale(this.blocksPerTeleport);
				mc.thePlayer.setPosition(mc.thePlayer.posX + vec.xCoord, mc.thePlayer.posY + vec.yCoord,
						mc.thePlayer.posZ + vec.zCoord);
			} else {
				final Vec3 vec = tpDirectionVec.scale(targetVec.distanceTo(mc.thePlayer.getPositionVector()));
				mc.thePlayer.setPosition(mc.thePlayer.posX + vec.xCoord, mc.thePlayer.posY + vec.yCoord,
						mc.thePlayer.posZ + vec.zCoord);
			}
			this.lastTp = System.currentTimeMillis();
		} else if (this.lastTp + 2000L < System.currentTimeMillis()) {
			mc.thePlayer.posX = this.lastPos.xCoord;
			mc.thePlayer.posY = this.lastPos.yCoord;
			mc.thePlayer.posZ = this.lastPos.zCoord;
		}
	}

	@EventTarget
	public void onPacket(EventPacket ep) {
		Packet p = ep.getPacket();
		if (p instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) ep.getPacket();

			EventManager.unregister(this);
		}
	}

}
