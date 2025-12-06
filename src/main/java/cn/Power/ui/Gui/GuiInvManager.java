package cn.Power.ui.Gui;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cn.Power.Font.FontManager;
import cn.Power.mod.mods.WORLD.InventoryManager;
import cn.Power.util.Colors;
import cn.Power.util.FileUtil;
import cn.Power.util.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiInvManager  extends GuiScreen {
	protected final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/widgets.png");
	protected final ResourceLocation trash = new ResourceLocation("textures/trash.png");
	private Translate translateBox = new Translate(0 , 100);
	public static ArrayList<InvManagerSlot> slots = new ArrayList();
	boolean isClicking;
	InvManagerSlot mouseSlot;
	InvManagerItem selected;
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height -80, 200, 20, "Close"));
    }
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                this.mc.displayGuiScreen(null);
                saveConfigs();
                break;
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        Minecraft m = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(m);
        int midX= res.getScaledWidth() / 2;
        int midY = res.getScaledHeight() / 2;
        int left = midX - 138;
        int top = midY - 20;
        int s = res.getScaleFactor();
    
        if(!slots.isEmpty()){
        	mouseSlot = null;
        	for(InvManagerSlot slot : slots){
        		int x = slot.getX();
        		int y = slot.getY();
        		InvManagerItem item = slot.getItem();
        		int renderX = left + x*21;
        		int renderY = top - y*21;
        		ItemStack stack = item == null ? null : new ItemStack(Item.getItemById(item.getId()));
        		if(stack != null)
        		if( Item.getIdFromItem(stack.getItem()) == Item.getIdFromItem(Items.skull) && stack.getItemDamage() !=3) {
        			stack.setItemDamage(3);
        		}
        		if(isMouseOverSlot(renderX, renderY, mouseX, mouseY)){
        			mouseSlot = slot;		
        		}
        		drawSlot(renderX, renderY);
        		
        		if(item != null)
        		if(!item.isSelected()){
        			drawItemStack(renderX+3, renderY+3, stack);
        		}else{
        			drawItemStack(mouseX-7, mouseY-7, stack);
        		}
        	}
        }
        float xT = (float) (left+42);
        float yT = (float) (top-15); 
        FontManager.big.drawString("\247aInventory", xT, yT, -1);
        FontManager.big.drawString("\247cClean", xT, yT+44, -1);
