package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.util.handler.MouseInputHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public class AntiObbyTrap extends Mod {

	private MouseInputHandler handler = new MouseInputHandler(1);
	public Value<Boolean> AntiForge = new Value("AntiObbyTrap_AntiForge", true);
	public Value<Boolean> noSwing = new Value("AntiObbyTrap_NoSwing", false);

	public static boolean Obs = false;

	public AntiObbyTrap() {
		super("AntiObbyTrap", Category.PLAYER);
	}

	@EventTarget
	public void Pre(EventPreMotion e) {
		Obs = false;
		Block obs = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ))
				.getBlock();
		Block block = mc.theWorld
				.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock();
		Block zbs = mc.theWorld
				.getBlockState(new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY + 1, mc.thePlayer.posZ))
				.getBlock();
		Block zbx = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ))
				.getBlock();
		BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
		BlockPos zs = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
		BlockPos zx = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY, mc.thePlayer.posZ);
		if (block != Blocks.air && block != Blocks.bedrock && block != Blocks.obsidian && obs == Blocks.obsidian
				&& mc.thePlayer.hurtTime > 8) {
			Obs = true;
			StartBlock(pos, EnumFacing.DOWN);
		}
		if (block != Blocks.air && (block == Blocks.bedrock || block == Blocks.obsidian) && obs == Blocks.obsidian
				&& mc.thePlayer.hurtTime > 8) {
			if (zbx != Blocks.air) {
				Obs = true;
				StartBlock(zx, EnumFacing.EAST);
			} else if (zbs != Blocks.air) {
				Obs = true;
				StartBlock(zs, EnumFacing.UP);
			}
		}
	}

	@EventTarget
	public void onPre(EventPreMotion e) {
		if(this.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
				|| !(this.mc.objectMouseOver.entityHit instanceof EntityArmorStand))
			return;
		
		Block forge = mc.theWorld
				.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ)).getBlock();

		if (forge != Blocks.air && forge == Blocks.furnace) {
			
			if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.entityHit != null) {
				if (this.handler.canExcecute() && AntiForge.getValueState()) {
					
					EntityArmorStand Forge = (EntityArmorStand) this.mc.objectMouseOver.entityHit;
					
					mc.theWorld.removeEntity(Forge);
				}
			}
		}
	}

	public void StartBlock(BlockPos pos, EnumFacing facing) {
		this.mc.getNetHandler().addToSendQueue(
				new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
		this.mc.getNetHandler().addToSendQueue(
				new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
		this.mc.getNetHandler().addToSendQueue(
				new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
		if (noSwing.getValueState()) {
			this.mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
		} else {
			this.mc.thePlayer.swingItem();
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
		mc.thePlayer.addChatMessage(txt);
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
