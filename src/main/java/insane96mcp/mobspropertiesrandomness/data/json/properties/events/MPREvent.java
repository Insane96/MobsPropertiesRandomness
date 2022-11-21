package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class MPREvent implements IMPRObject {

    public MPRModifiableValue chance;

    @SerializedName("play_sound")
    public String playSound;

    @SerializedName("function")
    public String _function;
    public transient CommandFunction.CacheableFunction function;

    @Override
    public void validate() throws JsonValidationException {
        if (this.chance != null)
            this.chance.validate();

        if (this.playSound != null) {
            ResourceLocation rl = ResourceLocation.tryParse(this.playSound);
            if (rl == null)
                throw new JsonValidationException("Invalid resource location for On Hit playSound: " + this);
            if (ForgeRegistries.SOUND_EVENTS.getValue(rl) == null)
                throw new JsonValidationException("Sound does not exist for On Hit playSound: " + this);
        }

        if (this._function != null) {
            this.function = new CommandFunction.CacheableFunction(new ResourceLocation(this._function));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldApply(LivingEntity entity) {
        return this.chance == null || entity.getRandom().nextDouble() >= this.chance.getValue(entity, entity.level);
    }

    public void tryPlaySound(LivingEntity entity) {
        SoundEvent sound;
        if (this.playSound != null)
            sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(this.playSound));
        else
            return;
        //noinspection ConstantConditions
        entity.level.playSound(null, entity, sound, SoundSource.HOSTILE, 1.0f, 1f);
    }

    public void tryExecuteFunction(LivingEntity entity) {
        MinecraftServer server = entity.level.getServer();
        if (server == null) return;
        this.function.get(server.getFunctions()).ifPresent((commandFunction) ->
                server.getFunctions().execute(commandFunction, server.getFunctions().getGameLoopSender().withPosition(new Vec3(entity.getX(), entity.getY(), entity.getZ())).withLevel((ServerLevel) entity.level).withEntity(entity)));
    }

    @Override
    public String toString() {
        return String.format("chance: %s, play_sound: %s, function: %s", this.chance, this.playSound, this._function);
    }
}
