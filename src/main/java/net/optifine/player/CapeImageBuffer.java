package net.optifine.player;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload
{
    private SoftReference<AbstractClientPlayer> player;
    private final ResourceLocation resourceLocation;
    private boolean elytraOfCape;

    public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation)
    {
        this.player = new SoftReference<AbstractClientPlayer>(player);
        this.resourceLocation = resourceLocation;
    }

    public BufferedImage parseUserSkin(BufferedImage imageRaw)
    {
        BufferedImage bufferedimage = CapeUtils.parseCape(imageRaw);
        this.elytraOfCape = CapeUtils.isElytraCape(imageRaw, bufferedimage);
        return bufferedimage;
    }

    public void skinAvailable()
    {
        if (this.player.get() != null)
        {
            this.player.get().setLocationOfCape(this.resourceLocation);
            this.player.get().setElytraOfCape(this.elytraOfCape);
        }

        this.cleanup();
    }

    public void cleanup()
    {
          this.player.clear();
    }

    public boolean isElytraOfCape()
    {
        return this.elytraOfCape;
    }
}
