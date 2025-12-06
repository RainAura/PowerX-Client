package net.optifine.shaders;

import net.minecraft.util.BlockPos;
import net.minecraft.item.ItemBlock;
import net.optifine.Lang;
import java.util.HashMap;
import net.minecraft.tileentity.TileEntity;
import net.optifine.util.EntityUtils;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.optifine.shaders.config.RenderScale;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import net.minecraft.util.Vec3;
import net.minecraft.entity.Entity;
import net.optifine.CustomColors;
import net.minecraft.potion.Potion;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.EntityLivingBase;
import java.util.regex.Matcher;
import java.util.IdentityHashMap;
import net.optifine.render.GlBlendState;
import net.optifine.render.GlAlphaState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import com.google.common.base.Charsets;
import org.lwjgl.BufferUtils;
import java.io.InputStreamReader;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.MacroState;
import java.io.BufferedReader;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import net.optifine.shaders.uniform.Smoother;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GLContext;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.resources.I18n;
import net.optifine.util.TimedEvent;
import net.optifine.GlErrors;
import org.lwjgl.opengl.EXTFramebufferObject;
import java.util.Comparator;
import java.util.Collections;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderOptionProfile;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.renderer.GLAllocation;
import org.apache.commons.io.IOUtils;
import java.util.Deque;
import net.optifine.texture.PixelType;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.TextureType;
import java.util.Collection;
import java.util.ArrayDeque;
import java.util.Arrays;
import net.optifine.config.ConnectedParser;
import net.minecraft.util.ResourceLocation;
import java.util.Iterator;
import net.optifine.util.StrUtils;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.CustomBlockLayers;
import net.optifine.reflect.Reflector;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import java.io.Writer;
import java.io.FileWriter;
import net.minecraft.src.Config;
import java.io.Reader;
import java.io.FileReader;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.util.PropertiesOrdered;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import net.optifine.shaders.uniform.CustomUniforms;
import java.util.List;
import net.minecraft.world.World;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.expr.IExpressionBool;
import net.optifine.shaders.config.ScreenShaderOptions;
import java.util.Map;
import net.optifine.shaders.config.ShaderProfile;
import java.util.Set;
import net.optifine.shaders.config.ShaderOption;
import java.io.File;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.minecraft.client.renderer.texture.ITextureObject;
import java.util.Properties;
import java.nio.IntBuffer;
import org.lwjgl.util.vector.Vector4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniforms;
import org.lwjgl.opengl.ContextCapabilities;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.Minecraft;

public class Shaders
{
    static Minecraft mc;
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce;
    public static boolean isShaderPackInitialized;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap;
    public static int countResetDisplayLists;
    private static int renderDisplayWidth;
    private static int renderDisplayHeight;
    public static int renderWidth;
    public static int renderHeight;
    public static boolean isRenderingWorld;
    public static boolean isRenderingSky;
    public static boolean isCompositeRendered;
    public static boolean isRenderingDfb;
    public static boolean isShadowPass;
    public static boolean isEntitiesGlowing;
    public static boolean isSleeping;
    private static boolean isRenderingFirstPersonHand;
    private static boolean isHandRenderedMain;
    private static boolean isHandRenderedOff;
    private static boolean skipRenderHandMain;
    private static boolean skipRenderHandOff;
    public static boolean renderItemKeepDepthMask;
    public static boolean itemToRenderMainTranslucent;
    public static boolean itemToRenderOffTranslucent;
    static float[] sunPosition;
    static float[] moonPosition;
    static float[] shadowLightPosition;
    static float[] upPosition;
    static float[] shadowLightPositionVector;
    static float[] upPosModelView;
    static float[] sunPosModelView;
    static float[] moonPosModelView;
    private static float[] tempMat;
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime;
    static long lastWorldTime;
    static long diffWorldTime;
    static float celestialAngle;
    static float sunAngle;
    static float shadowAngle;
    static int moonPhase;
    static long systemTime;
    static long lastSystemTime;
    static long diffSystemTime;
    static int frameCounter;
    static float frameTime;
    static float frameTimeCounter;
    static int systemTimeInt32;
    static float rainStrength;
    static float wetness;
    public static float wetnessHalfLife;
    public static float drynessHalfLife;
    public static float eyeBrightnessHalflife;
    static boolean usewetness;
    static int isEyeInWater;
    static int eyeBrightness;
    static float eyeBrightnessFadeX;
    static float eyeBrightnessFadeY;
    static float eyePosY;
    static float centerDepth;
    static float centerDepthSmooth;
    static float centerDepthSmoothHalflife;
    static boolean centerDepthSmoothEnabled;
    static int superSamplingLevel;
    static float nightVision;
    static float blindness;
    static boolean lightmapEnabled;
    static boolean fogEnabled;
    public static int entityAttrib;
    public static int midTexCoordAttrib;
    public static int tangentAttrib;
    public static boolean useEntityAttrib;
    public static boolean useMidTexCoordAttrib;
    public static boolean useTangentAttrib;
    public static boolean progUseEntityAttrib;
    public static boolean progUseMidTexCoordAttrib;
    public static boolean progUseTangentAttrib;
    private static boolean progArbGeometryShader4;
    private static int progMaxVerticesOut;
    private static boolean hasGeometryShaders;
    public static int atlasSizeX;
    public static int atlasSizeY;
    private static ShaderUniforms shaderUniforms;
    public static ShaderUniform4f uniform_entityColor;
    public static ShaderUniform1i uniform_entityId;
    public static ShaderUniform1i uniform_blockEntityId;
    public static ShaderUniform1i uniform_texture;
    public static ShaderUniform1i uniform_lightmap;
    public static ShaderUniform1i uniform_normals;
    public static ShaderUniform1i uniform_specular;
    public static ShaderUniform1i uniform_shadow;
    public static ShaderUniform1i uniform_watershadow;
    public static ShaderUniform1i uniform_shadowtex0;
    public static ShaderUniform1i uniform_shadowtex1;
    public static ShaderUniform1i uniform_depthtex0;
    public static ShaderUniform1i uniform_depthtex1;
    public static ShaderUniform1i uniform_shadowcolor;
    public static ShaderUniform1i uniform_shadowcolor0;
    public static ShaderUniform1i uniform_shadowcolor1;
    public static ShaderUniform1i uniform_noisetex;
    public static ShaderUniform1i uniform_gcolor;
    public static ShaderUniform1i uniform_gdepth;
    public static ShaderUniform1i uniform_gnormal;
    public static ShaderUniform1i uniform_composite;
    public static ShaderUniform1i uniform_gaux1;
    public static ShaderUniform1i uniform_gaux2;
    public static ShaderUniform1i uniform_gaux3;
    public static ShaderUniform1i uniform_gaux4;
    public static ShaderUniform1i uniform_colortex0;
    public static ShaderUniform1i uniform_colortex1;
    public static ShaderUniform1i uniform_colortex2;
    public static ShaderUniform1i uniform_colortex3;
    public static ShaderUniform1i uniform_colortex4;
    public static ShaderUniform1i uniform_colortex5;
    public static ShaderUniform1i uniform_colortex6;
    public static ShaderUniform1i uniform_colortex7;
    public static ShaderUniform1i uniform_gdepthtex;
    public static ShaderUniform1i uniform_depthtex2;
    public static ShaderUniform1i uniform_tex;
    public static ShaderUniform1i uniform_heldItemId;
    public static ShaderUniform1i uniform_heldBlockLightValue;
    public static ShaderUniform1i uniform_heldItemId2;
    public static ShaderUniform1i uniform_heldBlockLightValue2;
    public static ShaderUniform1i uniform_fogMode;
    public static ShaderUniform1f uniform_fogDensity;
    public static ShaderUniform3f uniform_fogColor;
    public static ShaderUniform3f uniform_skyColor;
    public static ShaderUniform1i uniform_worldTime;
    public static ShaderUniform1i uniform_worldDay;
    public static ShaderUniform1i uniform_moonPhase;
    public static ShaderUniform1i uniform_frameCounter;
    public static ShaderUniform1f uniform_frameTime;
    public static ShaderUniform1f uniform_frameTimeCounter;
    public static ShaderUniform1f uniform_sunAngle;
    public static ShaderUniform1f uniform_shadowAngle;
    public static ShaderUniform1f uniform_rainStrength;
    public static ShaderUniform1f uniform_aspectRatio;
    public static ShaderUniform1f uniform_viewWidth;
    public static ShaderUniform1f uniform_viewHeight;
    public static ShaderUniform1f uniform_near;
    public static ShaderUniform1f uniform_far;
    public static ShaderUniform3f uniform_sunPosition;
    public static ShaderUniform3f uniform_moonPosition;
    public static ShaderUniform3f uniform_shadowLightPosition;
    public static ShaderUniform3f uniform_upPosition;
    public static ShaderUniform3f uniform_previousCameraPosition;
    public static ShaderUniform3f uniform_cameraPosition;
    public static ShaderUniformM4 uniform_gbufferModelView;
    public static ShaderUniformM4 uniform_gbufferModelViewInverse;
    public static ShaderUniformM4 uniform_gbufferPreviousProjection;
    public static ShaderUniformM4 uniform_gbufferProjection;
    public static ShaderUniformM4 uniform_gbufferProjectionInverse;
    public static ShaderUniformM4 uniform_gbufferPreviousModelView;
    public static ShaderUniformM4 uniform_shadowProjection;
    public static ShaderUniformM4 uniform_shadowProjectionInverse;
    public static ShaderUniformM4 uniform_shadowModelView;
    public static ShaderUniformM4 uniform_shadowModelViewInverse;
    public static ShaderUniform1f uniform_wetness;
    public static ShaderUniform1f uniform_eyeAltitude;
    public static ShaderUniform2i uniform_eyeBrightness;
    public static ShaderUniform2i uniform_eyeBrightnessSmooth;
    public static ShaderUniform2i uniform_terrainTextureSize;
    public static ShaderUniform1i uniform_terrainIconSize;
    public static ShaderUniform1i uniform_isEyeInWater;
    public static ShaderUniform1f uniform_nightVision;
    public static ShaderUniform1f uniform_blindness;
    public static ShaderUniform1f uniform_screenBrightness;
    public static ShaderUniform1i uniform_hideGUI;
    public static ShaderUniform1f uniform_centerDepthSmooth;
    public static ShaderUniform2i uniform_atlasSize;
    public static ShaderUniform4i uniform_blendFunc;
    public static ShaderUniform1i uniform_instanceId;
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int cameraOffsetX;
    static int cameraOffsetZ;
    static int shadowPassInterval;
    public static boolean needResizeShadow;
    static int shadowMapWidth;
    static int shadowMapHeight;
    static int spShadowMapWidth;
    static int spShadowMapHeight;
    static float shadowMapFOV;
    static float shadowMapHalfPlane;
    static boolean shadowMapIsOrtho;
    static float shadowDistanceRenderMul;
    static int shadowPassCounter;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow;
    static boolean waterShadowEnabled;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers;
    static int usedDepthBuffers;
    static int usedShadowColorBuffers;
    static int usedShadowDepthBuffers;
    static int usedColorAttachs;
    static int usedDrawBuffers;
    static int dfb;
    static int sfb;
    private static int[] gbuffersFormat;
    public static boolean[] gbuffersClear;
    public static Vector4f[] gbuffersClearColor;
    private static Programs programs;
    public static final Program ProgramNone;
    public static final Program ProgramShadow;
    public static final Program ProgramShadowSolid;
    public static final Program ProgramShadowCutout;
    public static final Program ProgramBasic;
    public static final Program ProgramTextured;
    public static final Program ProgramTexturedLit;
    public static final Program ProgramSkyBasic;
    public static final Program ProgramSkyTextured;
    public static final Program ProgramClouds;
    public static final Program ProgramTerrain;
    public static final Program ProgramTerrainSolid;
    public static final Program ProgramTerrainCutoutMip;
    public static final Program ProgramTerrainCutout;
    public static final Program ProgramDamagedBlock;
    public static final Program ProgramBlock;
    public static final Program ProgramBeaconBeam;
    public static final Program ProgramItem;
    public static final Program ProgramEntities;
    public static final Program ProgramEntitiesGlowing;
    public static final Program ProgramArmorGlint;
    public static final Program ProgramSpiderEyes;
    public static final Program ProgramHand;
    public static final Program ProgramWeather;
    public static final Program ProgramDeferredPre;
    public static final Program[] ProgramsDeferred;
    public static final Program ProgramDeferred;
    public static final Program ProgramWater;
    public static final Program ProgramHandWater;
    public static final Program ProgramCompositePre;
    public static final Program[] ProgramsComposite;
    public static final Program ProgramComposite;
    public static final Program ProgramFinal;
    public static final int ProgramCount;
    public static final Program[] ProgramsAll;
    public static Program activeProgram;
    public static int activeProgramID;
    private static ProgramStack programStack;
    private static boolean hasDeferredPrograms;
    static IntBuffer activeDrawBuffers;
    private static int activeCompositeMipmapSetting;
    public static Properties loadedShaders;
    public static Properties shadersConfig;
    public static ITextureObject defaultTexture;
    public static boolean[] shadowHardwareFilteringEnabled;
    public static boolean[] shadowMipmapEnabled;
    public static boolean[] shadowFilterNearest;
    public static boolean[] shadowColorMipmapEnabled;
    public static boolean[] shadowColorFilterNearest;
    public static boolean configTweakBlockDamage;
    public static boolean configCloudShadow;
    public static float configHandDepthMul;
    public static float configRenderResMul;
    public static float configShadowResMul;
    public static int configTexMinFilB;
    public static int configTexMinFilN;
    public static int configTexMinFilS;
    public static int configTexMagFilB;
    public static int configTexMagFilN;
    public static int configTexMagFilS;
    public static boolean configShadowClipFrustrum;
    public static boolean configNormalMap;
    public static boolean configSpecularMap;
    public static PropertyDefaultTrueFalse configOldLighting;
    public static PropertyDefaultTrueFalse configOldHandLight;
    public static int configAntialiasingLevel;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc;
    public static final String[] texMagFilDesc;
    public static final int[] texMinFilValue;
    public static final int[] texMagFilValue;
    private static IShaderPack shaderPack;
    public static boolean shaderPackLoaded;
    public static String currentShaderName;
    public static final String SHADER_PACK_NAME_NONE = "OFF";
    public static final String SHADER_PACK_NAME_DEFAULT = "(internal)";
    public static final String SHADER_PACKS_DIR_NAME = "shaderpacks";
    public static final String OPTIONS_FILE_NAME = "optionsshaders.txt";
    public static final File shaderPacksDir;
    static File configFile;
    private static ShaderOption[] shaderPackOptions;
    private static Set<String> shaderPackOptionSliders;
    static ShaderProfile[] shaderPackProfiles;
    static Map<String, ScreenShaderOptions> shaderPackGuiScreens;
    static Map<String, IExpressionBool> shaderPackProgramConditions;
    public static final String PATH_SHADERS_PROPERTIES = "/shaders/shaders.properties";
    public static PropertyDefaultFastFancyOff shaderPackClouds;
    public static PropertyDefaultTrueFalse shaderPackOldLighting;
    public static PropertyDefaultTrueFalse shaderPackOldHandLight;
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent;
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay;
    public static PropertyDefaultTrueFalse shaderPackSun;
    public static PropertyDefaultTrueFalse shaderPackMoon;
    public static PropertyDefaultTrueFalse shaderPackVignette;
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped;
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent;
    public static PropertyDefaultTrueFalse shaderPackRainDepth;
    public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth;
    public static PropertyDefaultTrueFalse shaderPackSeparateAo;
    public static PropertyDefaultTrueFalse shaderPackFrustumCulling;
    private static Map<String, String> shaderPackResources;
    private static World currentWorld;
    private static List<Integer> shaderPackDimensions;
    private static ICustomTexture[] customTexturesGbuffers;
    private static ICustomTexture[] customTexturesComposite;
    private static ICustomTexture[] customTexturesDeferred;
    private static String noiseTexturePath;
    private static CustomUniforms customUniforms;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final int STAGE_DEFERRED = 2;
    private static final String[] STAGE_NAMES;
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    public static final boolean saveFinalShaders;
    public static float blockLightLevel05;
    public static float blockLightLevel06;
    public static float blockLightLevel08;
    public static float aoLevel;
    public static float sunPathRotation;
    public static float shadowAngleInterval;
    public static int fogMode;
    public static float fogDensity;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize;
    public static int terrainIconSize;
    public static int[] terrainTextureSize;
    private static ICustomTexture noiseTexture;
    private static boolean noiseTextureEnabled;
    private static int noiseTextureResolution;
    static final int[] colorTextureImageUnit;
    private static final int bigBufferSize;
    private static final ByteBuffer bigBuffer;
    static final float[] faProjection;
    static final float[] faProjectionInverse;
    static final float[] faModelView;
    static final float[] faModelViewInverse;
    static final float[] faShadowProjection;
    static final float[] faShadowProjectionInverse;
    static final float[] faShadowModelView;
    static final float[] faShadowModelViewInverse;
    static final FloatBuffer projection;
    static final FloatBuffer projectionInverse;
    static final FloatBuffer modelView;
    static final FloatBuffer modelViewInverse;
    static final FloatBuffer shadowProjection;
    static final FloatBuffer shadowProjectionInverse;
    static final FloatBuffer shadowModelView;
    static final FloatBuffer shadowModelViewInverse;
    static final FloatBuffer previousProjection;
    static final FloatBuffer previousModelView;
    static final FloatBuffer tempMatrixDirectBuffer;
    static final FloatBuffer tempDirectFloatBuffer;
    static final IntBuffer dfbColorTextures;
    static final IntBuffer dfbDepthTextures;
    static final IntBuffer sfbColorTextures;
    static final IntBuffer sfbDepthTextures;
    static final IntBuffer dfbDrawBuffers;
    static final IntBuffer sfbDrawBuffers;
    static final IntBuffer drawBuffersNone;
    static final IntBuffer drawBuffersColorAtt0;
    static final FlipTextures dfbColorTexturesFlip;
    static Map<Block, Integer> mapBlockToEntityData;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static int[] entityData;
    public static int entityDataIndex;
    
    private Shaders() {
    }
    
