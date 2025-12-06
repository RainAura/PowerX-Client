package net.optifine.entity.model.anim;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.optifine.expr.ExpressionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat
{
    LIMB_SWING("limb_swing"), 
    LIMB_SWING_SPEED("limb_speed"), 
    AGE("age"), 
    HEAD_YAW("head_yaw"), 
    HEAD_PITCH("head_pitch"), 
    SCALE("scale"), 
    HEALTH("health"), 
    HURT_TIME("hurt_time"), 
    IDLE_TIME("idle_time"), 
    MAX_HEALTH("max_health"), 
    MOVE_FORWARD("move_forward"), 
    MOVE_STRAFING("move_strafing"), 
    PARTIAL_TICKS("partial_ticks"), 
    POS_X("pos_x"), 
    POS_Y("pos_y"), 
    POS_Z("pos_z"), 
    REVENGE_TIME("revenge_time"), 
    SWING_PROGRESS("swing_progress");
    
    private String name;
    private RenderManager renderManager;
    private static final RenderEntityParameterFloat[] VALUES;
    
    private RenderEntityParameterFloat(final String name) {
        this.name = name;
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FLOAT;
    }
    
    @Override
    public float eval() {
        final Render render = this.renderManager.renderRender;
        if (render == null) {
            return 0.0f;
        }
        Label_0245: {
            if (render instanceof RendererLivingEntity) {
                final RendererLivingEntity rlb = (RendererLivingEntity)render;
                switch (this) {
                    case LIMB_SWING: {
                        return rlb.renderLimbSwing;
                    }
                    case LIMB_SWING_SPEED: {
                        return rlb.renderLimbSwingAmount;
                    }
                    case AGE: {
                        return rlb.renderAgeInTicks;
                    }
                    case HEAD_YAW: {
                        return rlb.renderHeadYaw;
                    }
                    case HEAD_PITCH: {
                        return rlb.renderHeadPitch;
                    }
                    case SCALE: {
                        return rlb.renderScaleFactor;
                    }
                    default: {
                        final EntityLivingBase entity = rlb.renderEntity;
                        if (entity == null) {
                            return 0.0f;
                        }
                        switch (this) {
                            case HEALTH: {
                                return entity.getHealth();
                            }
                            case HURT_TIME: {
                                return (float)entity.hurtTime;
                            }
                            case IDLE_TIME: {
                                return (float)entity.getAge();
                            }
                            case MAX_HEALTH: {
                                return entity.getMaxHealth();
                            }
                            case MOVE_FORWARD: {
                                return entity.moveForward;
                            }
                            case MOVE_STRAFING: {
                                return entity.moveStrafing;
                            }
                            case POS_X: {
                                return (float)entity.posX;
                            }
                            case POS_Y: {
                                return (float)entity.posY;
                            }
                            case POS_Z: {
                                return (float)entity.posZ;
                            }
                            case REVENGE_TIME: {
                                return (float)entity.getRevengeTimer();
                            }
                            case SWING_PROGRESS: {
                                return entity.getSwingProgress(rlb.renderPartialTicks);
                            }
                            default: {
                                break Label_0245;
                            }
                        }
                    }
                }
            }
        }
        return 0.0f;
    }
    
    public static RenderEntityParameterFloat parse(final String str) {
        if (str == null) {
            return null;
        }
        for (int i = 0; i < RenderEntityParameterFloat.VALUES.length; ++i) {
            final RenderEntityParameterFloat type = RenderEntityParameterFloat.VALUES[i];
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
