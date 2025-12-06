package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CommandGetPos extends Command {

	public CommandGetPos(String[] commands) {
		super(commands);
		this.setArgs("getpos [<compass(获取指南针坐标)> / <me(获取当前位置)>] ");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length < 2) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(),2000, Type.INFO);
			return;
		}

		if (args[1].equalsIgnoreCase("compass") || args[1].equalsIgnoreCase("c")) {

			BlockPos blockpos = Minecraft.getMinecraft().theWorld.getSpawnPoint();
				float x = (int) (blockpos.getX());
				float z = (int) (blockpos.getZ());
				float xDiff = (float) (Minecraft.getMinecraft().thePlayer.posX - x);
				float zDiff = (float) (Minecraft.getMinecraft().thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + zDiff * zDiff);
				
				LookTP.formattedMsg(
						"§a目标 §r" + "指南针" + " §a距离§c " + (int) dis + "§b 米 " + "§a坐标§BX: §r" + (int) x + " §BZ: §r"
								+ (int) z + "  ",
						"&9[§e§l点我TP&r&9]", "&aClick to TP~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp " + x + " " + args[1] != null ? args[1] : 120 + " " + z));
			
		}else if (args[1].equalsIgnoreCase("compassreal") || args[1].equalsIgnoreCase("cr")) {

			BlockPos blockpos = Minecraft.getMinecraft().theWorld.getSpawnPoint();
				float x = (int) (blockpos.getX());
				float z = (int) (blockpos.getZ());
				float xDiff = (float) (Minecraft.getMinecraft().thePlayer.posX - x);
				float zDiff = (float) (Minecraft.getMinecraft().thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + zDiff * zDiff);
				LookTP.formattedMsg(
						"§a目标 §r" + "指南针" + " §a距离§c " + (int) dis + "§b 米 " + "§a坐标§BX: §r" + (int) x + " §BZ: §r"
								+ (int) z + "  ",
						"&9[§e§l点我TP&r&9]", "&aClick to TP~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp " + x + " " + blockpos.getY() + " " + z));
			
		} else {
			BlockPos blockpos = Minecraft.getMinecraft().theWorld.getSpawnPoint();
				float x = (int) (blockpos.getX());
				float z = (int) (blockpos.getX());
				float xDiff = (float) (Minecraft.getMinecraft().thePlayer.posX - x);
				float zDiff = (float) (Minecraft.getMinecraft().thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + zDiff * zDiff);
				LookTP.formattedMsg(
						"§a目标 §r" + "当前位置" + " §a距离§c " + (int) dis + "§b 米 " + "§a坐标§BX: §r" + (int) x + " §BZ: §r"
								+ (int) z + "  ",
						"&9[§e§l点我TP&r&9]", "&aClick to TP~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp " + x + " " + 120 + " " + z));
			
		}
	}
}

//
