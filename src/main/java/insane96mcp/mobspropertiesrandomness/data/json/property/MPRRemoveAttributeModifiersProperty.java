package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRemoveAttributeModifiersProperty.Serializer.class)
public class MPRRemoveAttributeModifiersProperty extends MPRProperty {
    @Nullable
    public Holder<Attribute> attribute;
    public List<ResourceLocation> modifierIds;

    public MPRRemoveAttributeModifiersProperty(@Nullable Holder<Attribute> attribute, List<ResourceLocation> modifierIds, List<MPRCondition> conditions) {
        super(conditions);
        this.attribute = attribute;
        this.modifierIds = modifierIds;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.attribute == null)
            return false;
        AttributeInstance attributeInstance = living.getAttribute(this.attribute);
        if (attributeInstance == null)
            return false;
        for (ResourceLocation modifierId : this.modifierIds) {
            attributeInstance.removeModifier(modifierId);
        }
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRRemoveAttributeModifiersProperty>, JsonSerializer<MPRRemoveAttributeModifiersProperty> {
        @Override
        public MPRRemoveAttributeModifiersProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            Holder<Attribute> attribute = SerializerUtils.deserializeRegistryObjectAsHolder(jObject, "attribute", Registries.ATTRIBUTE);
            if (attribute == null)
                MPRLogger.warn("Invalid attribute: %s. Will be ignored.", jObject.get("attribute").getAsString());
            return new MPRRemoveAttributeModifiersProperty(attribute, SerializerUtils.deserializeLocationList(jObject, "modifier_ids", context),
                    MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRRemoveAttributeModifiersProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.attribute == null)
                throw new JsonParseException("Attribute is null");
            jObject.add("attribute", SerializerUtils.serializeRegistryObject(src.attribute, Registries.ATTRIBUTE));
            SerializerUtils.serializeLocationList(jObject, "modifier_ids", context, src.modifierIds);
            return src.endSerialization(jObject, context);
        }
    }

    @JsonAdapter(Stackable.Serializer.class)
    public static class Stackable {
        public static final Stackable ZERO = new Stackable(MPRRange.ZERO);
        public static final Stackable ONE = new Stackable(MPRRange.ONE);

        public MPRRange value;
        public boolean stack;
        public MPRRange cap;

        public Stackable(MPRRange value) {
            this(value, false, MPRRange.UNLIMITED);
        }

        public Stackable(MPRRange value, boolean stack, MPRRange cap) {
            this.value = value;
            this.stack = stack;
            this.cap = cap;
        }

        public double getStackedValue(LivingEntity living, double originalValue) {
            double value = this.value.getDoubleBetween(living);
            if (!this.stack)
                return value;
            if (this.cap != MPRRange.UNLIMITED)
                return Mth.clamp(value, this.cap.getMin(living), this.cap.getMax(living));
            return value + originalValue;
        }

        public int getStackedIntValue(LivingEntity living, int originalValue) {
            int value = this.value.getIntBetween(living);
            if (!this.stack)
                return value;
            if (this.cap != MPRRange.UNLIMITED)
                return Mth.clamp(value, (int) this.cap.getMin(living), (int) this.cap.getMax(living));
            return value + originalValue;
        }

        public static class Serializer implements JsonDeserializer<Stackable>, JsonSerializer<Stackable> {
            @Override
            public Stackable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonPrimitive())
                    return new Stackable(new MPRRange(json.getAsDouble()));
				JsonObject jObject = json.getAsJsonObject();
                MPRRange value;
				if (jObject.has("value"))
					value = GsonHelper.getAsObject(jObject, "value", context, MPRRange.class);
				else
					value = GsonHelper.convertToObject(json, json.toString(), context, MPRRange.class);

                boolean stack = GsonHelper.getAsBoolean(jObject, "stack", false);
                MPRRange cap = GsonHelper.getAsObject(jObject, "cap", MPRRange.UNLIMITED, context, MPRRange.class);

                return new Stackable(value, stack, cap);
            }

            @Override
            public JsonElement serialize(Stackable src, Type typeOfSrc, JsonSerializationContext context) {
                if (!src.stack && src.cap == MPRRange.UNLIMITED && src.value.min == src.value.max)
                    return new JsonPrimitive(src.value.min.value);
                JsonObject jObject = new JsonObject();
                jObject.add("value", context.serialize(src.value));
                if (src.stack)
                    jObject.addProperty("stack", true);
                if (src.cap != MPRRange.UNLIMITED)
                    jObject.add("cap", context.serialize(src.cap));
                return jObject;
            }
        }
    }
}
