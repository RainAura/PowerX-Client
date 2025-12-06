package cn.Power.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import cn.Power.mod.mods.PLAYER.*;
import cn.Power.native0;
import cn.Power.mod.mods.ClickGui;
import cn.Power.mod.mods.COMBAT.AimAssist;
import cn.Power.mod.mods.COMBAT.AntiBot;
import cn.Power.mod.mods.COMBAT.AntiKB;
import cn.Power.mod.mods.COMBAT.AutoClicker;
import cn.Power.mod.mods.COMBAT.AutoHeal;
import cn.Power.mod.mods.COMBAT.AutoPot;
import cn.Power.mod.mods.COMBAT.AutoSword;
import cn.Power.mod.mods.COMBAT.BowAimbot;
import cn.Power.mod.mods.COMBAT.Criticals;
import cn.Power.mod.mods.COMBAT.FastBow;
import cn.Power.mod.mods.COMBAT.Hitbox;
import cn.Power.mod.mods.COMBAT.KeepSprint;
import cn.Power.mod.mods.COMBAT.KillAura;
import cn.Power.mod.mods.COMBAT.Reach;
import cn.Power.mod.mods.COMBAT.Tpaura;
import cn.Power.mod.mods.COMBAT.Velocity;
import cn.Power.mod.mods.COMBAT.WTap;
import cn.Power.mod.mods.MOVEMENT.AirBounce;
import cn.Power.mod.mods.MOVEMENT.AntiFall;
import cn.Power.mod.mods.MOVEMENT.AutoMLG;
import cn.Power.mod.mods.MOVEMENT.Fly;
import cn.Power.mod.mods.MOVEMENT.InvMove;
import cn.Power.mod.mods.MOVEMENT.Jesus;
import cn.Power.mod.mods.MOVEMENT.LongJump;
import cn.Power.mod.mods.MOVEMENT.LookTP;
import cn.Power.mod.mods.MOVEMENT.NoFall;
import cn.Power.mod.mods.MOVEMENT.NoSlowdown;
import cn.Power.mod.mods.MOVEMENT.Safewalk;
import cn.Power.mod.mods.MOVEMENT.Scaffold;
import cn.Power.mod.mods.MOVEMENT.Sneak;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.MOVEMENT.SpeedChanger;
import cn.Power.mod.mods.MOVEMENT.Sprint;
import cn.Power.mod.mods.MOVEMENT.Strafe;
import cn.Power.mod.mods.MOVEMENT.TargetStrafe;
import cn.Power.mod.mods.MOVEMENT.ZoomFly;
import cn.Power.mod.mods.RENDER.Animation;
import cn.Power.mod.mods.RENDER.AntiInvis;
import cn.Power.mod.mods.RENDER.Arrow;
import cn.Power.mod.mods.RENDER.BlockESP;
import cn.Power.mod.mods.RENDER.BlockOverlay;
import cn.Power.mod.mods.RENDER.Chams;
import cn.Power.mod.mods.RENDER.ChestESP;
import cn.Power.mod.mods.RENDER.Crosshair;
import cn.Power.mod.mods.RENDER.DMGParticle;
import cn.Power.mod.mods.RENDER.Dab;
import cn.Power.mod.mods.RENDER.DragWings;
import cn.Power.mod.mods.RENDER.ESP;
import cn.Power.mod.mods.RENDER.ESP2D;
import cn.Power.mod.mods.RENDER.EnchantEffect;
import cn.Power.mod.mods.RENDER.FinalKillsCounter;
import cn.Power.mod.mods.RENDER.FullBright;
import cn.Power.mod.mods.RENDER.HorseFounder;
import cn.Power.mod.mods.RENDER.Hud;
import cn.Power.mod.mods.RENDER.ItemESP;
import cn.Power.mod.mods.RENDER.ItemPhysic;
import cn.Power.mod.mods.RENDER.ItemTags;
import cn.Power.mod.mods.RENDER.MotionBlur;
import cn.Power.mod.mods.RENDER.NameTag;
import cn.Power.mod.mods.RENDER.NameTag2D;
import cn.Power.mod.mods.RENDER.NoFov;
import cn.Power.mod.mods.RENDER.NoHurtcam;
import cn.Power.mod.mods.RENDER.Projectiles;
import cn.Power.mod.mods.RENDER.Radar;
import cn.Power.mod.mods.RENDER.ScoreBoard;
import cn.Power.mod.mods.RENDER.Skeletal;
import cn.Power.mod.mods.RENDER.ViewClip;
//import cn.Power.mod.mods.RENDER.Waypoints;
import cn.Power.mod.mods.RENDER.Weather;
import cn.Power.mod.mods.WORLD.AntiVanish;
import cn.Power.mod.mods.WORLD.AutoCraft;
import cn.Power.mod.mods.WORLD.AutoL;
import cn.Power.mod.mods.WORLD.BetterBeacons;
import cn.Power.mod.mods.WORLD.ChatCommands;
import cn.Power.mod.mods.WORLD.ChatFilter;
import cn.Power.mod.mods.WORLD.ChestStealer;
import cn.Power.mod.mods.WORLD.Eagle;
import cn.Power.mod.mods.WORLD.FakeName;
import cn.Power.mod.mods.WORLD.FastPlace;
import cn.Power.mod.mods.WORLD.InvCleaner;
import cn.Power.mod.mods.WORLD.InventoryManager;
import cn.Power.mod.mods.WORLD.LightningTrack;
import cn.Power.mod.mods.WORLD.MCF;
import cn.Power.mod.mods.WORLD.NoPush;
import cn.Power.mod.mods.WORLD.NoWallDamage;
import cn.Power.mod.mods.WORLD.Nuker;
import cn.Power.mod.mods.WORLD.ReachBlock;
import cn.Power.mod.mods.WORLD.Spammer;
import cn.Power.mod.mods.WORLD.SpeedMine;
import cn.Power.mod.mods.WORLD.Teams;
import cn.Power.mod.mods.WORLD.Timer;
import cn.Power.mod.mods.WORLD.Tracers;
import cn.Power.mod.mods.WORLD.WayPoints;
import cn.Power.mod.mods.WORLD.Xray;

