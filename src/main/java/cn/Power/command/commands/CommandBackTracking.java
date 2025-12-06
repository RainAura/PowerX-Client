package cn.Power.command.commands;

import cn.Power.command.Command;
import cn.Power.util.SkyBlockUtils;
import net.minecraft.client.Minecraft;

public class CommandBackTracking extends Command {

	public CommandBackTracking(String[] commands) {
		super(commands);
		this.setArgs("bt");
	}
 
	@Override
	public void onCmd(String[] args) {
		
		double[] oldPos = new double[] {Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ};
		
		if(SkyBlockUtils.isMWgame())
			Minecraft.getMinecraft().thePlayer.setPosition(-Minecraft.getMinecraft().thePlayer.posX, 70, 600);
		
		else
			Minecraft.getMinecraft().thePlayer.setPosition(-Minecraft.getMinecraft().thePlayer.posX, 170, 600);
		
		new Thread(()->{
			
			try {
				Thread.sleep(1600L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Minecraft.getMinecraft().thePlayer.setPosition(oldPos[0], oldPos[1] + 10, oldPos[2]);
		}).start();;
		
	}
	
}
