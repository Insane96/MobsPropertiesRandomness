package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRModifiableValue.Deserializer.class)
public class MPRModifiableValue extends MPRModifiable implements IMPRObject {
	protected Float value;

	public MPRModifiableValue(Float value) {
		this(value, null, null, null, null, null, null);
	}

	//TODO think about a builder
	public MPRModifiableValue(Float value, @Nullable MPRDifficultyModifier difficultyModifier, @Nullable MPRWorldSpawnDistanceModifier worldSpawnDistanceModifier, @Nullable MPRDepthModifier depthModifier, @Nullable MPRTimeExistedModifier timeExistedModifier, @Nullable List<MPRConditionModifier> conditionsModifier, @Nullable Integer round) {
		super(difficultyModifier, worldSpawnDistanceModifier, depthModifier, timeExistedModifier, conditionsModifier, round);
		this.value = value;
	}

	public void validate() throws JsonValidationException {
		if (this.value == null)
			throw new JsonValidationException("Missing \"value\" in Modifiable Value. " + this);

		super.validate();
	}

	public float getValue(LivingEntity entity) {
		return this.applyModifiersAndRound(entity, this.value);
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
			return new MPRModifiableValue(context.deserialize(json.getAsJsonObject().get("value"), Float.class),
					context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class),
					context.deserialize(json.getAsJsonObject().get("world_spawn_distance_modifier"), MPRWorldSpawnDistanceModifier.class),
					context.deserialize(json.getAsJsonObject().get("depth_modifier"), MPRDepthModifier.class),
					context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class),
					context.deserialize(json.getAsJsonObject().get("conditions_modifier"), new TypeToken<List<MPRConditionModifier>>() {}.getType()),
					context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}
}
