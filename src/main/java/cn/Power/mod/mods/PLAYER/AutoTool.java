package cn.Power.mod.mods.PLAYER;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPacket;
import cn.Power.events.EventTick;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

public class AutoTool extends Mod {
	public AutoTool() {
		super("AutoTool", Category.PLAYER);
	}

	boolean open;
	public Value<Boolean> SwordCheck = new Value("AutoTool_SwordCheck", false);

	@EventTarget
	public void handle(EventTick event) {
		if (mc.thePlayer == null)
			return;

		if (open && mc.thePlayer.getHeldItem() == null) {
			open = false;

		}

		if (mc.thePlayer.getHeldItem() == null)
			return;

		if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
			open = true;
		} else {
			open = false;
		}

	}

	@EventTarget
	public void handle(EventPacket event) {
		if ((event.getPacket() instanceof C07PacketPlayerDigging)) {
			C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.getPacket();
			if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
				if (SwordCheck.getValueState()) {
					if (open && !AntiObbyTrap.Obs) {
						return;
					}
				}
				autotool(packet.getPosition());
			}

		}
	}

	public void bestSword(Entity targetEntity) {
		int bestSlot = 0;
		float f = -1.0F;
		for (int i = 36; i < 45; ++i) {
			if (Minecraft.thePlayer.inventoryContainer.inventorySlots.toArray()[i] != null && targetEntity != null) {
				ItemStack curSlot = Minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (curSlot != null && curSlot.getItem() instanceof ItemSword) {
					ItemSword sword = (ItemSword) curSlot.getItem();
					if (sword.getDamageVsEntity() > f) {
						bestSlot = i - 36;
						f = sword.getDamageVsEntity();
					}
				}
			}
		}
		if (f > -1.0F) {
			Minecraft.thePlayer.inventory.currentItem = bestSlot;
			Minecraft.playerController.updateController();
		}

	}

	private static void autotool(BlockPos position) {
		Block block = Minecraft.theWorld.getBlockState(position).getBlock();
		int itemIndex = getStrongestItem(block);
		if (itemIndex < 0) {
			return;
		}
		float itemStrength = getStrengthAgainstBlock(block, Minecraft.thePlayer.inventory.mainInventory[itemIndex]);
		if (Minecraft.thePlayer.getHeldItem() != null
				&& getStrengthAgainstBlock(block, Minecraft.thePlayer.getHeldItem()) >= itemStrength) {
			return;
		}
		Minecraft.thePlayer.inventory.currentItem = itemIndex;
		Minecraft.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(itemIndex));
		mc.playerController.updateController();
	}

	private static int getStrongestItem(Block block) {
		float strength = Float.NEGATIVE_INFINITY;
		int strongest = -1;
		int i = 0;
		while (i < 9) {
			float itemStrength;
			ItemStack itemStack = Minecraft.thePlayer.inventory.mainInventory[i];
			if (itemStack != null && itemStack.getItem() != null
					&& (itemStrength = getStrengthAgainstBlock(block, itemStack)) > strength && itemStrength != 1.0f) {
				strongest = i;
				strength = itemStrength;
			}
			++i;
		}
		return strongest;
	}

	public static float getStrengthAgainstBlock(Block block, ItemStack item) {
		float strength = item.getStrVsBlock(block);
		int ID = Block.getIdFromBlock(block);
		
		if (block == Blocks.diamond_ore)
			if (item.getItem() instanceof ItemTool
					&& ((((ItemTool) item.getItem()).getToolMaterial() != Item.ToolMaterial.IRON)
							&& ((ItemTool) item.getItem()).getToolMaterial() != Item.ToolMaterial.EMERALD))
				return 1.0f;

		if ((item.getUnlocalizedName().contains("Stone") || (item.getItem() instanceof ItemTool
				&& ((ItemTool) item.getItem()).getToolMaterial() == Item.ToolMaterial.STONE))
				&& (ID == 14 || ID == 129 || ID == 56 || ID == 74 || ID == 73)) {
			return 5;
		}

		if (item.getItem() instanceof ItemTool
				&& ((ItemTool) item.getItem()).getToolMaterial() == Item.ToolMaterial.GOLD)
			strength = 4;



		if (!EnchantmentHelper.getEnchantments(item).containsKey(Enchantment.efficiency.effectId) || strength == 1.0f) {
			return strength;
		}

		int enchantLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, item);

		return strength + (float) (enchantLevel * enchantLevel + 1);
	}

	public static boolean isBestWeapon(ItemStack stack) {
		float damage = getDamage(stack);
		for (int i = 36; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.inventorySlots.toArray()[i] != null) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
					return false;
			}
		}
		if ((stack.getItem() instanceof ItemSword)) {
			return true;
		} else {
			return false;
		}
	}

	public void getBestWeapon() {
		for (int i = 36; i < 45; i++) {
			if (Minecraft.thePlayer.inventoryContainer.inventorySlots.toArray()[i] != null) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword)) {
					System.err.println(i);
					break;
				}
			}
		}
	}

	private static float getDamage(ItemStack stack) {
		float damage = 0;
		Item item = stack.getItem();
		if (item instanceof ItemTool) {
			ItemTool tool = (ItemTool) item;
			damage += tool.getDamage();
		}
		if (item instanceof ItemSword) {
			ItemSword sword = (ItemSword) item;
			damage += sword.getAttackDamage();
		}
		damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f
				+ EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
		return damage;
	}
}
