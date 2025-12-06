package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Collections;
import java.io.StringReader;
import java.io.Reader;
import net.minecraft.util.ResourceLocation;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class ModelBlock
{
    private static final Logger LOGGER;
    static final Gson SERIALIZER;
    private final List<BlockPart> elements;
    private final boolean gui3d;
    private final boolean ambientOcclusion;
    private ItemCameraTransforms cameraTransforms;
    public String name;
    public final Map<String, String> textures;
    protected ModelBlock parent;
    public ResourceLocation parentLocation;
    
    public static ModelBlock deserialize(final Reader readerIn) {
        return (ModelBlock)ModelBlock.SERIALIZER.fromJson(readerIn, (Class)ModelBlock.class);
    }
    
    public static ModelBlock deserialize(final String jsonString) {
        return deserialize(new StringReader(jsonString));
    }
    
    protected ModelBlock(final List<BlockPart> elementsIn, final Map<String, String> texturesIn, final boolean ambientOcclusionIn, final boolean gui3dIn, final ItemCameraTransforms cameraTransformsIn) {
        this(null, elementsIn, texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
    }
    
    protected ModelBlock(final ResourceLocation parentLocationIn, final Map<String, String> texturesIn, final boolean ambientOcclusionIn, final boolean gui3dIn, final ItemCameraTransforms cameraTransformsIn) {
        this(parentLocationIn, Collections.emptyList(), texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
    }
    
    private ModelBlock(final ResourceLocation parentLocationIn, final List<BlockPart> elementsIn, final Map<String, String> texturesIn, final boolean ambientOcclusionIn, final boolean gui3dIn, final ItemCameraTransforms cameraTransformsIn) {
        this.name = "";
        this.elements = elementsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.textures = texturesIn;
        this.parentLocation = parentLocationIn;
        this.cameraTransforms = cameraTransformsIn;
    }
    
    public List<BlockPart> getElements() {
        if (this.hasParent()) {
            return this.parent.getElements();
        }
        return this.elements;
    }
    
    private boolean hasParent() {
        return this.parent != null;
    }
    
    public boolean isAmbientOcclusion() {
        if (this.hasParent()) {
            return this.parent.isAmbientOcclusion();
        }
        return this.ambientOcclusion;
    }
    
    public boolean isGui3d() {
        return this.gui3d;
    }
    
    public boolean isResolved() {
        return this.parentLocation == null || (this.parent != null && this.parent.isResolved());
    }
    
    public void getParentFromMap(final Map<ResourceLocation, ModelBlock> p_178299_1_) {
        if (this.parentLocation != null) {
            this.parent = p_178299_1_.get(this.parentLocation);
        }
    }
    
    public boolean isTexturePresent(final String textureName) {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }
    
    public String resolveTextureName(String textureName) {
        if (!this.startsWithHash(textureName)) {
            textureName = '#' + textureName;
        }
        return this.resolveTextureName(textureName, new Bookkeep(this));
    }
    
    private String resolveTextureName(final String textureName, final Bookkeep p_178302_2_) {
        if (!this.startsWithHash(textureName)) {
            return textureName;
        }
        if (this == p_178302_2_.modelExt) {
            ModelBlock.LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
            return "missingno";
        }
        String lvt_3_1_ = (String)this.textures.get(textureName.substring(1));
        if (lvt_3_1_ == null && this.hasParent()) {
            lvt_3_1_ = this.parent.resolveTextureName(textureName, p_178302_2_);
        }
        p_178302_2_.modelExt = this;
        if (lvt_3_1_ != null && this.startsWithHash(lvt_3_1_)) {
            lvt_3_1_ = p_178302_2_.model.resolveTextureName(lvt_3_1_, p_178302_2_);
        }
        if (lvt_3_1_ == null || this.startsWithHash(lvt_3_1_)) {
            return "missingno";
        }
        return lvt_3_1_;
    }
    
    private boolean startsWithHash(final String hash) {
        return hash.charAt(0) == '#';
    }
    
    public ResourceLocation getParentLocation() {
        return this.parentLocation;
    }
    
    public ModelBlock getRootModel() {
        return this.hasParent() ? this.parent.getRootModel() : this;
    }
    
    public ItemCameraTransforms getAllTransforms() {
        final ItemTransformVec3f lvt_1_1_ = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
        final ItemTransformVec3f lvt_2_1_ = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
        final ItemTransformVec3f lvt_3_1_ = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
        final ItemTransformVec3f lvt_4_1_ = this.getTransform(ItemCameraTransforms.TransformType.GUI);
        final ItemTransformVec3f lvt_5_1_ = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
        final ItemTransformVec3f lvt_6_1_ = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
        return new ItemCameraTransforms(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
    }
    
    private ItemTransformVec3f getTransform(final ItemCameraTransforms.TransformType type) {
        if (this.parent != null && !this.cameraTransforms.func_181687_c(type)) {
            return this.parent.getTransform(type);
        }
        return this.cameraTransforms.getTransform(type);
    }
    
    public static void checkModelHierarchy(final Map<ResourceLocation, ModelBlock> p_178312_0_) {
        for (final ModelBlock lvt_2_1_ : p_178312_0_.values()) {
            try {
                for (ModelBlock lvt_3_1_ = lvt_2_1_.parent, lvt_4_1_ = lvt_3_1_.parent; lvt_3_1_ != lvt_4_1_; lvt_3_1_ = lvt_3_1_.parent, lvt_4_1_ = lvt_4_1_.parent.parent) {}
                throw new LoopException();
            }
            catch (NullPointerException ex) {
                continue;
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        SERIALIZER = new GsonBuilder().registerTypeAdapter((Type)ModelBlock.class, (Object)new Deserializer()).registerTypeAdapter((Type)BlockPart.class, (Object)new BlockPart.Deserializer()).registerTypeAdapter((Type)BlockPartFace.class, (Object)new BlockPartFace.Deserializer()).registerTypeAdapter((Type)BlockFaceUV.class, (Object)new BlockFaceUV.Deserializer()).registerTypeAdapter((Type)ItemTransformVec3f.class, (Object)new ItemTransformVec3f.Deserializer()).registerTypeAdapter((Type)ItemCameraTransforms.class, (Object)new ItemCameraTransforms.Deserializer()).create();
    }
    
    static final class Bookkeep
    {
        public final ModelBlock model;
        public ModelBlock modelExt;
        
        private Bookkeep(final ModelBlock p_i46223_1_) {
            this.model = p_i46223_1_;
        }
    }
    
    public static class Deserializer implements JsonDeserializer<ModelBlock>
    {
        public ModelBlock deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            final List<BlockPart> lvt_5_1_ = this.getModelElements(p_deserialize_3_, lvt_4_1_);
            final String lvt_6_1_ = this.getParent(lvt_4_1_);
            final boolean lvt_7_1_ = StringUtils.isEmpty((CharSequence)lvt_6_1_);
            final boolean lvt_8_1_ = lvt_5_1_.isEmpty();
            if (lvt_8_1_ && lvt_7_1_) {
                throw new JsonParseException("BlockModel requires either elements or parent, found neither");
            }
            if (!lvt_7_1_ && !lvt_8_1_) {
                throw new JsonParseException("BlockModel requires either elements or parent, found both");
            }
            final Map<String, String> lvt_9_1_ = this.getTextures(lvt_4_1_);
            final boolean lvt_10_1_ = this.getAmbientOcclusionEnabled(lvt_4_1_);
            ItemCameraTransforms lvt_11_1_ = ItemCameraTransforms.DEFAULT;
            if (lvt_4_1_.has("display")) {
                final JsonObject lvt_12_1_ = JsonUtils.getJsonObject(lvt_4_1_, "display");
                lvt_11_1_ = (ItemCameraTransforms)p_deserialize_3_.deserialize((JsonElement)lvt_12_1_, (Type)ItemCameraTransforms.class);
            }
            if (lvt_8_1_) {
                return new ModelBlock(new ResourceLocation(lvt_6_1_), lvt_9_1_, lvt_10_1_, true, lvt_11_1_);
            }
            return new ModelBlock(lvt_5_1_, lvt_9_1_, lvt_10_1_, true, lvt_11_1_);
        }
        
        private Map<String, String> getTextures(final JsonObject p_178329_1_) {
            final Map<String, String> lvt_2_1_ = Maps.newHashMap();
            if (p_178329_1_.has("textures")) {
                final JsonObject lvt_3_1_ = p_178329_1_.getAsJsonObject("textures");
                for (final Map.Entry<String, JsonElement> lvt_5_1_ : lvt_3_1_.entrySet()) {
                    lvt_2_1_.put(lvt_5_1_.getKey(), ((JsonElement)lvt_5_1_.getValue()).getAsString());
                }
            }
            return lvt_2_1_;
        }
        
        private String getParent(final JsonObject p_178326_1_) {
            return JsonUtils.getString(p_178326_1_, "parent", "");
        }
        
        protected boolean getAmbientOcclusionEnabled(final JsonObject p_178328_1_) {
            return JsonUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
        }
        
        protected List<BlockPart> getModelElements(final JsonDeserializationContext p_178325_1_, final JsonObject p_178325_2_) {
            final List<BlockPart> lvt_3_1_ = Lists.newArrayList();
            if (p_178325_2_.has("elements")) {
                for (final JsonElement lvt_5_1_ : JsonUtils.getJsonArray(p_178325_2_, "elements")) {
                    lvt_3_1_.add((BlockPart)p_178325_1_.deserialize(lvt_5_1_, (Type)BlockPart.class));
                }
            }
            return lvt_3_1_;
        }
    }
    
    public static class LoopException extends RuntimeException
    {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2470773988286929812L;
    }
}
