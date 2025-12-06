package cn.Power.mod.mods.WORLD;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventPacket;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.WayPoints.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class LightningTrack extends Mod {

	public Value<Boolean> wp = new Value<Boolean>("LightningTrack_WayPoints", false);

	public LightningTrack() {
		super("LightningTrack", Category.WORLD);
	}

	@EventTarget
	public void onPacketReceive(EventPacket packetEvent) {
		if (packetEvent.packet instanceof S2CPacketSpawnGlobalEntity) {
			S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity) packetEvent.packet;
//			if (packetIn.func_149053_g() == 1) {
				float x = (float) (packetIn.func_149051_d() / 32.0D);
				float y = (float) (packetIn.func_149050_e() / 32.0D);
				float z = (float) (packetIn.func_149049_f() / 32.0D);
				float xDiff = (float) (Minecraft.thePlayer.posX - x);
				float yDiff = (float) (Minecraft.thePlayer.posY - y);
				float zDiff = (float) (Minecraft.thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
//	            mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(
//						"\2478[\247c" + Client.CLIENT_name + "\2478]\247r\247r"+ "§a距离§c" + (int)dis +" 检测到雷击! "+"§a坐标§BX: §r" + (int)x + " §BY: §r" + (int)y + " §BZ: §r" + (int)z));

				if(dis > 0) {
					formattedMsg(
							"§a距离§c " + (int) dis + "m §b检测到雷击! " + "§a坐标§BX: §r" + (int) x + " §BY: §r" + (int) y
									+ " §BZ: §r" + (int) z + "  ",
							"&9[§e§l点我TP&r&9]", "&aClick to TP~",
							new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp " + x + " " + 120 + " " + z));
					
					if(wp.getValueState() && ModManager.getModByClass(WayPoints.class).isEnabled()) {
					
						Waypoint zerot = new Waypoint("LightningTrack" + WayPoints.waypoints.size(), x, y, z );
						WayPoints.waypoints.add(zerot);
					}
				}
//			}
		}
	}
	
	@native0
	public static void formattedMsg(String message, String base, String hover, ClickEvent clickEvent) {
		ChatStyle style = new ChatStyle();
		if (hover.length() > 0) {
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					(IChatComponent) new ChatComponentText(replace(hover))));
		}
		if (clickEvent != null) {
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					getSubString(clickEvent.toString(), "value='", "'}")));
		}
		IChatComponent txt;
		String prefix = "§8[§c" + Client.CLIENT_name + "§8]§r";
		if (base.equals("%r")) {
			txt = new ChatComponentText(replace(prefix + " " + message)).setChatStyle(style);
		} else {
			String[] split = message.split(base);
			txt = new ChatComponentText(replace(prefix + " " + split[0]))
					.appendSibling(new ChatComponentText(replace(base)).setChatStyle(style));
			if (split.length == 2) {
				txt.appendSibling((IChatComponent) new ChatComponentText(replace(split[1])));
			}
		}
		Minecraft.thePlayer.addChatMessage(txt);
	}
	
	public static String getSubString(String text, String left, String right) {
		String result = "";
		int zLen;
		if (left == null || left.isEmpty()) {
			zLen = 0;
		} else {
			zLen = text.indexOf(left);
			if (zLen > -1) {
				zLen += left.length();
			} else {
				zLen = 0;
			}
		}
		int yLen = text.indexOf(right, zLen);
		if (yLen < 0 || right == null || right.isEmpty()) {
			yLen = text.length();
		}
		result = text.substring(zLen, yLen);
		return result;
	}

	public static String replace(String text) {
		return text.replaceAll("&", "§");
	}
}
