package cn.Power.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.lwjgl.input.Keyboard;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.ui.login.Alt;
import cn.Power.ui.login.AltManager;
import cn.Power.util.friendManager.Friend;
import cn.Power.util.friendManager.FriendManager;
import net.minecraft.client.Minecraft;

public class FileUtil {

	private Minecraft mc = Minecraft.getMinecraft();
	private String fileDir;
	private static File dir;
	private static final File ALT;
	private static final File LASTALT;
//    public static final File WAYPOINTS;

	static {
		final File mcDataDir = Minecraft.getMinecraft().mcDataDir;
		FileUtil.dir = new File(mcDataDir, Client.CLIENT_File);
		ALT = getConfigFile("Alts");
		LASTALT = getConfigFile("LastAlt");
//        WAYPOINTS = getConfigFile("Waypoints");
	}

	public FileUtil() {
		this.fileDir = String.valueOf((Object) this.mc.mcDataDir.getAbsolutePath()) + "/" + Client.CLIENT_File;
		File fileFolder = new File(this.fileDir);
		if (!fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		try {
			this.loadKeys();
			this.loadValues();
			this.loadMods();
			// this.loadBlocks();
			this.loadNameProtect();
			this.loadHideMods();
			this.loadFriends();
			if (!FileUtil.dir.exists()) {
				FileUtil.dir.mkdir();
			}
			loadLastAlt();
			loadAlts();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getConfigFile(final String name) {
		final File file = new File(FileUtil.dir, String.format("%s.txt", name));
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
			}
		}
		return file;
	}

	public static void saveLastAlt() {
		try {
			final PrintWriter printWriter = new PrintWriter(FileUtil.LASTALT);
			final Alt alt = Client.instance.getAltManager().getLastAlt();
			if (alt != null) {
				if (alt.getMask().equals("")) {
					printWriter.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword());
				} else {
					printWriter.println(
							String.valueOf(alt.getMask()) + "    " + alt.getUsername() + ":" + alt.getPassword());
				}
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void loadLastAlt() {
		try {
			if (!FileUtil.LASTALT.exists()) {
				final PrintWriter printWriter = new PrintWriter(new FileWriter(FileUtil.LASTALT));
				printWriter.println();
				printWriter.close();
			} else if (FileUtil.LASTALT.exists()) {
				final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileUtil.LASTALT));
				String s;
				while ((s = bufferedReader.readLine()) != null) {
					if (s.contains("\t")) {
						s = s.replace("\t", "    ");
					}
					if (s.contains("    ")) {
						final String[] parts = s.split("    ");
						final String[] account = parts[1].split(":");
						if (account.length == 2) {
							Client.instance.getAltManager().setLastAlt(new Alt(account[0], account[1], parts[0]));
						} else {
							String pw = account[1];
							for (int i = 2; i < account.length; ++i) {
								pw = String.valueOf(pw) + ":" + account[i];
							}
							Client.instance.getAltManager().setLastAlt(new Alt(account[0], pw, parts[0]));
						}
					} else {
						final String[] account2 = s.split(":");
						if (account2.length == 1) {
							Client.instance.getAltManager().setLastAlt(new Alt(account2[0], ""));
						} else if (account2.length == 2) {
							Client.instance.getAltManager().setLastAlt(new Alt(account2[0], account2[1]));
						} else {
							String pw2 = account2[1];
							for (int j = 2; j < account2.length; ++j) {
								pw2 = String.valueOf(pw2) + ":" + account2[j];
							}
							Client.instance.getAltManager().setLastAlt(new Alt(account2[0], pw2));
						}
					}
				}
				bufferedReader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static void loadAlts() {
		try {
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileUtil.ALT));
			if (!FileUtil.ALT.exists()) {
				final PrintWriter printWriter = new PrintWriter(new FileWriter(FileUtil.ALT));
				printWriter.println();
				printWriter.close();
			} else if (FileUtil.ALT.exists()) {
				String s;
				while ((s = bufferedReader.readLine()) != null) {
					if (s.contains("\t")) {
						s = s.replace("\t", "    ");
					}
					if (s.contains("    ")) {
						final String[] parts = s.split("    ");
						final String[] account = parts[1].split(":");
						if (account.length == 2) {
							Client.instance.getAltManager();
							AltManager.getAlts().add(new Alt(account[0], account[1], parts[0]));
						} else {
							String pw = account[1];
							for (int i = 2; i < account.length; ++i) {
								pw = String.valueOf(pw) + ":" + account[i];
							}
							Client.instance.getAltManager();
							AltManager.getAlts().add(new Alt(account[0], pw, parts[0]));
						}
					} else {
						final String[] account2 = s.split(":");
						if (account2.length == 1) {
							Client.instance.getAltManager();
							AltManager.getAlts().add(new Alt(account2[0], ""));
						} else if (account2.length == 2) {
							try {
								Client.instance.getAltManager();
								AltManager.getAlts().add(new Alt(account2[0], account2[1]));
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							String pw2 = account2[1];
							for (int j = 2; j < account2.length; ++j) {
								pw2 = String.valueOf(pw2) + ":" + account2[j];
							}
							Client.instance.getAltManager();
							AltManager.getAlts().add(new Alt(account2[0], pw2));
						}
					}
				}
			}
			bufferedReader.close();
		} catch (Exception ex) {
		}
	}

	public static void saveAlts() {
		try {
			final PrintWriter printWriter = new PrintWriter(FileUtil.ALT);
			Client.instance.getAltManager();
			for (final Alt alt : AltManager.getAlts()) {
				if (alt.getMask().equals("")) {
					printWriter.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword());
				} else {
					printWriter.println(
							String.valueOf(alt.getMask()) + "    " + alt.getUsername() + ":" + alt.getPassword());
				}
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void saveBlocks() { File f = new File(String.valueOf(this.fileDir) +
	 * "/blocks.txt"); try { if (!f.exists()) { f.createNewFile(); } PrintWriter pw
	 * = new PrintWriter(f); Iterator<Integer> iterator =
	 * BlockESP.getBlockIds().iterator(); while (iterator.hasNext()) { int id =
	 * iterator.next(); pw.print(String.valueOf(String.valueOf(id)) + "\n"); }
	 * pw.close(); } catch (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * public void loadBlocks() throws IOException { File f = new
	 * File(String.valueOf(this.fileDir) + "/blocks.txt"); if (!f.exists()) {
	 * f.createNewFile(); } else { String line;
	 * 
	 * @SuppressWarnings("resource") BufferedReader br = new BufferedReader(new
	 * FileReader(f)); while ((line = br.readLine()) != null) { try { int id =
	 * Integer.valueOf(line); BlockESP.getBlockIds().add(id); } catch (Exception id)
	 * { } } } }
	 */
	public void saveKeys() {
		File f = new File(String.valueOf((Object) this.fileDir) + "/keys.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			for (Object m1 : ModManager.modList.values().toArray()) {
				Mod m = (Mod)m1;
				String keyName = m.getKey() < 0 ? "None" : Keyboard.getKeyName((int) m.getKey());
				pw.write(String.valueOf((Object) m.getName()) + ":" + keyName + "\n");
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadKeys() throws IOException {
		File f = new File(String.valueOf((Object) this.fileDir) + "/keys.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			String line;

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader((Reader) new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (!line.contains((CharSequence) ":"))
					continue;
				String[] split = line.split(":");
				Mod m = ModManager.getModByName((String) split[0]);
				int key = Keyboard.getKeyIndex((String) split[1]);
				if (m == null || key == -1)
					continue;
				m.setKey(key);
			}

		}
	}

	public void saveMods() {
		File f = new File(String.valueOf((Object) this.fileDir) + "/mods.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			for (Object m1 : ModManager.modList.values().toArray()) {
				Mod m = (Mod)m1;
				pw.print(String.valueOf((Object) m.getName()) + ":" + m.isEnabled() + "\n");
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMods() throws IOException {
		File f = new File(String.valueOf((Object) this.fileDir) + "/mods.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			String line;
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader((Reader) new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (!line.contains((CharSequence) ":"))
					continue;
				String[] split = line.split(":");
				Mod m = ModManager.getModByName((String) split[0]);
				boolean state = Boolean.parseBoolean((String) split[1]);
				if (m == null)
					continue;
				m.set(state, false);
			}
		}
	}

	
	public void saveNameProtect() {
        File f = new File(String.valueOf(this.fileDir) + "/fakename.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            PrintWriter pw = new PrintWriter(f);
            if(Objects.nonNull(Client.ClientCode))
            pw.print(Client.ClientCode);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNameProtect() throws IOException {
        File f = new File(String.valueOf(this.fileDir) + "/fakename.txt");
        if (!f.exists()) {
            f.createNewFile();
        } else {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while((line = br.readLine()) != null) {
                if(!line.equalsIgnoreCase("")){
                    Client.ClientCode = line;
                }
            }
            br.close();
        }
    }
    
	public void saveHideMods() {
		File f = new File(String.valueOf((Object) this.fileDir) + "/Hide.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			for (Object m1 : ModManager.modList.values().stream().toArray()) {
				Mod m = (Mod)m1;
				if (m.HideMod) {
					pw.print(String.valueOf((Object) m.getName()) + ":" + m.HideMod + "\n");
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadHideMods() throws IOException {
		File f = new File(String.valueOf((Object) this.fileDir) + "/Hide.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			String line;
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader((Reader) new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (!line.contains((CharSequence) ":"))
					continue;
				String[] split = line.split(":");
				Mod m = ModManager.getModByName((String) split[0]);
				boolean state = Boolean.parseBoolean((String) split[1]);
				if (m == null)
					continue;
				m.HideMod = state;
			}
		}
	}

	public void saveValues() {
		File f = new File(String.valueOf((Object) this.fileDir) + "/values.txt");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			for (Value value : Value.list) {
				String valueName = value.getValueName();
				if (value.isValueBoolean) {
					pw.print(String.valueOf((Object) valueName) + ":b:" + value.getValueState() + "\n");
					continue;
				}
				if (value.isValueDouble) {
					pw.print(String.valueOf((Object) valueName) + ":d:" + value.getValueState() + "\n");
					continue;
				}
				if (!value.isValueMode)
					continue;
				pw.print(String.valueOf((Object) valueName) + ":s:" + value.getModeTitle() + ":"
						+ value.getCurrentMode() + "\n");
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadValues() throws IOException {
		File f = new File(String.valueOf((Object) this.fileDir) + "/values.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			String line;
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader((Reader) new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (!line.contains((CharSequence) ":"))
					continue;
				String[] split = line.split(":");
				for (Value value : Value.list) {
					if (!split[0].equalsIgnoreCase(value.getValueName()))
						continue;
					if (value.isValueBoolean && split[1].equalsIgnoreCase("b")) {
						value.setValueState((Object) Boolean.parseBoolean((String) split[2]));
						continue;
					}
					if (value.isValueDouble && split[1].equalsIgnoreCase("d")) {
						value.setValueState((Object) Double.parseDouble((String) split[2]));
						continue;
					}
					if (!value.isValueMode || !split[1].equalsIgnoreCase("s")
							|| !split[2].equalsIgnoreCase(value.getModeTitle()))
						continue;
					value.setCurrentMode(Integer.parseInt((String) split[3]));
				}
			}
		}
	}

	public void saveFriends() {
		File f = new File(String.valueOf(this.fileDir) + "/friend.txt");

		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintWriter pw = new PrintWriter(f);
			Iterator var4 = FriendManager.getFriends().iterator();
			while (var4.hasNext()) {
				Friend friend = (Friend) var4.next();
				pw.print(friend.getName() + ":" + friend.getAlias() + "\n");
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFriends() throws IOException {
		File f = new File(String.valueOf(this.fileDir) + "/friend.txt");
		if (!f.exists()) {
			f.createNewFile();
		} else {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(f));

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(":")) {
					String[] split = line.split(":");
					if (line.length() >= 2) {
						Friend friend = new Friend(split[0], split[1]);
						FriendManager.getFriends().add(friend);
					}
				}
			}
		}

	}

	public static List<String> read(final File inputFile) {

		final List<String> readContent = new ArrayList<String>();
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
			String str;
			while ((str = in.readLine()) != null) {
				readContent.add(str);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readContent;
	}

	public static void write(final File outputFile, final List<String> writeContent, final boolean overrideContent) {
		try {
			final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			for (final String outputLine : writeContent) {
				out.write(String.valueOf(outputLine) + System.getProperty("line.separator"));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
