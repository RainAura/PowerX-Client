package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.mod.mods.WORLD.WayPoints;
import cn.Power.mod.mods.WORLD.WayPoints.Waypoint;
import cn.Power.notification.Notification.Type;
import cn.Power.util.Colors;
import cn.Power.util.misc.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

public class CommandWayPoints extends Command {
	public final static String chatPrefix = "\247c[\247fPower\247c]\2477 ";
	public Minecraft mc = Minecraft.getMinecraft();

	public CommandWayPoints(String[] commands) {
		super(commands);
		this.setArgs("-wp add/del/clear <Name> x y z");
	}

	@Override
	public void onCmd(String[] args) {
		
		if (args[1].equalsIgnoreCase("clear")) {
			WayPoints.waypoints.clear();
			
			ChatUtil.printChat(
					chatPrefix + "\2477Waypoints has been cleared.");
			
			return;
		}
		
		
		if (args.length < 2) {


			Client.instance.getNotificationManager().addNotification(this.getArgs(),2000, Type.WARNING);
			
			
			return;
		}

		if (args.length > 1) {
			if (args[1].equalsIgnoreCase("d") || args[1].equalsIgnoreCase("del")) {

				if (args.length == 3) {
					for (Waypoint waypoint : WayPoints.waypoints) {
						if (waypoint.getName().equalsIgnoreCase(args[2])) {
							WayPoints.waypoints.remove(waypoint);
							ChatUtil.printChat(
									chatPrefix + "\2477Waypoint \247c" + args[2] + "\2477 has been removed.");
							return;
						}
					}
					ChatUtil.printChat(
							chatPrefix + "\2477No Waypoint under the name \247c" + args[1] + "\2477 was found.");
					return;
				}
				getArgs();
				return;
			} else if (args[1].equalsIgnoreCase("a") || args[1].equalsIgnoreCase("add")) {
				if (args.length == 2) {
	
			
						WayPoints.waypoints.add(new Waypoint(args[2],
								mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ));
						ChatUtil.printChat(
								chatPrefix + "\2477Waypoint \247c" + args[2] + "\2477 has been successfully created.");
						return;
					
				} else if (args.length == 6) {
					

			
						WayPoints.waypoints.add(new Waypoint(args[2],
								Double.parseDouble(args[3]), Double.parseDouble(args[4]) + 1, Double.parseDouble(args[5])));
						ChatUtil.printChat(
								chatPrefix + "\2477Waypoint \247c" + args[2] + "\2477 has been successfully created.");
						return;
					
				} else {
					getArgs();
					return;
				}
			}
		} else {
			getArgs();
			return;
		}
	}

}
