package insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.lang.reflect.Type;

@JsonAdapter(MPRScalePehkui.Deserializer.class)
public class MPRScalePehkui implements IMPRObject {

    public MPRRange scale;

    public MPRScalePehkui(MPRRange scale) {
        this.scale = scale;
    }

    @Override
    public void validate() throws JsonValidationException {
        if (this.scale == null)
            throw new JsonValidationException("scale missing from MPRScalePehkui");
    }

    public void apply(LivingEntity entity) {
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(entity);
        scaleData.setScale(this.scale.getFloat(entity, entity.level));
    }

    public static class Deserializer implements JsonDeserializer<MPRScalePehkui> {
        @Override
        public MPRScalePehkui deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //Deserialize only if Pehkui is loaded
            if (!ModList.get().isLoaded("pehkui"))
                throw new JsonParseException("Pehkui is not present. This object can't be used: %s.".formatted(json));

            return new MPRScalePehkui(context.deserialize(json.getAsJsonObject().get("scale"), MPRRange.class));
        }
    }
}
