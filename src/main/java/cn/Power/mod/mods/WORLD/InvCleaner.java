package cn.Power.mod.mods.WORLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.AutoSword;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.PLAYER.AutoArmor;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InvCleaner extends Mod {

	private static List<Block> blacklistedBlocks;

	public static List<Block> getBlacklistedBlocks() {
		return blacklistedBlocks;
	}

	public Value mode = new Value("InvCleaner", "Mode", 0);
	public Value<Double> BLOCKCAP = new Value<Double>("InvCleaner_Blockcap", 128.0, 0.0, 512.0, 8.0);
	public Value<Double> DELAY = new Value<Double>("InvCleaner_Delay", 1.0, 1.0, 10.0, 1.0);
	public Value<Boolean> Tools = new Value("InvCleaner_Tools", true);
	public Value<Boolean> ARCHERY = new Value("InvCleaner_Archery", true);
	public Value<Boolean> FOOD = new Value("InvCleaner_FOOD", true);
	public Value<Boolean> UHC = new Value("InvCleaner_UHC", false);
	public Value<Boolean> AuraCheck = new Value("InvCleaner_AuraCheck", false);
	public Value<Boolean> TOGGLE = new Value("InvCleaner_Toggle", false);

	public static Timer timer = new Timer();
	ArrayList<Integer> whitelistedItems = new ArrayList<>();

	public static int weaponSlot = 36, pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;

	public static int weaponSlot1 = 36, shovelSlot1 = 42, axeSlot1 = 43, pickaxeSlot1 = 44;

	public InvCleaner() {
		super("InvCleaner", Category.WORLD);
		mode.mode.add("Basic");
		mode.mode.add("OpenInv");
		blacklistedBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.crafting_table, Blocks.gold_ore, Blocks.anvil, Blocks.tnt,
				Blocks.gold_block, Blocks.iron_block, Blocks.diamond_block);
	}

	@EventTarget
	public void onPre(EventUpdate event) {
		if(AuraCheck.getValueState()&& KillAura.Target != null) {
			return;
		}
		
		if(mc.thePlayer == null) {
			return;
		}
		
		if (Tools.getValueState()) {
			if (SkyBlockUtils.inDeathmatch()) {
				shovelSlot1 = -1;
				axeSlot1 = -1;
				pickaxeSlot1 = -1;
			} else {
				shovelSlot1 = 42;
				axeSlot1 = 43;
				pickaxeSlot1 = 44;
			}
		} else {
			shovelSlot1 = -1;
			axeSlot1 = -1;
			pickaxeSlot1 = -1;
		}

		long delay = DELAY.getValueState().longValue() * 50;

		if ((!mc.thePlayer.isEntityAlive() || (mc.currentScreen != null && mc.currentScreen instanceof GuiGameOver))) {
			this.toggle();
			// Notifications.getManager().post("InvManager","InvManager disabled due to
			// death.");
			return;
		}
		if (mc.thePlayer.ticksExisted <= 1) {
			this.toggle();
			// Notifications.getManager().post("InvManager","InvManager disabled due to
			// respawn.");
			return;
		}
		long Adelay = (2 * 50);

		if (AutoArmor.Look || !timer.check(Adelay)) {
			
			
			System.out.println("lock");
			
			if(AutoArmor.Look)
				timer.reset();
			
			return;
		}
		
		if (mc.currentScreen instanceof GuiChest) {
			return;
		}

		if (mode.isCurrentMode("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) {
			return;
		}
		
		if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory
				|| mc.currentScreen instanceof GuiChat) {
			/*
			 * if(timer.check(delay) && weaponSlot1 >= 36){
			 * 
			 * if (!mc.thePlayer.inventoryContainer.getSlot(weaponSlot1).getHasStack()){
			 * getBestWeapon(weaponSlot1);
			 * 
			 * }else{ if(!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot1).
			 * getStack())){ getBestWeapon(weaponSlot1); } } }
			 */
			if (timer.check(delay) && pickaxeSlot1 >= 36) {
				getBestPickaxe(pickaxeSlot1);
			}
			if (timer.check(delay) && shovelSlot1 >= 36) {
				getBestShovel(shovelSlot1);
			}
			if (timer.check(delay) && axeSlot1 >= 36) {
				getBestAxe(axeSlot1);
			}
			if (timer.check(delay)) {
				for (int i = 9; i < 45; i++) {
					if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
						if (shouldDrop(is, i)) {
							drop(i);
							timer.reset();
							if (delay > 0)
								break;
						}
					}
					if (i == 44) {
//						mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
						if (TOGGLE.getValueState().booleanValue()) {
							this.toggle();
						}
					}
				}
				
			}
		}

	}

	public void getBestWeapon(int slot) {
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword)) {
					swap(i, slot - 36);
					timer.reset();
					break;
				}
			}
		}
	}

	public void shiftClick(int slot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 2, 3, mc.thePlayer);
		
	}

	public void swap(int slot1, int hotbarSlot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, 2, 3, mc.thePlayer);
	}

	public void drop(int slot) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 2, 3, mc.thePlayer);
	}

	public boolean isBestWeapon(ItemStack stack) {
		float damage = getDamage(stack);
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
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

	private float getDamage(ItemStack stack) {
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
	
    private double getSwordDamage(ItemStack itemStack) {
        double damage = 0;

        Optional<AttributeModifier> attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();

        if (attributeModifier.isPresent()) {
            damage = attributeModifier.get().getAmount();
        }

        damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);

        return damage;
    }
    
	private ItemStack getBestItem(Class<? extends Item> itemType, Comparator comparator) {
        @SuppressWarnings("unchecked")
		Optional<ItemStack> bestItem = ((List<Slot>)mc.thePlayer.inventoryContainer.inventorySlots)
                .stream()
                .map(Slot::getStack)
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getItem().getClass().equals(itemType))
                .max(comparator);

        return bestItem.orElse(null);
    }

	public boolean shouldDrop(ItemStack stack, int slot) {

		Item item = stack.getItem();

		int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
		int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
		int protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
		int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack);

		if(SkyBlockUtils.isMWgame()) {
			if(stack.hasDisplayName() && stack.getDisplayName().startsWith("§"))
				return false;
			
			if(item instanceof ItemAppleGold || item instanceof ItemPotion || item instanceof ItemBucketMilk || item instanceof ItemFood || item == Items.beef || item == Items.arrow || item == Items.bow || item.getUnlocalizedName().toLowerCase().contains("ender"))
				return false;
		}
		
		if(item instanceof ItemSword)
			if((stack.hasDisplayName() && ((ItemSword)item).material == Item.ToolMaterial.EMERALD)
			       || (getBestItem(ItemSword.class, Comparator.comparingDouble(this::getSwordDamage)) == stack))
				return false;
		
		if(item instanceof ItemArmor)
			if((stack.hasDisplayName() && ((ItemArmor)item).material == ArmorMaterial.DIAMOND))
					return false;
		
		if(item instanceof ItemPickaxe)
			if((stack.hasDisplayName() && ((ItemPickaxe)item).getToolMaterial() == Item.ToolMaterial.EMERALD))
					return false;
		
		if(item instanceof ItemAxe)
			if((stack.hasDisplayName() && ((ItemAxe)item).getToolMaterial() == Item.ToolMaterial.EMERALD))
					return false;
		
		if(item instanceof ItemSnowball)
			if(stack.hasDisplayName())
					return false;
		
		if(item instanceof ItemSkull)
			if(stack.hasDisplayName())
					return false;
		
		if(item instanceof ItemAnvilBlock)
					return false;
				
		
		if(item instanceof ItemFood)
			return false;
		
		if (stack.getDisplayName().toLowerCase().contains("dragon armor")
				|| stack.getDisplayName().toLowerCase().contains("death's scythe")
				|| stack.getDisplayName().toLowerCase().contains("barbarian chestplate")
				|| stack.getDisplayName().toLowerCase().contains("exodus")
				|| stack.getDisplayName().toLowerCase().contains("exper seal")
				|| stack.getDisplayName().toLowerCase().contains("úril")
				|| stack.getDisplayName().toLowerCase().contains("perun")
				|| stack.getDisplayName().toLowerCase().contains("leviathan")
				|| stack.getDisplayName().toLowerCase().contains("excalibur")
				|| stack.getDisplayName().toLowerCase().contains("\247cFusion")) {
			return false;
		}
		
		if (stack.getDisplayName().toLowerCase().contains("dragon")) {
			return false;
		}
		
		if (stack.getDisplayName().toLowerCase().contains("\247a潮汐护腿")
				|| stack.getDisplayName().toLowerCase().contains("\247a永生帽")
				|| stack.getDisplayName().toLowerCase().contains("\247a霹隆之斧")
				|| stack.getDisplayName().toLowerCase().contains("\247a安都瑞尔之剑")
				|| stack.getDisplayName().toLowerCase().contains("\247a丰饶之角")
				|| stack.getDisplayName().toLowerCase().contains("\247a蛮族之甲")
				|| stack.getDisplayName().toLowerCase().contains("a巨龙之剑")
				|| stack.getDisplayName().contains("Axe of Perun")
				) {
			return false;
		}

		if (stack.getDisplayName().toLowerCase().contains("\247aforge")
				|| stack.getDisplayName().toLowerCase().contains("\247a锻炉")
				|| stack.getDisplayName().toLowerCase().contains("backpack")) {
			return false;
		}
		if (stack.getDisplayName().toLowerCase().contains("bloodlust")) {
			return false;
		}
		if (stack.getDisplayName().contains("Wither Skeleton Skull") || stack.getDisplayName().contains("Creeper Head")
				|| stack.getDisplayName().contains("Skeleton Skull")
				|| stack.getDisplayName().contains("Zombie Head")) {
			return true;
		}
		if ((Item.getIdFromItem(item) == 276 && (sharpness >= 3 || fireAspect >= 1))) {
			return false;
		}
		if (stack.getItem() instanceof ItemEnchantedBook) {
			NBTTagList nbttaglist = Items.enchanted_book.getEnchantments(stack);
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				int ID = nbttaglist.getCompoundTagAt(i).getShort("id");
				int LVL = nbttaglist.getCompoundTagAt(i).getShort("lvl");
				if (ID == Enchantment.sharpness.effectId && LVL >= 3 || ID == Enchantment.fireAspect.effectId
						|| ID == Enchantment.efficiency.effectId && LVL >= 3 || ID == Enchantment.fortune.effectId
						|| ID == Enchantment.featherFalling.effectId && LVL >= 3
						|| ID == Enchantment.protection.effectId || ID == Enchantment.punch.effectId
						|| ID == Enchantment.flame.effectId || ID == Enchantment.infinity.effectId
						|| ID == Enchantment.depthStrider.effectId)
					return false;
			}
			return true;
		}
		if (stack.getDisplayName().toLowerCase().contains("k||")
				|| stack.getDisplayName().toLowerCase().contains("cornucopia")) {
			return false;
		}
		if ((slot == weaponSlot1 && weaponSlot1 >= 0 && getBestItem(ItemSword.class, Comparator.comparingDouble(this::getSwordDamage)) == stack)) {
			return false;
		}
		if ((slot == pickaxeSlot1 && isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot1).getStack())
				&& pickaxeSlot1 >= 0)
				|| (slot == axeSlot1 && isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot1).getStack())
						&& axeSlot1 >= 0)
				|| (slot == shovelSlot1 && isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot1).getStack())
						&& shovelSlot1 >= 0)) {
			return false;
		}
		if (stack.getItem() instanceof ItemArmor) {
			for (int type = 1; type < 5; type++) {
				if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
					ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
					if (AutoArmor.isBestArmor(is, type)) {
						continue;
					}
				}
				if (AutoArmor.isBestArmor(stack, type)) {
					return false;
				}
			}
		}

		if (stack.getItem() instanceof ItemBlock && UHC.getValueState()
				&& (blacklistedBlocks.contains(((ItemBlock) stack.getItem()).getBlock()))) {
			return false;
		}

		if (stack.getItem() instanceof ItemBlock && (getBlockCount() > (BLOCKCAP.getValueState()).intValue()
				|| ((Scaffold)ModManager.getModByClass(Scaffold.class)).blacklisted.contains(((ItemBlock) stack.getItem()).getBlock()))) {
			return true;
		}

		if (Item.getIdFromItem(item) == 263 && getCoal() <= 64 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 345 && getZNZ() <= 1 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 265 && getiron() <= 64 && UHC.getValueState()) {
			return false;
		}
		if (Item.getIdFromItem(item) == 326 && getSHUI() <= 1 && UHC.getValueState()) {
			return false;
		}
		if (stack.getItem() instanceof ItemPotion) {
			if (isBadPotion(stack)) {
				return true;
			}
		}
		if ((stack.getItem() instanceof ItemAppleGold)
				|| (stack.getItem().getUnlocalizedName().contains("apple") && UHC.getValueState())) {
			return false;
		}

		if (stack.getItem() instanceof ItemFood && FOOD.getValueState()) {
			return true;
		}
		if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool
				|| stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
			return true;
		}
		if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow"))
				&& ARCHERY.getValueState()) {
			return true;
		}

		if (((stack.getItem().getUnlocalizedName().contains("tnt"))
				|| (stack.getItem().getUnlocalizedName().contains("stick"))
				|| (stack.getItem().getUnlocalizedName().contains("egg"))
				|| (stack.getItem().getUnlocalizedName().contains("string"))
				|| (stack.getItem().getUnlocalizedName().contains("cake"))
				|| (stack.getItem().getUnlocalizedName().contains("mushroom"))
				|| (stack.getItem().getUnlocalizedName().contains("flint"))
				|| (stack.getItem().getUnlocalizedName().contains("compass"))
				|| (stack.getItem().getUnlocalizedName().contains("dyePowder"))
				|| (stack.getItem().getUnlocalizedName().contains("feather"))
				|| (stack.getItem().getUnlocalizedName().contains("bucket"))
				|| (stack.getItem().getUnlocalizedName().contains("chest")
						&& !stack.getDisplayName().toLowerCase().contains("collect"))
				|| (stack.getItem().getUnlocalizedName().contains("snow"))
				|| (stack.getItem().getUnlocalizedName().contains("fish"))
				|| (stack.getItem().getUnlocalizedName().contains("enchant"))
				|| (stack.getItem().getUnlocalizedName().contains("exp"))
				|| (stack.getItem().getUnlocalizedName().contains("shears"))
				|| (stack.getItem().getUnlocalizedName().contains("anvil"))
				|| (stack.getItem().getUnlocalizedName().contains("torch"))
				|| (stack.getItem().getUnlocalizedName().contains("seeds"))
				|| (stack.getItem().getUnlocalizedName().contains("leather"))
				|| (stack.getItem().getUnlocalizedName().contains("reeds"))
				|| (stack.getItem().getUnlocalizedName().contains("Iron") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("Tag") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("slime") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("sign") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("blaze") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("horse") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("coal") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("bone") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("Gunpowder") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("paper") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("book") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("redstone") && UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("skull") && !UHC.getValueState())
				|| (stack.getItem().getUnlocalizedName().contains("record"))
				|| (stack.getItem().getUnlocalizedName().contains("snowball"))
				|| (stack.getItem() instanceof ItemGlassBottle)
				|| (stack.getItem().getUnlocalizedName().contains("piston")))) {
			return true;
		}

		return false;
	}

	public ArrayList<Integer> getWhitelistedItem() {
		return whitelistedItems;
	}

	private int getBlockCount() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (is.getItem() instanceof ItemBlock
						&& !Scaffold.blacklisted.contains(((ItemBlock) item).getBlock())) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getCoal() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 263) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getSHUI() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 326) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getZNZ() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 345) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private int getiron() {
		int blockCount = 0;
		for (int i = 0; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (Item.getIdFromItem(item) == 265) {
					blockCount += is.stackSize;
				}
			}
		}
		return blockCount;
	}

	private void getBestPickaxe(int slot) {
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestPickaxe(is) && pickaxeSlot1 != i) {
					if (!isBestWeapon(is))
						if (!mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot1).getHasStack()) {
							swap(i, pickaxeSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot1).getStack())) {
							swap(i, pickaxeSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private void getBestShovel(int slot) {
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestShovel(is) && shovelSlot1 != i) {
					if (!isBestWeapon(is))
						if (!mc.thePlayer.inventoryContainer.getSlot(shovelSlot1).getHasStack()) {
							swap(i, shovelSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot1).getStack())) {
							swap(i, shovelSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private void getBestAxe(int slot) {

		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

				if (isBestAxe(is) && axeSlot1 != i) {
					if (!isBestWeapon(is))
						if (!mc.thePlayer.inventoryContainer.getSlot(axeSlot1).getHasStack()) {
							swap(i, axeSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						} else if (!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot1).getStack())) {
							swap(i, axeSlot1 - 36);
							timer.reset();
							if (DELAY.getValueState().longValue() > 0)
								return;
						}

				}
			}
		}
	}

	private boolean isBestPickaxe(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemPickaxe))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isBestShovel(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemSpade))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
					return false;
				}

			}
		}
		return true;
	}

	private boolean isBestAxe(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemAxe))
			return false;
		float value = getToolEffect(stack);
		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
					return false;
				}

			}
		}
		return true;
	}

	private float getToolEffect(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof ItemTool))
			return 0;
		String name = item.getUnlocalizedName();
		ItemTool tool = (ItemTool) item;
		float value = 1;
		if (item instanceof ItemPickaxe) {
			value = tool.getStrVsBlock(stack, Blocks.stone);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else if (item instanceof ItemSpade) {
			value = tool.getStrVsBlock(stack, Blocks.dirt);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else if (item instanceof ItemAxe) {
			value = tool.getStrVsBlock(stack, Blocks.log);
			if (name.toLowerCase().contains("gold")) {
				value -= 5;
			}
		} else
			return 1f;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100d;
		return value;
	}

	private boolean isBadPotion(ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemPotion) {
			final ItemPotion potion = (ItemPotion) stack.getItem();
			if (potion.getEffects(stack) == null)
				return true;
			for (final Object o : potion.getEffects(stack)) {
				final PotionEffect effect = (PotionEffect) o;
				if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId()
						|| effect.getPotionID() == Potion.moveSlowdown.getId()
						|| effect.getPotionID() == Potion.weakness.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	boolean invContainsType(int type) {

		for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if (item instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) item;
					if (type == armor.armorType) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void getBestArmor() {
		for (int type = 1; type < 5; type++) {
			if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
				if (AutoArmor.isBestArmor(is, type)) {
					continue;
				} else {
					drop(4 + type);
				}
			}
			for (int i = 9; i < 45; i++) {
				if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
					if (AutoArmor.isBestArmor(is, type) && AutoArmor.getProtValue(is) > 0) {
						shiftClick(i);
						timer.reset();
						if (DELAY.getValueState().longValue() > 0)
							return;
					}
				}
			}
		}
	}

}