    private static ByteBuffer nextByteBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size);
        return buffer.slice();
    }
    
    public static IntBuffer nextIntBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asIntBuffer();
    }
    
    private static FloatBuffer nextFloatBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asFloatBuffer();
    }
    
    private static IntBuffer[] nextIntBufferArray(final int count, final int size) {
        final IntBuffer[] aib = new IntBuffer[count];
        for (int i = 0; i < count; ++i) {
            aib[i] = nextIntBuffer(size);
        }
        return aib;
    }
    
    public static void loadConfig() {
        SMCLog.info("Load shaders configuration.");
        try {
            if (!Shaders.shaderPacksDir.exists()) {
                Shaders.shaderPacksDir.mkdir();
            }
        }
        catch (Exception e) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + Shaders.shaderPacksDir);
        }
        (Shaders.shadersConfig = new PropertiesOrdered()).setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (Shaders.configFile.exists()) {
            try {
                final FileReader reader = new FileReader(Shaders.configFile);
                Shaders.shadersConfig.load(reader);
                reader.close();
            }
            catch (Exception ex) {}
        }
        if (!Shaders.configFile.exists()) {
            try {
                storeConfig();
            }
            catch (Exception ex2) {}
        }
        final EnumShaderOption[] ops = EnumShaderOption.values();
        for (int i = 0; i < ops.length; ++i) {
            final EnumShaderOption op = ops[i];
            final String key = op.getPropertyKey();
            final String def = op.getValueDefault();
            final String val = Shaders.shadersConfig.getProperty(key, def);
            setEnumShaderOption(op, val);
        }
        loadShaderPack();
    }
    
    private static void setEnumShaderOption(final EnumShaderOption eso, String str) {
        if (str == null) {
            str = eso.getValueDefault();
        }
        switch (eso) {
            case ANTIALIASING: {
                Shaders.configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            }
            case NORMAL_MAP: {
                Shaders.configNormalMap = Config.parseBoolean(str, true);
                break;
            }
            case SPECULAR_MAP: {
                Shaders.configSpecularMap = Config.parseBoolean(str, true);
                break;
            }
            case RENDER_RES_MUL: {
                Shaders.configRenderResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case SHADOW_RES_MUL: {
                Shaders.configShadowResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case HAND_DEPTH_MUL: {
                Shaders.configHandDepthMul = Config.parseFloat(str, 0.125f);
                break;
            }
            case CLOUD_SHADOW: {
                Shaders.configCloudShadow = Config.parseBoolean(str, true);
                break;
            }
            case OLD_HAND_LIGHT: {
                Shaders.configOldHandLight.setPropertyValue(str);
                break;
            }
            case OLD_LIGHTING: {
                Shaders.configOldLighting.setPropertyValue(str);
                break;
            }
            case SHADER_PACK: {
                Shaders.currentShaderName = str;
                break;
            }
            case TWEAK_BLOCK_DAMAGE: {
                Shaders.configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            }
            case SHADOW_CLIP_FRUSTRUM: {
                Shaders.configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            }
            case TEX_MIN_FIL_B: {
                Shaders.configTexMinFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_N: {
                Shaders.configTexMinFilN = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_S: {
                Shaders.configTexMinFilS = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_B: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_N: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_S: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + eso);
            }
        }
    }
    
    public static void storeConfig() {
        SMCLog.info("Save shaders configuration.");
        if (Shaders.shadersConfig == null) {
            Shaders.shadersConfig = new PropertiesOrdered();
        }
        final EnumShaderOption[] ops = EnumShaderOption.values();
        for (int i = 0; i < ops.length; ++i) {
            final EnumShaderOption op = ops[i];
            final String key = op.getPropertyKey();
            final String val = getEnumShaderOption(op);
            Shaders.shadersConfig.setProperty(key, val);
        }
        try {
            final FileWriter writer = new FileWriter(Shaders.configFile);
            Shaders.shadersConfig.store(writer, null);
            writer.close();
        }
        catch (Exception ex) {
            SMCLog.severe("Error saving configuration: " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
    
    public static String getEnumShaderOption(final EnumShaderOption eso) {
        switch (eso) {
            case ANTIALIASING: {
                return Integer.toString(Shaders.configAntialiasingLevel);
            }
            case NORMAL_MAP: {
                return Boolean.toString(Shaders.configNormalMap);
            }
            case SPECULAR_MAP: {
                return Boolean.toString(Shaders.configSpecularMap);
            }
            case RENDER_RES_MUL: {
                return Float.toString(Shaders.configRenderResMul);
            }
            case SHADOW_RES_MUL: {
                return Float.toString(Shaders.configShadowResMul);
            }
            case HAND_DEPTH_MUL: {
                return Float.toString(Shaders.configHandDepthMul);
            }
            case CLOUD_SHADOW: {
                return Boolean.toString(Shaders.configCloudShadow);
            }
            case OLD_HAND_LIGHT: {
                return Shaders.configOldHandLight.getPropertyValue();
            }
            case OLD_LIGHTING: {
                return Shaders.configOldLighting.getPropertyValue();
            }
            case SHADER_PACK: {
                return Shaders.currentShaderName;
            }
            case TWEAK_BLOCK_DAMAGE: {
                return Boolean.toString(Shaders.configTweakBlockDamage);
            }
            case SHADOW_CLIP_FRUSTRUM: {
                return Boolean.toString(Shaders.configShadowClipFrustrum);
            }
            case TEX_MIN_FIL_B: {
                return Integer.toString(Shaders.configTexMinFilB);
            }
            case TEX_MIN_FIL_N: {
                return Integer.toString(Shaders.configTexMinFilN);
            }
            case TEX_MIN_FIL_S: {
                return Integer.toString(Shaders.configTexMinFilS);
            }
            case TEX_MAG_FIL_B: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            case TEX_MAG_FIL_N: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            case TEX_MAG_FIL_S: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + eso);
            }
        }
    }
    
    public static void setShaderPack(final String par1name) {
        Shaders.currentShaderName = par1name;
        Shaders.shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        loadShaderPack();
    }
    
    public static void loadShaderPack() {
        final boolean shaderPackLoadedPrev = Shaders.shaderPackLoaded;
        final boolean oldLightingPrev = isOldLighting();
        if (Shaders.mc.renderGlobal != null) {
            Shaders.mc.renderGlobal.pauseChunkUpdates();
        }
        Shaders.shaderPackLoaded = false;
        if (Shaders.shaderPack != null) {
            Shaders.shaderPack.close();
            Shaders.shaderPack = null;
            Shaders.shaderPackResources.clear();
            Shaders.shaderPackDimensions.clear();
            Shaders.shaderPackOptions = null;
            Shaders.shaderPackOptionSliders = null;
            Shaders.shaderPackProfiles = null;
            Shaders.shaderPackGuiScreens = null;
            Shaders.shaderPackProgramConditions.clear();
            Shaders.shaderPackClouds.resetValue();
            Shaders.shaderPackOldHandLight.resetValue();
            Shaders.shaderPackDynamicHandLight.resetValue();
            Shaders.shaderPackOldLighting.resetValue();
            resetCustomTextures();
            Shaders.noiseTexturePath = null;
        }
        boolean shadersBlocked = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            shadersBlocked = true;
        }
        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            shadersBlocked = true;
        }
        if (Config.isFastRender()) {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            shadersBlocked = true;
        }
        final String packName = Shaders.shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "(internal)");
        if (!shadersBlocked) {
            Shaders.shaderPack = getShaderPack(packName);
            Shaders.shaderPackLoaded = (Shaders.shaderPack != null);
        }
        if (Shaders.shaderPackLoaded) {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        }
        else {
            SMCLog.info("No shaderpack loaded.");
            Shaders.shaderPack = new ShaderPackNone();
        }
        if (Shaders.saveFinalShaders) {
            clearDirectory(new File(Shaders.shaderPacksDir, "debug"));
        }
        loadShaderPackResources();
        loadShaderPackDimensions();
        Shaders.shaderPackOptions = loadShaderPackOptions();
        loadShaderPackProperties();
        final boolean formatChanged = Shaders.shaderPackLoaded != shaderPackLoadedPrev;
        final boolean oldLightingChanged = isOldLighting() != oldLightingPrev;
        if (formatChanged || oldLightingChanged) {
            DefaultVertexFormats.updateVertexFormats();

            updateBlockLightLevel();
        }
        if (Shaders.mc.getResourcePackRepository() != null) {
            CustomBlockLayers.update();
        }
        if (Shaders.mc.renderGlobal != null) {
            Shaders.mc.renderGlobal.resumeChunkUpdates();
        }
        if ((formatChanged || oldLightingChanged) && Shaders.mc.getResourceManager() != null) {
            Shaders.mc.scheduleResourcesRefresh();
        }
    }
    
    public static IShaderPack getShaderPack(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if (name.isEmpty() || name.equals("OFF")) {
            return null;
        }
        if (name.equals("(internal)")) {
            return new ShaderPackDefault();
        }
        try {
            final File packFile = new File(Shaders.shaderPacksDir, name);
            if (packFile.isDirectory()) {
                return new ShaderPackFolder(name, packFile);
            }
            if (packFile.isFile() && name.toLowerCase().endsWith(".zip")) {
                return new ShaderPackZip(name, packFile);
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static IShaderPack getShaderPack() {
        return Shaders.shaderPack;
    }
    
    private static void loadShaderPackDimensions() {
        Shaders.shaderPackDimensions.clear();
        for (int i = -128; i <= 128; ++i) {
            final String worldDir = "/shaders/world" + i;
            if (Shaders.shaderPack.hasDirectory(worldDir)) {
                Shaders.shaderPackDimensions.add(i);
            }
        }
        if (Shaders.shaderPackDimensions.size() > 0) {
            final Integer[] ids = Shaders.shaderPackDimensions.toArray(new Integer[Shaders.shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString(ids));
        }
    }
    
    private static void loadShaderPackProperties() {
        Shaders.shaderPackClouds.resetValue();
        Shaders.shaderPackOldHandLight.resetValue();
        Shaders.shaderPackDynamicHandLight.resetValue();
        Shaders.shaderPackOldLighting.resetValue();
        Shaders.shaderPackShadowTranslucent.resetValue();
        Shaders.shaderPackUnderwaterOverlay.resetValue();
        Shaders.shaderPackSun.resetValue();
        Shaders.shaderPackMoon.resetValue();
        Shaders.shaderPackVignette.resetValue();
        Shaders.shaderPackBackFaceSolid.resetValue();
        Shaders.shaderPackBackFaceCutout.resetValue();
        Shaders.shaderPackBackFaceCutoutMipped.resetValue();
        Shaders.shaderPackBackFaceTranslucent.resetValue();
        Shaders.shaderPackRainDepth.resetValue();
        Shaders.shaderPackBeaconBeamDepth.resetValue();
        Shaders.shaderPackSeparateAo.resetValue();
        Shaders.shaderPackFrustumCulling.resetValue();
        BlockAliases.reset();
        ItemAliases.reset();
        EntityAliases.reset();
        Shaders.customUniforms = null;
        for (int i = 0; i < Shaders.ProgramsAll.length; ++i) {
            final Program p = Shaders.ProgramsAll[i];
            p.resetProperties();
        }
        if (Shaders.shaderPack == null) {
            return;
        }
        BlockAliases.update(Shaders.shaderPack);
        ItemAliases.update(Shaders.shaderPack);
        EntityAliases.update(Shaders.shaderPack);
        final String path = "/shaders/shaders.properties";
        try {
            InputStream in = Shaders.shaderPack.getResourceAsStream(path);
            if (in == null) {
                return;
            }
            in = MacroProcessor.process(in, path);
            final Properties props = new PropertiesOrdered();
            props.load(in);
            in.close();
            Shaders.shaderPackClouds.loadFrom(props);
            Shaders.shaderPackOldHandLight.loadFrom(props);
            Shaders.shaderPackDynamicHandLight.loadFrom(props);
            Shaders.shaderPackOldLighting.loadFrom(props);
            Shaders.shaderPackShadowTranslucent.loadFrom(props);
            Shaders.shaderPackUnderwaterOverlay.loadFrom(props);
            Shaders.shaderPackSun.loadFrom(props);
            Shaders.shaderPackVignette.loadFrom(props);
            Shaders.shaderPackMoon.loadFrom(props);
            Shaders.shaderPackBackFaceSolid.loadFrom(props);
            Shaders.shaderPackBackFaceCutout.loadFrom(props);
            Shaders.shaderPackBackFaceCutoutMipped.loadFrom(props);
            Shaders.shaderPackBackFaceTranslucent.loadFrom(props);
            Shaders.shaderPackRainDepth.loadFrom(props);
            Shaders.shaderPackBeaconBeamDepth.loadFrom(props);
            Shaders.shaderPackSeparateAo.loadFrom(props);
            Shaders.shaderPackFrustumCulling.loadFrom(props);
            Shaders.shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(props, Shaders.shaderPackOptions);
            Shaders.shaderPackProfiles = ShaderPackParser.parseProfiles(props, Shaders.shaderPackOptions);
            Shaders.shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(props, Shaders.shaderPackProfiles, Shaders.shaderPackOptions);
            Shaders.shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(props, Shaders.shaderPackOptions);
            Shaders.customTexturesGbuffers = loadCustomTextures(props, 0);
            Shaders.customTexturesComposite = loadCustomTextures(props, 1);
            Shaders.customTexturesDeferred = loadCustomTextures(props, 2);
            Shaders.noiseTexturePath = props.getProperty("texture.noise");
            if (Shaders.noiseTexturePath != null) {
                Shaders.noiseTextureEnabled = true;
            }
            Shaders.customUniforms = ShaderPackParser.parseCustomUniforms(props);
            ShaderPackParser.parseAlphaStates(props);
            ShaderPackParser.parseBlendStates(props);
            ShaderPackParser.parseRenderScales(props);
            ShaderPackParser.parseBuffersFlip(props);
        }
        catch (IOException e) {
            Config.warn("[Shaders] Error reading: " + path);
        }
    }
    
    private static ICustomTexture[] loadCustomTextures(final Properties props, final int stage) {
        final String PREFIX_TEXTURE = "texture." + Shaders.STAGE_NAMES[stage] + ".";
        final Set keys = props.keySet();
        final List<ICustomTexture> list = new ArrayList<ICustomTexture>();
        for (final Object o : keys) {
        	
        	String key = (String) o ;
        	
            if (!key.startsWith(PREFIX_TEXTURE)) {
                continue;
            }
            String name = StrUtils.removePrefix(key, PREFIX_TEXTURE);
            name = StrUtils.removeSuffix(name, new String[] { ".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9" });
            final String path = props.getProperty(key).trim();
            final int index = getTextureIndex(stage, name);
            if (index < 0) {
                SMCLog.warning("Invalid texture name: " + key);
            }
            else {
                final ICustomTexture ct = loadCustomTexture(index, path);
                if (ct == null) {
                    continue;
                }
                SMCLog.info("Custom texture: " + key + " = " + path);
                list.add(ct);
            }
        }
        if (list.size() <= 0) {
            return null;
        }
        final ICustomTexture[] cts = list.toArray(new ICustomTexture[list.size()]);
        return cts;
    }
    
    private static ICustomTexture loadCustomTexture(final int textureUnit, String path) {
        if (path == null) {
            return null;
        }
        path = path.trim();
        if (path.indexOf(58) >= 0) {
            return loadCustomTextureLocation(textureUnit, path);
        }
        if (path.indexOf(32) >= 0) {
            return loadCustomTextureRaw(textureUnit, path);
        }
        return loadCustomTextureShaders(textureUnit, path);
    }
    
    private static ICustomTexture loadCustomTextureLocation(final int textureUnit, final String path) {
        String pathFull = path.trim();
        int variant = 0;
        if (pathFull.startsWith("minecraft:textures/")) {
            pathFull = StrUtils.addSuffixCheck(pathFull, ".png");
            if (pathFull.endsWith("_n.png")) {
                pathFull = StrUtils.replaceSuffix(pathFull, "_n.png", ".png");
                variant = 1;
            }
            else if (pathFull.endsWith("_s.png")) {
                pathFull = StrUtils.replaceSuffix(pathFull, "_s.png", ".png");
                variant = 2;
            }
        }
        final ResourceLocation loc = new ResourceLocation(pathFull);
        final CustomTextureLocation ctv = new CustomTextureLocation(textureUnit, loc, variant);
        return ctv;
    }
    
    private static ICustomTexture loadCustomTextureRaw(final int textureUnit, final String line) {
        final ConnectedParser cp = new ConnectedParser("Shaders");
        final String[] parts = Config.tokenize(line, " ");
        final Deque<String> names = new ArrayDeque<String>(Arrays.asList(parts));
        final String path = names.poll();
        final TextureType type = (TextureType)cp.parseEnum(names.poll(), TextureType.values(), "texture type");
        if (type == null) {
            SMCLog.warning("Invalid raw texture type: " + line);
            return null;
        }
        final InternalFormat internalFormat = (InternalFormat)cp.parseEnum(names.poll(), InternalFormat.values(), "internal format");
        if (internalFormat == null) {
            SMCLog.warning("Invalid raw texture internal format: " + line);
            return null;
        }
        int width = 0;
        int height = 0;
        int depth = 0;
        switch (type) {
            case TEXTURE_1D: {
                width = cp.parseInt(names.poll(), -1);
                break;
            }
            case TEXTURE_2D: {
                width = cp.parseInt(names.poll(), -1);
                height = cp.parseInt(names.poll(), -1);
                break;
            }
            case TEXTURE_3D: {
                width = cp.parseInt(names.poll(), -1);
                height = cp.parseInt(names.poll(), -1);
                depth = cp.parseInt(names.poll(), -1);
                break;
            }
            case TEXTURE_RECTANGLE: {
                width = cp.parseInt(names.poll(), -1);
                height = cp.parseInt(names.poll(), -1);
                break;
            }
            default: {
                SMCLog.warning("Invalid raw texture type: " + type);
                return null;
            }
        }
        if (width < 0 || height < 0 || depth < 0) {
            SMCLog.warning("Invalid raw texture size: " + line);
            return null;
        }
        final PixelFormat pixelFormat = (PixelFormat)cp.parseEnum(names.poll(), PixelFormat.values(), "pixel format");
        if (pixelFormat == null) {
            SMCLog.warning("Invalid raw texture pixel format: " + line);
            return null;
        }
        final PixelType pixelType = (PixelType)cp.parseEnum(names.poll(), PixelType.values(), "pixel type");
        if (pixelType == null) {
            SMCLog.warning("Invalid raw texture pixel type: " + line);
            return null;
        }
        if (!names.isEmpty()) {
            SMCLog.warning("Invalid raw texture, too many nameeters: " + line);
            return null;
        }
        return loadCustomTextureRaw(textureUnit, line, path, type, internalFormat, width, height, depth, pixelFormat, pixelType);
    }
    
    private static ICustomTexture loadCustomTextureRaw(final int textureUnit, final String line, final String path, final TextureType type, final InternalFormat internalFormat, final int width, final int height, final int depth, final PixelFormat pixelFormat, final PixelType pixelType) {
        try {
            final String pathFull = "shaders/" + StrUtils.removePrefix(path, "/");
            final InputStream in = Shaders.shaderPack.getResourceAsStream(pathFull);
            if (in == null) {
                SMCLog.warning("Raw texture not found: " + path);
                return null;
            }
            final byte[] bytes = Config.readAll(in);
            IOUtils.closeQuietly(in);
            final ByteBuffer bb = GLAllocation.createDirectByteBuffer(bytes.length);
            bb.put(bytes);
            bb.flip();
            final TextureMetadataSection tms = SimpleShaderTexture.loadTextureMetadataSection(pathFull, new TextureMetadataSection(true, true, new ArrayList<Integer>()));
            final CustomTextureRaw ctr = new CustomTextureRaw(type, internalFormat, width, height, depth, pixelFormat, pixelType, bb, textureUnit, tms.getTextureBlur(), tms.getTextureClamp());
            return ctr;
        }
        catch (IOException e) {
            SMCLog.warning("Error loading raw texture: " + path);
            SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
    
    private static ICustomTexture loadCustomTextureShaders(final int textureUnit, String path) {
        path = path.trim();
        if (path.indexOf(46) < 0) {
            path += ".png";
        }
        try {
            final String pathFull = "shaders/" + StrUtils.removePrefix(path, "/");
            final InputStream in = Shaders.shaderPack.getResourceAsStream(pathFull);
            if (in == null) {
                SMCLog.warning("Texture not found: " + path);
                return null;
            }
            IOUtils.closeQuietly(in);
            final SimpleShaderTexture tex = new SimpleShaderTexture(pathFull);
            tex.loadTexture(Shaders.mc.getResourceManager());
            final CustomTexture ct = new CustomTexture(textureUnit, pathFull, tex);
            return ct;
        }
        catch (IOException e) {
            SMCLog.warning("Error loading texture: " + path);
            SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
    
    private static int getTextureIndex(final int stage, final String name) {
        if (stage == 0) {
            if (name.equals("texture")) {
                return 0;
            }
            if (name.equals("lightmap")) {
                return 1;
            }
            if (name.equals("normals")) {
                return 2;
            }
            if (name.equals("specular")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return Shaders.waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0")) {
                return 6;
            }
            if (name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        if (stage == 1 || stage == 2) {
            if (name.equals("colortex0") || name.equals("colortex0")) {
                return 0;
            }
            if (name.equals("colortex1") || name.equals("gdepth")) {
                return 1;
            }
            if (name.equals("colortex2") || name.equals("gnormal")) {
                return 2;
            }
            if (name.equals("colortex3") || name.equals("composite")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return Shaders.waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0") || name.equals("gdepthtex")) {
                return 6;
            }
            if (name.equals("colortex4") || name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("colortex5") || name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("colortex6") || name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("colortex7") || name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 11;
            }
            if (name.equals("depthtex2")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        return -1;
    }
    
    private static void bindCustomTextures(final ICustomTexture[] cts) {
        if (cts == null) {
            return;
        }
        for (int i = 0; i < cts.length; ++i) {
            final ICustomTexture ct = cts[i];
            GlStateManager.setActiveTexture(33984 + ct.getTextureUnit());
            final int texId = ct.getTextureId();
            final int target = ct.getTarget();
            if (target == 3553) {
                GlStateManager.bindTexture(texId);
            }
            else {
                GL11.glBindTexture(target, texId);
            }
        }
    }
    
    private static void resetCustomTextures() {
        deleteCustomTextures(Shaders.customTexturesGbuffers);
        deleteCustomTextures(Shaders.customTexturesComposite);
        deleteCustomTextures(Shaders.customTexturesDeferred);
        Shaders.customTexturesGbuffers = null;
        Shaders.customTexturesComposite = null;
        Shaders.customTexturesDeferred = null;
    }
    
    private static void deleteCustomTextures(final ICustomTexture[] cts) {
        if (cts == null) {
            return;
        }
        for (int i = 0; i < cts.length; ++i) {
            final ICustomTexture ct = cts[i];
            ct.deleteTexture();
        }
    }
    
    public static ShaderOption[] getShaderPackOptions(final String screenName) {
        ShaderOption[] ops = Shaders.shaderPackOptions.clone();
        if (Shaders.shaderPackGuiScreens == null) {
            if (Shaders.shaderPackProfiles != null) {
                final ShaderOptionProfile optionProfile = new ShaderOptionProfile(Shaders.shaderPackProfiles, ops);
                ops = (ShaderOption[])Config.addObjectToArray(ops, optionProfile, 0);
            }
            ops = getVisibleOptions(ops);
            return ops;
        }
        final String key = (screenName != null) ? ("screen." + screenName) : "screen";
        final ScreenShaderOptions sso = Shaders.shaderPackGuiScreens.get(key);
        if (sso == null) {
            return new ShaderOption[0];
        }
        final ShaderOption[] sos = sso.getShaderOptions();
        final List<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < sos.length; ++i) {
            final ShaderOption so = sos[i];
            if (so == null) {
                list.add(null);
            }
            else if (so instanceof ShaderOptionRest) {
                final ShaderOption[] restOps = getShaderOptionsRest(Shaders.shaderPackGuiScreens, ops);
                list.addAll(Arrays.asList(restOps));
            }
            else {
                list.add(so);
            }
        }
        final ShaderOption[] sosExp = list.toArray(new ShaderOption[list.size()]);
        return sosExp;
    }
    
    public static int getShaderPackColumns(final String screenName, final int def) {
        final String key = (screenName != null) ? ("screen." + screenName) : "screen";
        if (Shaders.shaderPackGuiScreens == null) {
            return def;
        }
        final ScreenShaderOptions sso = Shaders.shaderPackGuiScreens.get(key);
        if (sso == null) {
            return def;
        }
        return sso.getColumns();
    }
    
    private static ShaderOption[] getShaderOptionsRest(final Map<String, ScreenShaderOptions> mapScreens, final ShaderOption[] ops) {
        final Set<String> setNames = new HashSet<String>();
        final Set<String> keys = mapScreens.keySet();
        for (final String key : keys) {
            final ScreenShaderOptions sso = mapScreens.get(key);
            final ShaderOption[] sos = sso.getShaderOptions();
            for (int v = 0; v < sos.length; ++v) {
                final ShaderOption so = sos[v];
                if (so != null) {
                    setNames.add(so.getName());
                }
            }
        }
        final List<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ops.length; ++i) {
            final ShaderOption so2 = ops[i];
            if (so2.isVisible()) {
                final String name = so2.getName();
                if (!setNames.contains(name)) {
                    list.add(so2);
                }
            }
        }
        final ShaderOption[] sos2 = list.toArray(new ShaderOption[list.size()]);
        return sos2;
    }
    
    public static ShaderOption getShaderOption(final String name) {
        return ShaderUtils.getShaderOption(name, Shaders.shaderPackOptions);
    }
    
    public static ShaderOption[] getShaderPackOptions() {
        return Shaders.shaderPackOptions;
    }
    
    public static boolean isShaderPackOptionSlider(final String name) {
        return Shaders.shaderPackOptionSliders != null && Shaders.shaderPackOptionSliders.contains(name);
    }
    
    private static ShaderOption[] getVisibleOptions(final ShaderOption[] ops) {
        final List<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ops.length; ++i) {
            final ShaderOption so = ops[i];
            if (so.isVisible()) {
                list.add(so);
            }
        }
        final ShaderOption[] sos = list.toArray(new ShaderOption[list.size()]);
        return sos;
    }
    
    public static void saveShaderPackOptions() {
        saveShaderPackOptions(Shaders.shaderPackOptions, Shaders.shaderPack);
    }
    
    private static void saveShaderPackOptions(final ShaderOption[] sos, final IShaderPack sp) {
        final Properties props = new PropertiesOrdered();
        if (Shaders.shaderPackOptions != null) {
            for (int i = 0; i < sos.length; ++i) {
                final ShaderOption so = sos[i];
                if (so.isChanged()) {
                    if (so.isEnabled()) {
                        props.setProperty(so.getName(), so.getValue());
                    }
                }
            }
        }
        try {
            saveOptionProperties(sp, props);
        }
        catch (IOException e) {
            Config.warn("[Shaders] Error saving configuration for " + Shaders.shaderPack.getName());
            e.printStackTrace();
        }
    }
    
    private static void saveOptionProperties(final IShaderPack sp, final Properties props) throws IOException {
        final String path = "shaderpacks/" + sp.getName() + ".txt";
        final File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);
        if (props.isEmpty()) {
            propFile.delete();
            return;
        }
        final FileOutputStream fos = new FileOutputStream(propFile);
        props.store(fos, null);
        fos.flush();
        fos.close();
    }
    
    private static ShaderOption[] loadShaderPackOptions() {
        try {
            final String[] programNames = Shaders.programs.getProgramNames();
            final ShaderOption[] sos = ShaderPackParser.parseShaderPackOptions(Shaders.shaderPack, programNames, Shaders.shaderPackDimensions);
            final Properties props = loadOptionProperties(Shaders.shaderPack);
            for (int i = 0; i < sos.length; ++i) {
                final ShaderOption so = sos[i];
                final String val = props.getProperty(so.getName());
                if (val != null) {
                    so.resetValue();
                    if (!so.setValue(val)) {
                        Config.warn("[Shaders] Invalid value, option: " + so.getName() + ", value: " + val);
                    }
                }
            }
            return sos;
        }
        catch (IOException e) {
            Config.warn("[Shaders] Error reading configuration for " + Shaders.shaderPack.getName());
            e.printStackTrace();
            return null;
        }
    }
    
    private static Properties loadOptionProperties(final IShaderPack sp) throws IOException {
        final Properties props = new PropertiesOrdered();
        final String path = "shaderpacks/" + sp.getName() + ".txt";
        final File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);
        if (!propFile.exists() || !propFile.isFile() || !propFile.canRead()) {
            return props;
        }
        final FileInputStream fis = new FileInputStream(propFile);
        props.load(fis);
        fis.close();
        return props;
    }
    
    public static ShaderOption[] getChangedOptions(final ShaderOption[] ops) {
        final List<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i = 0; i < ops.length; ++i) {
            final ShaderOption op = ops[i];
            if (op.isEnabled()) {
                if (op.isChanged()) {
                    list.add(op);
                }
            }
        }
        final ShaderOption[] cops = list.toArray(new ShaderOption[list.size()]);
        return cops;
    }
    
    private static String applyOptions(String line, final ShaderOption[] ops) {
        if (ops == null || ops.length <= 0) {
            return line;
        }
        for (int i = 0; i < ops.length; ++i) {
            final ShaderOption op = ops[i];
            if (op.matchesLine(line)) {
                line = op.getSourceLine();
                break;
            }
        }
        return line;
    }
    
    public static ArrayList listOfShaders() {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("OFF");
        list.add("(internal)");
        final int countFixed = list.size();
        try {
            if (!Shaders.shaderPacksDir.exists()) {
                Shaders.shaderPacksDir.mkdir();
            }
            final File[] listOfFiles = Shaders.shaderPacksDir.listFiles();
            for (int i = 0; i < listOfFiles.length; ++i) {
                final File file = listOfFiles[i];
                final String name = file.getName();
                if (file.isDirectory()) {
                    if (!name.equals("debug")) {
                        final File subDir = new File(file, "shaders");
                        if (subDir.exists() && subDir.isDirectory()) {
                            list.add(name);
                        }
                    }
                }
                else if (file.isFile() && name.toLowerCase().endsWith(".zip")) {
                    list.add(name);
                }
            }
        }
        catch (Exception ex) {}
        final List<String> sortList = list.subList(countFixed, list.size());
        Collections.sort(sortList, String.CASE_INSENSITIVE_ORDER);
        return list;
    }
    
    public static int checkFramebufferStatus(final String location) {
        final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", status, location);
        }
        return status;
    }
    
    public static int checkGLError(final String location) {
        final int errorCode = GlStateManager.glGetError();
        if (errorCode != 0 && GlErrors.isEnabled(errorCode)) {
            final String errorText = Config.getGlErrorString(errorCode);
            final String shadersInfo = getErrorInfo(errorCode, location);
            final String messageLog = String.format("OpenGL error: %s (%s)%s, at: %s", errorCode, errorText, shadersInfo, location);
            SMCLog.severe(messageLog);
            if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorShaders", 10000L)) {
                final String messageChat = I18n.format("of.message.openglError", errorCode, errorText);
                printChat(messageChat);
            }
        }
        return errorCode;
    }
    
    private static String getErrorInfo(final int errorCode, final String location) {
        final StringBuilder sb = new StringBuilder();
        if (errorCode == 1286) {
            final int statusCode = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            final String statusText = getFramebufferStatusText(statusCode);
            final String info = ", fbStatus: " + statusCode + " (" + statusText + ")";
            sb.append(info);
        }
        String programName = Shaders.activeProgram.getName();
        if (programName.isEmpty()) {
            programName = "none";
        }
        sb.append(", program: " + programName);
        final Program activeProgramReal = getProgramById(Shaders.activeProgramID);
        if (activeProgramReal != Shaders.activeProgram) {
            String programRealName = activeProgramReal.getName();
            if (programRealName.isEmpty()) {
                programRealName = "none";
            }
            sb.append(" (" + programRealName + ")");
        }
        if (location.equals("setDrawBuffers")) {
            sb.append(", drawBuffers: " + Shaders.activeProgram.getDrawBufSettings());
        }
        return sb.toString();
    }
    
    private static Program getProgramById(final int programID) {
        for (int i = 0; i < Shaders.ProgramsAll.length; ++i) {
            final Program pi = Shaders.ProgramsAll[i];
            if (pi.getId() == programID) {
                return pi;
            }
        }
        return Shaders.ProgramNone;
    }
    
    private static String getFramebufferStatusText(final int fbStatusCode) {
        switch (fbStatusCode) {
            case 36053: {
                return "Complete";
            }
            case 33305: {
                return "Undefined";
            }
            case 36054: {
                return "Incomplete attachment";
            }
            case 36055: {
                return "Incomplete missing attachment";
            }
            case 36059: {
                return "Incomplete draw buffer";
            }
            case 36060: {
                return "Incomplete read buffer";
            }
            case 36061: {
                return "Unsupported";
            }
            case 36182: {
                return "Incomplete multisample";
            }
            case 36264: {
                return "Incomplete layer targets";
            }
            default: {
                return "Unknown";
            }
        }
    }
    
    private static void printChat(final String str) {
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }
    
    private static void printChatAndLogError(final String str) {
        SMCLog.severe(str);
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }
    
    public static void printIntBuffer(final String title, final IntBuffer buf) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        for (int lim = buf.limit(), i = 0; i < lim; ++i) {
            sb.append(" ").append(buf.get(i));
        }
        sb.append("]");
        SMCLog.info(sb.toString());
    }
    
    public static void startup(Minecraft mc) {
        checkShadersModInstalled();
        Shaders.mc = mc;
        mc = Minecraft.getMinecraft();
        Shaders.capabilities = GLContext.getCapabilities();
        Shaders.glVersionString = GL11.glGetString(7938);
        Shaders.glVendorString = GL11.glGetString(7936);
        Shaders.glRendererString = GL11.glGetString(7937);
        SMCLog.info("OpenGL Version: " + Shaders.glVersionString);
        SMCLog.info("Vendor:  " + Shaders.glVendorString);
        SMCLog.info("Renderer: " + Shaders.glRendererString);
        SMCLog.info("Capabilities: " + (Shaders.capabilities.OpenGL20 ? " 2.0 " : " - ") + (Shaders.capabilities.OpenGL21 ? " 2.1 " : " - ") + (Shaders.capabilities.OpenGL30 ? " 3.0 " : " - ") + (Shaders.capabilities.OpenGL32 ? " 3.2 " : " - ") + (Shaders.capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
        Shaders.hasGlGenMipmap = Shaders.capabilities.OpenGL30;
        loadConfig();
    }
    
    public static void updateBlockLightLevel() {
        if (isOldLighting()) {
            Shaders.blockLightLevel05 = 0.5f;
            Shaders.blockLightLevel06 = 0.6f;
            Shaders.blockLightLevel08 = 0.8f;
        }
        else {
            Shaders.blockLightLevel05 = 1.0f;
            Shaders.blockLightLevel06 = 1.0f;
            Shaders.blockLightLevel08 = 1.0f;
        }
    }
    
    public static boolean isOldHandLight() {
        if (!Shaders.configOldHandLight.isDefault()) {
            return Shaders.configOldHandLight.isTrue();
        }
        return Shaders.shaderPackOldHandLight.isDefault() || Shaders.shaderPackOldHandLight.isTrue();
    }
    
    public static boolean isDynamicHandLight() {
        return Shaders.shaderPackDynamicHandLight.isDefault() || Shaders.shaderPackDynamicHandLight.isTrue();
    }
    
    public static boolean isOldLighting() {
        if (!Shaders.configOldLighting.isDefault()) {
            return Shaders.configOldLighting.isTrue();
        }
        return Shaders.shaderPackOldLighting.isDefault() || Shaders.shaderPackOldLighting.isTrue();
    }
    
    public static boolean isRenderShadowTranslucent() {
        return !Shaders.shaderPackShadowTranslucent.isFalse();
    }
    
    public static boolean isUnderwaterOverlay() {
        return !Shaders.shaderPackUnderwaterOverlay.isFalse();
    }
    
    public static boolean isSun() {
        return !Shaders.shaderPackSun.isFalse();
    }
    
    public static boolean isMoon() {
        return !Shaders.shaderPackMoon.isFalse();
    }
    
    public static boolean isVignette() {
        return !Shaders.shaderPackVignette.isFalse();
    }
    
    public static boolean isRenderBackFace(final EnumWorldBlockLayer blockLayerIn) {
        switch (blockLayerIn) {
            case SOLID: {
                return Shaders.shaderPackBackFaceSolid.isTrue();
            }
            case CUTOUT: {
                return Shaders.shaderPackBackFaceCutout.isTrue();
            }
            case CUTOUT_MIPPED: {
                return Shaders.shaderPackBackFaceCutoutMipped.isTrue();
            }
            case TRANSLUCENT: {
                return Shaders.shaderPackBackFaceTranslucent.isTrue();
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isRainDepth() {
        return Shaders.shaderPackRainDepth.isTrue();
    }
    
    public static boolean isBeaconBeamDepth() {
        return Shaders.shaderPackBeaconBeamDepth.isTrue();
    }
    
    public static boolean isSeparateAo() {
        return Shaders.shaderPackSeparateAo.isTrue();
    }
    
    public static boolean isFrustumCulling() {
        return !Shaders.shaderPackFrustumCulling.isFalse();
    }
    
    public static void init() {
        boolean firstInit;
        if (!Shaders.isInitializedOnce) {
            Shaders.isInitializedOnce = true;
            firstInit = true;
        }
        else {
            firstInit = false;
        }
        if (!Shaders.isShaderPackInitialized) {
            checkGLError("Shaders.init pre");
            if (getShaderPackName() != null) {}
            if (!Shaders.capabilities.OpenGL20) {
                printChatAndLogError("No OpenGL 2.0");
            }
            if (!Shaders.capabilities.GL_EXT_framebuffer_object) {
                printChatAndLogError("No EXT_framebuffer_object");
            }
            Shaders.dfbDrawBuffers.position(0).limit(8);
            Shaders.dfbColorTextures.position(0).limit(16);
            Shaders.dfbDepthTextures.position(0).limit(3);
            Shaders.sfbDrawBuffers.position(0).limit(8);
            Shaders.sfbDepthTextures.position(0).limit(2);
            Shaders.sfbColorTextures.position(0).limit(8);
            Shaders.usedColorBuffers = 4;
            Shaders.usedDepthBuffers = 1;
            Shaders.usedShadowColorBuffers = 0;
            Shaders.usedShadowDepthBuffers = 0;
            Shaders.usedColorAttachs = 1;
            Shaders.usedDrawBuffers = 1;
            Arrays.fill(Shaders.gbuffersFormat, 6408);
            Arrays.fill(Shaders.gbuffersClear, true);
            Arrays.fill(Shaders.gbuffersClearColor, null);
            Arrays.fill(Shaders.shadowHardwareFilteringEnabled, false);
            Arrays.fill(Shaders.shadowMipmapEnabled, false);
            Arrays.fill(Shaders.shadowFilterNearest, false);
            Arrays.fill(Shaders.shadowColorMipmapEnabled, false);
            Arrays.fill(Shaders.shadowColorFilterNearest, false);
            Shaders.centerDepthSmoothEnabled = false;
            Shaders.noiseTextureEnabled = false;
            Shaders.sunPathRotation = 0.0f;
            Shaders.shadowIntervalSize = 2.0f;
            Shaders.shadowMapWidth = 1024;
            Shaders.shadowMapHeight = 1024;
            Shaders.spShadowMapWidth = 1024;
            Shaders.spShadowMapHeight = 1024;
            Shaders.shadowMapFOV = 90.0f;
            Shaders.shadowMapHalfPlane = 160.0f;
            Shaders.shadowMapIsOrtho = true;
            Shaders.shadowDistanceRenderMul = -1.0f;
            Shaders.aoLevel = -1.0f;
            Shaders.useEntityAttrib = false;
            Shaders.useMidTexCoordAttrib = false;
            Shaders.useTangentAttrib = false;
            Shaders.waterShadowEnabled = false;
            Shaders.hasGeometryShaders = false;
            updateBlockLightLevel();
            Smoother.resetValues();
            Shaders.shaderUniforms.reset();
            if (Shaders.customUniforms != null) {
                Shaders.customUniforms.reset();
            }
            final ShaderProfile activeProfile = ShaderUtils.detectProfile(Shaders.shaderPackProfiles, Shaders.shaderPackOptions, false);
            String worldPrefix = "";
            if (Shaders.currentWorld != null) {
                final int dimId = Shaders.currentWorld.provider.getDimensionId();
                if (Shaders.shaderPackDimensions.contains(dimId)) {
                    worldPrefix = "world" + dimId + "/";
                }
            }
            for (int i = 0; i < Shaders.ProgramsAll.length; ++i) {
                final Program p = Shaders.ProgramsAll[i];
                p.resetId();
                p.resetConfiguration();
                if (p.getProgramStage() != ProgramStage.NONE) {
                    String programName = p.getName();
                    String programPath = worldPrefix + programName;
                    boolean enabled = true;
                    if (Shaders.shaderPackProgramConditions.containsKey(programPath)) {
                        enabled = (enabled && Shaders.shaderPackProgramConditions.get(programPath).eval());
                    }
                    if (activeProfile != null) {
                        enabled = (enabled && !activeProfile.isProgramDisabled(programPath));
                    }
                    if (!enabled) {
                        SMCLog.info("Program disabled: " + programPath);
                        programName = "<disabled>";
                        programPath = worldPrefix + programName;
                    }
                    final String programFullPath = "/shaders/" + programPath;
                    final String programFullPathVertex = programFullPath + ".vsh";
                    final String programFullPathGeometry = programFullPath + ".gsh";
                    final String programFullPathFragment = programFullPath + ".fsh";
                    setupProgram(p, programFullPathVertex, programFullPathGeometry, programFullPathFragment);
                    final int pr = p.getId();
                    if (pr > 0) {
                        SMCLog.info("Program loaded: " + programPath);
                    }
                    initDrawBuffers(p);
                    updateToggleBuffers(p);
                }
            }
            Shaders.hasDeferredPrograms = false;
            for (int cp = 0; cp < Shaders.ProgramsDeferred.length; ++cp) {
                if (Shaders.ProgramsDeferred[cp].getId() != 0) {
                    Shaders.hasDeferredPrograms = true;
                    break;
                }
            }
            Shaders.usedColorAttachs = Shaders.usedColorBuffers;
            Shaders.shadowPassInterval = ((Shaders.usedShadowDepthBuffers > 0) ? 1 : 0);
            Shaders.shouldSkipDefaultShadow = (Shaders.usedShadowDepthBuffers > 0);
            SMCLog.info("usedColorBuffers: " + Shaders.usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + Shaders.usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + Shaders.usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + Shaders.usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + Shaders.usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + Shaders.usedDrawBuffers);
            Shaders.dfbDrawBuffers.position(0).limit(Shaders.usedDrawBuffers);
            Shaders.dfbColorTextures.position(0).limit(Shaders.usedColorBuffers * 2);
            Shaders.dfbColorTexturesFlip.reset();
            for (int i = 0; i < Shaders.usedDrawBuffers; ++i) {
                Shaders.dfbDrawBuffers.put(i, 36064 + i);
            }
            final int maxDrawBuffers = GL11.glGetInteger(34852);
            if (Shaders.usedDrawBuffers > maxDrawBuffers) {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + Shaders.usedDrawBuffers + ", available: " + maxDrawBuffers);
            }
            Shaders.sfbDrawBuffers.position(0).limit(Shaders.usedShadowColorBuffers);
            for (int j = 0; j < Shaders.usedShadowColorBuffers; ++j) {
                Shaders.sfbDrawBuffers.put(j, 36064 + j);
            }
            for (int j = 0; j < Shaders.ProgramsAll.length; ++j) {
                Program pn;
                Program pi;
                for (pi = (pn = Shaders.ProgramsAll[j]); pn.getId() == 0 && pn.getProgramBackup() != pn; pn = pn.getProgramBackup()) {}
                if (pn != pi && pi != Shaders.ProgramShadow) {
                    pi.copyFrom(pn);
                }
            }
            resize();
            resizeShadow();
            if (Shaders.noiseTextureEnabled) {
                setupNoiseTexture();
            }
            if (Shaders.defaultTexture == null) {
                Shaders.defaultTexture = ShadersTex.createDefaultTexture();
            }
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            preCelestialRotate();
            postCelestialRotate();
            GlStateManager.popMatrix();
            Shaders.isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayLists();
            if (!firstInit) {}
            checkGLError("Shaders.init");
        }
    }
    
    private static void initDrawBuffers(final Program p) {
        final int maxDrawBuffers = GL11.glGetInteger(34852);
        Arrays.fill(p.getToggleColorTextures(), false);
        if (p == Shaders.ProgramFinal) {
            p.setDrawBuffers(null);
            return;
        }
        if (p.getId() == 0) {
            if (p == Shaders.ProgramShadow) {
                p.setDrawBuffers(Shaders.drawBuffersNone);
            }
            else {
                p.setDrawBuffers(Shaders.drawBuffersColorAtt0);
            }
            return;
        }
        final String str = p.getDrawBufSettings();
        if (str == null) {
            if (p != Shaders.ProgramShadow && p != Shaders.ProgramShadowSolid && p != Shaders.ProgramShadowCutout) {
                p.setDrawBuffers(Shaders.dfbDrawBuffers);
                Shaders.usedDrawBuffers = Shaders.usedColorBuffers;
                Arrays.fill(p.getToggleColorTextures(), 0, Shaders.usedColorBuffers, true);
            }
            else {
                p.setDrawBuffers(Shaders.sfbDrawBuffers);
            }
            return;
        }
        final IntBuffer intbuf = p.getDrawBuffersBuffer();
        int numDB = str.length();
        Shaders.usedDrawBuffers = Math.max(Shaders.usedDrawBuffers, numDB);
        numDB = Math.min(numDB, maxDrawBuffers);
        p.setDrawBuffers(intbuf);
        intbuf.limit(numDB);
        for (int i = 0; i < numDB; ++i) {
            final int drawBuffer = getDrawBuffer(p, str, i);
            intbuf.put(i, drawBuffer);
        }
    }
    
    private static int getDrawBuffer(final Program p, final String str, final int i) {
        int drawBuffer = 0;
        if (i >= str.length()) {
            return drawBuffer;
        }
        final int ca = str.charAt(i) - '0';
        if (p == Shaders.ProgramShadow) {
            if (ca >= 0 && ca <= 1) {
                drawBuffer = ca + 36064;
                Shaders.usedShadowColorBuffers = Math.max(Shaders.usedShadowColorBuffers, ca);
            }
            return drawBuffer;
        }
        if (ca >= 0 && ca <= 7) {
            p.getToggleColorTextures()[ca] = true;
            drawBuffer = ca + 36064;
            Shaders.usedColorAttachs = Math.max(Shaders.usedColorAttachs, ca);
            Shaders.usedColorBuffers = Math.max(Shaders.usedColorBuffers, ca);
        }
        return drawBuffer;
    }
    
    private static void updateToggleBuffers(final Program p) {
        final boolean[] toggleBuffers = p.getToggleColorTextures();
        final Boolean[] flipBuffers = p.getBuffersFlip();
        for (int i = 0; i < flipBuffers.length; ++i) {
            final Boolean flip = flipBuffers[i];
            if (flip != null) {
                toggleBuffers[i] = flip;
            }
        }
    }
    
    public static void resetDisplayLists() {
        SMCLog.info("Reset model renderers");
        ++Shaders.countResetDisplayLists;
        SMCLog.info("Reset world renderers");
        Shaders.mc.renderGlobal.loadRenderers();
    }
    
    private static void setupProgram(final Program program, final String vShaderPath, final String gShaderPath, final String fShaderPath) {
        checkGLError("pre setupProgram");
        int programid = ARBShaderObjects.glCreateProgramObjectARB();
        checkGLError("create");
        if (programid != 0) {
            Shaders.progUseEntityAttrib = false;
            Shaders.progUseMidTexCoordAttrib = false;
            Shaders.progUseTangentAttrib = false;
            final int vShader = createVertShader(program, vShaderPath);
            final int gShader = createGeomShader(program, gShaderPath);
            final int fShader = createFragShader(program, fShaderPath);
            checkGLError("create");
            if (vShader != 0 || gShader != 0 || fShader != 0) {
                if (vShader != 0) {
                    ARBShaderObjects.glAttachObjectARB(programid, vShader);
                    checkGLError("attach");
                }
                if (gShader != 0) {
                    ARBShaderObjects.glAttachObjectARB(programid, gShader);
                    checkGLError("attach");
                    if (Shaders.progArbGeometryShader4) {
                        ARBGeometryShader4.glProgramParameteriARB(programid, 36315, 4);
                        ARBGeometryShader4.glProgramParameteriARB(programid, 36316, 5);
                        ARBGeometryShader4.glProgramParameteriARB(programid, 36314, Shaders.progMaxVerticesOut);
                        checkGLError("arbGeometryShader4");
                    }
                    Shaders.hasGeometryShaders = true;
                }
                if (fShader != 0) {
                    ARBShaderObjects.glAttachObjectARB(programid, fShader);
                    checkGLError("attach");
                }
                if (Shaders.progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.entityAttrib, (CharSequence)"mc_Entity");
                    checkGLError("mc_Entity");
                }
                if (Shaders.progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.midTexCoordAttrib, (CharSequence)"mc_midTexCoord");
                    checkGLError("mc_midTexCoord");
                }
                if (Shaders.progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.tangentAttrib, (CharSequence)"at_tangent");
                    checkGLError("at_tangent");
                }
                ARBShaderObjects.glLinkProgramARB(programid);
                if (GL20.glGetProgrami(programid, 35714) != 1) {
                    SMCLog.severe("Error linking program: " + programid + " (" + program.getName() + ")");
                }
                printLogInfo(programid, program.getName());
                if (vShader != 0) {
                    ARBShaderObjects.glDetachObjectARB(programid, vShader);
                    ARBShaderObjects.glDeleteObjectARB(vShader);
                }
                if (gShader != 0) {
                    ARBShaderObjects.glDetachObjectARB(programid, gShader);
                    ARBShaderObjects.glDeleteObjectARB(gShader);
                }
                if (fShader != 0) {
                    ARBShaderObjects.glDetachObjectARB(programid, fShader);
                    ARBShaderObjects.glDeleteObjectARB(fShader);
                }
                program.setId(programid);
                program.setRef(programid);
                useProgram(program);
                ARBShaderObjects.glValidateProgramARB(programid);
                useProgram(Shaders.ProgramNone);
                printLogInfo(programid, program.getName());
                final int valid = GL20.glGetProgrami(programid, 35715);
                if (valid != 1) {
                    final String Q = "\"";
                    printChatAndLogError("[Shaders] Error: Invalid program " + Q + program.getName() + Q);
                    ARBShaderObjects.glDeleteObjectARB(programid);
                    programid = 0;
                    program.resetId();
                }
            }
            else {
                ARBShaderObjects.glDeleteObjectARB(programid);
                programid = 0;
                program.resetId();
            }
        }
    }
    
    private static int createVertShader(final Program program, final String filename) {
        final int vertShader = ARBShaderObjects.glCreateShaderObjectARB(35633);
        if (vertShader == 0) {
            return 0;
        }
        final StringBuilder vertexCode = new StringBuilder(131072);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(getShaderReader(filename));
        }
        catch (Exception e2) {
            ARBShaderObjects.glDeleteObjectARB(vertShader);
            return 0;
        }
        final ShaderOption[] activeOptions = getChangedOptions(Shaders.shaderPackOptions);
        final List<String> listFiles = new ArrayList<String>();
        if (reader != null) {
            try {
                reader = ShaderPackParser.resolveIncludes(reader, filename, Shaders.shaderPack, 0, listFiles, 0);
                final MacroState macroState = new MacroState();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    line = applyOptions(line, activeOptions);
                    vertexCode.append(line).append('\n');
                    if (!macroState.processLine(line)) {
                        continue;
                    }
                    final ShaderLine sl = ShaderParser.parseLine(line);
                    if (sl == null) {
                        continue;
                    }
                    if (sl.isAttribute("mc_Entity")) {
                        Shaders.useEntityAttrib = true;
                        Shaders.progUseEntityAttrib = true;
                    }
                    else if (sl.isAttribute("mc_midTexCoord")) {
                        Shaders.useMidTexCoordAttrib = true;
                        Shaders.progUseMidTexCoordAttrib = true;
                    }
                    else if (sl.isAttribute("at_tangent")) {
                        Shaders.useTangentAttrib = true;
                        Shaders.progUseTangentAttrib = true;
                    }
                    if (!sl.isConstInt("countInstances")) {
                        continue;
                    }
                    program.setCountInstances(sl.getValueInt());
                    SMCLog.info("countInstances: " + program.getCountInstances());
                }
                reader.close();
            }
            catch (Exception e) {
                SMCLog.severe("Couldn't read " + filename + "!");
                e.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(vertShader);
                return 0;
            }
        }
        if (Shaders.saveFinalShaders) {
            saveShader(filename, vertexCode.toString());
        }
        ARBShaderObjects.glShaderSourceARB(vertShader, (CharSequence)vertexCode);
        ARBShaderObjects.glCompileShaderARB(vertShader);
        if (GL20.glGetShaderi(vertShader, 35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + filename);
        }
        printShaderLogInfo(vertShader, filename, listFiles);
        return vertShader;
    }
    
    private static int createGeomShader(final Program program, final String filename) {
        final int geomShader = ARBShaderObjects.glCreateShaderObjectARB(36313);
        if (geomShader == 0) {
            return 0;
        }
        final StringBuilder geomCode = new StringBuilder(131072);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(getShaderReader(filename));
        }
        catch (Exception e2) {
            ARBShaderObjects.glDeleteObjectARB(geomShader);
            return 0;
        }
        final ShaderOption[] activeOptions = getChangedOptions(Shaders.shaderPackOptions);
        final List<String> listFiles = new ArrayList<String>();
        Shaders.progArbGeometryShader4 = false;
        Shaders.progMaxVerticesOut = 3;
        if (reader != null) {
            try {
                reader = ShaderPackParser.resolveIncludes(reader, filename, Shaders.shaderPack, 0, listFiles, 0);
                final MacroState macroState = new MacroState();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    line = applyOptions(line, activeOptions);
                    geomCode.append(line).append('\n');
                    if (!macroState.processLine(line)) {
                        continue;
                    }
                    final ShaderLine sl = ShaderParser.parseLine(line);
                    if (sl == null) {
                        continue;
                    }
                    if (sl.isExtension("GL_ARB_geometry_shader4")) {
                        final String val = Config.normalize(sl.getValue());
                        if (val.equals("enable") || val.equals("require") || val.equals("warn")) {
                            Shaders.progArbGeometryShader4 = true;
                        }
                    }
                    if (!sl.isConstInt("maxVerticesOut")) {
                        continue;
                    }
                    Shaders.progMaxVerticesOut = sl.getValueInt();
                }
                reader.close();
            }
            catch (Exception e) {
                SMCLog.severe("Couldn't read " + filename + "!");
                e.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(geomShader);
                return 0;
            }
        }
        if (Shaders.saveFinalShaders) {
            saveShader(filename, geomCode.toString());
        }
        ARBShaderObjects.glShaderSourceARB(geomShader, (CharSequence)geomCode);
        ARBShaderObjects.glCompileShaderARB(geomShader);
        if (GL20.glGetShaderi(geomShader, 35713) != 1) {
            SMCLog.severe("Error compiling geometry shader: " + filename);
        }
        printShaderLogInfo(geomShader, filename, listFiles);
        return geomShader;
    }
    
    private static int createFragShader(final Program program, final String filename) {
        final int fragShader = ARBShaderObjects.glCreateShaderObjectARB(35632);
        if (fragShader == 0) {
            return 0;
        }
        final StringBuilder fragCode = new StringBuilder(131072);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(getShaderReader(filename));
        }
        catch (Exception e2) {
            ARBShaderObjects.glDeleteObjectARB(fragShader);
            return 0;
        }
        final ShaderOption[] activeOptions = getChangedOptions(Shaders.shaderPackOptions);
        final List<String> listFiles = new ArrayList<String>();
        if (reader != null) {
            try {
                reader = ShaderPackParser.resolveIncludes(reader, filename, Shaders.shaderPack, 0, listFiles, 0);
                final MacroState macroState = new MacroState();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    line = applyOptions(line, activeOptions);
                    fragCode.append(line).append('\n');
                    if (!macroState.processLine(line)) {
                        continue;
                    }
                    final ShaderLine sl = ShaderParser.parseLine(line);
                    if (sl == null) {
                        continue;
                    }
                    if (sl.isUniform()) {
                        final String uniform = sl.getName();
                        int index;
                        if ((index = ShaderParser.getShadowDepthIndex(uniform)) >= 0) {
                            Shaders.usedShadowDepthBuffers = Math.max(Shaders.usedShadowDepthBuffers, index + 1);
                        }
                        else if ((index = ShaderParser.getShadowColorIndex(uniform)) >= 0) {
                            Shaders.usedShadowColorBuffers = Math.max(Shaders.usedShadowColorBuffers, index + 1);
                        }
                        else if ((index = ShaderParser.getDepthIndex(uniform)) >= 0) {
                            Shaders.usedDepthBuffers = Math.max(Shaders.usedDepthBuffers, index + 1);
                        }
                        else if (uniform.equals("gdepth") && Shaders.gbuffersFormat[1] == 6408) {
                            Shaders.gbuffersFormat[1] = 34836;
                        }
                        else if ((index = ShaderParser.getColorIndex(uniform)) >= 0) {
                            Shaders.usedColorBuffers = Math.max(Shaders.usedColorBuffers, index + 1);
                        }
                        else {
                            if (!uniform.equals("centerDepthSmooth")) {
                                continue;
                            }
                            Shaders.centerDepthSmoothEnabled = true;
                        }
                    }
                    else if (sl.isConstInt("shadowMapResolution") || sl.isProperty("SHADOWRES")) {
                        Shaders.spShadowMapWidth = (Shaders.spShadowMapHeight = sl.getValueInt());
                        Shaders.shadowMapWidth = (Shaders.shadowMapHeight = Math.round(Shaders.spShadowMapWidth * Shaders.configShadowResMul));
                        SMCLog.info("Shadow map resolution: " + Shaders.spShadowMapWidth);
                    }
                    else if (sl.isConstFloat("shadowMapFov") || sl.isProperty("SHADOWFOV")) {
                        Shaders.shadowMapFOV = sl.getValueFloat();
                        Shaders.shadowMapIsOrtho = false;
                        SMCLog.info("Shadow map field of view: " + Shaders.shadowMapFOV);
                    }
                    else if (sl.isConstFloat("shadowDistance") || sl.isProperty("SHADOWHPL")) {
                        Shaders.shadowMapHalfPlane = sl.getValueFloat();
                        Shaders.shadowMapIsOrtho = true;
                        SMCLog.info("Shadow map distance: " + Shaders.shadowMapHalfPlane);
                    }
                    else if (sl.isConstFloat("shadowDistanceRenderMul")) {
                        Shaders.shadowDistanceRenderMul = sl.getValueFloat();
                        SMCLog.info("Shadow distance render mul: " + Shaders.shadowDistanceRenderMul);
                    }
                    else if (sl.isConstFloat("shadowIntervalSize")) {
                        Shaders.shadowIntervalSize = sl.getValueFloat();
                        SMCLog.info("Shadow map interval size: " + Shaders.shadowIntervalSize);
                    }
                    else if (sl.isConstBool("generateShadowMipmap", true)) {
                        Arrays.fill(Shaders.shadowMipmapEnabled, true);
                        SMCLog.info("Generate shadow mipmap");
                    }
                    else if (sl.isConstBool("generateShadowColorMipmap", true)) {
                        Arrays.fill(Shaders.shadowColorMipmapEnabled, true);
                        SMCLog.info("Generate shadow color mipmap");
                    }
                    else if (sl.isConstBool("shadowHardwareFiltering", true)) {
                        Arrays.fill(Shaders.shadowHardwareFilteringEnabled, true);
                        SMCLog.info("Hardware shadow filtering enabled.");
                    }
                    else if (sl.isConstBool("shadowHardwareFiltering0", true)) {
                        Shaders.shadowHardwareFilteringEnabled[0] = true;
                        SMCLog.info("shadowHardwareFiltering0");
                    }
                    else if (sl.isConstBool("shadowHardwareFiltering1", true)) {
                        Shaders.shadowHardwareFilteringEnabled[1] = true;
                        SMCLog.info("shadowHardwareFiltering1");
                    }
                    else if (sl.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true)) {
                        Shaders.shadowMipmapEnabled[0] = true;
                        SMCLog.info("shadowtex0Mipmap");
                    }
                    else if (sl.isConstBool("shadowtex1Mipmap", true)) {
                        Shaders.shadowMipmapEnabled[1] = true;
                        SMCLog.info("shadowtex1Mipmap");
                    }
                    else if (sl.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true)) {
                        Shaders.shadowColorMipmapEnabled[0] = true;
                        SMCLog.info("shadowcolor0Mipmap");
                    }
                    else if (sl.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true)) {
                        Shaders.shadowColorMipmapEnabled[1] = true;
                        SMCLog.info("shadowcolor1Mipmap");
                    }
                    else if (sl.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true)) {
                        Shaders.shadowFilterNearest[0] = true;
                        SMCLog.info("shadowtex0Nearest");
                    }
                    else if (sl.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true)) {
                        Shaders.shadowFilterNearest[1] = true;
                        SMCLog.info("shadowtex1Nearest");
                    }
                    else if (sl.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true)) {
                        Shaders.shadowColorFilterNearest[0] = true;
                        SMCLog.info("shadowcolor0Nearest");
                    }
                    else if (sl.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true)) {
                        Shaders.shadowColorFilterNearest[1] = true;
                        SMCLog.info("shadowcolor1Nearest");
                    }
                    else if (sl.isConstFloat("wetnessHalflife") || sl.isProperty("WETNESSHL")) {
                        Shaders.wetnessHalfLife = sl.getValueFloat();
                        SMCLog.info("Wetness halflife: " + Shaders.wetnessHalfLife);
                    }
                    else if (sl.isConstFloat("drynessHalflife") || sl.isProperty("DRYNESSHL")) {
                        Shaders.drynessHalfLife = sl.getValueFloat();
                        SMCLog.info("Dryness halflife: " + Shaders.drynessHalfLife);
                    }
                    else if (sl.isConstFloat("eyeBrightnessHalflife")) {
                        Shaders.eyeBrightnessHalflife = sl.getValueFloat();
                        SMCLog.info("Eye brightness halflife: " + Shaders.eyeBrightnessHalflife);
                    }
                    else if (sl.isConstFloat("centerDepthHalflife")) {
                        Shaders.centerDepthSmoothHalflife = sl.getValueFloat();
                        SMCLog.info("Center depth halflife: " + Shaders.centerDepthSmoothHalflife);
                    }
                    else if (sl.isConstFloat("sunPathRotation")) {
                        Shaders.sunPathRotation = sl.getValueFloat();
                        SMCLog.info("Sun path rotation: " + Shaders.sunPathRotation);
                    }
                    else if (sl.isConstFloat("ambientOcclusionLevel")) {
                        Shaders.aoLevel = Config.limit(sl.getValueFloat(), 0.0f, 1.0f);
                        SMCLog.info("AO Level: " + Shaders.aoLevel);
                    }
                    else if (sl.isConstInt("superSamplingLevel")) {
                        final int ssaa = sl.getValueInt();
                        if (ssaa > 1) {
                            SMCLog.info("Super sampling level: " + ssaa + "x");
                            Shaders.superSamplingLevel = ssaa;
                        }
                        else {
                            Shaders.superSamplingLevel = 1;
                        }
                    }
                    else if (sl.isConstInt("noiseTextureResolution")) {
                        Shaders.noiseTextureResolution = sl.getValueInt();
                        Shaders.noiseTextureEnabled = true;
                        SMCLog.info("Noise texture enabled");
                        SMCLog.info("Noise texture resolution: " + Shaders.noiseTextureResolution);
                    }
                    else if (sl.isConstIntSuffix("Format")) {
                        final String name = StrUtils.removeSuffix(sl.getName(), "Format");
                        final String value = sl.getValue();
                        final int bufferindex = getBufferIndexFromString(name);
                        final int format = getTextureFormatFromString(value);
                        if (bufferindex < 0 || format == 0) {
                            continue;
                        }
                        Shaders.gbuffersFormat[bufferindex] = format;
                        SMCLog.info("%s format: %s", name, value);
                    }
                    else if (sl.isConstBoolSuffix("Clear", false)) {
                        if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename)) {
                            continue;
                        }
                        final String name = StrUtils.removeSuffix(sl.getName(), "Clear");
                        final int bufferindex2 = getBufferIndexFromString(name);
                        if (bufferindex2 < 0) {
                            continue;
                        }
                        Shaders.gbuffersClear[bufferindex2] = false;
                        SMCLog.info("%s clear disabled", name);
                    }
                    else if (sl.isConstVec4Suffix("ClearColor")) {
                        if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename)) {
                            continue;
                        }
                        final String name = StrUtils.removeSuffix(sl.getName(), "ClearColor");
                        final int bufferindex2 = getBufferIndexFromString(name);
                        if (bufferindex2 < 0) {
                            continue;
                        }
                        final Vector4f col = sl.getValueVec4();
                        if (col != null) {
                            Shaders.gbuffersClearColor[bufferindex2] = col;
                            SMCLog.info("%s clear color: %s %s %s %s", name, col.getX(), col.getY(), col.getZ(), col.getW());
                        }
                        else {
                            SMCLog.warning("Invalid color value: " + sl.getValue());
                        }
                    }
                    else if (sl.isProperty("GAUX4FORMAT", "RGBA32F")) {
                        Shaders.gbuffersFormat[7] = 34836;
                        SMCLog.info("gaux4 format : RGB32AF");
                    }
                    else if (sl.isProperty("GAUX4FORMAT", "RGB32F")) {
                        Shaders.gbuffersFormat[7] = 34837;
                        SMCLog.info("gaux4 format : RGB32F");
                    }
                    else if (sl.isProperty("GAUX4FORMAT", "RGB16")) {
                        Shaders.gbuffersFormat[7] = 32852;
                        SMCLog.info("gaux4 format : RGB16");
                    }
                    else if (sl.isConstBoolSuffix("MipmapEnabled", true)) {
                        if (!ShaderParser.isComposite(filename) && !ShaderParser.isDeferred(filename) && !ShaderParser.isFinal(filename)) {
                            continue;
                        }
                        final String name = StrUtils.removeSuffix(sl.getName(), "MipmapEnabled");
                        final int bufferindex2 = getBufferIndexFromString(name);
                        if (bufferindex2 < 0) {
                            continue;
                        }
                        int compositeMipmapSetting = program.getCompositeMipmapSetting();
                        compositeMipmapSetting |= 1 << bufferindex2;
                        program.setCompositeMipmapSetting(compositeMipmapSetting);
                        SMCLog.info("%s mipmap enabled", name);
                    }
                    else {
                        if (!sl.isProperty("DRAWBUFFERS")) {
                            continue;
                        }
                        final String val = sl.getValue();
                        if (ShaderParser.isValidDrawBuffers(val)) {
                            program.setDrawBufSettings(val);
                        }
                        else {
                            SMCLog.warning("Invalid draw buffers: " + val);
                        }
                    }
                }
                reader.close();
            }
            catch (Exception e) {
                SMCLog.severe("Couldn't read " + filename + "!");
                e.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(fragShader);
                return 0;
            }
        }
        if (Shaders.saveFinalShaders) {
            saveShader(filename, fragCode.toString());
        }
        ARBShaderObjects.glShaderSourceARB(fragShader, (CharSequence)fragCode);
        ARBShaderObjects.glCompileShaderARB(fragShader);
        if (GL20.glGetShaderi(fragShader, 35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + filename);
        }
        printShaderLogInfo(fragShader, filename, listFiles);
        return fragShader;
    }
    
    private static Reader getShaderReader(final String filename) {
        return new InputStreamReader(Shaders.shaderPack.getResourceAsStream(filename));
    }
    
    public static void saveShader(final String filename, final String code) {
        try {
            final File file = new File(Shaders.shaderPacksDir, "debug/" + filename);
            file.getParentFile().mkdirs();
            Config.writeFile(file, code);
        }
        catch (IOException e) {
            Config.warn("Error saving: " + filename);
            e.printStackTrace();
        }
    }
    
    private static void clearDirectory(final File dir) {
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()) {
            return;
        }
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            if (file.isDirectory()) {
                clearDirectory(file);
            }
            file.delete();
        }
    }
    
    private static boolean printLogInfo(final int obj, final String name) {
        final IntBuffer iVal = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(obj, 35716, iVal);
        final int length = iVal.get();
        if (length > 1) {
            final ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
            final byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            if (infoBytes[length - 1] == 0) {
                infoBytes[length - 1] = 10;
            }
            String out = new String(infoBytes, Charsets.US_ASCII);
            out = StrUtils.trim(out, " \n\r\t");
            SMCLog.info("Info log: " + name + "\n" + out);
            return false;
        }
        return true;
    }
    
    private static boolean printShaderLogInfo(final int shader, final String name, final List<String> listFiles) {
        final IntBuffer iVal = BufferUtils.createIntBuffer(1);
        final int length = GL20.glGetShaderi(shader, 35716);
        if (length > 1) {
            for (int i = 0; i < listFiles.size(); ++i) {
                final String path = listFiles.get(i);
                SMCLog.info("File: " + (i + 1) + " = " + path);
            }
            String log = GL20.glGetShaderInfoLog(shader, length);
            log = StrUtils.trim(log, " \n\r\t");
            SMCLog.info("Shader info log: " + name + "\n" + log);
            return false;
        }
        return true;
    }
    
    public static void setDrawBuffers(IntBuffer drawBuffers) {
        if (drawBuffers == null) {
            drawBuffers = Shaders.drawBuffersNone;
        }
        if (Shaders.activeDrawBuffers != drawBuffers) {
            GL20.glDrawBuffers(Shaders.activeDrawBuffers = drawBuffers);
            checkGLError("setDrawBuffers");
        }
    }
    
    public static void useProgram(Program program) {
        checkGLError("pre-useProgram");
        if (Shaders.isShadowPass) {
            program = Shaders.ProgramShadow;
        }
        else if (Shaders.isEntitiesGlowing) {
            program = Shaders.ProgramEntitiesGlowing;
        }
        if (Shaders.activeProgram == program) {
            return;
        }
        updateAlphaBlend(Shaders.activeProgram, program);
        Shaders.activeProgram = program;
        int programID = program.getId();
        ARBShaderObjects.glUseProgramObjectARB(Shaders.activeProgramID = programID);
        if (checkGLError("useProgram") != 0) {
            program.setId(0);
            programID = program.getId();
            ARBShaderObjects.glUseProgramObjectARB(Shaders.activeProgramID = programID);
        }
        Shaders.shaderUniforms.setProgram(programID);
        if (Shaders.customUniforms != null) {
            Shaders.customUniforms.setProgram(programID);
        }
        if (programID == 0) {
            return;
        }
        final IntBuffer drawBuffers = program.getDrawBuffers();
        if (Shaders.isRenderingDfb) {
            setDrawBuffers(drawBuffers);
        }
        Shaders.activeCompositeMipmapSetting = program.getCompositeMipmapSetting();
        switch (program.getProgramStage()) {
            case GBUFFERS: {
                setProgramUniform1i(Shaders.uniform_texture, 0);
                setProgramUniform1i(Shaders.uniform_lightmap, 1);
                setProgramUniform1i(Shaders.uniform_normals, 2);
                setProgramUniform1i(Shaders.uniform_specular, 3);
                setProgramUniform1i(Shaders.uniform_shadow, Shaders.waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(Shaders.uniform_watershadow, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex0, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex1, 5);
                setProgramUniform1i(Shaders.uniform_depthtex0, 6);
                if (Shaders.customTexturesGbuffers != null || Shaders.hasDeferredPrograms) {
                    setProgramUniform1i(Shaders.uniform_gaux1, 7);
                    setProgramUniform1i(Shaders.uniform_gaux2, 8);
                    setProgramUniform1i(Shaders.uniform_gaux3, 9);
                    setProgramUniform1i(Shaders.uniform_gaux4, 10);
                }
                setProgramUniform1i(Shaders.uniform_depthtex1, 11);
                setProgramUniform1i(Shaders.uniform_shadowcolor, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor0, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor1, 14);
                setProgramUniform1i(Shaders.uniform_noisetex, 15);
                break;
            }
            case DEFERRED:
            case COMPOSITE: {
                setProgramUniform1i(Shaders.uniform_gcolor, 0);
                setProgramUniform1i(Shaders.uniform_gdepth, 1);
                setProgramUniform1i(Shaders.uniform_gnormal, 2);
                setProgramUniform1i(Shaders.uniform_composite, 3);
                setProgramUniform1i(Shaders.uniform_gaux1, 7);
                setProgramUniform1i(Shaders.uniform_gaux2, 8);
                setProgramUniform1i(Shaders.uniform_gaux3, 9);
                setProgramUniform1i(Shaders.uniform_gaux4, 10);
                setProgramUniform1i(Shaders.uniform_colortex0, 0);
                setProgramUniform1i(Shaders.uniform_colortex1, 1);
                setProgramUniform1i(Shaders.uniform_colortex2, 2);
                setProgramUniform1i(Shaders.uniform_colortex3, 3);
                setProgramUniform1i(Shaders.uniform_colortex4, 7);
                setProgramUniform1i(Shaders.uniform_colortex5, 8);
                setProgramUniform1i(Shaders.uniform_colortex6, 9);
                setProgramUniform1i(Shaders.uniform_colortex7, 10);
                setProgramUniform1i(Shaders.uniform_shadow, Shaders.waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(Shaders.uniform_watershadow, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex0, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex1, 5);
                setProgramUniform1i(Shaders.uniform_gdepthtex, 6);
                setProgramUniform1i(Shaders.uniform_depthtex0, 6);
                setProgramUniform1i(Shaders.uniform_depthtex1, 11);
                setProgramUniform1i(Shaders.uniform_depthtex2, 12);
                setProgramUniform1i(Shaders.uniform_shadowcolor, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor0, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor1, 14);
                setProgramUniform1i(Shaders.uniform_noisetex, 15);
                break;
            }
            case SHADOW: {
                setProgramUniform1i(Shaders.uniform_tex, 0);
                setProgramUniform1i(Shaders.uniform_texture, 0);
                setProgramUniform1i(Shaders.uniform_lightmap, 1);
                setProgramUniform1i(Shaders.uniform_normals, 2);
                setProgramUniform1i(Shaders.uniform_specular, 3);
                setProgramUniform1i(Shaders.uniform_shadow, Shaders.waterShadowEnabled ? 5 : 4);
                setProgramUniform1i(Shaders.uniform_watershadow, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex0, 4);
                setProgramUniform1i(Shaders.uniform_shadowtex1, 5);
                if (Shaders.customTexturesGbuffers != null) {
                    setProgramUniform1i(Shaders.uniform_gaux1, 7);
                    setProgramUniform1i(Shaders.uniform_gaux2, 8);
                    setProgramUniform1i(Shaders.uniform_gaux3, 9);
                    setProgramUniform1i(Shaders.uniform_gaux4, 10);
                }
                setProgramUniform1i(Shaders.uniform_shadowcolor, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor0, 13);
                setProgramUniform1i(Shaders.uniform_shadowcolor1, 14);
                setProgramUniform1i(Shaders.uniform_noisetex, 15);
                break;
            }
        }
        final ItemStack stack = (Shaders.mc.thePlayer != null) ? Shaders.mc.thePlayer.getHeldItem() : null;
        final Item item = (stack != null) ? stack.getItem() : null;
        int itemID = -1;
        Block block = null;
        if (item != null) {
            itemID = Item.itemRegistry.getIDForObject(item);
            block = Block.blockRegistry.getObjectById(itemID);
            itemID = ItemAliases.getItemAliasId(itemID);
        }
        final int blockLight = (block != null) ? block.getLightValue() : 0;
        setProgramUniform1i(Shaders.uniform_heldItemId, itemID);
        setProgramUniform1i(Shaders.uniform_heldBlockLightValue, blockLight);
        setProgramUniform1i(Shaders.uniform_fogMode, Shaders.fogEnabled ? Shaders.fogMode : 0);
        setProgramUniform1f(Shaders.uniform_fogDensity, Shaders.fogEnabled ? Shaders.fogDensity : 0.0f);
        setProgramUniform3f(Shaders.uniform_fogColor, Shaders.fogColorR, Shaders.fogColorG, Shaders.fogColorB);
        setProgramUniform3f(Shaders.uniform_skyColor, Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
        setProgramUniform1i(Shaders.uniform_worldTime, (int)(Shaders.worldTime % 24000L));
        setProgramUniform1i(Shaders.uniform_worldDay, (int)(Shaders.worldTime / 24000L));
        setProgramUniform1i(Shaders.uniform_moonPhase, Shaders.moonPhase);
        setProgramUniform1i(Shaders.uniform_frameCounter, Shaders.frameCounter);
        setProgramUniform1f(Shaders.uniform_frameTime, Shaders.frameTime);
        setProgramUniform1f(Shaders.uniform_frameTimeCounter, Shaders.frameTimeCounter);
        setProgramUniform1f(Shaders.uniform_sunAngle, Shaders.sunAngle);
        setProgramUniform1f(Shaders.uniform_shadowAngle, Shaders.shadowAngle);
        setProgramUniform1f(Shaders.uniform_rainStrength, Shaders.rainStrength);
        setProgramUniform1f(Shaders.uniform_aspectRatio, Shaders.renderWidth / (float)Shaders.renderHeight);
        setProgramUniform1f(Shaders.uniform_viewWidth, (float)Shaders.renderWidth);
        setProgramUniform1f(Shaders.uniform_viewHeight, (float)Shaders.renderHeight);
        setProgramUniform1f(Shaders.uniform_near, 0.05f);
        setProgramUniform1f(Shaders.uniform_far, (float)(Shaders.mc.gameSettings.renderDistanceChunks * 16));
        setProgramUniform3f(Shaders.uniform_sunPosition, Shaders.sunPosition[0], Shaders.sunPosition[1], Shaders.sunPosition[2]);
        setProgramUniform3f(Shaders.uniform_moonPosition, Shaders.moonPosition[0], Shaders.moonPosition[1], Shaders.moonPosition[2]);
        setProgramUniform3f(Shaders.uniform_shadowLightPosition, Shaders.shadowLightPosition[0], Shaders.shadowLightPosition[1], Shaders.shadowLightPosition[2]);
        setProgramUniform3f(Shaders.uniform_upPosition, Shaders.upPosition[0], Shaders.upPosition[1], Shaders.upPosition[2]);
        setProgramUniform3f(Shaders.uniform_previousCameraPosition, (float)Shaders.previousCameraPositionX, (float)Shaders.previousCameraPositionY, (float)Shaders.previousCameraPositionZ);
        setProgramUniform3f(Shaders.uniform_cameraPosition, (float)Shaders.cameraPositionX, (float)Shaders.cameraPositionY, (float)Shaders.cameraPositionZ);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferModelView, false, Shaders.modelView);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferModelViewInverse, false, Shaders.modelViewInverse);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferPreviousProjection, false, Shaders.previousProjection);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferProjection, false, Shaders.projection);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferProjectionInverse, false, Shaders.projectionInverse);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferPreviousModelView, false, Shaders.previousModelView);
        if (Shaders.usedShadowDepthBuffers > 0) {
            setProgramUniformMatrix4ARB(Shaders.uniform_shadowProjection, false, Shaders.shadowProjection);
            setProgramUniformMatrix4ARB(Shaders.uniform_shadowProjectionInverse, false, Shaders.shadowProjectionInverse);
            setProgramUniformMatrix4ARB(Shaders.uniform_shadowModelView, false, Shaders.shadowModelView);
            setProgramUniformMatrix4ARB(Shaders.uniform_shadowModelViewInverse, false, Shaders.shadowModelViewInverse);
        }
        setProgramUniform1f(Shaders.uniform_wetness, Shaders.wetness);
        setProgramUniform1f(Shaders.uniform_eyeAltitude, Shaders.eyePosY);
        setProgramUniform2i(Shaders.uniform_eyeBrightness, Shaders.eyeBrightness & 0xFFFF, Shaders.eyeBrightness >> 16);
        setProgramUniform2i(Shaders.uniform_eyeBrightnessSmooth, Math.round(Shaders.eyeBrightnessFadeX), Math.round(Shaders.eyeBrightnessFadeY));
        setProgramUniform2i(Shaders.uniform_terrainTextureSize, Shaders.terrainTextureSize[0], Shaders.terrainTextureSize[1]);
        setProgramUniform1i(Shaders.uniform_terrainIconSize, Shaders.terrainIconSize);
        setProgramUniform1i(Shaders.uniform_isEyeInWater, Shaders.isEyeInWater);
        setProgramUniform1f(Shaders.uniform_nightVision, Shaders.nightVision);
        setProgramUniform1f(Shaders.uniform_blindness, Shaders.blindness);
        setProgramUniform1f(Shaders.uniform_screenBrightness, Shaders.mc.gameSettings.gammaSetting);
        setProgramUniform1i(Shaders.uniform_hideGUI, Shaders.mc.gameSettings.hideGUI ? 1 : 0);
        setProgramUniform1f(Shaders.uniform_centerDepthSmooth, Shaders.centerDepthSmooth);
        setProgramUniform2i(Shaders.uniform_atlasSize, Shaders.atlasSizeX, Shaders.atlasSizeY);
        if (Shaders.customUniforms != null) {
            Shaders.customUniforms.update();
        }
        checkGLError("end useProgram");
    }
    
    private static void updateAlphaBlend(final Program programOld, final Program programNew) {
        if (programOld.getAlphaState() != null) {
            GlStateManager.unlockAlpha();
        }
        if (programOld.getBlendState() != null) {
            GlStateManager.unlockBlend();
        }
        final GlAlphaState alphaNew = programNew.getAlphaState();
        if (alphaNew != null) {
            GlStateManager.lockAlpha(alphaNew);
        }
        final GlBlendState blendNew = programNew.getBlendState();
        if (blendNew != null) {
            GlStateManager.lockBlend(blendNew);
        }
    }
    
    private static void setProgramUniform1i(final ShaderUniform1i su, final int value) {
        su.setValue(value);
    }
    
    private static void setProgramUniform2i(final ShaderUniform2i su, final int i0, final int i1) {
        su.setValue(i0, i1);
    }
    
    private static void setProgramUniform1f(final ShaderUniform1f su, final float value) {
        su.setValue(value);
    }
    
    private static void setProgramUniform3f(final ShaderUniform3f su, final float f0, final float f1, final float f2) {
        su.setValue(f0, f1, f2);
    }
    
    private static void setProgramUniformMatrix4ARB(final ShaderUniformM4 su, final boolean transpose, final FloatBuffer matrix) {
        su.setValue(transpose, matrix);
    }
    
    public static int getBufferIndexFromString(final String name) {
        if (name.equals("colortex0") || name.equals("gcolor")) {
            return 0;
        }
        if (name.equals("colortex1") || name.equals("gdepth")) {
            return 1;
        }
        if (name.equals("colortex2") || name.equals("gnormal")) {
            return 2;
        }
        if (name.equals("colortex3") || name.equals("composite")) {
            return 3;
        }
        if (name.equals("colortex4") || name.equals("gaux1")) {
            return 4;
        }
        if (name.equals("colortex5") || name.equals("gaux2")) {
            return 5;
        }
        if (name.equals("colortex6") || name.equals("gaux3")) {
            return 6;
        }
        if (name.equals("colortex7") || name.equals("gaux4")) {
            return 7;
        }
        return -1;
    }
    
    private static int getTextureFormatFromString(String par) {
        par = par.trim();
        for (int i = 0; i < Shaders.formatNames.length; ++i) {
            final String name = Shaders.formatNames[i];
            if (par.equals(name)) {
                return Shaders.formatIds[i];
            }
        }
        return 0;
    }
    
    private static void setupNoiseTexture() {
        if (Shaders.noiseTexture == null && Shaders.noiseTexturePath != null) {
            Shaders.noiseTexture = loadCustomTexture(15, Shaders.noiseTexturePath);
        }
        if (Shaders.noiseTexture == null) {
            Shaders.noiseTexture = new HFNoiseTexture(Shaders.noiseTextureResolution, Shaders.noiseTextureResolution);
        }
    }
    
    private static void loadEntityDataMap() {
        Shaders.mapBlockToEntityData = new IdentityHashMap<Block, Integer>(300);
        if (Shaders.mapBlockToEntityData.isEmpty()) {
            for (final ResourceLocation key : Block.blockRegistry.getKeys()) {
                final Block block = Block.blockRegistry.getObject(key);
                final int id = Block.blockRegistry.getIDForObject(block);
                Shaders.mapBlockToEntityData.put(block, id);
            }
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Shaders.shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception ex) {}
        if (reader != null) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final Matcher m = Shaders.patternLoadEntityDataMap.matcher(line);
                    if (m.matches()) {
                        final String name = m.group(1);
                        final String value = m.group(2);
                        final int id2 = Integer.parseInt(value);
                        final Block block2 = Block.getBlockFromName(name);
                        if (block2 != null) {
                            Shaders.mapBlockToEntityData.put(block2, id2);
                        }
                        else {
                            SMCLog.warning("Unknown block name %s", name);
                        }
                    }
                    else {
                        SMCLog.warning("unmatched %s\n", line);
                    }
                }
            }
            catch (Exception e) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }
        if (reader != null) {
            try {
                reader.close();
            }
            catch (Exception ex2) {}
        }
    }
    
    private static IntBuffer fillIntBufferZero(final IntBuffer buf) {
        for (int limit = buf.limit(), i = buf.position(); i < limit; ++i) {
            buf.put(i, 0);
        }
        return buf;
    }
    
    public static void uninit() {
        if (Shaders.isShaderPackInitialized) {
            checkGLError("Shaders.uninit pre");
            for (int i = 0; i < Shaders.ProgramsAll.length; ++i) {
                final Program pi = Shaders.ProgramsAll[i];
                if (pi.getRef() != 0) {
                    ARBShaderObjects.glDeleteObjectARB(pi.getRef());
                    checkGLError("del programRef");
                }
                pi.setRef(0);
                pi.setId(0);
                pi.setDrawBufSettings(null);
                pi.setDrawBuffers(null);
                pi.setCompositeMipmapSetting(0);
            }
            Shaders.hasDeferredPrograms = false;
            if (Shaders.dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.dfb);
                Shaders.dfb = 0;
                checkGLError("del dfb");
            }
            if (Shaders.sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.sfb);
                Shaders.sfb = 0;
                checkGLError("del sfb");
            }
            if (Shaders.dfbDepthTextures != null) {
                GlStateManager.deleteTextures(Shaders.dfbDepthTextures);
                fillIntBufferZero(Shaders.dfbDepthTextures);
                checkGLError("del dfbDepthTextures");
            }
            if (Shaders.dfbColorTextures != null) {
                GlStateManager.deleteTextures(Shaders.dfbColorTextures);
                fillIntBufferZero(Shaders.dfbColorTextures);
                checkGLError("del dfbTextures");
            }
            if (Shaders.sfbDepthTextures != null) {
                GlStateManager.deleteTextures(Shaders.sfbDepthTextures);
                fillIntBufferZero(Shaders.sfbDepthTextures);
                checkGLError("del shadow depth");
            }
            if (Shaders.sfbColorTextures != null) {
                GlStateManager.deleteTextures(Shaders.sfbColorTextures);
                fillIntBufferZero(Shaders.sfbColorTextures);
                checkGLError("del shadow color");
            }
            if (Shaders.dfbDrawBuffers != null) {
                fillIntBufferZero(Shaders.dfbDrawBuffers);
            }
            if (Shaders.noiseTexture != null) {
                Shaders.noiseTexture.deleteTexture();
                Shaders.noiseTexture = null;
            }
            SMCLog.info("Uninit");
            Shaders.shadowPassInterval = 0;
            Shaders.shouldSkipDefaultShadow = false;
            Shaders.isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }
    
    public static void scheduleResize() {
        Shaders.renderDisplayHeight = 0;
    }
    
    public static void scheduleResizeShadow() {
        Shaders.needResizeShadow = true;
    }
    
    private static void resize() {
        Shaders.renderDisplayWidth = Shaders.mc.displayWidth;
        Shaders.renderDisplayHeight = Shaders.mc.displayHeight;
        Shaders.renderWidth = Math.round(Shaders.renderDisplayWidth * Shaders.configRenderResMul);
        Shaders.renderHeight = Math.round(Shaders.renderDisplayHeight * Shaders.configRenderResMul);
        setupFrameBuffer();
    }
    
    private static void resizeShadow() {
        Shaders.needResizeShadow = false;
        Shaders.shadowMapWidth = Math.round(Shaders.spShadowMapWidth * Shaders.configShadowResMul);
        Shaders.shadowMapHeight = Math.round(Shaders.spShadowMapHeight * Shaders.configShadowResMul);
        setupShadowFrameBuffer();
    }
    
    private static void setupFrameBuffer() {
        if (Shaders.dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.dfb);
            GlStateManager.deleteTextures(Shaders.dfbDepthTextures);
            GlStateManager.deleteTextures(Shaders.dfbColorTextures);
        }
        Shaders.dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)Shaders.dfbDepthTextures.clear().limit(Shaders.usedDepthBuffers));
        GL11.glGenTextures((IntBuffer)Shaders.dfbColorTextures.clear().limit(16));
        Shaders.dfbDepthTextures.position(0);
        Shaders.dfbColorTextures.position(0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
        GL20.glDrawBuffers(0);
        GL11.glReadBuffer(0);
        for (int i = 0; i < Shaders.usedDepthBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(i));
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 34891, 6409);
            GL11.glTexImage2D(3553, 0, 6402, Shaders.renderWidth, Shaders.renderHeight, 0, 6402, 5126, (FloatBuffer)null);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(Shaders.dfbDrawBuffers);
        GL11.glReadBuffer(0);
        checkGLError("FT d");
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(i));
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, Shaders.gbuffersFormat[i], Shaders.renderWidth, Shaders.renderHeight, 0, getPixelFormat(Shaders.gbuffersFormat[i]), 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getA(i), 0);
            checkGLError("FT c");
        }
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getB(i));
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, Shaders.gbuffersFormat[i], Shaders.renderWidth, Shaders.renderHeight, 0, getPixelFormat(Shaders.gbuffersFormat[i]), 33639, (ByteBuffer)null);
            checkGLError("FT ca");
        }
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status == 36058) {
            printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");
            for (int j = 0; j < Shaders.usedColorBuffers; ++j) {
                GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(j));
                GL11.glTexImage2D(3553, 0, 6408, Shaders.renderWidth, Shaders.renderHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, Shaders.dfbColorTexturesFlip.getA(j), 0);
                checkGLError("FT c");
            }
            status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (status == 36053) {
                SMCLog.info("complete");
            }
        }
        GlStateManager.bindTexture(0);
        if (status != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + status + ")");
        }
        else {
            SMCLog.info("Framebuffer created.");
        }
    }
    
    private static int getPixelFormat(final int internalFormat) {
        switch (internalFormat) {
            case 33333:
            case 33334:
            case 33339:
            case 33340:
            case 36208:
            case 36209:
            case 36226:
            case 36227: {
                return 36251;
            }
            default: {
                return 32993;
            }
        }
    }
    
    private static void setupShadowFrameBuffer() {
        if (Shaders.usedShadowDepthBuffers == 0) {
            return;
        }
        if (Shaders.sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.sfb);
            GlStateManager.deleteTextures(Shaders.sfbDepthTextures);
            GlStateManager.deleteTextures(Shaders.sfbColorTextures);
        }
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb = EXTFramebufferObject.glGenFramebuffersEXT());
        GL11.glDrawBuffer(0);
        GL11.glReadBuffer(0);
        GL11.glGenTextures((IntBuffer)Shaders.sfbDepthTextures.clear().limit(Shaders.usedShadowDepthBuffers));
        GL11.glGenTextures((IntBuffer)Shaders.sfbColorTextures.clear().limit(Shaders.usedShadowColorBuffers));
        Shaders.sfbDepthTextures.position(0);
        Shaders.sfbColorTextures.position(0);
        for (int i = 0; i < Shaders.usedShadowDepthBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(i));
            GL11.glTexParameterf(3553, 10242, 33071.0f);
            GL11.glTexParameterf(3553, 10243, 33071.0f);
            final int filter = Shaders.shadowFilterNearest[i] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, filter);
            GL11.glTexParameteri(3553, 10240, filter);
            if (Shaders.shadowHardwareFilteringEnabled[i]) {
                GL11.glTexParameteri(3553, 34892, 34894);
            }
            GL11.glTexImage2D(3553, 0, 6402, Shaders.shadowMapWidth, Shaders.shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
        checkGLError("FT sd");
        for (int i = 0; i < Shaders.usedShadowColorBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.sfbColorTextures.get(i));
            GL11.glTexParameterf(3553, 10242, 33071.0f);
            GL11.glTexParameterf(3553, 10243, 33071.0f);
            final int filter = Shaders.shadowColorFilterNearest[i] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, filter);
            GL11.glTexParameteri(3553, 10240, filter);
            GL11.glTexImage2D(3553, 0, 6408, Shaders.shadowMapWidth, Shaders.shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.sfbColorTextures.get(i), 0);
            checkGLError("FT sc");
        }
        GlStateManager.bindTexture(0);
        if (Shaders.usedShadowColorBuffers > 0) {
            GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
        }
        final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + status + ")");
        }
        else {
            SMCLog.info("Shadow framebuffer created.");
        }
    }
    
    public static void beginRender(final Minecraft minecraft, final float partialTicks, final long finishTimeNano) {
        checkGLError("pre beginRender");
        checkWorldChanged(Shaders.mc.theWorld);
        Shaders.mc = minecraft;
        Shaders.mc.mcProfiler.startSection("init");
        Shaders.entityRenderer = Shaders.mc.entityRenderer;
        if (!Shaders.isShaderPackInitialized) {
            try {
                init();
            }
            catch (IllegalStateException e) {
                if (Config.normalize(e.getMessage()).equals("Function is not supported")) {
                    printChatAndLogError("[Shaders] Error: " + e.getMessage());
                    e.printStackTrace();
                    setShaderPack("OFF");
                    return;
                }
            }
        }
        if (Shaders.mc.displayWidth != Shaders.renderDisplayWidth || Shaders.mc.displayHeight != Shaders.renderDisplayHeight) {
            resize();
        }
        if (Shaders.needResizeShadow) {
            resizeShadow();
        }
        Shaders.worldTime = Shaders.mc.theWorld.getWorldTime();
        Shaders.diffWorldTime = (Shaders.worldTime - Shaders.lastWorldTime) % 24000L;
        if (Shaders.diffWorldTime < 0L) {
            Shaders.diffWorldTime += 24000L;
        }
        Shaders.lastWorldTime = Shaders.worldTime;
        Shaders.moonPhase = Shaders.mc.theWorld.getMoonPhase();
        ++Shaders.frameCounter;
        if (Shaders.frameCounter >= 720720) {
            Shaders.frameCounter = 0;
        }
        Shaders.systemTime = System.currentTimeMillis();
        if (Shaders.lastSystemTime == 0L) {
            Shaders.lastSystemTime = Shaders.systemTime;
        }
        Shaders.diffSystemTime = Shaders.systemTime - Shaders.lastSystemTime;
        Shaders.lastSystemTime = Shaders.systemTime;
        Shaders.frameTime = Shaders.diffSystemTime / 1000.0f;
        Shaders.frameTimeCounter += Shaders.frameTime;
        Shaders.frameTimeCounter %= 3600.0f;
        Shaders.rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        final float fadeScalar = Shaders.diffSystemTime * 0.01f;
        final float temp1 = (float)Math.exp(Math.log(0.5) * fadeScalar / ((Shaders.wetness < Shaders.rainStrength) ? Shaders.drynessHalfLife : Shaders.wetnessHalfLife));
        Shaders.wetness = Shaders.wetness * temp1 + Shaders.rainStrength * (1.0f - temp1);
        final Entity renderViewEntity = Shaders.mc.getRenderViewEntity();
        if (renderViewEntity != null) {
            Shaders.isSleeping = (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping());
            Shaders.eyePosY = (float)renderViewEntity.posY * partialTicks + (float)renderViewEntity.lastTickPosY * (1.0f - partialTicks);
            Shaders.eyeBrightness = renderViewEntity.getBrightnessForRender(partialTicks);
            final float fadeScalar2 = Shaders.diffSystemTime * 0.01f;
            final float temp2 = (float)Math.exp(Math.log(0.5) * fadeScalar2 / Shaders.eyeBrightnessHalflife);
            Shaders.eyeBrightnessFadeX = Shaders.eyeBrightnessFadeX * temp2 + (Shaders.eyeBrightness & 0xFFFF) * (1.0f - temp2);
            Shaders.eyeBrightnessFadeY = Shaders.eyeBrightnessFadeY * temp2 + (Shaders.eyeBrightness >> 16) * (1.0f - temp2);
            final Block cameraBlock = ActiveRenderInfo.getBlockAtEntityViewpoint(Shaders.mc.theWorld, renderViewEntity, partialTicks);
            final Material cameraPosMaterial = cameraBlock.getMaterial();
            if (cameraPosMaterial == Material.water) {
                Shaders.isEyeInWater = 1;
            }
            else if (cameraPosMaterial == Material.lava) {
                Shaders.isEyeInWater = 2;
            }
            else {
                Shaders.isEyeInWater = 0;
            }
            if (Shaders.mc.thePlayer != null) {
                Shaders.nightVision = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    Shaders.nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(Shaders.mc.thePlayer, partialTicks);
                }
                Shaders.blindness = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.blindness)) {
                    final int blindnessTicks = Shaders.mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    Shaders.blindness = Config.limit(blindnessTicks / 20.0f, 0.0f, 1.0f);
                }
            }
            Vec3 skyColorV = Shaders.mc.theWorld.getSkyColor(renderViewEntity, partialTicks);
            skyColorV = CustomColors.getWorldSkyColor(skyColorV, Shaders.currentWorld, renderViewEntity, partialTicks);
            Shaders.skyColorR = (float)skyColorV.xCoord;
            Shaders.skyColorG = (float)skyColorV.yCoord;
            Shaders.skyColorB = (float)skyColorV.zCoord;
        }
        Shaders.isRenderingWorld = true;
        Shaders.isCompositeRendered = false;
        Shaders.isShadowPass = false;
        Shaders.isHandRenderedMain = false;
        Shaders.isHandRenderedOff = false;
        Shaders.skipRenderHandMain = false;
        Shaders.skipRenderHandOff = false;
        bindGbuffersTextures();
        Shaders.previousCameraPositionX = Shaders.cameraPositionX;
        Shaders.previousCameraPositionY = Shaders.cameraPositionY;
        Shaders.previousCameraPositionZ = Shaders.cameraPositionZ;
        Shaders.previousProjection.position(0);
        Shaders.projection.position(0);
        Shaders.previousProjection.put(Shaders.projection);
        Shaders.previousProjection.position(0);
        Shaders.projection.position(0);
        Shaders.previousModelView.position(0);
        Shaders.modelView.position(0);
        Shaders.previousModelView.put(Shaders.modelView);
        Shaders.previousModelView.position(0);
        Shaders.modelView.position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(Shaders.entityRenderer, 0, partialTicks, finishTimeNano);
        Shaders.mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getA(i), 0);
        }
        checkGLError("end beginRender");
    }
    
    private static void bindGbuffersTextures() {
        if (Shaders.usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
            if (Shaders.usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
            }
        }
        GlStateManager.setActiveTexture(33984);
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(i));
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getB(i));
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }
        GlStateManager.bindTexture(0);
        for (int i = 0; i < 4 && 4 + i < Shaders.usedColorBuffers; ++i) {
            GlStateManager.setActiveTexture(33991 + i);
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(4 + i));
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(0));
        if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(1));
            if (Shaders.usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(2));
            }
        }
        for (int i = 0; i < Shaders.usedShadowColorBuffers; ++i) {
            GlStateManager.setActiveTexture(33997 + i);
            GlStateManager.bindTexture(Shaders.sfbColorTextures.get(i));
        }
        if (Shaders.noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(Shaders.noiseTexture.getTextureId());
        }
        bindCustomTextures(Shaders.customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
    }
    
    public static void checkWorldChanged(final World world) {
        if (Shaders.currentWorld == world) {
            return;
        }
        final World oldWorld = Shaders.currentWorld;
        Shaders.currentWorld = world;
        setCameraOffset(Shaders.mc.getRenderViewEntity());
        final int dimIdOld = getDimensionId(oldWorld);
        final int dimIdNew = getDimensionId(world);
        if (dimIdNew != dimIdOld) {
            final boolean dimShadersOld = Shaders.shaderPackDimensions.contains(dimIdOld);
            final boolean dimShadersNew = Shaders.shaderPackDimensions.contains(dimIdNew);
            if (dimShadersOld || dimShadersNew) {
                uninit();
            }
        }
        Smoother.resetValues();
    }
    
    private static int getDimensionId(final World world) {
        if (world == null) {
            return Integer.MIN_VALUE;
        }
        return world.provider.getDimensionId();
    }
    
    public static void beginRenderPass(final int pass, final float partialTicks, final long finishTimeNano) {
        if (Shaders.isShadowPass) {
            return;
        }
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
        GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
        Shaders.activeDrawBuffers = null;
        ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
        useProgram(Shaders.ProgramTextured);
        checkGLError("end beginRenderPass");
    }
    
    public static void setViewport(final int vx, final int vy, final int vw, final int vh) {
        GlStateManager.colorMask(true, true, true, true);
        if (Shaders.isShadowPass) {
            GL11.glViewport(0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
        }
        else {
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
            Shaders.isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            setDrawBuffers(Shaders.drawBuffersNone);
            useProgram(Shaders.ProgramTextured);
            checkGLError("beginRenderPass");
        }
    }
    
    public static void setFogMode(final int value) {
        Shaders.fogMode = value;
        if (Shaders.fogEnabled) {
            setProgramUniform1i(Shaders.uniform_fogMode, value);
        }
    }
    
    public static void setFogColor(final float r, final float g, final float b) {
        Shaders.fogColorR = r;
        Shaders.fogColorG = g;
        Shaders.fogColorB = b;
        setProgramUniform3f(Shaders.uniform_fogColor, Shaders.fogColorR, Shaders.fogColorG, Shaders.fogColorB);
    }
    
    public static void setClearColor(final float red, final float green, final float blue, final float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
        Shaders.clearColorR = red;
        Shaders.clearColorG = green;
        Shaders.clearColorB = blue;
    }
    
    public static void clearRenderBuffer() {
        if (Shaders.isShadowPass) {
            checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL20.glDrawBuffers(Shaders.ProgramShadow.getDrawBuffers());
            checkFramebufferStatus("shadow clear");
            GL11.glClear(16640);
            checkGLError("shadow clear");
            return;
        }
        checkGLError("clear pre");
        if (Shaders.gbuffersClear[0]) {
            final Vector4f col = Shaders.gbuffersClearColor[0];
            if (col != null) {
                GL11.glClearColor(col.getX(), col.getY(), col.getZ(), col.getW());
            }
            if (Shaders.dfbColorTexturesFlip.isChanged(0)) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.dfbColorTexturesFlip.getB(0), 0);
                GL20.glDrawBuffers(36064);
                GL11.glClear(16384);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.dfbColorTexturesFlip.getA(0), 0);
            }
            GL20.glDrawBuffers(36064);
            GL11.glClear(16384);
        }
        if (Shaders.gbuffersClear[1]) {
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            final Vector4f col = Shaders.gbuffersClearColor[1];
            if (col != null) {
                GL11.glClearColor(col.getX(), col.getY(), col.getZ(), col.getW());
            }
            if (Shaders.dfbColorTexturesFlip.isChanged(1)) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, Shaders.dfbColorTexturesFlip.getB(1), 0);
                GL20.glDrawBuffers(36065);
                GL11.glClear(16384);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, Shaders.dfbColorTexturesFlip.getA(1), 0);
            }
            GL20.glDrawBuffers(36065);
            GL11.glClear(16384);
        }
        for (int i = 2; i < Shaders.usedColorBuffers; ++i) {
            if (Shaders.gbuffersClear[i]) {
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                final Vector4f col2 = Shaders.gbuffersClearColor[i];
                if (col2 != null) {
                    GL11.glClearColor(col2.getX(), col2.getY(), col2.getZ(), col2.getW());
                }
                if (Shaders.dfbColorTexturesFlip.isChanged(i)) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getB(i), 0);
                    GL20.glDrawBuffers(36064 + i);
                    GL11.glClear(16384);
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getA(i), 0);
                }
                GL20.glDrawBuffers(36064 + i);
                GL11.glClear(16384);
            }
        }
        setDrawBuffers(Shaders.dfbDrawBuffers);
        checkFramebufferStatus("clear");
        checkGLError("clear");
    }
    
    public static void setCamera(final float partialTicks) {
        final Entity viewEntity = Shaders.mc.getRenderViewEntity();
        final double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        final double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        final double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        updateCameraOffset(viewEntity);
        Shaders.cameraPositionX = x - Shaders.cameraOffsetX;
        Shaders.cameraPositionY = y;
        Shaders.cameraPositionZ = z - Shaders.cameraOffsetZ;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.projectionInverse.position(0), (FloatBuffer)Shaders.projection.position(0), Shaders.faProjectionInverse, Shaders.faProjection);
        Shaders.projection.position(0);
        Shaders.projectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.modelViewInverse.position(0), (FloatBuffer)Shaders.modelView.position(0), Shaders.faModelViewInverse, Shaders.faModelView);
        Shaders.modelView.position(0);
        Shaders.modelViewInverse.position(0);
        checkGLError("setCamera");
    }
    
    private static void updateCameraOffset(final Entity viewEntity) {
        final double adx = Math.abs(Shaders.cameraPositionX - Shaders.previousCameraPositionX);
        final double adz = Math.abs(Shaders.cameraPositionZ - Shaders.previousCameraPositionZ);
        final double apx = Math.abs(Shaders.cameraPositionX);
        final double apz = Math.abs(Shaders.cameraPositionZ);
        if (adx > 1000.0 || adz > 1000.0 || apx > 1000000.0 || apz > 1000000.0) {
            setCameraOffset(viewEntity);
        }
    }
    
    private static void setCameraOffset(final Entity viewEntity) {
        if (viewEntity == null) {
            Shaders.cameraOffsetX = 0;
            Shaders.cameraOffsetZ = 0;
            return;
        }
        Shaders.cameraOffsetX = (int)viewEntity.posX / 1000 * 1000;
        Shaders.cameraOffsetZ = (int)viewEntity.posZ / 1000 * 1000;
    }
    
    public static void setCameraShadow(final float partialTicks) {
        final Entity viewEntity = Shaders.mc.getRenderViewEntity();
        final double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        final double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        final double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        updateCameraOffset(viewEntity);
        Shaders.cameraPositionX = x - Shaders.cameraOffsetX;
        Shaders.cameraPositionY = y;
        Shaders.cameraPositionZ = z - Shaders.cameraOffsetZ;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.projectionInverse.position(0), (FloatBuffer)Shaders.projection.position(0), Shaders.faProjectionInverse, Shaders.faProjection);
        Shaders.projection.position(0);
        Shaders.projectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.modelViewInverse.position(0), (FloatBuffer)Shaders.modelView.position(0), Shaders.faModelViewInverse, Shaders.faModelView);
        Shaders.modelView.position(0);
        Shaders.modelViewInverse.position(0);
        GL11.glViewport(0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        if (Shaders.shadowMapIsOrtho) {
            GL11.glOrtho((double)(-Shaders.shadowMapHalfPlane), (double)Shaders.shadowMapHalfPlane, (double)(-Shaders.shadowMapHalfPlane), (double)Shaders.shadowMapHalfPlane, 0.05000000074505806, 256.0);
        }
        else {
            GLU.gluPerspective(Shaders.shadowMapFOV, Shaders.shadowMapWidth / (float)Shaders.shadowMapHeight, 0.05f, 256.0f);
        }
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -100.0f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        Shaders.celestialAngle = Shaders.mc.theWorld.getCelestialAngle(partialTicks);
        Shaders.sunAngle = ((Shaders.celestialAngle < 0.75f) ? (Shaders.celestialAngle + 0.25f) : (Shaders.celestialAngle - 0.75f));
        final float angle = Shaders.celestialAngle * -360.0f;
        final float angleInterval = (Shaders.shadowAngleInterval > 0.0f) ? (angle % Shaders.shadowAngleInterval - Shaders.shadowAngleInterval * 0.5f) : 0.0f;
        if (Shaders.sunAngle <= 0.5) {
            GL11.glRotatef(angle - angleInterval, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(Shaders.sunPathRotation, 1.0f, 0.0f, 0.0f);
            Shaders.shadowAngle = Shaders.sunAngle;
        }
        else {
            GL11.glRotatef(angle + 180.0f - angleInterval, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(Shaders.sunPathRotation, 1.0f, 0.0f, 0.0f);
            Shaders.shadowAngle = Shaders.sunAngle - 0.5f;
        }
        if (Shaders.shadowMapIsOrtho) {
            final float trans = Shaders.shadowIntervalSize;
            final float trans2 = trans / 2.0f;
            GL11.glTranslatef((float)x % trans - trans2, (float)y % trans - trans2, (float)z % trans - trans2);
        }
        final float raSun = Shaders.sunAngle * 6.2831855f;
        final float x2 = (float)Math.cos(raSun);
        final float y2 = (float)Math.sin(raSun);
        final float raTilt = Shaders.sunPathRotation * 6.2831855f;
        float x3 = x2;
        float y3 = y2 * (float)Math.cos(raTilt);
        float z2 = y2 * (float)Math.sin(raTilt);
        if (Shaders.sunAngle > 0.5) {
            x3 = -x3;
            y3 = -y3;
            z2 = -z2;
        }
        Shaders.shadowLightPositionVector[0] = x3;
        Shaders.shadowLightPositionVector[1] = y3;
        Shaders.shadowLightPositionVector[2] = z2;
        Shaders.shadowLightPositionVector[3] = 0.0f;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.shadowProjection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.shadowProjectionInverse.position(0), (FloatBuffer)Shaders.shadowProjection.position(0), Shaders.faShadowProjectionInverse, Shaders.faShadowProjection);
        Shaders.shadowProjection.position(0);
        Shaders.shadowProjectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.shadowModelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.shadowModelViewInverse.position(0), (FloatBuffer)Shaders.shadowModelView.position(0), Shaders.faShadowModelViewInverse, Shaders.faShadowModelView);
        Shaders.shadowModelView.position(0);
        Shaders.shadowModelViewInverse.position(0);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferProjection, false, Shaders.projection);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferProjectionInverse, false, Shaders.projectionInverse);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferPreviousProjection, false, Shaders.previousProjection);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferModelView, false, Shaders.modelView);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferModelViewInverse, false, Shaders.modelViewInverse);
        setProgramUniformMatrix4ARB(Shaders.uniform_gbufferPreviousModelView, false, Shaders.previousModelView);
        setProgramUniformMatrix4ARB(Shaders.uniform_shadowProjection, false, Shaders.shadowProjection);
        setProgramUniformMatrix4ARB(Shaders.uniform_shadowProjectionInverse, false, Shaders.shadowProjectionInverse);
        setProgramUniformMatrix4ARB(Shaders.uniform_shadowModelView, false, Shaders.shadowModelView);
        setProgramUniformMatrix4ARB(Shaders.uniform_shadowModelViewInverse, false, Shaders.shadowModelViewInverse);
        Shaders.mc.gameSettings.thirdPersonView = 1;
        checkGLError("setCamera");
    }
    
    public static void preCelestialRotate() {
        GL11.glRotatef(Shaders.sunPathRotation * 1.0f, 0.0f, 0.0f, 1.0f);
        checkGLError("preCelestialRotate");
    }
    
    public static void postCelestialRotate() {
        final FloatBuffer modelView = Shaders.tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(2982, modelView);
        modelView.get(Shaders.tempMat, 0, 16);
        SMath.multiplyMat4xVec4(Shaders.sunPosition, Shaders.tempMat, Shaders.sunPosModelView);
        SMath.multiplyMat4xVec4(Shaders.moonPosition, Shaders.tempMat, Shaders.moonPosModelView);
        System.arraycopy((Shaders.shadowAngle == Shaders.sunAngle) ? Shaders.sunPosition : Shaders.moonPosition, 0, Shaders.shadowLightPosition, 0, 3);
        setProgramUniform3f(Shaders.uniform_sunPosition, Shaders.sunPosition[0], Shaders.sunPosition[1], Shaders.sunPosition[2]);
        setProgramUniform3f(Shaders.uniform_moonPosition, Shaders.moonPosition[0], Shaders.moonPosition[1], Shaders.moonPosition[2]);
        setProgramUniform3f(Shaders.uniform_shadowLightPosition, Shaders.shadowLightPosition[0], Shaders.shadowLightPosition[1], Shaders.shadowLightPosition[2]);
        if (Shaders.customUniforms != null) {
            Shaders.customUniforms.update();
        }
        checkGLError("postCelestialRotate");
    }
    
    public static void setUpPosition() {
        final FloatBuffer modelView = Shaders.tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(2982, modelView);
        modelView.get(Shaders.tempMat, 0, 16);
        SMath.multiplyMat4xVec4(Shaders.upPosition, Shaders.tempMat, Shaders.upPosModelView);
        setProgramUniform3f(Shaders.uniform_upPosition, Shaders.upPosition[0], Shaders.upPosition[1], Shaders.upPosition[2]);
        if (Shaders.customUniforms != null) {
            Shaders.customUniforms.update();
        }
    }
    
    public static void genCompositeMipmap() {
        if (Shaders.hasGlGenMipmap) {
            for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
                if ((Shaders.activeCompositeMipmapSetting & 1 << i) != 0x0) {
                    GlStateManager.setActiveTexture(33984 + Shaders.colorTextureImageUnit[i]);
                    GL11.glTexParameteri(3553, 10241, 9987);
                    GL30.glGenerateMipmap(3553);
                }
            }
            GlStateManager.setActiveTexture(33984);
        }
    }
    
    public static void drawComposite() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawCompositeQuad();
        final int countInstances = Shaders.activeProgram.getCountInstances();
        if (countInstances > 1) {
            for (int i = 1; i < countInstances; ++i) {
                Shaders.uniform_instanceId.setValue(i);
                drawCompositeQuad();
            }
            Shaders.uniform_instanceId.setValue(0);
        }
    }
    
    private static void drawCompositeQuad() {
        if (!canRenderQuads()) {
            GL11.glBegin(5);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(0.0f, 0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(1.0f, 0.0f, 0.0f);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(0.0f, 1.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(1.0f, 1.0f, 0.0f);
            GL11.glEnd();
            return;
        }
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glEnd();
    }
    
    public static void renderDeferred() {
        if (Shaders.isShadowPass) {
            return;
        }
        boolean buffersChanged = checkBufferFlip(Shaders.ProgramDeferredPre);
        if (Shaders.hasDeferredPrograms) {
            checkGLError("pre-render Deferred");
            renderComposites(Shaders.ProgramsDeferred, false);
            buffersChanged = true;
        }
        if (buffersChanged) {
            bindGbuffersTextures();
            for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getA(i), 0);
            }
            if (Shaders.ProgramWater.getDrawBuffers() != null) {
                setDrawBuffers(Shaders.ProgramWater.getDrawBuffers());
            }
            else {
                setDrawBuffers(Shaders.dfbDrawBuffers);
            }
            GlStateManager.setActiveTexture(33984);
            Shaders.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        }
    }
    
    public static void renderCompositeFinal() {
        if (Shaders.isShadowPass) {
            return;
        }
        checkBufferFlip(Shaders.ProgramCompositePre);
        checkGLError("pre-render CompositeFinal");
        renderComposites(Shaders.ProgramsComposite, true);
    }
    
    private static boolean checkBufferFlip(final Program program) {
        boolean flipped = false;
        final Boolean[] buffersFlip = program.getBuffersFlip();
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            if (Config.isTrue(buffersFlip[i])) {
                Shaders.dfbColorTexturesFlip.flip(i);
                flipped = true;
            }
        }
        return flipped;
    }
    
    private static void renderComposites(final Program[] ps, final boolean renderFinal) {
        if (Shaders.isShadowPass) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        if (Shaders.usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
            if (Shaders.usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
            }
        }
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            GlStateManager.setActiveTexture(33984 + Shaders.colorTextureImageUnit[i]);
            GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(i));
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(0));
        if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(1));
            if (Shaders.usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(Shaders.dfbDepthTextures.get(2));
            }
        }
        for (int i = 0; i < Shaders.usedShadowColorBuffers; ++i) {
            GlStateManager.setActiveTexture(33997 + i);
            GlStateManager.bindTexture(Shaders.sfbColorTextures.get(i));
        }
        if (Shaders.noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(Shaders.noiseTexture.getTextureId());
        }
        if (renderFinal) {
            bindCustomTextures(Shaders.customTexturesComposite);
        }
        else {
            bindCustomTextures(Shaders.customTexturesDeferred);
        }
        GlStateManager.setActiveTexture(33984);
        for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesFlip.getB(i), 0);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(Shaders.dfbDrawBuffers);
        checkGLError("pre-composite");
        for (int cp = 0; cp < ps.length; ++cp) {
            final Program program = ps[cp];
            if (program.getId() != 0) {
                useProgram(program);
                checkGLError(program.getName());
                if (Shaders.activeCompositeMipmapSetting != 0) {
                    genCompositeMipmap();
                }
                preDrawComposite();
                drawComposite();
                postDrawComposite();
                for (int j = 0; j < Shaders.usedColorBuffers; ++j) {
                    if (program.getToggleColorTextures()[j]) {
                        Shaders.dfbColorTexturesFlip.flip(j);
                        GlStateManager.setActiveTexture(33984 + Shaders.colorTextureImageUnit[j]);
                        GlStateManager.bindTexture(Shaders.dfbColorTexturesFlip.getA(j));
                        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, Shaders.dfbColorTexturesFlip.getB(j), 0);
                    }
                }
                GlStateManager.setActiveTexture(33984);
            }
        }
        checkGLError("composite");
        if (renderFinal) {
            renderFinal();
            Shaders.isCompositeRendered = true;
        }
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        useProgram(Shaders.ProgramNone);
    }
    
    private static void preDrawComposite() {
        final RenderScale rs = Shaders.activeProgram.getRenderScale();
        if (rs != null) {
            final int x = (int)(Shaders.renderWidth * rs.getOffsetX());
            final int y = (int)(Shaders.renderHeight * rs.getOffsetY());
            final int w = (int)(Shaders.renderWidth * rs.getScale());
            final int h = (int)(Shaders.renderHeight * rs.getScale());
            GL11.glViewport(x, y, w, h);
        }
    }
    
    private static void postDrawComposite() {
        final RenderScale rs = Shaders.activeProgram.getRenderScale();
        if (rs != null) {
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
        }
    }
    
    private static void renderFinal() {
        Shaders.isRenderingDfb = false;
        Shaders.mc.getFramebuffer().bindFramebuffer(true);
        OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, Shaders.mc.getFramebuffer().framebufferTexture, 0);
        GL11.glViewport(0, 0, Shaders.mc.displayWidth, Shaders.mc.displayHeight);
        if (EntityRenderer.anaglyphEnable) {
            final boolean maskR = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(maskR, !maskR, !maskR, true);
        }
        GlStateManager.depthMask(true);
        GL11.glClearColor(Shaders.clearColorR, Shaders.clearColorG, Shaders.clearColorB, 1.0f);
        GL11.glClear(16640);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        checkGLError("pre-final");
        useProgram(Shaders.ProgramFinal);
        checkGLError("final");
        if (Shaders.activeCompositeMipmapSetting != 0) {
            genCompositeMipmap();
        }
        drawComposite();
        checkGLError("renderCompositeFinal");
    }
    
    public static void endRender() {
        if (Shaders.isShadowPass) {
            checkGLError("shadow endRender");
            return;
        }
        if (!Shaders.isCompositeRendered) {
            renderCompositeFinal();
        }
        Shaders.isRenderingWorld = false;
        GlStateManager.colorMask(true, true, true, true);
        useProgram(Shaders.ProgramNone);
        RenderHelper.disableStandardItemLighting();
        checkGLError("endRender end");
    }
    
    public static void beginSky() {
        Shaders.isRenderingSky = true;
        Shaders.fogEnabled = true;
        setDrawBuffers(Shaders.dfbDrawBuffers);
        useProgram(Shaders.ProgramSkyTextured);
        pushEntity(-2, 0);
    }
    
    public static void setSkyColor(final Vec3 v3color) {
        Shaders.skyColorR = (float)v3color.xCoord;
        Shaders.skyColorG = (float)v3color.yCoord;
        Shaders.skyColorB = (float)v3color.zCoord;
        setProgramUniform3f(Shaders.uniform_skyColor, Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
    }
    
    public static void drawHorizon() {
        final WorldRenderer tess = Tessellator.getInstance().getWorldRenderer();
        final float farDistance = (float)(Shaders.mc.gameSettings.renderDistanceChunks * 16);
        final double xzq = farDistance * 0.9238;
        final double xzp = farDistance * 0.3826;
        final double xzn = -xzp;
        final double xzm = -xzq;
        final double top = 16.0;
        final double bot = -Shaders.cameraPositionY;
        tess.begin(7, DefaultVertexFormats.POSITION);
        tess.pos(xzn, bot, xzm).endVertex();
        tess.pos(xzn, top, xzm).endVertex();
        tess.pos(xzm, top, xzn).endVertex();
        tess.pos(xzm, bot, xzn).endVertex();
        tess.pos(xzm, bot, xzn).endVertex();
        tess.pos(xzm, top, xzn).endVertex();
        tess.pos(xzm, top, xzp).endVertex();
        tess.pos(xzm, bot, xzp).endVertex();
        tess.pos(xzm, bot, xzp).endVertex();
        tess.pos(xzm, top, xzp).endVertex();
        tess.pos(xzn, top, xzq).endVertex();
        tess.pos(xzn, bot, xzq).endVertex();
        tess.pos(xzn, bot, xzq).endVertex();
        tess.pos(xzn, top, xzq).endVertex();
        tess.pos(xzp, top, xzq).endVertex();
        tess.pos(xzp, bot, xzq).endVertex();
        tess.pos(xzp, bot, xzq).endVertex();
        tess.pos(xzp, top, xzq).endVertex();
        tess.pos(xzq, top, xzp).endVertex();
        tess.pos(xzq, bot, xzp).endVertex();
        tess.pos(xzq, bot, xzp).endVertex();
        tess.pos(xzq, top, xzp).endVertex();
        tess.pos(xzq, top, xzn).endVertex();
        tess.pos(xzq, bot, xzn).endVertex();
        tess.pos(xzq, bot, xzn).endVertex();
        tess.pos(xzq, top, xzn).endVertex();
        tess.pos(xzp, top, xzm).endVertex();
        tess.pos(xzp, bot, xzm).endVertex();
        tess.pos(xzp, bot, xzm).endVertex();
        tess.pos(xzp, top, xzm).endVertex();
        tess.pos(xzn, top, xzm).endVertex();
        tess.pos(xzn, bot, xzm).endVertex();
        tess.pos(xzm, bot, xzm).endVertex();
        tess.pos(xzm, bot, xzq).endVertex();
        tess.pos(xzq, bot, xzq).endVertex();
        tess.pos(xzq, bot, xzm).endVertex();
        Tessellator.getInstance().draw();
    }
    
    public static void preSkyList() {
        setUpPosition();
        GL11.glColor3f(Shaders.fogColorR, Shaders.fogColorG, Shaders.fogColorB);
        drawHorizon();
        GL11.glColor3f(Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
    }
    
    public static void endSky() {
        Shaders.isRenderingSky = false;
        setDrawBuffers(Shaders.dfbDrawBuffers);
        useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
        popEntity();
    }
    
    public static void beginUpdateChunks() {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");
        if (!Shaders.isShadowPass) {
            useProgram(Shaders.ProgramTerrain);
        }
        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }
    
    public static void endUpdateChunks() {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");
        if (!Shaders.isShadowPass) {
            useProgram(Shaders.ProgramTerrain);
        }
        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }
    
    public static boolean shouldRenderClouds(final GameSettings gs) {
        if (!Shaders.shaderPackLoaded) {
            return true;
        }
        checkGLError("shouldRenderClouds");
        return Shaders.isShadowPass ? Shaders.configCloudShadow : (gs.clouds > 0);
    }
    
    public static void beginClouds() {
        Shaders.fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(Shaders.ProgramClouds);
    }
    
    public static void endClouds() {
        disableFog();
        popEntity();
        useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
    }
    
    public static void beginEntities() {
        if (Shaders.isRenderingWorld) {
            useProgram(Shaders.ProgramEntities);
        }
    }
    
    public static void nextEntity(final Entity entity) {
        if (Shaders.isRenderingWorld) {
            useProgram(Shaders.ProgramEntities);
            setEntityId(entity);
        }
    }
    
    public static void setEntityId(final Entity entity) {
        if (Shaders.uniform_entityId.isDefined()) {
            int id = EntityUtils.getEntityIdByClass(entity);
            final int idAlias = EntityAliases.getEntityAliasId(id);
            if (idAlias >= 0) {
                id = idAlias;
            }
            Shaders.uniform_entityId.setValue(id);
        }
    }
    
    public static void beginSpiderEyes() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.ProgramSpiderEyes.getId() != Shaders.ProgramNone.getId()) {
                useProgram(Shaders.ProgramSpiderEyes);
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(516, 0.0f);
                GlStateManager.blendFunc(770, 771);
            }
        }
    }
    
    public static void endSpiderEyes() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.ProgramSpiderEyes.getId() != Shaders.ProgramNone.getId()) {
                useProgram(Shaders.ProgramEntities);
                GlStateManager.disableAlpha();
            }
        }
    }
    
    public static void endEntities() {
        if (Shaders.isRenderingWorld) {
            setEntityId(null);
            useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
        }
    }
    
    public static void beginEntitiesGlowing() {
        if (Shaders.isRenderingWorld) {
            Shaders.isEntitiesGlowing = true;
        }
    }
    
    public static void endEntitiesGlowing() {
        if (Shaders.isRenderingWorld) {
            Shaders.isEntitiesGlowing = false;
        }
    }
    
    public static void setEntityColor(final float r, final float g, final float b, final float a) {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass) {
            Shaders.uniform_entityColor.setValue(r, g, b, a);
        }
    }
    
    public static void beginLivingDamage() {
        if (Shaders.isRenderingWorld) {
            ShadersTex.bindTexture(Shaders.defaultTexture);
            if (!Shaders.isShadowPass) {
                setDrawBuffers(Shaders.drawBuffersColorAtt0);
            }
        }
    }
    
    public static void endLivingDamage() {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass) {
            setDrawBuffers(Shaders.ProgramEntities.getDrawBuffers());
        }
    }
    
    public static void beginBlockEntities() {
        if (Shaders.isRenderingWorld) {
            checkGLError("beginBlockEntities");
            useProgram(Shaders.ProgramBlock);
        }
    }
    
    public static void nextBlockEntity(final TileEntity tileEntity) {
        if (Shaders.isRenderingWorld) {
            checkGLError("nextBlockEntity");
            useProgram(Shaders.ProgramBlock);
            setBlockEntityId(tileEntity);
        }
    }
    
    public static void setBlockEntityId(final TileEntity tileEntity) {
        if (Shaders.uniform_blockEntityId.isDefined()) {
            final int blockId = getBlockEntityId(tileEntity);
            Shaders.uniform_blockEntityId.setValue(blockId);
        }
    }
    
    private static int getBlockEntityId(final TileEntity tileEntity) {
        if (tileEntity == null) {
            return -1;
        }
        final Block block = tileEntity.getBlockType();
        if (block == null) {
            return 0;
        }
        int blockId = Block.getIdFromBlock(block);
        final int metadata = tileEntity.getBlockMetadata();
        final int blockAliasId = BlockAliases.getBlockAliasId(blockId, metadata);
        if (blockAliasId >= 0) {
            blockId = blockAliasId;
        }
        return blockId;
    }
    
    public static void endBlockEntities() {
        if (Shaders.isRenderingWorld) {
            checkGLError("endBlockEntities");
            setBlockEntityId(null);
            useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
            ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
        }
    }
    
    public static void beginLitParticles() {
        useProgram(Shaders.ProgramTexturedLit);
    }
    
    public static void beginParticles() {
        useProgram(Shaders.ProgramTextured);
    }
    
    public static void endParticles() {
        useProgram(Shaders.ProgramTexturedLit);
    }
    
    public static void readCenterDepth() {
        if (!Shaders.isShadowPass && Shaders.centerDepthSmoothEnabled) {
            Shaders.tempDirectFloatBuffer.clear();
            GL11.glReadPixels(Shaders.renderWidth / 2, Shaders.renderHeight / 2, 1, 1, 6402, 5126, Shaders.tempDirectFloatBuffer);
            Shaders.centerDepth = Shaders.tempDirectFloatBuffer.get(0);
            final float fadeScalar = Shaders.diffSystemTime * 0.01f;
            final float fadeFactor = (float)Math.exp(Math.log(0.5) * fadeScalar / Shaders.centerDepthSmoothHalflife);
            Shaders.centerDepthSmooth = Shaders.centerDepthSmooth * fadeFactor + Shaders.centerDepth * (1.0f - fadeFactor);
        }
    }
    
    public static void beginWeather() {
        if (!Shaders.isShadowPass) {
            if (Shaders.usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
                GlStateManager.setActiveTexture(33984);
            }
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            useProgram(Shaders.ProgramWeather);
        }
    }
    
    public static void endWeather() {
        GlStateManager.disableBlend();
        useProgram(Shaders.ProgramTexturedLit);
    }
    
    public static void preWater() {
        if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
            checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }
        ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
    }
    
    public static void beginWater() {
        if (Shaders.isRenderingWorld) {
            if (!Shaders.isShadowPass) {
                renderDeferred();
                useProgram(Shaders.ProgramWater);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            }
            else {
                GlStateManager.depthMask(true);
            }
        }
    }
    
    public static void endWater() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.isShadowPass) {}
            useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
        }
    }
    
    public static void applyHandDepth() {
        if (Shaders.configHandDepthMul != 1.0) {
            GL11.glScaled(1.0, 1.0, (double)Shaders.configHandDepthMul);
        }
    }
    
    public static void beginHand(final boolean translucent) {
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5888);
        if (translucent) {
            useProgram(Shaders.ProgramHandWater);
        }
        else {
            useProgram(Shaders.ProgramHand);
        }
        checkGLError("beginHand");
        checkFramebufferStatus("beginHand");
    }
    
    public static void endHand() {
        checkGLError("pre endHand");
        checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        checkGLError("endHand");
    }
    
    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }
    
    public static void endFPOverlay() {
    }
    
    public static void glEnableWrapper(final int cap) {
        GL11.glEnable(cap);
        if (cap == 3553) {
            enableTexture2D();
        }
        else if (cap == 2912) {
            enableFog();
        }
    }
    
    public static void glDisableWrapper(final int cap) {
        GL11.glDisable(cap);
        if (cap == 3553) {
            disableTexture2D();
        }
        else if (cap == 2912) {
            disableFog();
        }
    }
    
    public static void sglEnableT2D(final int cap) {
        GL11.glEnable(cap);
        enableTexture2D();
    }
    
    public static void sglDisableT2D(final int cap) {
        GL11.glDisable(cap);
        disableTexture2D();
    }
    
    public static void sglEnableFog(final int cap) {
        GL11.glEnable(cap);
        enableFog();
    }
    
    public static void sglDisableFog(final int cap) {
        GL11.glDisable(cap);
        disableFog();
    }
    
    public static void enableTexture2D() {
        if (Shaders.isRenderingSky) {
            useProgram(Shaders.ProgramSkyTextured);
        }
        else if (Shaders.activeProgram == Shaders.ProgramBasic) {
            useProgram(Shaders.lightmapEnabled ? Shaders.ProgramTexturedLit : Shaders.ProgramTextured);
        }
    }
    
    public static void disableTexture2D() {
        if (Shaders.isRenderingSky) {
            useProgram(Shaders.ProgramSkyBasic);
        }
        else if (Shaders.activeProgram == Shaders.ProgramTextured || Shaders.activeProgram == Shaders.ProgramTexturedLit) {
            useProgram(Shaders.ProgramBasic);
        }
    }
    
    public static void pushProgram() {
        Shaders.programStack.push(Shaders.activeProgram);
    }
    
    public static void popProgram() {
        final Program program = Shaders.programStack.pop();
        useProgram(program);
    }
    
    public static void beginLeash() {
        pushProgram();
        useProgram(Shaders.ProgramBasic);
    }
    
    public static void endLeash() {
        popProgram();
    }
    
    public static void enableFog() {
        Shaders.fogEnabled = true;
        setProgramUniform1i(Shaders.uniform_fogMode, Shaders.fogMode);
        setProgramUniform1f(Shaders.uniform_fogDensity, Shaders.fogDensity);
    }
    
    public static void disableFog() {
        Shaders.fogEnabled = false;
        setProgramUniform1i(Shaders.uniform_fogMode, 0);
    }
    
    public static void setFogDensity(final float value) {
        Shaders.fogDensity = value;
        if (Shaders.fogEnabled) {
            setProgramUniform1f(Shaders.uniform_fogDensity, value);
        }
    }
    
    public static void sglFogi(final int pname, final int name) {
        GL11.glFogi(pname, name);
        if (pname == 2917) {
            Shaders.fogMode = name;
            if (Shaders.fogEnabled) {
                setProgramUniform1i(Shaders.uniform_fogMode, Shaders.fogMode);
            }
        }
    }
    
    public static void enableLightmap() {
        Shaders.lightmapEnabled = true;
        if (Shaders.activeProgram == Shaders.ProgramTextured) {
            useProgram(Shaders.ProgramTexturedLit);
        }
    }
    
    public static void disableLightmap() {
        Shaders.lightmapEnabled = false;
        if (Shaders.activeProgram == Shaders.ProgramTexturedLit) {
            useProgram(Shaders.ProgramTextured);
        }
    }
    
    public static int getEntityData() {
        return Shaders.entityData[Shaders.entityDataIndex * 2];
    }
    
    public static int getEntityData2() {
        return Shaders.entityData[Shaders.entityDataIndex * 2 + 1];
    }
    
    public static int setEntityData1(final int data1) {
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((Shaders.entityData[Shaders.entityDataIndex * 2] & 0xFFFF) | data1 << 16);
        return data1;
    }
    
    public static int setEntityData2(final int data2) {
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = ((Shaders.entityData[Shaders.entityDataIndex * 2 + 1] & 0xFFFF0000) | (data2 & 0xFFFF));
        return data2;
    }
    
    public static void pushEntity(final int data0, final int data1) {
        ++Shaders.entityDataIndex;
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((data0 & 0xFFFF) | data1 << 16);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void pushEntity(final int data0) {
        ++Shaders.entityDataIndex;
        Shaders.entityData[Shaders.entityDataIndex * 2] = (data0 & 0xFFFF);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void pushEntity(final Block block) {
        ++Shaders.entityDataIndex;
        final int blockRenderType = block.getRenderType();
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((Block.blockRegistry.getIDForObject(block) & 0xFFFF) | blockRenderType << 16);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void popEntity() {
        Shaders.entityData[Shaders.entityDataIndex * 2] = 0;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
        --Shaders.entityDataIndex;
    }
    
    public static void mcProfilerEndSection() {
        Shaders.mc.mcProfiler.endSection();
    }
    
    public static String getShaderPackName() {
        if (Shaders.shaderPack == null) {
            return null;
        }
        if (Shaders.shaderPack instanceof ShaderPackNone) {
            return null;
        }
        return Shaders.shaderPack.getName();
    }
    
    public static InputStream getShaderPackResourceStream(final String path) {
        if (Shaders.shaderPack == null) {
            return null;
        }
        return Shaders.shaderPack.getResourceAsStream(path);
    }
    
    public static void nextAntialiasingLevel(final boolean forward) {
        if (forward) {
            Shaders.configAntialiasingLevel += 2;
            if (Shaders.configAntialiasingLevel > 4) {
                Shaders.configAntialiasingLevel = 0;
            }
        }
        else {
            Shaders.configAntialiasingLevel -= 2;
            if (Shaders.configAntialiasingLevel < 0) {
                Shaders.configAntialiasingLevel = 4;
            }
        }
        Shaders.configAntialiasingLevel = Shaders.configAntialiasingLevel / 2 * 2;
        Shaders.configAntialiasingLevel = Config.limit(Shaders.configAntialiasingLevel, 0, 4);
    }
    
    public static void checkShadersModInstalled() {
        try {
            final Class cls = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable e) {
            return;
        }
        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }
    
    public static void resourcesReloaded() {
        loadShaderPackResources();
        if (Shaders.shaderPackLoaded) {
            BlockAliases.resourcesReloaded();
            ItemAliases.resourcesReloaded();
            EntityAliases.resourcesReloaded();
        }
    }
    
    private static void loadShaderPackResources() {
        Shaders.shaderPackResources = new HashMap<String, String>();
        if (!Shaders.shaderPackLoaded) {
            return;
        }
        final List<String> listFiles = new ArrayList<String>();
        final String PREFIX = "/shaders/lang/";
        final String EN_US = "en_US";
        final String SUFFIX = ".lang";
        listFiles.add(PREFIX + EN_US + SUFFIX);
        if (!Config.getGameSettings().language.equals(EN_US)) {
            listFiles.add(PREFIX + Config.getGameSettings().language + SUFFIX);
        }
        try {
            for (final String file : listFiles) {
                final InputStream in = Shaders.shaderPack.getResourceAsStream(file);
                if (in == null) {
                    continue;
                }
                final Properties props = new PropertiesOrdered();
                Lang.loadLocaleData(in, props);
                in.close();
                final Set keys = props.keySet();
                for (final Object o  : keys) {
                	String key = (String) o;
                	
                    final String value = props.getProperty(key);
                    Shaders.shaderPackResources.put(key, value);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String translate(final String key, final String def) {
        final String str = Shaders.shaderPackResources.get(key);
        if (str == null) {
            return def;
        }
        return str;
    }
    
    public static boolean isProgramPath(String path) {
        if (path == null) {
            return false;
        }
        if (path.length() <= 0) {
            return false;
        }
        final int pos = path.lastIndexOf("/");
        if (pos >= 0) {
            path = path.substring(pos + 1);
        }
        final Program p = getProgram(path);
        return p != null;
    }
    
    public static Program getProgram(final String name) {
        return Shaders.programs.getProgram(name);
    }
    
    public static void setItemToRenderMain(final ItemStack itemToRenderMain) {
        Shaders.itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
    }
    
    public static void setItemToRenderOff(final ItemStack itemToRenderOff) {
        Shaders.itemToRenderOffTranslucent = isTranslucentBlock(itemToRenderOff);
    }
    
    public static boolean isItemToRenderMainTranslucent() {
        return Shaders.itemToRenderMainTranslucent;
    }
    
    public static boolean isItemToRenderOffTranslucent() {
        return Shaders.itemToRenderOffTranslucent;
    }
    
    public static boolean isBothHandsRendered() {
        return Shaders.isHandRenderedMain && Shaders.isHandRenderedOff;
    }
    
    private static boolean isTranslucentBlock(final ItemStack stack) {
        if (stack == null) {
            return false;
        }
        final Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        final ItemBlock itemBlock = (ItemBlock)item;
        final Block block = itemBlock.getBlock();
        if (block == null) {
            return false;
        }
        final EnumWorldBlockLayer blockRenderLayer = block.getBlockLayer();
        return blockRenderLayer == EnumWorldBlockLayer.TRANSLUCENT;
    }
    
    public static boolean isSkipRenderHand() {
        return Shaders.skipRenderHandMain;
    }
    
    public static boolean isRenderBothHands() {
        return !Shaders.skipRenderHandMain && !Shaders.skipRenderHandOff;
    }
    
    public static void setSkipRenderHands(final boolean skipMain, final boolean skipOff) {
        Shaders.skipRenderHandMain = skipMain;
        Shaders.skipRenderHandOff = skipOff;
    }
    
    public static void setHandsRendered(final boolean handMain, final boolean handOff) {
        Shaders.isHandRenderedMain = handMain;
        Shaders.isHandRenderedOff = handOff;
    }
    
    public static boolean isHandRenderedMain() {
        return Shaders.isHandRenderedMain;
    }
    
    public static boolean isHandRenderedOff() {
        return Shaders.isHandRenderedOff;
    }
    
    public static float getShadowRenderDistance() {
        if (Shaders.shadowDistanceRenderMul < 0.0f) {
            return -1.0f;
        }
        return Shaders.shadowMapHalfPlane * Shaders.shadowDistanceRenderMul;
    }
    
    public static void setRenderingFirstPersonHand(final boolean flag) {
        Shaders.isRenderingFirstPersonHand = flag;
    }
    
    public static boolean isRenderingFirstPersonHand() {
        return Shaders.isRenderingFirstPersonHand;
    }
    
    public static void beginBeacon() {
        if (Shaders.isRenderingWorld) {
            useProgram(Shaders.ProgramBeaconBeam);
        }
    }
    
    public static void endBeacon() {
        if (Shaders.isRenderingWorld) {
            useProgram(Shaders.ProgramBlock);
        }
    }
    
    public static World getCurrentWorld() {
        return Shaders.currentWorld;
    }
    
    public static BlockPos getCameraPosition() {
        return new BlockPos(Shaders.cameraPositionX, Shaders.cameraPositionY, Shaders.cameraPositionZ);
    }
    
    public static boolean isCustomUniforms() {
        return Shaders.customUniforms != null;
    }
    
    public static boolean canRenderQuads() {
        return !Shaders.hasGeometryShaders || Shaders.capabilities.GL_NV_geometry_shader4;
    }
    
    static {
        Shaders.isInitializedOnce = false;
        Shaders.isShaderPackInitialized = false;
        Shaders.hasGlGenMipmap = false;
        Shaders.countResetDisplayLists = 0;
        Shaders.renderDisplayWidth = 0;
        Shaders.renderDisplayHeight = 0;
        Shaders.renderWidth = 0;
        Shaders.renderHeight = 0;
        Shaders.isRenderingWorld = false;
        Shaders.isRenderingSky = false;
        Shaders.isCompositeRendered = false;
        Shaders.isRenderingDfb = false;
        Shaders.isShadowPass = false;
        Shaders.isEntitiesGlowing = false;
        Shaders.renderItemKeepDepthMask = false;
        Shaders.itemToRenderMainTranslucent = false;
        Shaders.itemToRenderOffTranslucent = false;
        Shaders.sunPosition = new float[4];
        Shaders.moonPosition = new float[4];
        Shaders.shadowLightPosition = new float[4];
        Shaders.upPosition = new float[4];
        Shaders.shadowLightPositionVector = new float[4];
        Shaders.upPosModelView = new float[] { 0.0f, 100.0f, 0.0f, 0.0f };
        Shaders.sunPosModelView = new float[] { 0.0f, 100.0f, 0.0f, 0.0f };
        Shaders.moonPosModelView = new float[] { 0.0f, -100.0f, 0.0f, 0.0f };
        Shaders.tempMat = new float[16];
        Shaders.worldTime = 0L;
        Shaders.lastWorldTime = 0L;
        Shaders.diffWorldTime = 0L;
        Shaders.celestialAngle = 0.0f;
        Shaders.sunAngle = 0.0f;
        Shaders.shadowAngle = 0.0f;
        Shaders.moonPhase = 0;
        Shaders.systemTime = 0L;
        Shaders.lastSystemTime = 0L;
        Shaders.diffSystemTime = 0L;
        Shaders.frameCounter = 0;
        Shaders.frameTime = 0.0f;
        Shaders.frameTimeCounter = 0.0f;
        Shaders.systemTimeInt32 = 0;
        Shaders.rainStrength = 0.0f;
        Shaders.wetness = 0.0f;
        Shaders.wetnessHalfLife = 600.0f;
        Shaders.drynessHalfLife = 200.0f;
        Shaders.eyeBrightnessHalflife = 10.0f;
        Shaders.usewetness = false;
        Shaders.isEyeInWater = 0;
        Shaders.eyeBrightness = 0;
        Shaders.eyeBrightnessFadeX = 0.0f;
        Shaders.eyeBrightnessFadeY = 0.0f;
        Shaders.eyePosY = 0.0f;
        Shaders.centerDepth = 0.0f;
        Shaders.centerDepthSmooth = 0.0f;
        Shaders.centerDepthSmoothHalflife = 1.0f;
        Shaders.centerDepthSmoothEnabled = false;
        Shaders.superSamplingLevel = 1;
        Shaders.nightVision = 0.0f;
        Shaders.blindness = 0.0f;
        Shaders.lightmapEnabled = false;
        Shaders.fogEnabled = true;
        Shaders.entityAttrib = 10;
        Shaders.midTexCoordAttrib = 11;
        Shaders.tangentAttrib = 12;
        Shaders.useEntityAttrib = false;
        Shaders.useMidTexCoordAttrib = false;
        Shaders.useTangentAttrib = false;
        Shaders.progUseEntityAttrib = false;
        Shaders.progUseMidTexCoordAttrib = false;
        Shaders.progUseTangentAttrib = false;
        Shaders.progArbGeometryShader4 = false;
        Shaders.progMaxVerticesOut = 3;
        Shaders.hasGeometryShaders = false;
        Shaders.atlasSizeX = 0;
        Shaders.atlasSizeY = 0;
        Shaders.shaderUniforms = new ShaderUniforms();
        Shaders.uniform_entityColor = Shaders.shaderUniforms.make4f("entityColor");
        Shaders.uniform_entityId = Shaders.shaderUniforms.make1i("entityId");
        Shaders.uniform_blockEntityId = Shaders.shaderUniforms.make1i("blockEntityId");
        Shaders.uniform_texture = Shaders.shaderUniforms.make1i("texture");
        Shaders.uniform_lightmap = Shaders.shaderUniforms.make1i("lightmap");
        Shaders.uniform_normals = Shaders.shaderUniforms.make1i("normals");
        Shaders.uniform_specular = Shaders.shaderUniforms.make1i("specular");
        Shaders.uniform_shadow = Shaders.shaderUniforms.make1i("shadow");
        Shaders.uniform_watershadow = Shaders.shaderUniforms.make1i("watershadow");
        Shaders.uniform_shadowtex0 = Shaders.shaderUniforms.make1i("shadowtex0");
        Shaders.uniform_shadowtex1 = Shaders.shaderUniforms.make1i("shadowtex1");
        Shaders.uniform_depthtex0 = Shaders.shaderUniforms.make1i("depthtex0");
        Shaders.uniform_depthtex1 = Shaders.shaderUniforms.make1i("depthtex1");
        Shaders.uniform_shadowcolor = Shaders.shaderUniforms.make1i("shadowcolor");
        Shaders.uniform_shadowcolor0 = Shaders.shaderUniforms.make1i("shadowcolor0");
        Shaders.uniform_shadowcolor1 = Shaders.shaderUniforms.make1i("shadowcolor1");
        Shaders.uniform_noisetex = Shaders.shaderUniforms.make1i("noisetex");
        Shaders.uniform_gcolor = Shaders.shaderUniforms.make1i("gcolor");
        Shaders.uniform_gdepth = Shaders.shaderUniforms.make1i("gdepth");
        Shaders.uniform_gnormal = Shaders.shaderUniforms.make1i("gnormal");
        Shaders.uniform_composite = Shaders.shaderUniforms.make1i("composite");
        Shaders.uniform_gaux1 = Shaders.shaderUniforms.make1i("gaux1");
        Shaders.uniform_gaux2 = Shaders.shaderUniforms.make1i("gaux2");
        Shaders.uniform_gaux3 = Shaders.shaderUniforms.make1i("gaux3");
        Shaders.uniform_gaux4 = Shaders.shaderUniforms.make1i("gaux4");
        Shaders.uniform_colortex0 = Shaders.shaderUniforms.make1i("colortex0");
        Shaders.uniform_colortex1 = Shaders.shaderUniforms.make1i("colortex1");
        Shaders.uniform_colortex2 = Shaders.shaderUniforms.make1i("colortex2");
        Shaders.uniform_colortex3 = Shaders.shaderUniforms.make1i("colortex3");
        Shaders.uniform_colortex4 = Shaders.shaderUniforms.make1i("colortex4");
        Shaders.uniform_colortex5 = Shaders.shaderUniforms.make1i("colortex5");
        Shaders.uniform_colortex6 = Shaders.shaderUniforms.make1i("colortex6");
        Shaders.uniform_colortex7 = Shaders.shaderUniforms.make1i("colortex7");
        Shaders.uniform_gdepthtex = Shaders.shaderUniforms.make1i("gdepthtex");
        Shaders.uniform_depthtex2 = Shaders.shaderUniforms.make1i("depthtex2");
        Shaders.uniform_tex = Shaders.shaderUniforms.make1i("tex");
        Shaders.uniform_heldItemId = Shaders.shaderUniforms.make1i("heldItemId");
        Shaders.uniform_heldBlockLightValue = Shaders.shaderUniforms.make1i("heldBlockLightValue");
        Shaders.uniform_heldItemId2 = Shaders.shaderUniforms.make1i("heldItemId2");
        Shaders.uniform_heldBlockLightValue2 = Shaders.shaderUniforms.make1i("heldBlockLightValue2");
        Shaders.uniform_fogMode = Shaders.shaderUniforms.make1i("fogMode");
        Shaders.uniform_fogDensity = Shaders.shaderUniforms.make1f("fogDensity");
        Shaders.uniform_fogColor = Shaders.shaderUniforms.make3f("fogColor");
        Shaders.uniform_skyColor = Shaders.shaderUniforms.make3f("skyColor");
        Shaders.uniform_worldTime = Shaders.shaderUniforms.make1i("worldTime");
        Shaders.uniform_worldDay = Shaders.shaderUniforms.make1i("worldDay");
        Shaders.uniform_moonPhase = Shaders.shaderUniforms.make1i("moonPhase");
        Shaders.uniform_frameCounter = Shaders.shaderUniforms.make1i("frameCounter");
        Shaders.uniform_frameTime = Shaders.shaderUniforms.make1f("frameTime");
        Shaders.uniform_frameTimeCounter = Shaders.shaderUniforms.make1f("frameTimeCounter");
        Shaders.uniform_sunAngle = Shaders.shaderUniforms.make1f("sunAngle");
        Shaders.uniform_shadowAngle = Shaders.shaderUniforms.make1f("shadowAngle");
        Shaders.uniform_rainStrength = Shaders.shaderUniforms.make1f("rainStrength");
        Shaders.uniform_aspectRatio = Shaders.shaderUniforms.make1f("aspectRatio");
        Shaders.uniform_viewWidth = Shaders.shaderUniforms.make1f("viewWidth");
        Shaders.uniform_viewHeight = Shaders.shaderUniforms.make1f("viewHeight");
        Shaders.uniform_near = Shaders.shaderUniforms.make1f("near");
        Shaders.uniform_far = Shaders.shaderUniforms.make1f("far");
        Shaders.uniform_sunPosition = Shaders.shaderUniforms.make3f("sunPosition");
        Shaders.uniform_moonPosition = Shaders.shaderUniforms.make3f("moonPosition");
        Shaders.uniform_shadowLightPosition = Shaders.shaderUniforms.make3f("shadowLightPosition");
        Shaders.uniform_upPosition = Shaders.shaderUniforms.make3f("upPosition");
        Shaders.uniform_previousCameraPosition = Shaders.shaderUniforms.make3f("previousCameraPosition");
        Shaders.uniform_cameraPosition = Shaders.shaderUniforms.make3f("cameraPosition");
        Shaders.uniform_gbufferModelView = Shaders.shaderUniforms.makeM4("gbufferModelView");
        Shaders.uniform_gbufferModelViewInverse = Shaders.shaderUniforms.makeM4("gbufferModelViewInverse");
        Shaders.uniform_gbufferPreviousProjection = Shaders.shaderUniforms.makeM4("gbufferPreviousProjection");
        Shaders.uniform_gbufferProjection = Shaders.shaderUniforms.makeM4("gbufferProjection");
        Shaders.uniform_gbufferProjectionInverse = Shaders.shaderUniforms.makeM4("gbufferProjectionInverse");
        Shaders.uniform_gbufferPreviousModelView = Shaders.shaderUniforms.makeM4("gbufferPreviousModelView");
        Shaders.uniform_shadowProjection = Shaders.shaderUniforms.makeM4("shadowProjection");
        Shaders.uniform_shadowProjectionInverse = Shaders.shaderUniforms.makeM4("shadowProjectionInverse");
        Shaders.uniform_shadowModelView = Shaders.shaderUniforms.makeM4("shadowModelView");
        Shaders.uniform_shadowModelViewInverse = Shaders.shaderUniforms.makeM4("shadowModelViewInverse");
        Shaders.uniform_wetness = Shaders.shaderUniforms.make1f("wetness");
        Shaders.uniform_eyeAltitude = Shaders.shaderUniforms.make1f("eyeAltitude");
        Shaders.uniform_eyeBrightness = Shaders.shaderUniforms.make2i("eyeBrightness");
        Shaders.uniform_eyeBrightnessSmooth = Shaders.shaderUniforms.make2i("eyeBrightnessSmooth");
        Shaders.uniform_terrainTextureSize = Shaders.shaderUniforms.make2i("terrainTextureSize");
        Shaders.uniform_terrainIconSize = Shaders.shaderUniforms.make1i("terrainIconSize");
        Shaders.uniform_isEyeInWater = Shaders.shaderUniforms.make1i("isEyeInWater");
        Shaders.uniform_nightVision = Shaders.shaderUniforms.make1f("nightVision");
        Shaders.uniform_blindness = Shaders.shaderUniforms.make1f("blindness");
        Shaders.uniform_screenBrightness = Shaders.shaderUniforms.make1f("screenBrightness");
        Shaders.uniform_hideGUI = Shaders.shaderUniforms.make1i("hideGUI");
        Shaders.uniform_centerDepthSmooth = Shaders.shaderUniforms.make1f("centerDepthSmooth");
        Shaders.uniform_atlasSize = Shaders.shaderUniforms.make2i("atlasSize");
        Shaders.uniform_blendFunc = Shaders.shaderUniforms.make4i("blendFunc");
        Shaders.uniform_instanceId = Shaders.shaderUniforms.make1i("instanceId");
        Shaders.shadowPassInterval = 0;
        Shaders.needResizeShadow = false;
        Shaders.shadowMapWidth = 1024;
        Shaders.shadowMapHeight = 1024;
        Shaders.spShadowMapWidth = 1024;
        Shaders.spShadowMapHeight = 1024;
        Shaders.shadowMapFOV = 90.0f;
        Shaders.shadowMapHalfPlane = 160.0f;
        Shaders.shadowMapIsOrtho = true;
        Shaders.shadowDistanceRenderMul = -1.0f;
        Shaders.shadowPassCounter = 0;
        Shaders.shouldSkipDefaultShadow = false;
        Shaders.waterShadowEnabled = false;
        Shaders.usedColorBuffers = 0;
        Shaders.usedDepthBuffers = 0;
        Shaders.usedShadowColorBuffers = 0;
        Shaders.usedShadowDepthBuffers = 0;
        Shaders.usedColorAttachs = 0;
        Shaders.usedDrawBuffers = 0;
        Shaders.dfb = 0;
        Shaders.sfb = 0;
        Shaders.gbuffersFormat = new int[8];
        Shaders.gbuffersClear = new boolean[8];
        Shaders.gbuffersClearColor = new Vector4f[8];
        Shaders.programs = new Programs();
        ProgramNone = Shaders.programs.getProgramNone();
        ProgramShadow = Shaders.programs.makeShadow("shadow", Shaders.ProgramNone);
        ProgramShadowSolid = Shaders.programs.makeShadow("shadow_solid", Shaders.ProgramShadow);
        ProgramShadowCutout = Shaders.programs.makeShadow("shadow_cutout", Shaders.ProgramShadow);
        ProgramBasic = Shaders.programs.makeGbuffers("gbuffers_basic", Shaders.ProgramNone);
        ProgramTextured = Shaders.programs.makeGbuffers("gbuffers_textured", Shaders.ProgramBasic);
        ProgramTexturedLit = Shaders.programs.makeGbuffers("gbuffers_textured_lit", Shaders.ProgramTextured);
        ProgramSkyBasic = Shaders.programs.makeGbuffers("gbuffers_skybasic", Shaders.ProgramBasic);
        ProgramSkyTextured = Shaders.programs.makeGbuffers("gbuffers_skytextured", Shaders.ProgramTextured);
        ProgramClouds = Shaders.programs.makeGbuffers("gbuffers_clouds", Shaders.ProgramTextured);
        ProgramTerrain = Shaders.programs.makeGbuffers("gbuffers_terrain", Shaders.ProgramTexturedLit);
        ProgramTerrainSolid = Shaders.programs.makeGbuffers("gbuffers_terrain_solid", Shaders.ProgramTerrain);
        ProgramTerrainCutoutMip = Shaders.programs.makeGbuffers("gbuffers_terrain_cutout_mip", Shaders.ProgramTerrain);
        ProgramTerrainCutout = Shaders.programs.makeGbuffers("gbuffers_terrain_cutout", Shaders.ProgramTerrain);
        ProgramDamagedBlock = Shaders.programs.makeGbuffers("gbuffers_damagedblock", Shaders.ProgramTerrain);
        ProgramBlock = Shaders.programs.makeGbuffers("gbuffers_block", Shaders.ProgramTerrain);
        ProgramBeaconBeam = Shaders.programs.makeGbuffers("gbuffers_beaconbeam", Shaders.ProgramTextured);
        ProgramItem = Shaders.programs.makeGbuffers("gbuffers_item", Shaders.ProgramTexturedLit);
        ProgramEntities = Shaders.programs.makeGbuffers("gbuffers_entities", Shaders.ProgramTexturedLit);
        ProgramEntitiesGlowing = Shaders.programs.makeGbuffers("gbuffers_entities_glowing", Shaders.ProgramEntities);
        ProgramArmorGlint = Shaders.programs.makeGbuffers("gbuffers_armor_glint", Shaders.ProgramTextured);
        ProgramSpiderEyes = Shaders.programs.makeGbuffers("gbuffers_spidereyes", Shaders.ProgramTextured);
        ProgramHand = Shaders.programs.makeGbuffers("gbuffers_hand", Shaders.ProgramTexturedLit);
        ProgramWeather = Shaders.programs.makeGbuffers("gbuffers_weather", Shaders.ProgramTexturedLit);
        ProgramDeferredPre = Shaders.programs.makeVirtual("deferred_pre");
        ProgramsDeferred = Shaders.programs.makeDeferreds("deferred", 16);
        ProgramDeferred = Shaders.ProgramsDeferred[0];
        ProgramWater = Shaders.programs.makeGbuffers("gbuffers_water", Shaders.ProgramTerrain);
        ProgramHandWater = Shaders.programs.makeGbuffers("gbuffers_hand_water", Shaders.ProgramHand);
        ProgramCompositePre = Shaders.programs.makeVirtual("composite_pre");
        ProgramsComposite = Shaders.programs.makeComposites("composite", 16);
        ProgramComposite = Shaders.ProgramsComposite[0];
        ProgramFinal = Shaders.programs.makeComposite("final");
        ProgramCount = Shaders.programs.getCount();
        ProgramsAll = Shaders.programs.getPrograms();
        Shaders.activeProgram = Shaders.ProgramNone;
        Shaders.activeProgramID = 0;
        Shaders.programStack = new ProgramStack();
        Shaders.hasDeferredPrograms = false;
        Shaders.activeDrawBuffers = null;
        Shaders.activeCompositeMipmapSetting = 0;
        Shaders.loadedShaders = null;
        Shaders.shadersConfig = null;
        Shaders.defaultTexture = null;
        Shaders.shadowHardwareFilteringEnabled = new boolean[2];
        Shaders.shadowMipmapEnabled = new boolean[2];
        Shaders.shadowFilterNearest = new boolean[2];
        Shaders.shadowColorMipmapEnabled = new boolean[8];
        Shaders.shadowColorFilterNearest = new boolean[8];
        Shaders.configTweakBlockDamage = false;
        Shaders.configCloudShadow = false;
        Shaders.configHandDepthMul = 0.125f;
        Shaders.configRenderResMul = 1.0f;
        Shaders.configShadowResMul = 1.0f;
        Shaders.configTexMinFilB = 0;
        Shaders.configTexMinFilN = 0;
        Shaders.configTexMinFilS = 0;
        Shaders.configTexMagFilB = 0;
        Shaders.configTexMagFilN = 0;
        Shaders.configTexMagFilS = 0;
        Shaders.configShadowClipFrustrum = true;
        Shaders.configNormalMap = true;
        Shaders.configSpecularMap = true;
        Shaders.configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        Shaders.configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        Shaders.configAntialiasingLevel = 0;
        texMinFilDesc = new String[] { "Nearest", "Nearest-Nearest", "Nearest-Linear" };
        texMagFilDesc = new String[] { "Nearest", "Linear" };
        texMinFilValue = new int[] { 9728, 9984, 9986 };
        texMagFilValue = new int[] { 9728, 9729 };
        Shaders.shaderPack = null;
        Shaders.shaderPackLoaded = false;
        shaderPacksDir = new File(Minecraft.getMinecraft().mcDataDir, "shaderpacks");
        Shaders.configFile = new File(Minecraft.getMinecraft().mcDataDir, "optionsshaders.txt");
        Shaders.shaderPackOptions = null;
        Shaders.shaderPackOptionSliders = null;
        Shaders.shaderPackProfiles = null;
        Shaders.shaderPackGuiScreens = null;
        Shaders.shaderPackProgramConditions = new HashMap<String, IExpressionBool>();
        Shaders.shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
        Shaders.shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        Shaders.shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        Shaders.shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
        Shaders.shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
        Shaders.shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
        Shaders.shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
        Shaders.shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
        Shaders.shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
        Shaders.shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
        Shaders.shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
        Shaders.shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
        Shaders.shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
        Shaders.shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth", 0);
        Shaders.shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth", 0);
        Shaders.shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO", 0);
        Shaders.shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling", 0);
        Shaders.shaderPackResources = new HashMap<String, String>();
        Shaders.currentWorld = null;
        Shaders.shaderPackDimensions = new ArrayList<Integer>();
        Shaders.customTexturesGbuffers = null;
        Shaders.customTexturesComposite = null;
        Shaders.customTexturesDeferred = null;
        Shaders.noiseTexturePath = null;
        Shaders.customUniforms = null;
        STAGE_NAMES = new String[] { "gbuffers", "composite", "deferred" };
        saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
        Shaders.blockLightLevel05 = 0.5f;
        Shaders.blockLightLevel06 = 0.6f;
        Shaders.blockLightLevel08 = 0.8f;
        Shaders.aoLevel = -1.0f;
        Shaders.sunPathRotation = 0.0f;
        Shaders.shadowAngleInterval = 0.0f;
        Shaders.fogMode = 0;
        Shaders.fogDensity = 0.0f;
        Shaders.shadowIntervalSize = 2.0f;
        Shaders.terrainIconSize = 16;
        Shaders.terrainTextureSize = new int[2];
        Shaders.noiseTextureEnabled = false;
        Shaders.noiseTextureResolution = 256;
        colorTextureImageUnit = new int[] { 0, 1, 2, 3, 7, 8, 9, 10 };
        bigBufferSize = (285 + 8 * Shaders.ProgramCount) * 4;
        bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer(Shaders.bigBufferSize).limit(0);
        faProjection = new float[16];
        faProjectionInverse = new float[16];
        faModelView = new float[16];
        faModelViewInverse = new float[16];
        faShadowProjection = new float[16];
        faShadowProjectionInverse = new float[16];
        faShadowModelView = new float[16];
        faShadowModelViewInverse = new float[16];
        projection = nextFloatBuffer(16);
        projectionInverse = nextFloatBuffer(16);
        modelView = nextFloatBuffer(16);
        modelViewInverse = nextFloatBuffer(16);
        shadowProjection = nextFloatBuffer(16);
        shadowProjectionInverse = nextFloatBuffer(16);
        shadowModelView = nextFloatBuffer(16);
        shadowModelViewInverse = nextFloatBuffer(16);
        previousProjection = nextFloatBuffer(16);
        previousModelView = nextFloatBuffer(16);
        tempMatrixDirectBuffer = nextFloatBuffer(16);
        tempDirectFloatBuffer = nextFloatBuffer(16);
        dfbColorTextures = nextIntBuffer(16);
        dfbDepthTextures = nextIntBuffer(3);
        sfbColorTextures = nextIntBuffer(8);
        sfbDepthTextures = nextIntBuffer(2);
        dfbDrawBuffers = nextIntBuffer(8);
        sfbDrawBuffers = nextIntBuffer(8);
        drawBuffersNone = (IntBuffer)nextIntBuffer(8).limit(0);
        drawBuffersColorAtt0 = (IntBuffer)nextIntBuffer(8).put(36064).position(0).limit(1);
        dfbColorTexturesFlip = new FlipTextures(Shaders.dfbColorTextures, 8);
        formatNames = new String[] { "R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R16F", "RG16F", "RGB16F", "RGBA16F", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F", "RGB9_E5" };
        formatIds = new int[] { 33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33325, 33327, 34843, 34842, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898, 35901 };
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        Shaders.entityData = new int[32];
        Shaders.entityDataIndex = 0;
    }
}
