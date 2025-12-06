package cn.Power.mod.mods.WORLD;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.darkmagician6.eventapi.EventTarget;
import com.sun.javafx.geom.Vec3d;

import cn.Power.Client;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.notification.Notification;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class AntiVanish extends Mod {

	private List<Integer> vanished = new CopyOnWriteArrayList<Integer>();

	public AntiVanish() {
		super("AntiVanish", Category.WORLD);
	}

	@EventTarget(1)
	private void packet(EventPacket ep) {
		if (ep.getPacket() instanceof S13PacketDestroyEntities) {

			ep.setCancelled(true);

			S13PacketDestroyEntities destroy = (S13PacketDestroyEntities) ep.getPacket();

			for (int i = 0; i < destroy.getEntityIDs().length; ++i) {
				if (Minecraft.theWorld.getEntityByID(destroy.getEntityIDs()[i]) != null)
					if (!(Minecraft.theWorld.getEntityByID(destroy.getEntityIDs()[i]) instanceof EntityPlayer)) {

						final int entityid = destroy.getEntityIDs()[i];
						mc.addScheduledTask(() -> mc.theWorld.removeEntityFromWorld(entityid));

					} else if (!vanished.contains(destroy.getEntityIDs()[i]))
						vanished.add(destroy.getEntityIDs()[i]);

			}

		} else

		if (ep.getPacket() instanceof S0FPacketSpawnMob) {

			ep.setCancelled(true);

			S0FPacketSpawnMob packetIn = (S0FPacketSpawnMob) ep.getPacket();

			EntityLivingBase entitylivingbase = (EntityLivingBase) EntityList.createEntityByID(packetIn.getEntityType(),
					Minecraft.theWorld);

			double d0 = packetIn.getX() / 32.0D;
			double d1 = packetIn.getY() / 32.0D;
			double d2 = packetIn.getZ() / 32.0D;
			float f = packetIn.getYaw() * 360 / 256.0F;
			float f1 = packetIn.getPitch() * 360 / 256.0F;

			entitylivingbase.serverPosX = packetIn.getX();
			entitylivingbase.serverPosY = packetIn.getY();
			entitylivingbase.serverPosZ = packetIn.getZ();
			entitylivingbase.renderYawOffset = entitylivingbase.rotationYawHead = packetIn.getHeadPitch() * 360
					/ 256.0F;
			Entity[] aentity = entitylivingbase.getParts();

			if (aentity != null) {
				int i = packetIn.getEntityID() - entitylivingbase.getEntityId();

				for (int j = 0; j < aentity.length; ++j) {
					aentity[j].setEntityId(aentity[j].getEntityId() + i);
				}
			}

			entitylivingbase.setEntityId(packetIn.getEntityID());
			entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
			entitylivingbase.motionX = packetIn.getVelocityX() / 8000.0F;
			entitylivingbase.motionY = packetIn.getVelocityY() / 8000.0F;
			entitylivingbase.motionZ = packetIn.getVelocityZ() / 8000.0F;

			mc.addScheduledTask(() -> {

				mc.theWorld.addEntityToWorld(packetIn.getEntityID(), entitylivingbase);
				List<DataWatcher.WatchableObject> list = packetIn.func_149027_c();

				if (list != null) {
					entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
				}
			});

			vanished.forEach(entityid -> {

				EntityPlayer player = (EntityPlayer) mc.theWorld.getEntityByID(entityid);

				if (Math.abs(Math.abs(MathHelper.wrapAngleTo180_float(player.getRotationYawHead()))
						- Math.abs(MathHelper.wrapAngleTo180_float(entitylivingbase.rotationYawHead))) < 20) {

					BlockPos p = new BlockPos(d0, d1, d2);

					if (player.getDistanceSq(p) < 3) {

						// if(getDistanceBetweenAngles( f , player.rotationYaw) < 20 )
						// if(getDistanceBetweenAngles( f , player.rotationYaw) < 10 ) {

						if (player.ridingEntity == null)
							player.mountEntity(entitylivingbase);

						// System.out.println("kkk");

					}

				}

				// }

				// }

			});

		}

	}

	public static float getDistanceBetweenAngles(float angle1, float angle2) {
		float angle = Math.abs(angle1 - angle2) % 360.0f;
		if (angle > 180.0f) {
			angle = 360.0f - angle;
		}
		return angle;
	}

}
