package cn.Power.mod.mods.COMBAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventTick;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.notification.Notification;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.Timer;
import cn.Power.util.timeUtils.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class AntiBot extends Mod {

	public AntiBot() {
		super("AntiBot", Category.COMBAT);
	}

	@native0
	@Override
	public void onEnable() {
		this.setDisplayName("Hypixel");
	}
	
	@EventTarget
	public void onPacket(EventPacket e) {
		if(e.getPacket() instanceof S18PacketEntityTeleport) {
			S18PacketEntityTeleport te = (S18PacketEntityTeleport) e.getPacket();
			
            final Entity ent = mc.theWorld.getEntityByID(te.getEntityId());
            if (ent instanceof EntityPlayer && ent.isInvisibleToPlayer(mc.thePlayer) && ent.ticksExisted > 3 && mc.theWorld.playerEntities.contains(ent) && Freecam.freecamEntity != ent) {
				if (mc.getNetHandler().getPlayerInfo(ent.getUniqueID()) == null || mc.getNetHandler().getPlayerInfo(ent.getUniqueID()).getResponseTime() != 1) {
					if(ent != mc.thePlayer && !((EntityPlayer) ent).isSpectator())
						mc.theWorld.removeEntity(ent);
				}
            }
		}
	}

	@EventTarget(4)
	public void OnPre(EventPreMotion e) {

		// Loop through entity list
		Minecraft.theWorld.getLoadedEntityList().stream().filter(ent -> {

			return ent instanceof EntityPlayer && ent != Minecraft.thePlayer && !ent.isInvisible()
					&& (double) ent.posY > (double) Minecraft.thePlayer.posY
					&& (double) Minecraft.thePlayer.getDistanceToEntity(ent) <= 7.0
					&& ent != Freecam.freecamEntity && Minecraft.getNetHandler().getPlayerInfo(ent.getUniqueID()) != null
					&& Minecraft.getNetHandler().getPlayerInfo(ent.getUniqueID()).getResponseTime() != 1;

		}).forEach(o -> {

			EntityPlayer ent = (EntityPlayer) o;

			String formated = ent.getDisplayName().getFormattedText();

			if (Minecraft.getNetHandler().getPlayerInfo(ent.getUniqueID()).getResponseTime() > 20 &&formated.startsWith("\247c") && formated.endsWith("\247r") && !formated.startsWith("\247c[\247fYOUTUBE\247c]")) {
				Minecraft.thePlayer.addChatMessage((IChatComponent) new ChatComponentText("\2478[\247c" + Client.CLIENT_name
						+ "\2478]\247r\247r" + " Check " + ent.getDisplayName().getFormattedText() + " - Mod Bot!"));
				System.err.println(Minecraft.getNetHandler().getPlayerInfo(ent.getUniqueID()).getResponseTime());
				Minecraft.theWorld.removeEntity(ent);
				Minecraft.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(
						"\2478[\247c" + Client.CLIENT_name + "\2478]\247r\247r" + " Detected Mod Bot!"));

			}

		});

	}

	@native0
	@Override
	public void onDisable() {
		super.onDisable();
	}

	public List<EntityPlayer> getTabPlayerList() {
		NetHandlerPlayClient nhpc = Minecraft.thePlayer.sendQueue;
		List<EntityPlayer> list = new ArrayList<>();
		new GuiPlayerTabOverlay(mc, mc.ingameGUI);
		List players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(nhpc.getPlayerInfoMap());
		for (final Object o : players) {
			final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
			if (info == null) {
				continue;
			}
			list.add(Minecraft.theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
		}
		return list;
	}

}
