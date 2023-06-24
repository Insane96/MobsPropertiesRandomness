package insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.util.TriConsumer;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRScalePehkui.Deserializer.class)
public class MPRScalePehkui implements IMPRObject {

    public MPRRange scale;
    @SerializedName("scale_types")
    public List<String> scaleTypes;

    public Operation operation;

    public MPRScalePehkui(MPRRange scale, List<String> scaleTypes, Operation operation) {
        this.scale = scale;
        this.scaleTypes = scaleTypes;
        this.operation = operation;
    }

    @Override
    public void validate() throws JsonValidationException {
        if (this.scale == null)
            throw new JsonValidationException("scale missing from ScalePehkui");
        if (this.scaleTypes == null || this.scaleTypes.isEmpty())
            throw new JsonValidationException("scale_types missing or empty from ScalePehkui");
        if (this.operation == null)
            throw new JsonValidationException("operation missing from ScalePehkui");
    }

    public void apply(LivingEntity entity) {
        float scale = this.scale.getFloatBetween(entity);
        for (String scaleType : this.scaleTypes) {
            ScaleType type = ScaleRegistries.SCALE_TYPES.get(new ResourceLocation(scaleType));
            ScaleData scaleData = type.getScaleData(entity);
            this.operation.applyScale(scaleData, scale, entity);
        }
    }

    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>(){}.getType();

    public static class Deserializer implements JsonDeserializer<MPRScalePehkui> {
        @Override
        public MPRScalePehkui deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //Deserialize only if Pehkui is loaded
            if (!ModList.get().isLoaded("pehkui"))
                throw new JsonParseException("Pehkui is not present. This object can't be used: %s.".formatted(json));

            return new MPRScalePehkui(context.deserialize(json.getAsJsonObject().get("scale"), MPRRange.class), context.deserialize(json.getAsJsonObject().get("scale_types"), STRING_LIST_TYPE), context.deserialize(json.getAsJsonObject().get("operation"), Operation.class));
        }
    }

    enum Operation {
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
