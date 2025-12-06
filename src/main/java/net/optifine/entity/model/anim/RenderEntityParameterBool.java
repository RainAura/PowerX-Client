package net.optifine.entity.model.anim;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.optifine.expr.ExpressionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.optifine.expr.IExpressionBool;

public enum RenderEntityParameterBool implements IExpressionBool
{
    IS_ALIVE("is_alive"), 
    IS_BURNING("is_burning"), 
    IS_CHILD("is_child"), 
    IS_GLOWING("is_glowing"), 
    IS_HURT("is_hurt"), 
    IS_IN_LAVA("is_in_lava"), 
    IS_IN_WATER("is_in_water"), 
    IS_INVISIBLE("is_invisible"), 
    IS_ON_GROUND("is_on_ground"), 
    IS_RIDDEN("is_ridden"), 
    IS_RIDING("is_riding"), 
    IS_SNEAKING("is_sneaking"), 
    IS_SPRINTING("is_sprinting"), 
    IS_WET("is_wet");
    
    private String name;
    private RenderManager renderManager;
    private static final RenderEntityParameterBool[] VALUES;
    
    private RenderEntityParameterBool(final String name) {
        this.name = name;
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.BOOL;
    }
    
    @Override
    public boolean eval() {
        final Render render = this.renderManager.renderRender;
        if (render == null) {
            return false;
        }
        if (render instanceof RendererLivingEntity) {
            final RendererLivingEntity rlb = (RendererLivingEntity)render;
            final EntityLivingBase entity = rlb.renderEntity;
            if (entity == null) {
                return false;
            }
            switch (this) {
                case IS_ALIVE: {
                    return entity.isEntityAlive();
                }
                case IS_BURNING: {
                    return entity.isBurning();
                }
                case IS_CHILD: {
                    return entity.isChild();
                }
                case IS_HURT: {
                    return entity.hurtTime > 0;
                }
                case IS_IN_LAVA: {
                    return entity.isInLava();
                }
                case IS_IN_WATER: {
                    return entity.isInWater();
                }
                case IS_INVISIBLE: {
                    return entity.isInvisible();
                }
                case IS_ON_GROUND: {
                    return entity.onGround;
                }
                case IS_RIDDEN: {
                    return entity.riddenByEntity != null;
                }
                case IS_RIDING: {
                    return entity.isRiding();
                }
                case IS_SNEAKING: {
                    return entity.isSneaking();
                }
                case IS_SPRINTING: {
                    return entity.isSprinting();
                }
                case IS_WET: {
                    return entity.isWet();
                }
            }
        }
        return false;
    }
    
    public static RenderEntityParameterBool parse(final String str) {
        if (str == null) {
            return null;
        }
        for (int i = 0; i < RenderEntityParameterBool.VALUES.length; ++i) {
            final RenderEntityParameterBool type = RenderEntityParameterBool.VALUES[i];
            if (type.getName().equals(str)) {
                return type;
            }
        }
        return null;
    }
    
    static {
        VALUES = values();
    }
}