public class ModManager {
	public static HashMap<Class<? extends Mod>, Mod> modList = new HashMap<Class<? extends Mod>, Mod>();


	public ModManager() {
		
		onCreate();
		
	}
	
	@native0
	public void onCreate() {
		// NOTHING
				this.addMod(new ClickGui());

				// COMBAT
				this.addMod(new WTap());
				this.addMod(new Reach());
				this.addMod(new Hitbox());
				this.addMod(new AntiBot());
				this.addMod(new Tpaura());
				this.addMod(new KillAura());
				this.addMod(new AntiKB());
				this.addMod(new Velocity());
				this.addMod(new AutoPot());
				this.addMod(new Criticals());
				this.addMod(new FastBow());
				this.addMod(new AutoHeal());
				this.addMod(new AimAssist());
				this.addMod(new KeepSprint());
				this.addMod(new AutoSword());
				this.addMod(new AutoClicker());
				this.addMod(new BowAimbot());

				// MOVEMENT
				this.addMod(new Fly());
				this.addMod(new Sprint());
				this.addMod(new Jesus());
				this.addMod(new Speed());
				this.addMod(new Sneak());
				this.addMod(new Strafe());
				this.addMod(new LookTP());
				this.addMod(new NoFall());
				this.addMod(new NoSlowdown());
				this.addMod(new TargetStrafe());
				this.addMod(new Scaffold());
				this.addMod(new ZoomFly());
				this.addMod(new InvMove());
				this.addMod(new AntiFall());
				this.addMod(new AutoMLG());
				this.addMod(new Safewalk());
		    	this.addMod(new AirBounce());
				this.addMod(new LongJump());
				this.addMod(new HorseFounder());

				// this.addMod(new Scaffold2());
				// this.addMod(new Scaffold3());
				// this.addMod(new Scaffold4());

				// RENDER
				this.addMod(new Hud());
				this.addMod(new Dab());
				this.addMod(new ESP());
				this.addMod(new Arrow());
				this.addMod(new Radar());
				this.addMod(new NoFov());
				this.addMod(new Chams());
				this.addMod(new ESP2D());
				this.addMod(new Tracers());
				this.addMod(new Skeletal());
				this.addMod(new ItemESP());
				this.addMod(new NameTag());
				this.addMod(new ScoreBoard());
				this.addMod(new NameTag2D());
				this.addMod(new DragWings());
//		    	this.addMod(new Waypoints());
				this.addMod(new WayPoints());
				this.addMod(new Weather());
				this.addMod(new ViewClip());
				this.addMod(new AntiInvis());
				this.addMod(new BlockESP());
				this.addMod(new ChestESP());
				this.addMod(new FullBright());
				this.addMod(new Crosshair());
				this.addMod(new Animation());
				this.addMod(new Projectiles());
				this.addMod(new ItemPhysic());
				this.addMod(new MotionBlur());
				this.addMod(new NoHurtcam());
				this.addMod(new DMGParticle());
				this.addMod(new BlockOverlay());
				this.addMod(new EnchantEffect());
				this.addMod(new SpeedChanger());
				this.addMod(new FastEat());
				this.addMod(new FinalKillsCounter());
				this.addMod(new ItemTags());

				// PLAYER
				this.addMod(new Step());
				this.addMod(new Derp());
				this.addMod(new Blink());
				this.addMod(new Phase());
		    	this.addMod(new AntiDesync());
				this.addMod(new Germ());
				this.addMod(new ReachBlock());
				this.addMod(new Teleport());
				this.addMod(new AntiObbyTrap());
				this.addMod(new AutoTool());
				this.addMod(new Freecam());
				this.addMod(new Respawn());
				this.addMod(new NoRotate());
				this.addMod(new BedFucker());
				this.addMod(new AutoArmor());
				this.addMod(new DankBobbing());
				this.addMod(new NoHitBox());
				this.addMod(new FreeCamTP());

				// WORLD
				// this.addMod(new XrayESP());
				this.addMod(new LightningTrack());
				this.addMod(new Xray());
//		    	this.addMod(new XrayESP());
				this.addMod(new MCF());
				this.addMod(new Eagle());
				this.addMod(new AutoL());
				this.addMod(new Timer());
				this.addMod(new Teams());
		    //  	this.addMod(new EntitySpeed());  	
			//	this.addMod(new Penshen());
				this.addMod(new FastPlace());
				this.addMod(new Spammer());
			//	this.addMod(new PingSpoof());
				this.addMod(new AutoCraft());
				this.addMod(new FakeName());
				this.addMod(new ChatFilter());
				this.addMod(new ChatCommands());
		    	this.addMod(new NoWallDamage());
				this.addMod(new SpeedMine());
				this.addMod(new InvCleaner());
				this.addMod(new InventoryManager());
				this.addMod(new ChestStealer());
//				this.addMod(new Dismount());
				this.addMod(new Nuker());
				this.addMod(new AntiVanish());
//				this.addMod(new BoatFlyHelper());
				this.addMod(new NoPush());
//				this.addMod(new Exploit());
				this.addMod(new BetterBeacons());
	}
	

