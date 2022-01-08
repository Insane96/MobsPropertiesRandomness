package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRModifiable;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRPosModifier;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRTimeExistedModifier;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty.MPRDifficultyModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Type;

@JsonAdapter(MPRModifiableValue.Deserializer.class)
public class MPRModifiableValue extends MPRModifiable implements IMPRObject {
	private Float value;

	public MPRModifiableValue(float value) {
		super(null, null, null, null);
		this.value = value;
	}

	public MPRModifiableValue(float value, @Nullable MPRDifficultyModifier difficultyModifier, @Nullable MPRPosModifier posModifier, @Nullable MPRTimeExistedModifier timeExistedModifier, @Nullable Integer round) {
		super(difficultyModifier, posModifier, timeExistedModifier, round);
		this.value = value;
	}

	public void validate(final File file) throws InvalidJsonException {
		if (this.value == null)
			throw new InvalidJsonException("Missing value. " + this, file);

		super.validate(file);
	}

	public float getValue(LivingEntity entity, World world) {
		float value = this.value;

		if (this.difficultyModifier != null)
			value = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getCurrentDifficultyAt(entity.blockPosition()).getEffectiveDifficulty(), value);

		if (this.posModifier != null)
			value = this.posModifier.applyModifier(world, entity.position(), value);

		if (this.timeExistedModifier != null)
			value = this.timeExistedModifier.applyModifier(world, entity, value);

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
			return new MPRModifiableValue(json.getAsJsonObject().get("value").getAsFloat(), context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class), context.deserialize(json.getAsJsonObject().get("pos_modifier"), MPRPosModifier.class), context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class), context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}
}
