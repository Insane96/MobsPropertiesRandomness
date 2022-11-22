package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

@JsonAdapter(MPRPlaySound.Deserializer.class)
public class MPRPlaySound implements IMPRObject {

    public String sound;
    private transient SoundEvent cachedSound;

    public Float volume;
    public Float pitch;

    public MPRPlaySound(String sound) {
        this(sound, null, null);
    }

    public MPRPlaySound(String sound, Float volume, Float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void validate() throws JsonValidationException {
        if (this.sound != null) {
            ResourceLocation rl = ResourceLocation.tryParse(this.sound);
            if (rl == null)
                throw new JsonValidationException("Invalid resource location for On Hit playSound: " + this);
            this.cachedSound = ForgeRegistries.SOUND_EVENTS.getValue(rl);
            if (this.cachedSound == null)
                throw new JsonValidationException("Sound does not exist for On Hit playSound: " + this);
        }
        else {
            throw new JsonValidationException("Missing sound for MPRPlaySound");
        }

        if (this.volume == null) {
            this.volume = 1f;
        }
        else if (this.volume <= 0f || this.volume > 32f){
            Logger.warn("Invalid volume (%s) for MPRPlaySound. Must be between 0.1 and 32. Set it to 1", this.volume);
            this.volume = 1f;
        }
        if (this.pitch == null) {
            this.pitch = 1f;
        }
        else if (this.pitch <= 0f || this.pitch > 32f){
            Logger.warn("Invalid pitch (%s) for MPRPlaySound. Must be between 0.5 and 2. Set it to 1", this.pitch);
            this.pitch = 1f;
        }
    }

    public void playSound(LivingEntity livingEntity) {
        livingEntity.level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), this.cachedSound, livingEntity.getSoundSource(), this.volume, this.pitch);
    }

    @Override
    public String toString() {
        return "MPRPlaySound{sound: '%s', volume: %s, pitch: %s}".formatted(this.sound, this.volume, this.pitch);
    }

    public static class Deserializer implements JsonDeserializer<MPRPlaySound> {
        @Override
        public MPRPlaySound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive())
                return new MPRPlaySound(json.getAsString());
            return new MPRPlaySound(json.getAsJsonObject().get("sound").getAsString(),
                    context.deserialize(json.getAsJsonObject().get("volume"), Float.class),
                    context.deserialize(json.getAsJsonObject().get("pitch"), Float.class));
        }
    }
}