//        drawTrash(xT, yT);
        
        
    }

    
    @Override
    protected void mouseClicked(int clicX, int clicY, int button) {
        try {
            if(mouseSlot != null){
            	if(mouseSlot.getItem() != null){
            		mouseSlot.getItem().setSelected(true);
            		selected = mouseSlot.getItem();
            	}
            }
            super.mouseClicked(clicX, clicY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    	if(selected != null){
			if(mouseSlot == null){
				selected.getSlot().setItem(new InvManagerItem(selected.getSlot(), selected.getId(), false));
			}else{
				if(mouseSlot.getItem() == null){
					selected.getSlot().setItem(null);
		   			mouseSlot.setItem(new InvManagerItem(mouseSlot, selected.getId(), false));
				}else{
					selected.getSlot().setItem(new InvManagerItem(selected.getSlot(), selected.getId(), false));
				}  		
			}		
			selected = null;	
		}
    	
		super.mouseReleased(mouseX, mouseY, state);
    
    }
    private boolean isMouseOverSlot(int slotX, int slotY, int mouseX, int mouseY){
    	int top = slotY+1;
    	int bottom = slotY + 21;
    	int left = slotX;
    	int right = slotX + 21;
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY <  bottom;
    }
  
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	if(keyCode == Keyboard.KEY_ESCAPE){
    		this.mc.displayGuiScreen(null);
    		saveConfigs();
    	}else if(keyCode == mc.gameSettings.keyBindInventory.getKeyCode()){
    		this.mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    		saveConfigs();
    	}
    }
    
    void drawSlot(int x, int y) {
    	this.mc.getTextureManager().bindTexture(inventoryBackground);
    	this.drawVerticalLine(x+21,y-1, y+22, Colors.getColor(0, 0, 0, 255));
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        this.drawTexturedModalRect(x, y, 0.0D, 0, 21, 22);	
    }
    
    void drawTrash(double x, double y){
       	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);      
    	this.mc.getTextureManager().bindTexture(trash);
    	GL11.glScalef(0.17f, 0.17f, 0.17f);
        this.drawTexturedModalRect(x*10 -420, y*10-300, 0, 0, 220, 255);	  
    }
    
    public void drawTexturedModalRect(double x, double y, double textureX, double textureY, double width, double height) {

		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (x + 0), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + height) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1))
				.endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		worldrenderer.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		tessellator.draw();
	}
    
    void drawItemStack(int x, int y, ItemStack stack){
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
		RenderHelper.enableGUIStandardItemLighting();

		
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, x, y, "");
        RenderHelper.disableStandardItemLighting();
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        GL11.glColor4f(1, 1, 1, 1);
    }
    public static void saveConfigs(){
    	  List<String> fileContent = new ArrayList<>();
          for (InvManagerSlot slot : slots) {
        	  InvManagerItem it = slot.getItem();
        	  if(it != null){
        		  String add = it.getId() + ":" + it.getSlot().getID();
        		  fileContent.add(add);
        	  }
          }
          File SETTINGS_DIR = FileUtil.getConfigFile("InvManager");
          FileUtil.write(SETTINGS_DIR, fileContent, true);
          for (InvManagerSlot slot : slots) {
        	  InvManagerItem it = slot.getItem();
        	  if(it == null)
        	  	continue;
        	  int id = it.getId();
        	  int slotID = slot.getID();
        	  
        	  if(id > 0)
        	  if(id==276){
        		  InventoryManager.weaponSlot = 36 + slotID;
        	  }else if(id == 322){
        		  InventoryManager.gappleSlot = 36 + slotID;
        	  }else if(id == 397){
        		  InventoryManager.gheadSlot = 36 + slotID;
        	  }else if(id == 277){
        		  InventoryManager.shovelSlot = 36 + slotID;
        	  }else if(id == 278){
        		  InventoryManager.pickaxeSlot = 36 + slotID;
        	  }else if(id == 279){
        		  InventoryManager.axeSlot = 36 +  slotID;
        	  }
          }	 
    }
    
    
    
    
    public static void loadConfig(){ 	
        try {
        	File SETTINGS_DIR = FileUtil.getConfigFile("InvManager");
            List<String> fileContent = FileUtil.read(SETTINGS_DIR);
       
            int lenght = fileContent.size();
            int id = 276;
            int[] item = {276,322,397,277,278,279};
            
            GuiInvManager.slots.clear();
            
            for(int i = -9; i < 9; i++){
            	int x = i<0? i+11: i+2;
            	int y = i < 0 ? -2 : 0;	
            	slots.add(new InvManagerSlot(i, x, y, null));
            }
//            for(int i = -4; i < 9; i++){
//            	int x = i<0?0: i+2;
//            	int y = i < 0? i+1:0;	
//            	slots.add(new InvManagerSlot(i, x, y, null));
//            }

            if(fileContent.isEmpty()){
            	for(int i = 9 ; i < 15; i++){
                	slots.get(i).setItem(new InvManagerItem(slots.get(i), item[i-9], false));
                }
            }else{
            	for(int i = 0; i < lenght; i++){
                	String[] split = fileContent.get(i).split(":");
                	id = Integer.parseInt(split[0]);
                	int slotID = Integer.parseInt(split[1]);
                	for(InvManagerSlot slot : slots){
                		if(slot.getID() == slotID){
                			slot.setItem(new InvManagerItem(slot, id, false));
                		}
                	}
                } 
            }   
            saveConfigs();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
