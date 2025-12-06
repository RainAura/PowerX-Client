package cn.Power.command.commands;

import cn.Power.Client;
import cn.Power.command.Command;
import cn.Power.notification.Notification.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class CommandXM extends Command {
	Minecraft mc;
	Entity entity;

	public CommandXM(String[] commands) {
		super(commands);
		this.setArgs("XM");
	}

	@Override
	public void onCmd(String[] args) {
		if (args.length < 1) {
			Client.instance.getNotificationManager().addNotification(this.getArgs(), Type.WARNING);
		} else {
			this.mc = Minecraft.getMinecraft();
			if (this.mc.thePlayer.ridingEntity != null) {
				this.entity = this.mc.thePlayer.ridingEntity;
				mc.theWorld.removeEntity(entity);
			}
			Client.instance.getNotificationManager().addNotification("Go up", Type.WARNING);
		}
	}
}