	public void addMod(Mod m) {
		modList.put(m.getClass(), m);
	}

	public ArrayList<Mod> getToggled() {
		return (ArrayList<Mod>) modList.values().stream().filter(mod -> ((Mod) mod).isEnabled()).collect(Collectors.toCollection(ArrayList::new));
//		ArrayList<Mod> toggled = new ArrayList();
//		for (Object m1 : this.modList) {
//			Mod m = (Mod) m1;
//			if (m.isEnabled()) {
//				toggled.add(m);
//			}
//		}
//		return toggled;
	}

	public ArrayList<Mod> getHUDToggled() {
		
		return (ArrayList<Mod>) modList.values().stream().filter(m -> (!(m == getModByClass(Hud.class)) && !(m == getModByClass(ChatCommands.class))
				&& !(m == getModByClass(Animation.class)) && !(m == getModByClass(ItemPhysic.class)))).collect(Collectors.toCollection(ArrayList::new));
//		ArrayList<Mod> toggled = new ArrayList();
//		for (Object m1 : this.modList) {
//			Mod m = (Mod) m1;
//			if (!(m == getModByClass(Hud.class)) && !(m == getModByClass(ChatCommands.class))
//					&& !(m == getModByClass(Animation.class)) && !(m == getModByClass(ItemPhysic.class))) {
//				if (m.isEnabled()) {
//					toggled.add(m);
//				}
//			}
//		}
//		return toggled;
	}

