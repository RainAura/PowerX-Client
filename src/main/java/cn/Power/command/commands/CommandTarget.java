package cn.Power.command.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.native0;
import cn.Power.command.Command;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventTick;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.notification.Notification.Type;
import cn.Power.util.AStarCustomPathFinder;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.ChatUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class CommandTarget extends Command {
	static Minecraft mc = Minecraft.getMinecraft();

	public CommandTarget(String[] command) {
		super(command);
		this.setArgs("target name/clean");
	}

	@native0
	@Override
	public void onCmd(String[] args) {

		if(args.length == 1)
			return;
		if(args[1].equalsIgnoreCase("clean") || args[1].equalsIgnoreCase("c") || args[1].equalsIgnoreCase("clear")) {
			KillAura.vips.clear();
			ChatUtil.printChat("[Target] \247aCleanned vip targets successfully.");
			return;
		}
		
		boolean found = false;
		for(EntityPlayer player : mc.theWorld.playerEntities) {
			if(player.getName().toLowerCase().equalsIgnoreCase(args[1])) {
				
				found = true;
				
				break;
			}
		}
		
		if(!found)
			ChatUtil.printChat("[Target Warnning] \247ccannot find " + args[1] + " in your render distance.");
			
			KillAura.vips.add(args[1].toLowerCase());
			ChatUtil.printChat("[Target] \247aTargeted \247b" + args[1]);
	}

}
