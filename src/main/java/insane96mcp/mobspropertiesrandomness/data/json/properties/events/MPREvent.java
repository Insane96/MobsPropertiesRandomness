package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki.MPRScalePehkui;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class MPREvent implements IMPRObject {

    public Target target;
    public MPRModifiableValue chance;

    @SerializedName("play_sound")
    public MPRPlaySound playSound;

    @SerializedName("function")
    public String functionId;
    public transient CommandFunction.CacheableFunction function;

    @SerializedName("scale_pehkui")
    public List<MPRScalePehkui> scalePehkui;

    @Override
    public void validate() throws JsonValidationException {
        if (this.chance != null)
            this.chance.validate();

        if (this.playSound != null)
            this.playSound.validate();

        if (this.functionId != null)
            this.function = new CommandFunction.CacheableFunction(new ResourceLocation(this.functionId));

        if (this.scalePehkui != null)
        {
            for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
                scalePehkui1.validate();
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldApply(LivingEntity entity) {
        return this.chance == null || entity.getRandom().nextDouble() < this.chance.getValue(entity);
    }

    public void tryApply(LivingEntity entity) {
        this.tryPlaySound(entity);
        this.tryExecuteFunction(entity);
        this.tryApplyPehkuiScale(entity);
    }

    public void tryPlaySound(LivingEntity entity) {
        if (this.playSound == null)
            return;
        this.playSound.playSound(this.target, entity);
    }

    public void tryExecuteFunction(LivingEntity entity) {
        if (this.function == null)
            return;
        MinecraftServer server = entity.level().getServer();
        if (server == null)
            return;
        this.function.get(server.getFunctions()).ifPresent((commandFunction) ->
                server.getFunctions().execute(commandFunction, server.getFunctions().getGameLoopSender().withPosition(new Vec3(entity.getX(), entity.getY(), entity.getZ())).withLevel((ServerLevel) entity.level()).withEntity(entity)));
    }

    public void tryApplyPehkuiScale(LivingEntity entity) {
        if (this.scalePehkui == null)
            return;

        for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
            scalePehkui1.apply(entity);
        }
    }

    @Override
    public String toString() {
        return String.format("chance: %s, play_sound: %s, function: %s, scale_pehkui: %s", this.chance, this.playSound, this.functionId, this.scalePehkui);
    }
}
