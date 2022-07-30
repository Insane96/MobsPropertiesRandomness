package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRModifiableValue.Deserializer.class)
public class MPRModifiableValue extends MPRModifiable implements IMPRObject {
	private Float value;

	public MPRModifiableValue(Float value) {
		this(value, null, null, null, null);
	}

	public MPRModifiableValue(Float value, @Nullable MPRDifficultyModifier difficultyModifier, @Nullable MPRPosModifier posModifier, @Nullable MPRTimeExistedModifier timeExistedModifier, @Nullable Integer round) {
		super(difficultyModifier, posModifier, timeExistedModifier, round);
		this.value = value;
	}

	public void validate() throws JsonValidationException {
		if (this.value == null)
			throw new JsonValidationException("Missing value for Modifiable Value. " + this);

		super.validate();
	}

	public float getValue(LivingEntity entity, Level level) {
		float value = this.value;

		if (this.difficultyModifier != null)
			value = this.difficultyModifier.applyModifier(level.getDifficulty(), value);

		if (this.posModifier != null)
			value = this.posModifier.applyModifier(level, entity.position(), value);

		if (this.timeExistedModifier != null)
			value = this.timeExistedModifier.applyModifier(level, entity, value);

		return this.round(value);
	}

	@Override
	public String toString() {
		return String.format("ModifiableValue{value: %f, %s}", this.value, super.toString());
	}

	public static class Deserializer implements JsonDeserializer<MPRModifiableValue> {
		@Override
		public MPRModifiableValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRModifiableValue(json.getAsFloat());
			return new MPRModifiableValue(context.deserialize(json.getAsJsonObject().get("value"), Float.class), context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class), context.deserialize(json.getAsJsonObject().get("pos_modifier"), MPRPosModifier.class), context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class), context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}
}
