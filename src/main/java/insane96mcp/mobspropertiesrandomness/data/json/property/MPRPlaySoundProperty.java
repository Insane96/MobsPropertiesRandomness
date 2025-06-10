package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@JsonAdapter(MPRPlaySoundProperty.Serializer.class)
public class MPRPlaySoundProperty extends MPRProperty {
    public SoundEvent sound;
    public MPRRange volume;
    public MPRRange pitch;

    public MPRPlaySoundProperty(SoundEvent sound, MPRRange volume, MPRRange pitch, List<MPRCondition> conditions) {
        super(conditions);
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        living.playSound(this.sound, (float) this.volume.getDoubleBetween(living), (float) this.pitch.getDoubleBetween(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRPlaySoundProperty>, JsonSerializer<MPRPlaySoundProperty> {
        @Override
        public MPRPlaySoundProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            ResourceLocation rl = ResourceLocation.parse(GsonHelper.getAsString(jObject, "sound"));
            SoundEvent soundEvent = Holder.direct(SoundEvent.createVariableRangeEvent(rl)).value();
            //TODO limit to valid values
            MPRRange volume = GsonHelper.getAsObject(jObject, "volume", MPRRange.ONE, context, MPRRange.class);
            MPRRange pitch = GsonHelper.getAsObject(jObject, "pitch", MPRRange.ONE, context, MPRRange.class);
            return new MPRPlaySoundProperty(soundEvent, volume, pitch, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRPlaySoundProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("sound", src.sound.getLocation().toString());
            if (!Objects.equals(src.volume, MPRRange.ONE))
                jObject.add("volume", context.serialize(src.volume));
            if (!Objects.equals(src.pitch, MPRRange.ONE))
                jObject.add("pitch", context.serialize(src.pitch));
            return src.endSerialization(jObject, context);
        }
    }

}
