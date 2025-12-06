package cn.Power.mod.mods.PLAYER;

import cn.Power.events.EventPacket;
import cn.Power.mod.Category;
import cn.Power.mod.Mod;
import com.darkmagician6.eventapi.EventTarget;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class Germ extends Mod {
    public Germ() {
        super("Germ", Category.PLAYER);
    }
    private String field_3481;
    private String field_3285;
    private String field_955;
    @EventTarget
    public void handle(EventPacket event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload p = (C17PacketCustomPayload) event.getPacket();

            String channel = p.getChannelName();
            ByteBuf payload = p.getBufferData();
            int oldIndex = payload.readerIndex();

            byte[] data = new byte[payload.readableBytes()];
            payload.readBytes(data);
            payload.readerIndex(oldIndex);

            String payloadString = new String(data);

            if (channel.equals("MC|Brand") && payloadString.equals("vanilla")) {
                payload.clear();
                payload.writeBytes("fml,forge".getBytes());
            }
        }
        if (packet instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload payloadPacket = (S3FPacketCustomPayload) packet;
            //ChatUtil.print(payloadPacket.getChannelName());
            switch (payloadPacket.getChannelName()) {
                case "REGISTER": {
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes("FML|HS\u0000FML\u0000FML|MP\u0000FML\u0000FORGE\u0000germplugin-netease\u0000hyt0\u0000armourers".getBytes()))));
                    break;
                }
                case "germplugin-netease": {
                    PacketBuffer packetBuffer = new PacketBuffer(payloadPacket.getBufferData());
                    ;
                    if (packetBuffer.readInt() == getPacketID()) {
                        method_4441(packetBuffer);
                        if (!field_3481.equals("gui")) return;
                        PacketBuffer packetBuffer2 = new PacketBuffer(Unpooled.buffer());
                        method_6786(packetBuffer2);
                        mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer2));
                        Yaml yaml = new Yaml();
                        Map<String, Object> objectMap = yaml.load(field_955);
                        if (objectMap != null) {
                            PacketBuffer packetBuffer3 = new PacketBuffer(Unpooled.buffer().writeInt(13));
                            packetBuffer3.writeString(field_3285);
                            packetBuffer3.writeString(getToken(objectMap));
                            packetBuffer3.writeInt(0);
                            mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer3));
                        }
                    }
                }
            }
        }

    }
    public String getToken(Map<String, Object> objectMap){
        if (objectMap == null) return "";
        StringBuilder sb = new StringBuilder();
        Map<String, Object> newMap = (Map<String, Object>) objectMap.get(field_3285);
        if (newMap != null){
            Map<String,Object> newMap2 = null;
            String omg = "";
            for (String s : newMap.keySet()){
                newMap2 = (Map<String, Object>) newMap.get(s);
                omg = s;
            }
            sb.append(omg).append("$");
            Map<String,Object> scrollableParts = (Map<String, Object>) newMap2.get("scrollableParts");
            Map<String,Object> newMap4 = null;
            for (String s : scrollableParts.keySet()) {
                newMap4 = (Map<String, Object>) scrollableParts.get(s);
                sb.append(s).append("$");
                break;
            }
            Map<String,Object> relativeParts = (Map<String, Object>) newMap4.get("relativeParts");
            for (String s : relativeParts.keySet()) {
                sb.append(s);
                break;
            }
            return sb.toString();
        }
        return "";
    }
    public void method_6786(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(4);
        packetBuffer.writeInt(0);
        packetBuffer.writeInt(0);
        packetBuffer.writeString(this.field_3285);
        packetBuffer.writeString(this.field_3285);
        packetBuffer.writeString(this.field_3285);
    }
    public void method_4441(PacketBuffer packetBuffer) {
        this.field_3481 = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
        this.field_3285 = packetBuffer.readStringFromBuffer(Short.MAX_VALUE);
        this.field_955 = packetBuffer.readStringFromBuffer(9999999);
    }
    private int getPacketID(){
        return 73;
    }
}

