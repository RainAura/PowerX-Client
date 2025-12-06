package net.minecraft.client.gui.inventory;


import cn.Power.mod.ModManager;
import cn.Power.mod.mods.WORLD.ChestStealer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiChest extends GuiContainer {
	/** The ResourceLocation containing the chest GUI texture. */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(
			"textures/gui/container/generic_54.png");
	private IInventory upperChestInventory;
	public IInventory lowerChestInventory;

	/**
	 * window height is calculated with these values; the more rows, the heigher
	 */
	private int inventoryRows;

	public GuiChest(IInventory upperInv, IInventory lowerInv) {
		super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;
		this.allowUserInput = false;
		int i = 222;
		int j = i - 108;
		this.inventoryRows = lowerInv.getSizeInventory() / 9;
		this.ySize = j + this.inventoryRows * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the
	 * items). Args : mouseX, mouseY
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}

//	@Override
//	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		String name = lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
//		String GuiName = lowerChestInventory.getDisplayName().getUnformattedText();
//
//		if (StatCollector.translateToLocal("container.chest").equalsIgnoreCase(GuiName)
//				|| StatCollector.translateToLocal("container.chestDouble").equalsIgnoreCase(GuiName)) {
//
//			/*
//			 * for(String str : LIST) { if(GuiName.contains(str)) return; }
//			 */
//
//			String[] list = new String[] { "menu", "selector", "game", "gui", "server", "inventory", "play",
//					"teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user",
//					"team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele",
//					"port", "map", "kit", "select", "lobby", "vault", "lock" };
//			for (String str : list) {
//				if (name.contains(str))
//					return;
//			}
//			if ((ModManager.getModByClass(ChestStealer.class).isEnabled()
//					&& ((ChestStealer) ModManager.getModByClass(ChestStealer.class)).chestaura.getValueState()) ) {
//				
//				if( (((ChestStealer) ModManager.getModByClass(ChestStealer.class)).silent.getValueState()) ){
//					Minecraft.getMinecraft().inGameHasFocus = true;
//					Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
//				
//					return;
//				}
//			}
//		}
//
//		super.drawScreen(mouseX, mouseY, partialTicks);
//	}
}
