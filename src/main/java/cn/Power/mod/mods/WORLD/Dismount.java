package cn.Power.mod.mods.WORLD;

import java.net.URLClassLoader;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import com.darkmagician6.eventapi.types.Priority;

import cn.Power.Client;
import cn.Power.Value;
import cn.Power.native0;
import cn.Power.events.EventLiquidCollide;
import cn.Power.events.EventMove;
import cn.Power.events.EventPacket;
import cn.Power.events.EventPreMotion;
import cn.Power.events.EventRespawn;
import cn.Power.events.EventUpdate;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import cn.Power.mod.ModManager;
import cn.Power.mod.mods.COMBAT.Criticals;
import cn.Power.mod.mods.COMBAT.Tpaura;
import cn.Power.mod.mods.MOVEMENT.Speed;
import cn.Power.mod.mods.PLAYER.Freecam;
import cn.Power.notification.Notification;
import cn.Power.util.ChatType;
import cn.Power.util.ClientUtil;
import cn.Power.util.SkyBlockUtils;
import cn.Power.util.misc.ChatUtil;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

public class Dismount extends Mod {
	public static Entity entity;
	
	public static double[] pos;
	public static double[] posBegin;
	
	public int oldspeed = -1;
	public static boolean hasHurt = false;

	public Value<Boolean> Lagback = new Value("Dismount_LagBackCheck", true);
	public Value<Boolean> Jetpack = new Value("Dismount_Jetpack", true);
	public Value<Boolean> LessPacket = new Value("Dismount_LessPacket", true);
	public Value<Boolean> PitchOffset = new Value("Dismount_PitchOffset", false);
	public Value<Boolean> SwitchSpeed = new Value("Dismount_SwitchSpeed", false);
	public Value<Double> pitchOffset = new Value<Double>("Dismount_PitchOffset", 38.7d, 2.0d, 80.0d, 0.1d);
	public Value<Double> YOffset = new Value<Double>("Dismount_YOffset", 0.15d, 0.0d, 2.0d, 0.01d);
	
	public double offsetDown = 0;
	
	
	public Dismount() {
		super("Dismount", Category.WORLD);
	}
	
	@EventTarget(Priority.HIGHEST)
	public void onPacket(EventPacket ep) {
//		if(entity == null) {
//			EventManager.unregister(this);
//			this.set(false);
//			
//			mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());
//			
//			
//			return;
//		}
		
//		Packetnative0(ep);
	}
	
	@native0
	public void Packetnative0(EventPacket ep) {
		
		
//		
//			if(this.mc.thePlayer.ridingEntity != null) {
//				
//				if(this.entity != null) {
//					this.entity = this.mc.thePlayer.ridingEntity;
//                
//					this.mc.thePlayer.mountEntity(null); 
//					this.mc.theWorld.removeEntity(this.entity);
//	
//				}
//			}
			
			if(ep.getPacket() instanceof C0BPacketEntityAction) {
				C0BPacketEntityAction c0b = (C0BPacketEntityAction)ep.getPacket();
				if(c0b.getAction()  == C0BPacketEntityAction.Action.STOP_SPRINTING || c0b.getAction()  == C0BPacketEntityAction.Action.START_SPRINTING || c0b.getAction()  == C0BPacketEntityAction.Action.STOP_SNEAKING || 
						c0b.getAction()  == C0BPacketEntityAction.Action.START_SNEAKING)
					ep.setCancelled(true);
			}
			if(ep.getPacket() instanceof C03PacketPlayer) {
	//			C03PacketPlayer pk = (C03PacketPlayer)ep.getPacket();
	//			if(!(pk instanceof C03PacketPlayer.C05PacketPlayerLook)) {
					ep.setCancelled(true);
	//			 }
			}

			if (ep.getPacket() instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) ep.getPacket();
				
				ChatUtil.printChat("Lagback!");
				
				ep.setCancelled(true);
				
				if (Lagback.getValueState()) {
					Client.instance.getNotificationManager().addNotification("Lagback checks!",1000, Notification.Type.WARNING);
					mc.thePlayer.onGround = false;
					mc.thePlayer.motionX *= 0;
					mc.thePlayer.motionZ *= 0;
					mc.thePlayer.jumpMovementFactor = 0;
					mc.thePlayer.setPosition(pac.x, pac.y, pac.z);
					
					mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());
					
					
					this.entity = null;
					EventManager.unregister(this);
					set(false);
				}
			}
			