	public ArrayList<Mod> getopenValues() {
		return (ArrayList<Mod>) modList.values().stream().filter(mod -> ((Mod) mod).openValues).collect(Collectors.toCollection(ArrayList::new));

//		ArrayList<Mod> Open = new ArrayList();
//		for (Object m1 : this.modList) {
//			Mod m = (Mod) m1;
//			if (m.openValues) {
//				Open.add(m);
//			}
//		}
//		return Open;
	}

	public static Mod getModByName(String modname) {
		return (Mod) modList.values().stream().filter(mod -> ((Mod) mod).getName().equalsIgnoreCase(modname)).findFirst().orElse(null);

//		for (Object m1 : modList) {
//			Mod m = (Mod) m1;
//			if (!m.getName().equalsIgnoreCase(mod))
//				continue;
//			return m;
//		}
//		return null;
	}

//	public static Mod getModByClass(Class<? extends Mod> cls) {
//		return  (Mod) modList.get(cls);
//	}

	public static <Module extends Mod> Module getModByClass(Class<? extends Module> cls) {
		return  (Module) modList.get(cls);
	}
	
	public static HashMap<Class<? extends Mod>, Mod> getModList() {
		return modList;
	}

	public ArrayList<Mod> getModHidden() {
		return (ArrayList<Mod>) modList.values().stream().filter(m -> (!(m == ModManager.getModByClass(Hud.class)) && !(m == ModManager.getModByClass(ChatCommands.class))
				&& !(m == ModManager.getModByClass(Animation.class))
				&& !(m == ModManager.getModByClass(ItemPhysic.class))
				&& !(m == ModManager.getModByClass(DragWings.class))
				&& !(m == ModManager.getModByClass(BlockOverlay.class))
				&& !(m == ModManager.getModByClass(DMGParticle.class))
				&& !(m == ModManager.getModByClass(NoHurtcam.class))
				&& !(m == ModManager.getModByClass(FullBright.class)))).collect(Collectors.toCollection(ArrayList::new));
//
//		ArrayList<Mod> Hidden = new ArrayList();
//		for (Mod m : this.modList) {
//			if (!(m == ModManager.getModByClass(Hud.class)) && !(m == ModManager.getModByClass(ChatCommands.class))
//					&& !(m == ModManager.getModByClass(Animation.class))
//					&& !(m == ModManager.getModByClass(ItemPhysic.class))
//					&& !(m == ModManager.getModByClass(DragWings.class))
//					&& !(m == ModManager.getModByClass(BlockOverlay.class))
//					&& !(m == ModManager.getModByClass(DMGParticle.class))
//					&& !(m == ModManager.getModByClass(NoHurtcam.class))
//					&& !(m == ModManager.getModByClass(FullBright.class))) {
//				Hidden.add(m);
//			}
//		}
//		return Hidden;
	}
}
