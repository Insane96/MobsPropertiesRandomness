package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import net.minecraft.world.entity.LivingEntity;

public class MPROnTick extends MPREvent {

    @SerializedName("update_speed")
    public Integer updateSpeed;

    @Override
    public void validate() throws JsonValidationException {
        super.validate();
        if (updateSpeed == null)
            updateSpeed = 20;
    }

    public void apply(LivingEntity entity) {
        if (!super.shouldApply(entity))
            return;
        if (entity.tickCount == 0 || entity.tickCount % updateSpeed != 0)
            return;
        this.tryPlaySound(entity);
        this.tryExecuteFunction(entity);
    }

    @Override
    public String toString() {
        return String.format("OnTick{%s}", super.toString());
    }
}