//			if(ep.getPacket() instanceof S1BPacketEntityAttach) {
//				
//				S1BPacketEntityAttach attach = (S1BPacketEntityAttach) ep.getPacket();
//				
//				if(attach.getEntityId() == mc.thePlayer.getEntityId())
//					ep.setCancelled(true);
//			}
			
		
	}
	
	@native0
	public void onnative0() {
		if(pos==null)return;
		
		float pitch = (float)pos[4];
		if(PitchOffset.getValueState() && pitch + pitchOffset.getValueState().floatValue() + 1 < 90f)
			pitch += pitchOffset.getValueState().floatValue();
		
		 
		double mx = Math.cos(Math.toRadians(pos[3] -180.0f));
        double mz = Math.sin(Math.toRadians(pos[3] -180.0f));
        double x = 1.0f * 0.4 * mx + 0.0f * 0.4 * mz;
        double z = 1.0f * 0.4 * mz - 0.0f * 0.4 * mx;
		if (this.entity instanceof EntityBoat && Client.isBeta) {
			pos[0] += x;
			pos[2] += z;
		}
		
		
		double x1 = Freecam.freecamEntity != null ?  Freecam.freecamEntity.posX : pos[0];
		double y1 = Freecam.freecamEntity != null ?  Freecam.freecamEntity.posY + YOffset.getValueState().doubleValue() : pos[1] + YOffset.getValueState().doubleValue();
		double z1 = Freecam.freecamEntity != null ?  Freecam.freecamEntity.posZ : pos[2];

	
		 mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook((float)pos[3], (float) pos[4], false));

		 mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0CPacketInput(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing , false, false));
		
		
		PacketWrapper exloit = PacketWrapper.create(0x10, null, mc.getNetHandler().getNetworkManager().user);
		exloit.write(Type.DOUBLE,  x1);
		exloit.write(Type.DOUBLE,  y1);
		exloit.write(Type.DOUBLE,  z1);
		exloit.write(Type.FLOAT, (float) pos[3]);
		exloit.write(Type.FLOAT, (float) pos[4] / 2);
		
	

		
		try {
			exloit.scheduleSendToServer(Protocol1_8To1_9.class, true);
		} catch (Throwable e) {
		}
	}
	
	public static boolean isHorseGod() {
		return ModManager.getModByClass(Dismount.class).isEnabled();
	}
	
	@EventTarget
	public void onBlockCollide(EventLiquidCollide event) {
		
		int x = event.getPos().getX();
		int y = event.getPos().getY();
		int z = event.getPos().getZ();
			
		if (!(this.entity instanceof EntityBoat)) {
		if(mc.thePlayer.isSneaking()) {
			if (mc.theWorld.getBlockState(new BlockPos(x, y + 1, z)).getBlock() instanceof BlockLiquid) {
				event.setCancelled(true);
			}
		}else {
			if (mc.theWorld.getBlockState(new BlockPos(x, y , z)).getBlock() instanceof BlockLiquid && y != (int)mc.thePlayer.posY) {
				event.setCancelled(true);
			}
			}
		}
	}

	@EventTarget
	public void PreMotion(EventPreMotion e) {
//		native2(e);
	}
	
	@native0
	public void native2(EventPreMotion e) {

		this.pos = new double[] { e.getX(), e.getY(), e.getZ(),e.getYaw(),e.getPitch()};
	}
	
	@EventTarget
	public void onUpdate(EventUpdate event) {
		
//		
//	     if (this.entity != null) {
//	            try {
//	            	if(Jetpack.getValueState() && mc.gameSettings.keyBindJump.isKeyDown() && !ModManager.getModByClass(Speed.class).isEnabled())
//	            		mc.thePlayer.motionY = 0.4f;
//	            	
//	            		mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 1000, 0));
//	            	
//	        			this.entity.posX = pos[0];
//	        			this.entity.posY = pos[1];
//	        			this.entity.posZ = pos[2];
//	        			
//	        			

	        			
	        			onnative0();
//	        		
//	                
//	            }
//	            catch (Exception ex) {}
//	        }
	}
	
	@EventTarget
	public void fuck(EventRespawn rs) {
//		EventManager.unregister(this);
//		this.set(false);
		
//		this.entity = null;
	}

	@Override
	public void onEnable() {
		
		hasHurt = false;
		
		if(this.mc.thePlayer == null) {
			this.set(false);
			EventManager.unregister(this);
		}
		if (ViaLoadingBase.getInstance().getTargetVersion().getOriginalVersion() == 47) {
			ClientUtil.sendChatMessage(String.valueOf("Dismount : Pls Use ViaVersion 1.12!"), ChatType.INFO);
			set(false);
			return;
		}
		if (this.mc.thePlayer.ridingEntity == null) {
			
			mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());
			
			entity = null;
			
			EventManager.unregister(this);
			
			ClientUtil.sendChatMessage(String.valueOf("Entity Null"), ChatType.INFO);
			set(false);
			return;
		}
        if (this.mc.thePlayer.ridingEntity != null) {

        	
        		this.entity = this.mc.thePlayer.ridingEntity;
            
        		
            	this.mc.thePlayer.mountEntity(null);
            	
 
            	
            	
            	
        }
	}
	
	@Override
	public void onDisable() {
		
		this.entity = null;
    	
		mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());

		
	//	((Speed)ModManager.getModByClass(Speed.class)).mode.setCurrentMode(id_dis.getValueState().intValue()-1);
		EventManager.unregister(this);
	}
   
}
