package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import net.minecraft.util.JsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Iterator;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.io.Reader;
import java.util.Map;
import com.google.gson.Gson;

public class ModelBlockDefinition
{
    static final Gson GSON;
    private final Map<String, Variants> mapVariants;
    
    public static ModelBlockDefinition parseFromReader(final Reader p_178331_0_) {
        return (ModelBlockDefinition)ModelBlockDefinition.GSON.fromJson(p_178331_0_, (Class)ModelBlockDefinition.class);
    }
    
    public ModelBlockDefinition(final Collection<Variants> p_i46221_1_) {
        this.mapVariants = Maps.newHashMap();
        for (final Variants lvt_3_1_ : p_i46221_1_) {
            this.mapVariants.put(lvt_3_1_.name, lvt_3_1_);
        }
    }
    
    public ModelBlockDefinition(final List<ModelBlockDefinition> p_i46222_1_) {
        this.mapVariants = Maps.newHashMap();
        for (final ModelBlockDefinition lvt_3_1_ : p_i46222_1_) {
            this.mapVariants.putAll(lvt_3_1_.mapVariants);
        }
    }
    
    public Variants getVariants(final String p_178330_1_) {
        final Variants lvt_2_1_ = (Variants)this.mapVariants.get(p_178330_1_);
        if (lvt_2_1_ == null) {
            throw new MissingVariantException();
        }
        return lvt_2_1_;
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof ModelBlockDefinition) {
            final ModelBlockDefinition lvt_2_1_ = (ModelBlockDefinition)p_equals_1_;
            return this.mapVariants.equals(lvt_2_1_.mapVariants);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.mapVariants.hashCode();
    }
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter((Type)ModelBlockDefinition.class, (Object)new Deserializer()).registerTypeAdapter((Type)Variant.class, (Object)new Variant.Deserializer()).create();
    }
    
    public static class Variants
    {
        private final String name;
        private final List<Variant> listVariants;
        
        public Variants(final String nameIn, final List<Variant> listVariantsIn) {
            this.name = nameIn;
            this.listVariants = listVariantsIn;
        }
        
        public List<Variant> getVariants() {
            return this.listVariants;
        }
        
        @Override
        public boolean equals(final Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            }
            if (!(p_equals_1_ instanceof Variants)) {
                return false;
            }
            final Variants lvt_2_1_ = (Variants)p_equals_1_;
            return this.name.equals(lvt_2_1_.name) && this.listVariants.equals(lvt_2_1_.listVariants);
        }
        
        @Override
        public int hashCode() {
            int lvt_1_1_ = this.name.hashCode();
            lvt_1_1_ = 31 * lvt_1_1_ + this.listVariants.hashCode();
            return lvt_1_1_;
        }
    }
    
    public static class Variant
    {
        private final ResourceLocation modelLocation;
        private final ModelRotation modelRotation;
        private final boolean uvLock;
        private final int weight;
        
        public Variant(final ResourceLocation modelLocationIn, final ModelRotation modelRotationIn, final boolean uvLockIn, final int weightIn) {
            this.modelLocation = modelLocationIn;
            this.modelRotation = modelRotationIn;
            this.uvLock = uvLockIn;
            this.weight = weightIn;
        }
        
        public ResourceLocation getModelLocation() {
            return this.modelLocation;
        }
        
        public ModelRotation getRotation() {
            return this.modelRotation;
        }
        
        public boolean isUvLocked() {
            return this.uvLock;
        }
        
        public int getWeight() {
            return this.weight;
        }
        
        @Override
        public boolean equals(final Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            }
            if (p_equals_1_ instanceof Variant) {
                final Variant lvt_2_1_ = (Variant)p_equals_1_;
                return this.modelLocation.equals(lvt_2_1_.modelLocation) && this.modelRotation == lvt_2_1_.modelRotation && this.uvLock == lvt_2_1_.uvLock;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int lvt_1_1_ = this.modelLocation.hashCode();
            lvt_1_1_ = 31 * lvt_1_1_ + ((this.modelRotation != null) ? this.modelRotation.hashCode() : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.uvLock ? 1 : 0);
            return lvt_1_1_;
        }
        
        public static class Deserializer implements JsonDeserializer<Variant>
        {
            public Variant deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
                final JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
                final String lvt_5_1_ = this.parseModel(lvt_4_1_);
                final ModelRotation lvt_6_1_ = this.parseRotation(lvt_4_1_);
                final boolean lvt_7_1_ = this.parseUvLock(lvt_4_1_);
                final int lvt_8_1_ = this.parseWeight(lvt_4_1_);
                return new Variant(this.makeModelLocation(lvt_5_1_), lvt_6_1_, lvt_7_1_, lvt_8_1_);
            }
            
            private ResourceLocation makeModelLocation(final String p_178426_1_) {
                ResourceLocation lvt_2_1_ = new ResourceLocation(p_178426_1_);
                lvt_2_1_ = new ResourceLocation(lvt_2_1_.getResourceDomain(), "block/" + lvt_2_1_.getResourcePath());
                return lvt_2_1_;
            }
            
            private boolean parseUvLock(final JsonObject p_178429_1_) {
                return JsonUtils.getBoolean(p_178429_1_, "uvlock", false);
            }
            
            protected ModelRotation parseRotation(final JsonObject p_178428_1_) {
                final int lvt_2_1_ = JsonUtils.getInt(p_178428_1_, "x", 0);
                final int lvt_3_1_ = JsonUtils.getInt(p_178428_1_, "y", 0);
                final ModelRotation lvt_4_1_ = ModelRotation.getModelRotation(lvt_2_1_, lvt_3_1_);
                if (lvt_4_1_ == null) {
                    throw new JsonParseException("Invalid BlockModelRotation x: " + lvt_2_1_ + ", y: " + lvt_3_1_);
                }
                return lvt_4_1_;
            }
            
            protected String parseModel(final JsonObject p_178424_1_) {
                return JsonUtils.getString(p_178424_1_, "model");
            }
            
            protected int parseWeight(final JsonObject p_178427_1_) {
                return JsonUtils.getInt(p_178427_1_, "weight", 1);
            }
        }
    }
    
    public static class Deserializer implements JsonDeserializer<ModelBlockDefinition>
    {
        public ModelBlockDefinition deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            final List<Variants> lvt_5_1_ = this.parseVariantsList(p_deserialize_3_, lvt_4_1_);
            return new ModelBlockDefinition(lvt_5_1_);
        }
        
        protected List<Variants> parseVariantsList(final JsonDeserializationContext p_178334_1_, final JsonObject p_178334_2_) {
            final JsonObject lvt_3_1_ = JsonUtils.getJsonObject(p_178334_2_, "variants");
            final List<Variants> lvt_4_1_ = Lists.newArrayList();
            for (final Map.Entry<String, JsonElement> lvt_6_1_ : lvt_3_1_.entrySet()) {
                lvt_4_1_.add(this.parseVariants(p_178334_1_, lvt_6_1_));
            }
            return lvt_4_1_;
        }
        
        protected Variants parseVariants(final JsonDeserializationContext p_178335_1_, final Map.Entry<String, JsonElement> p_178335_2_) {
            final String lvt_3_1_ = (String)p_178335_2_.getKey();
            final List<Variant> lvt_4_1_ = Lists.newArrayList();
            final JsonElement lvt_5_1_ = (JsonElement)p_178335_2_.getValue();
            if (lvt_5_1_.isJsonArray()) {
                for (final JsonElement lvt_7_1_ : lvt_5_1_.getAsJsonArray()) {
                    lvt_4_1_.add((Variant)p_178335_1_.deserialize(lvt_7_1_, (Type)Variant.class));
                }
            }
            else {
                lvt_4_1_.add((Variant)p_178335_1_.deserialize(lvt_5_1_, (Type)Variant.class));
            }
            return new Variants(lvt_3_1_, lvt_4_1_);
        }
    }
    
    public class MissingVariantException extends RuntimeException
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = -4085165415179016645L;

		protected MissingVariantException() {
        }
    }
}
