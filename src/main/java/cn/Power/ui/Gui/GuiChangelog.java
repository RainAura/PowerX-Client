package cn.Power.ui.Gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.Power.Font.FontManager;
import cn.Power.util.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiChangelog extends GuiScreen {
	
	private final GuiScreen previousScreen;

	private final List<String> added = new ArrayList<String>();
	private final List<String> removed = new ArrayList<String>();
	private final List<String> changed = new ArrayList<String>();
	String changelog ;
    public GuiChangelog(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }
    
	@Override
	public void initGui() {
		clear();
//		if(changelog == null)
		changelog = sendGet("http://PowerClient.fun/PowerX/Changelog");
		parseText(changelog);
        this.buttonList.add(new GuiButton(0, this.width-75, this.height - 20, 75, 20, I18n.format("gui.cancel")));
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground(0);
		
		RenderUtil.rect(width / 2 - 200, 0, 400, height, new Color(20, 20, 20, 200));
		
		int yPosOffset = 20;
		
		if (added.size() > 0) {
			FontManager.sw15.drawStringWithShadow("Added:", width / 2 - 180, yPosOffset, new Color(50, 250, 50).getRGB());
			
			yPosOffset += FontManager.sw15.FONT_HEIGHT;
			
			for (String line : added) {
				FontManager.sw15.drawStringWithShadow(line, width / 2 - 180, yPosOffset, new Color(50, 250, 50).getRGB());
				yPosOffset += FontManager.sw15.FONT_HEIGHT + 2;
			}
			
			yPosOffset += 20;
		}
		
		if (changed.size() > 0) {
			FontManager.sw15.drawStringWithShadow("Changed:", width / 2 - 180, yPosOffset, new Color(250, 150, 50).getRGB());
			
			yPosOffset += FontManager.sw15.FONT_HEIGHT + 5;
			
			for (String line : changed) {
				FontManager.sw15.drawStringWithShadow(line, width / 2 - 180, yPosOffset, new Color(250, 150, 50).getRGB());
				yPosOffset += FontManager.sw15.FONT_HEIGHT + 2;
			}
			
			yPosOffset += 20;
		}
		
		if (removed.size() > 0) {
			FontManager.sw15.drawStringWithShadow("Removed:", width / 2 - 180, yPosOffset, new Color(250, 50, 50).getRGB());
			
			yPosOffset += FontManager.sw15.FONT_HEIGHT;
			
			for (String line : removed) {
				FontManager.sw15.drawStringWithShadow(line, width / 2 - 180, yPosOffset, new Color(250, 50, 50).getRGB());
				yPosOffset += FontManager.sw15.FONT_HEIGHT + 2;
			}
		}
		
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	 @Override
	    public void actionPerformed(GuiButton button) {
	        switch (button.id) {
	            case 0: {
	            	   this.mc.displayGuiScreen(this.previousScreen);	            	
	            }
	        }
	 }
	 public static String sendGet(final String url) {
		    String result = "";
	        try {
	    	    final String urlNameString = url;
	    	    final URL realurl = new URL(urlNameString);
		        HttpURLConnection httpUrlConn = (HttpURLConnection) realurl.openConnection();
		        httpUrlConn.setDoInput(true);  
	            httpUrlConn.setRequestMethod("GET");  
	            httpUrlConn.setRequestProperty("User-Agent"," Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
	            InputStream input = httpUrlConn.getInputStream();
	            InputStreamReader read = new InputStreamReader(input, "utf-8");
	            BufferedReader br = new BufferedReader(read);
	            String data = br.readLine();
	            while(data!=null) {
	                result = String.valueOf(result) + data + "\n";
	                data=br.readLine();
	            }
	       br.close();  
	       read.close();  
	       input.close();  
	       httpUrlConn.disconnect();  
	       } catch (MalformedURLException e) {
	       } catch (IOException e) {
	       }
		    return result;
	    }
	 
	private void clear() {
		added.clear();
		changed.clear();
		removed.clear();
	}
	
	private void parseText(String text) {
		if (text.isEmpty()) return;
		final String[] lines = text.split("\n");
		for (String line : lines) {
			StringType type = getTypeOfString(line);
			if (type == StringType.ADD) {
				added.add(line);
			} else if (type == StringType.REMOVE) {
				removed.add(line);
			} else if (type == StringType.CHANGE) {
				changed.add(line);
			}
		}
	}
	
	private StringType getTypeOfString(String string) {
		StringType type = null;
		if (string.startsWith("+")) {
			type = StringType.ADD;
		} else if (string.startsWith("-")) {
			type = StringType.REMOVE;
		} else if (string.startsWith("~")) {
			type = StringType.CHANGE;
		}
		return type;
	}
	
	public enum StringType {
		ADD, REMOVE, CHANGE
	}
	
}
