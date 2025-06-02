package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRModifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class MPRModifiable {
	public List<MPRModifier> modifiers;

	//Rounds the final result to this decimal places
	public Integer round;

	public MPRModifiable(List<MPRModifier> modifiers, Integer round) {
		this.modifiers = modifiers;
		this.round = round;
	}

	public double applyModifiers(double value, LivingEntity living) {
		for (MPRModifier modifier : this.modifiers)
			value = modifier.tryApply(value, living);
		return value;
	}

	public double applyModifiersAndRound(double value, LivingEntity living) {
		return this.round(this.applyModifiers(value, living));
	}

	public double round(double value) {
		if (this.round == null)
			return value;
		return MathHelper.round(value, this.round);
	}

	public static List<MPRModifier> deserializeList(JsonObject jObject, JsonDeserializationContext context) {
		if (!jObject.has("modifiers"))
			return new ArrayList<>();
		return MPRModifier.deserializeList(jObject, "modifiers", context);
	}

	public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
		if (!this.modifiers.isEmpty())
			jObject.add("modifiers", context.serialize(this.modifiers));
		if (this.round != null)
			jObject.addProperty("round", this.round);
		return jObject;
	}
}
