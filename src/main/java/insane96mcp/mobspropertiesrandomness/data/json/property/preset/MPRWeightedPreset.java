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
    private final ResourceLocation presetId;
    @Nullable
    private MPRProperties properties = null;
    private final MPRModifiableValue modifiableWeight;
    private int _weight;

    public MPRWeightedPreset(ResourceLocation presetId, MPRModifiableValue modifiableWeight, List<MPRCondition> conditions) {
        super(conditions);
        this.presetId = presetId;
        this.modifiableWeight = modifiableWeight;
    }

    @Nullable
    public MPRWeightedPreset computeAndGet(LivingEntity entity) {
        if (this.properties == null
                || !MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);
        if (this._weight <= 0)
            return null;

        return this;
    }

    public void tryApply(LivingEntity living) {
        if (this.properties == null)
            return;
        this.properties.tryApply(living);
    }

    @Override
    public int getWeight() {
        return this._weight;
    }

    public boolean isValid() {
        return this.properties != null;
    }

    public void resolve() {
        this.properties = MPRPresetReloadListener.PRESETS.get(this.presetId);
        if (this.properties == null)
            Logger.warn("Preset %s does not exist. Ignoring", this.presetId);
    }

    public static class Serializer implements JsonSerializer<MPRWeightedPreset>, JsonDeserializer<MPRWeightedPreset> {
        @Override
        public MPRWeightedPreset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String sPreset = GsonHelper.getAsString(jObject, "preset");
            ResourceLocation presetId = ResourceLocation.parse(sPreset);
            return new MPRWeightedPreset(
                    presetId,
                    GsonHelper.getAsObject(jObject, "weight", MPRModifiableValue.ONE, context, MPRModifiableValue.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRWeightedPreset src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("preset", src.presetId.toString());
            jObject.add("weight", context.serialize(src.modifiableWeight));
            return src.endSerialization(jObject, context);
        }
    }
}
