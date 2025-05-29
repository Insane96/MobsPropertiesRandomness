package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.util.TriConsumer;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRScalePehkuiProperty.Serializer.class)
public class MPRScalePehkuiProperty extends MPRProperty {
    public static final ResourceLocation SCALE_PEHKUI_APPLIED = ResourceLocation.fromNamespaceAndPath(MPR.MOD_ID, "scale_pehkui_applied");

    public MPRRange scale;
    public List<ResourceLocation> scaleTypes;
    public Operation operation;

    public MPRScalePehkuiProperty(MPRRange scale, List<ResourceLocation> scaleTypes, Operation operation, List<MPRCondition> conditions) {
        super(conditions);
        this.scale = scale;
        this.scaleTypes = scaleTypes;
        this.operation = operation;
    }

    private static final HashMultimap<LivingEntity, MPRScalePehkuiProperty> TO_APPLY = HashMultimap.create();

    @Override
    protected boolean apply(LivingEntity living) {
        TO_APPLY.put(living, this);
        return true;
    }

    public static void applyScheduled(LivingEntity entity) {
        TO_APPLY.get(entity).forEach(mprScalePehkui -> {
            mprScalePehkui.actuallyApply(entity);
        });
        ModNBTData.put(entity, SCALE_PEHKUI_APPLIED, true);
        TO_APPLY.removeAll(entity);
    }

    public void actuallyApply(LivingEntity living) {
        float scale = this.scale.getFloatBetween(living);
        for (ResourceLocation scaleType : this.scaleTypes) {
            ScaleType type = ScaleRegistries.SCALE_TYPES.get(scaleType);
            ScaleData scaleData = type.getScaleData(living);
            this.operation.applyScale(scaleData, scale, living);
        }
    }

    public static class Serializer implements JsonDeserializer<MPRScalePehkuiProperty>, JsonSerializer<MPRScalePehkuiProperty> {
        @Override
        public MPRScalePehkuiProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRScalePehkuiProperty(
                    context.deserialize(jObject.get("scale"), MPRRange.class),
                    SerializerUtils.deserializeLocationList(jObject, "scale_types", context),
                    context.deserialize(jObject.get("operation"), Operation.class),
                    deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRScalePehkuiProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("scale", context.serialize(src.scale));
            SerializerUtils.serializeLocationList(jObject, "scale_types", context, src.scaleTypes);
            jObject.add("operation", context.serialize(src.operation));
            return src.endSerialization(jObject, context);
        }
    }

    public enum Operation {
        @SerializedName("set")
        SET((scaleData, scale, entity) ->
                scaleData.setScale(scale)),
        @SerializedName("add")
        ADD((scaleData, scale, entity) ->
                scaleData.setScale(scaleData.getScale() + scale)),
        @SerializedName("multiply")
        MULTIPLY((scaleData, scale, entity) ->
                scaleData.setScale(scaleData.getScale() * scale));

        final TriConsumer<ScaleData, Float, LivingEntity> apply;

        Operation(TriConsumer<ScaleData, Float, LivingEntity> apply) {
            this.apply = apply;
        }

        public void applyScale(ScaleData scaleData, Float mprRange, LivingEntity entity) {
            this.apply.accept(scaleData, mprRange, entity);
        }
    }
}
