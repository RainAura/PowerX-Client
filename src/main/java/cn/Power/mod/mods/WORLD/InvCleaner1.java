package cn.Power.mod.mods.WORLD;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.darkmagician6.eventapi.EventTarget;

import cn.Power.Value;
import cn.Power.events.EventPreMotion;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.util.timeUtils.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class InvCleaner1 extends Mod {
	public final Set<Integer> blacklistedItemIDs = new HashSet<>();
	TimeUtil timer = new TimeUtil();
	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final Random RANDOM = new Random();
	private int slots;
	private double numberIdkWillfigureout;
	private boolean bol;

	public ItemStack[] bestArmorSet;
	public ItemStack bestSword;
	public ItemStack bestPickAxe;
	public ItemStack bestShovel;
	public ItemStack bestAxe;
	public ItemStack bestBow;

	public Value<Double> clspeed = new Value("InvCleaner_CleanSpeed", 30d, 1d, 20d, 1d);
	public Value<Boolean> onInv = new Value("InvCleaner_OpenInv", true);
	public Value<Boolean> Food = new Value("InvCleaner_Food", false);
	public Value<Boolean> autodisable = new Value("InvCleaner_AutoDisable", true);

	public InvCleaner1() {
		super("InvCleaner", Category.WORLD);
		Arrays.stream(new int[] {
				// ������
				404,
				// Egg
				344,
				// Stick
				280,
				// String
				287,
				// Flint
				318,
				/*
				 * //Compass 345,
				 */
				// Feather
				288,
				// Experience Bottle
				384,
				// Enchanting Table
				116,
				// Chest
				54,
				// Snowball
				332,
				// Anvil
				145 }).forEach(this.blacklistedItemIDs::add);
	}

	@Override
	public void onEnable() {
		this.slots = 9;

		super.onEnable();
	}

	public boolean getOpenInv() {
		return this.onInv.getValueState().booleanValue();
	}

	public int getCleanSpeed() {
		return this.clspeed.getValueState().intValue();
	}

	public boolean getAutoDisable() {
		return this.autodisable.getValueState().booleanValue();
	}

	@EventTarget
	public void onUpdate(EventPreMotion event) {
		if (this.getOpenInv() && mc.currentScreen == null) {
			return;
		}
		if (MC.thePlayer.ticksExisted % getCleanSpeed() == 0) {
			this.bestArmorSet = getBestArmorSet();
			this.bestSword = getBestItem(ItemSword.class, Comparator.comparingDouble(this::getSwordDamage));
			this.bestAxe = getBestItem(ItemAxe.class, Comparator.comparingDouble(this::getMiningSpeed));
			this.bestShovel = getBestItem(ItemSpade.class, Comparator.comparingDouble(this::getMiningSpeed));
			this.bestPickAxe = getBestItem(ItemPickaxe.class, Comparator.comparingDouble(this::getMiningSpeed));
			this.bestBow = getBestItem(ItemBow.class, Comparator.comparingDouble(this::getBowPower));

			Optional<Slot> blacklistedItem = ((List<Slot>) MC.thePlayer.inventoryContainer.inventorySlots).stream()
					.filter(Slot::getHasStack)
					.filter(slot -> Arrays.stream(MC.thePlayer.inventory.armorInventory)
							.noneMatch(slot.getStack()::equals))
					.filter(slot -> !slot.getStack().equals(MC.thePlayer.getHeldItem()))
					.filter(slot -> isItemBlackListed(slot.getStack())).findFirst();

			if (blacklistedItem.isPresent()) {
				this.dropItem(blacklistedItem.get().slotNumber);
			} else {
				if (this.getAutoDisable()) {
					this.set(false);
				}
			}
		}
	}

	public void dropItem(int slotID) {
		MC.playerController.windowClick(0, slotID, 1, 4, MC.thePlayer);
	}

	// Objects.requireNonNull is just for debugging. It can't be null
	// Things to throw out
	public boolean isItemBlackListed(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return blacklistedItemIDs.contains(Item.getIdFromItem(item))
				|| item instanceof ItemBow && !this.bestBow.equals(itemStack)
				|| item instanceof ItemSpade && !this.bestShovel.equals(itemStack)
				|| item instanceof ItemAxe && !this.bestAxe.equals(itemStack)
				|| item instanceof ItemPickaxe && !this.bestPickAxe.equals(itemStack) || item instanceof ItemFishingRod
				|| item instanceof ItemGlassBottle || item instanceof ItemBucket
				|| (item instanceof ItemFood && Food.getValueState()) && !(item instanceof ItemAppleGold)
				|| item instanceof ItemSword && !this.bestSword.equals(itemStack)
				|| item instanceof ItemArmor && !this.bestArmorSet[((ItemArmor) item).armorType].equals(itemStack)
				|| item instanceof ItemPotion && isPotionNegative(itemStack);
	}

	// Improved check to reduce copy pasty code
	public ItemStack getBestItem(Class<? extends Item> itemType, Comparator comparator) {
		Optional<ItemStack> bestItem = ((List<Slot>) MC.thePlayer.inventoryContainer.inventorySlots).stream()
				.map(Slot::getStack).filter(Objects::nonNull)
				.filter(itemStack -> itemStack.getItem().getClass().equals(itemType)).max(comparator);

		return bestItem.orElse(null);
	}

	// Armor check
	public ItemStack[] getBestArmorSet() {
		ItemStack[] bestArmorSet = new ItemStack[4];

		List<ItemStack> armor = ((List<Slot>) MC.thePlayer.inventoryContainer.inventorySlots).stream()
				.filter(Slot::getHasStack).map(Slot::getStack)
				.filter(itemStack -> itemStack.getItem() instanceof ItemArmor).collect(Collectors.toList());

		for (ItemStack itemStack : armor) {
			ItemArmor itemArmor = (ItemArmor) itemStack.getItem();

			ItemStack bestArmor = bestArmorSet[itemArmor.armorType];

			if (bestArmor == null || getArmorDamageReduction(itemStack) > getArmorDamageReduction(bestArmor)) {
				bestArmorSet[itemArmor.armorType] = itemStack;
			}
		}

		return bestArmorSet;
	}

	public double getSwordDamage(ItemStack itemStack) {
		double damage = 0;

		Optional<AttributeModifier> attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();

		if (attributeModifier.isPresent()) {
			damage = attributeModifier.get().getAmount();
		}

		damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);

		return damage;
	}

	public double getBowPower(ItemStack itemStack) {
		double power = 0;

		Optional<AttributeModifier> attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();

		if (attributeModifier.isPresent()) {
			power = attributeModifier.get().getAmount();
		}

		power += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);

		return power;
	}

	public double getMiningSpeed(ItemStack itemStack) {
		double speed = 0;

		Optional<AttributeModifier> attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();

		if (attributeModifier.isPresent()) {
			speed = attributeModifier.get().getAmount();
		}

		speed += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);

		return speed;
	}

	public double getArmorDamageReduction(ItemStack itemStack) {
		int damageReductionAmount = ((ItemArmor) itemStack.getItem()).damageReduceAmount;

		damageReductionAmount += EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { itemStack },
				DamageSource.causePlayerDamage(MC.thePlayer));

		return damageReductionAmount;
	}

	public boolean isPotionNegative(ItemStack itemStack) {
		ItemPotion potion = (ItemPotion) itemStack.getItem();

		List<PotionEffect> potionEffectList = potion.getEffects(itemStack);

		return potionEffectList.stream().map(potionEffect -> Potion.potionTypes[potionEffect.getPotionID()])
				.anyMatch(Potion::isBadEffect);
	}

}
