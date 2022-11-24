package insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.lang.reflect.Type;

public class MPRScalePehkui implements IMPRObject {

    public Float scale;

    public MPRScalePehkui(float scale) {
        this.scale = scale;
    }

    @Override
    public void validate() throws JsonValidationException {
        if (this.scale <= 0f) {
            throw new JsonValidationException("Invalid scale (%s) for MPRScalePehkui".formatted(this.scale));
        }
    }

    public void apply(LivingEntity entity) {
        ScaleData scaleData = ScaleTypes.BASE.getScaleData(entity);
        scaleData.setScale(this.scale);
    }

    public static class Deserializer implements JsonDeserializer<MPRScalePehkui> {
        @Override
        public MPRScalePehkui deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //Deserialize only if tinkers construct is loaded
            if (!ModList.get().isLoaded("pehkui"))
                throw new JsonParseException("Pehkui is not present. This object can't be used: %s.".formatted(json));

            return new MPRScalePehkui(json.getAsJsonObject().get("materials").getAsFloat());
        }
    }
}
