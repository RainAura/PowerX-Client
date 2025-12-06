package cn.Power.command.commands;

import java.util.Collection;
import java.util.List;

import cn.Power.command.Command;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class CommandPall extends Command {

	public static Minecraft mc = Minecraft.getMinecraft();

	public CommandPall(String[] commands) {
		super(commands);
		this.setArgs("Pall");
	}

	@Override
	public void onCmd(String[] p0) {
	

		
		(new Thread(() -> {
			myRun();
		})).start();
		

	}

	public void myRun() {

		NetHandlerPlayClient nhpc = Minecraft.thePlayer.sendQueue;
		new GuiPlayerTabOverlay(mc, mc.ingameGUI);
		List<?> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(nhpc.getPlayerInfoMap());
		for (final Object o : players) {
			final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
			if (info == null) {
				continue;
			}
			Minecraft.thePlayer.sendChatMessage("/p " + info.getGameProfile().getName());
			
			try {
				Thread.sleep(1050L);
			} catch (InterruptedException e) {

			}
		}

		Minecraft.getMinecraft();
		Collection<?> playersC = (Collection<?>) Minecraft.getNetHandler().getGameProfile();
		playersC.forEach(loadedPlayer -> {
			String loadedPlayerName = ((NetworkPlayerInfo) loadedPlayer).getGameProfile().getName();
			Minecraft.getMinecraft();
			Minecraft.thePlayer.sendChatMessage("/p " + loadedPlayerName);

			try {
				Thread.sleep(1050L);
			} catch (InterruptedException e) {

			}

		});

	}

}
