package insane96mcp.mobspropertiesrandomness.data.json.property.preset;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRWeightedPreset.Serializer.class)
public class MPRWeightedPreset extends MPRConditionable implements IWeightedRandom {
    @Nullable
    public MPRProperties properties;
    private final MPRModifiableValue modifiableWeight;
    private int _weight;

    private boolean valid = true;

    public MPRWeightedPreset(@Nullable MPRProperties properties, MPRModifiableValue modifiableWeight, List<MPRCondition> conditions) {
        super(conditions);
        this.properties = properties;
        this.modifiableWeight = modifiableWeight;
    }

    @Nullable
    public MPRWeightedPreset computeAndGet(LivingEntity entity) {
        if (!this.valid
                || !MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);

        return this;
    }

    @Override
    public int getWeight() {
        return this._weight;
    }

    public boolean isValid() {
        return this.valid;
    }

    public static class Serializer implements JsonSerializer<MPRWeightedPreset>, JsonDeserializer<MPRWeightedPreset> {
        @Override
        public MPRWeightedPreset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String sPreset = GsonHelper.getAsString(jObject, "preset");
            ResourceLocation presetLocation = ResourceLocation.parse(sPreset);
            MPRProperties preset = MPRPresetReloadListener.PRESETS.get(presetLocation);
            MPRWeightedPreset weightedPreset = new MPRWeightedPreset(
                    preset,
                    GsonHelper.getAsObject(jObject, "weight", MPRModifiableValue.ONE, context, MPRModifiableValue.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
            if (preset == null) {
                Logger.warn("Preset " + sPreset + " does not exist");
                weightedPreset.valid = false;
            }
            return weightedPreset;
        }

        @Override
        public JsonElement serialize(MPRWeightedPreset src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.properties != null)
                jObject.addProperty("preset", MPRPresetReloadListener.getKey(src.properties).toString());
            jObject.add("weight", context.serialize(src.modifiableWeight));
            return src.endSerialization(jObject, context);
        }
    }
}
