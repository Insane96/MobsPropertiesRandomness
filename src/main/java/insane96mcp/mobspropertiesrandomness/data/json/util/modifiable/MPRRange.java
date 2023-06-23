package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRRange.Deserializer.class)
public class MPRRange implements IMPRObject {
	private MPRModifiableValue min;
	private MPRModifiableValue max;

	public MPRRange(MPRModifiableValue min, @Nullable MPRModifiableValue max) {
		this.min = min;
		if (max != null)
			this.max = max;
		else
			this.max = this.min;
	}

	public MPRRange(MPRModifiableValue min) {
		this(min, null);
	}

	public MPRRange(float min, float max) {
		this(new MPRModifiableValue(min), new MPRModifiableValue(max));
	}

	public MPRRange(float min) {
		this(new MPRModifiableValue(min));
	}

	public void validate() throws JsonValidationException {
		if (this.min == null)
			throw new JsonValidationException("Missing min. " + this);
		else
			this.min.validate();

		if (this.max == null) {
			Logger.debug("Missing max for " + this + ". Max will be equal to min.");
			this.max = this.min;
		}
		else
			this.max.validate();
	}

	public float getMin(LivingEntity entity) {
		return this.min.getValue(entity);
	}

	public float getMax(LivingEntity entity) {
		return this.max.getValue(entity);
	}

	/**
	 * Returns a random float value between min and max
	 */
	public float getFloatBetween(LivingEntity entity) {
		return Mth.nextFloat(entity.level.random, this.getMin(entity), this.getMax(entity));
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getIntBetween(LivingEntity entity) {
		return Mth.nextInt(entity.level.random, (int) this.getMin(entity), (int) this.getMax(entity));
	}

	@Override
	public String toString() {
		return String.format("Range{min: %s, max: %s}", this.min, this.max);
	}

	public static class Deserializer implements JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(new MPRModifiableValue(json.getAsFloat()));
			MPRModifiableValue min = context.deserialize(json.getAsJsonObject().get("min"), MPRModifiableValue.class);
			if (min == null)
				min = context.deserialize(json.getAsJsonObject().get("value"), MPRModifiableValue.class);
			return new MPRRange(min,
					context.deserialize(json.getAsJsonObject().get("max"), MPRModifiableValue.class));
		}
	}


}