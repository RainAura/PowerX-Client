package cn.Power.command.commands;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.command.Command;
import cn.Power.command.CommandManager;
import cn.Power.events.EventPacket;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S01PacketJoinGame;

public class CommandRejoin extends Command {

	public final Minecraft mc = Minecraft.getMinecraft();
	boolean succ,start;

	public CommandRejoin(String[] commands) {
		super(commands);
		this.setArgs("rejoin");
	}

	@Override
	public void onCmd(String[] p0) {
		succ = start = false;
		Minecraft.thePlayer.sendChatMessage("/l");
		EventManager.register(this);
	}
	
	@EventTarget
	public void onRespawn(EventPacket event) {
		
		if(event.getPacket() instanceof S01PacketJoinGame) {
		
			Minecraft.thePlayer.sendChatMessage("/rejoin");
			EventManager.unregister(this);
			
			event.setCancelled(true);
			
		}
		
	}

}
