package insane96mcp.mobspropertiesrandomness.data.json.property.preset;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRPresetsProperty.Serializer.class)
public class MPRPresetsProperty extends MPRProperty {
    public Mode mode;
    public Boolean applyAll;
    public List<MPRWeightedPreset> weightedPresets;

    public MPRPresetsProperty(Mode mode, Boolean applyAll, List<MPRWeightedPreset> weightedPresets, List<MPRCondition> conditions) {
        super(conditions);
        this.mode = mode;
        this.applyAll = applyAll;
        this.weightedPresets = weightedPresets;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected boolean apply(LivingEntity living) {
        if (this.applyAll) {
            List<MPRWeightedPreset> items = this.getPresets(living);
            items.forEach(weightedPreset -> weightedPreset.properties.tryApply(living));
            return true;
        }
        else {
            MPRWeightedPreset weightedPreset = this.getRandomPreset(living);
            if (weightedPreset == null)
                return false;
            weightedPreset.properties.tryApply(living);
        }
        return true;
    }

    /// Returns a list of all the valid presets
    private List<MPRWeightedPreset> getPresets(LivingEntity entity){
        ArrayList<MPRWeightedPreset> weightedPresets = new ArrayList<>();
        for (MPRWeightedPreset weightedPreset : this.weightedPresets) {
            MPRWeightedPreset mprWeightedPreset = weightedPreset.computeAndGet(entity);
            if (mprWeightedPreset != null && mprWeightedPreset.isValid())
                weightedPresets.add(mprWeightedPreset);
        }
        return weightedPresets;
    }

    /**
     * Returns a random item from the pool based of weights and conditions or null if no items were available
     */
    @Nullable
    public MPRWeightedPreset getRandomPreset(LivingEntity entity) {
        List<MPRWeightedPreset> items = this.getPresets(entity);
        if (items.isEmpty())
            return null;
        return WeightedRandom.getRandomItem(entity.level().random, items);
    }

    public static class Serializer implements JsonDeserializer<MPRPresetsProperty>, JsonSerializer<MPRPresetsProperty> {
        @Override
        public MPRPresetsProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<MPRWeightedPreset> presets = SerializerUtils.deserializeList(jObject, "presets", context, MPRWeightedPreset.class);
            if (presets.isEmpty())
                throw new JsonParseException("Presets list is empty");
            return new MPRPresetsProperty(
                    GsonHelper.getAsObject(jObject, "mode", Mode.EXCLUSIVE, context, Mode.class),
                    GsonHelper.getAsBoolean(jObject, "apply_all", false),
                    presets,
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRPresetsProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("mode", context.serialize(src.mode));
            jObject.addProperty("apply_all", src.applyAll);
            jObject.add("presets", context.serialize(src.weightedPresets));
            return src.endSerialization(jObject, context);
        }
    }

    public enum Mode {
        @SerializedName("exclusive")
        EXCLUSIVE,
        @SerializedName("before")
        BEFORE,
        @SerializedName("after")
        AFTER
    }
}
