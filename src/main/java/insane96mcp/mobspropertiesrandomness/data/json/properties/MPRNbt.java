package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class MPRNbt implements IMPRObject {

    @SerializedName("nbt_tag")
    public String nbtTag;
    public Type type;
    public MPRRange value;
    @SerializedName("persistent_data")
    public Boolean isPersistentData;

    public MPRConditions conditions;

    @Override
    public void validate() throws JsonValidationException {
        if (this.nbtTag == null || this.nbtTag.equals(""))
            throw new JsonValidationException("Missing or empty nbt_tag for Nbt object: %s".formatted(this));
        if (this.type == null)
            throw new JsonValidationException("Missing type for Nbt object: %s".formatted(this));
        if (this.value == null)
            throw new JsonValidationException("Missing value for Nbt object: %s".formatted(this));

        this.value.validate();

        if (this.isPersistentData == null)
            this.isPersistentData = false;

        if (this.conditions != null)
            this.conditions.validate();
    }

    public void apply(LivingEntity entity) {
        if (this.conditions != null && !this.conditions.conditionsApply(entity))
            return;

        CompoundTag nbt = new CompoundTag();
        if (!this.isPersistentData) {
            entity.addAdditionalSaveData(nbt);
            switch (this.type) {
                case DOUBLE -> nbt.putDouble(this.nbtTag, this.value.getFloatBetween(entity));
                case INTEGER -> nbt.putInt(this.nbtTag, this.value.getIntBetween(entity));
                case BOOLEAN -> nbt.putBoolean(this.nbtTag, entity.getRandom().nextFloat() < this.value.getFloatBetween(entity));
            }
            entity.readAdditionalSaveData(nbt);
        }
        else {
            nbt = entity.getPersistentData();
            switch (this.type) {
                case DOUBLE -> nbt.putDouble(this.nbtTag, this.value.getFloatBetween(entity));
                case INTEGER -> nbt.putInt(this.nbtTag, this.value.getIntBetween(entity));
                case BOOLEAN -> nbt.putBoolean(this.nbtTag, entity.getRandom().nextFloat() < this.value.getFloatBetween(entity));
            }
        }
    }

    public enum Type {
        @SerializedName("double")
        DOUBLE,
        @SerializedName("integer")
        INTEGER,
        @SerializedName("boolean")
        BOOLEAN,
    }

    @Override
    public String toString() {
        return String.format("Nbt{nbt_tag: %s, type: %s, value: %s, conditions: %s}", this.nbtTag, this.type, this.value, this.conditions);
    }
}
