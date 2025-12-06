package cn.Power.mod.mods.MOVEMENT;

import java.util.List;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.Dismount;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.handler.MouseInputHandler;
import cn.Power.util.misc.ChatUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class LookTP extends Mod {
	private MouseInputHandler handler = new MouseInputHandler(2);
	public Value<Double> Reach = new Value<Double>("LookTP_Reach", 200d, 50d, 500.0d, 10d);
	public static Entity TragetEntity;

	public LookTP() {
		super("LookTP", Category.MOVEMENT);

	}

	@EventTarget
	private void Tick(EventTick e) {
		if (this.handler.canExcecute() ) {
		final Object[] objects = a(Reach.getValueState(), 0.2, 0.0f);
			if (objects != null && objects[0] instanceof EntityPlayer) {
				TragetEntity = (Entity) objects[0];
				float x = (int) (TragetEntity.posX);
				float y = (int) (TragetEntity.posY);
				float z = (int) (TragetEntity.posZ);
				float xDiff = (float) (mc.thePlayer.posX - x);
				float yDiff = (float) (mc.thePlayer.posY - y);
				float zDiff = (float) (mc.thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
				ChatUtil.printChat("\247c===========================================");
				formattedMsg(
						"§a目标 §r" + TragetEntity.getDisplayName().getFormattedText() + " §a距离§c " + (int) dis + "§b 米 "
								+ "§a坐标§BX: §r" + (int) x + " §BY: §r" + y  + " §BZ: §r" + (int) z   + "  ",
						"&9[§e§l点我TP实时坐标&r&9]", "&aClick to TP~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp LookTP"));
				ChatUtil.printChat("\247c============================================");
				
				formattedMsg(" §8[§3Aura Target§8]§r §7Click me to Target this player in ka",
						"&9[§e§l点我Target&r&9]", "&3Click to target this player in killaura~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-target "+TragetEntity.getName().toLowerCase() ));
				
				ChatUtil.printChat("\247c============================================");
			}else if (objects != null && objects[0] instanceof EntityWither) {
				TragetEntity = (Entity) objects[0];
				float x = (int) (TragetEntity.posX);
				float y = (int) (TragetEntity.posY + 1);
				float z = (int) (TragetEntity.posZ);
				float xDiff = (float) (mc.thePlayer.posX - x);
				float yDiff = (float) (mc.thePlayer.posY - y);
				float zDiff = (float) (mc.thePlayer.posZ - z);
				float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
				formattedMsg(
						"§a目标 §r" + TragetEntity.getDisplayName().getFormattedText() + " §a距离§c " + (int) dis + "§b 米 "
								+ "§a坐标§BX: §r" + (int) x  + " §BY: §r" + (int) y  + " §BZ: §r" + (int) z + "  ",
						"&9[§e§l点我TP实时坐标&r&9]", "&aClick to TP~",
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp "+ (double) ( x + ThreadLocalRandom.current().nextInt(3) + Math.random()) + " " + (double) (y + ThreadLocalRandom.current().nextInt(1) + Math.random()) + " " + (double) (z + ThreadLocalRandom.current().nextInt(3) + Math.random())));
			}
			if (mc.thePlayer.getCurrentEquippedItem().getItem().equals(Items.compass)) {
				BlockPos blockpos = mc.theWorld.getSpawnPoint();
				if ( SkyBlockUtils.isUHCgame()) {
					float x = (int) (blockpos.getX());
					float z = (int) (blockpos.getZ());
					float xDiff = (float) (mc.thePlayer.posX - x);
					float zDiff = (float) (mc.thePlayer.posZ - z);
					float dis = MathHelper.sqrt_float(xDiff * xDiff+ zDiff * zDiff);
						formattedMsg(
								"§a目标 §r" + "当前指南针目标" + " §a距离§c " + (int) dis + "§b 米 "
										+ "§a坐标§BX: §r" + (int) x  + " §BZ: §r" + (int) z + "  ",
								"&9[§e§l点我TP&r&9]", "&aClick to TP~",
								new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "-tp " + x + " " + 100 + " " + z));
					}
			}
		}
	}

	public static Object[] a(final double d, final double expand, final float partialTicks) {
		final Entity var2 = mc.getRenderViewEntity();
		Entity entity = null;
		if (var2 == null || mc.theWorld == null) {
			return null;
		}
		mc.mcProfiler.startSection("pick");
		final Vec3 var3 = var2.getPositionEyes(0.0f);
		final Vec3 var4 = var2.getLook(0.0f);
		final Vec3 var5 = var3.addVector(var4.xCoord * d, var4.yCoord * d, var4.zCoord * d);
		Vec3 var6 = null;
		final float var7 = 1.0f;
		List<Entity> var8 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, var2.getEntityBoundingBox()
				.addCoord(var4.xCoord * d, var4.yCoord * d, var4.zCoord * d).expand(0.5, 0.5, 0.5));
		double var9 = d;
		for (int var10 = 0; var10 < var8.size(); ++var10) {
			final Entity var11 = var8.get(var10);
			if (var11.canBeCollidedWith()) {
//				float var12 = var11.getCollisionBorderSize();
				float var12 = 0.5f;
				AxisAlignedBB var13 = var11.getEntityBoundingBox().expand((double) var12, (double) var12,
						(double) var12);
				var13 = var13.expand(expand, expand, expand);
				final MovingObjectPosition var14 = var13.calculateIntercept(var3, var5);
				if (var13.isVecInside(var3)) {
					if (0.0 < var9 || var9 == 0.0) {
						entity = var11;
						var6 = ((var14 == null) ? var3 : var14.hitVec);
						var9 = 0.0;
					}
				} else if (var14 != null) {
					final double var15 = var3.distanceTo(var14.hitVec);
					if (var15 < var9 || var9 == 0.0) {
						final boolean canRiderInteract = false;
						if (var11 == var2.ridingEntity) {
							if (var9 == 0.0) {
								entity = var11;
								var6 = var14.hitVec;
							}
						} else {
							entity = var11;
							var6 = var14.hitVec;
							var9 = var15;
						}
					}
				}
			}
		}
		if (var9 < d && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
			entity = null;
		}
		mc.mcProfiler.endSection();
		if (entity == null || var6 == null) {
			return null;
		}
		return new Object[] { entity, var6 };
	}

	@native0
	public static void formattedMsg(final String message, final String base, final String hover,
			final ClickEvent clickEvent) {
		final ChatStyle style = new ChatStyle();
		if (hover.length() > 0) {
			style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					(IChatComponent) new ChatComponentText(replace(hover))));
		}
		if (clickEvent != null) {
			style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					getSubString(clickEvent.toString(), "value='", "'}")));
		}
		IChatComponent txt;
		String prefix = "§8[§cLookTP§8]§r";
		if (base.equals("%r")) {
			txt = new ChatComponentText(replace(prefix + " " + message)).setChatStyle(style);
		} else {
			final String[] split = message.split(base);
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

	public static String replace(final String text) {
		return text.replaceAll("&", "§");
	}
}
