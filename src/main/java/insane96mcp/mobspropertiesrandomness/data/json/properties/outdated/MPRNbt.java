package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class MPRNbt implements IMPRObject {

    @SerializedName("nbt_tag")
    public String nbtTag;
    public NBTType type;
    public MPRRange value;
    @SerializedName("persistent_data")
    public Boolean isPersistentData;

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
    }

    public void apply(LivingEntity entity) {
        CompoundTag nbt = new CompoundTag();
        if (!this.isPersistentData) {
            entity.saveWithoutId(nbt);
            switch (this.type) {
                case DOUBLE -> nbt.putDouble(this.nbtTag, this.value.getFloatBetween(entity));
                case INTEGER -> nbt.putInt(this.nbtTag, this.value.getIntBetween(entity));
                case BOOLEAN -> nbt.putBoolean(this.nbtTag, entity.getRandom().nextFloat() < this.value.getFloatBetween(entity));
            }
            entity.load(nbt);
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

    public enum NBTType {
        @SerializedName("double")
        DOUBLE,
        @SerializedName("integer")
        INTEGER,
        @SerializedName("boolean")
        BOOLEAN,
    }
}
