package cn.Power.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.util.EntityUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;

public class Helper {
	public static Minecraft mc = Minecraft.getMinecraft();

	private static EntityUtils entityUtils;
	private static RenderUtils.R2DUtils r2DUtils;
	private static RenderUtils.R3DUtils r3DUtils;
	private static RenderUtils.ColorUtils colorUtils;

	public static boolean Phase = false;

	public static boolean Crit = false;

	public static boolean UHCBypass;

	public static boolean xray = false;
	public static boolean xrayesp = false;
	public static HashSet<Integer> blockIDs = new HashSet<Integer>();
	public static int opacity;
	public static Boolean bypass = true;
	public static List<Block> dimblock = new ArrayList<Block>();
	public static ArrayList<BlockPos> glow = new ArrayList<BlockPos>();

	protected static List<Entity> getLoadedEntities() {
		Minecraft.getMinecraft();
		return Minecraft.theWorld.loadedEntityList;
	}

	public static boolean hasArmor(EntityPlayer player) {
		if (player.inventory == null) {
			return false;
		}
		ItemStack boots = player.inventory.armorInventory[0];
		ItemStack pants = player.inventory.armorInventory[1];
		ItemStack chest = player.inventory.armorInventory[2];
		ItemStack head = player.inventory.armorInventory[3];
		if (boots == null && pants == null && chest == null && head == null) {
			return false;
		}
		return true;
	}

	public static Minecraft mc() {
		return Minecraft.getMinecraft();
	}

	public static EntityPlayerSP player() {
		Helper.mc();
		return Minecraft.thePlayer;
	}

	public static WorldClient world() {
		Helper.mc();
		return Minecraft.theWorld;
	}

	public static EntityUtils entityUtils() {
		return entityUtils;
	}

	public static ScaledResolution scaled() {
		return new ScaledResolution(Helper.mc());
	}

	public static RenderUtils.R2DUtils get2DUtils() {
		return r2DUtils;
	}

	public static RenderUtils.R3DUtils get3DUtils() {
		return r3DUtils;
	}

	public static RenderUtils.ColorUtils colorUtils() {
		return colorUtils;
	}

	/*
	 * public static MathUtils mathUtils() { return mathUtils; }
	 */

	public static void sendPacket(Packet p) {
		Helper.mc().getNetHandler().addToSendQueue(p);
	}

	static {
		r2DUtils = new RenderUtils.R2DUtils();
		r3DUtils = new RenderUtils.R3DUtils();
		colorUtils = new RenderUtils.ColorUtils();
	}

	public static boolean onServer(String server) {
		if (!mc.isSingleplayer() && Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server)) {
			return true;
		}
		return false;
	}

	public static boolean containsID(int id) {
		return blockIDs.contains(id);
	}
}
