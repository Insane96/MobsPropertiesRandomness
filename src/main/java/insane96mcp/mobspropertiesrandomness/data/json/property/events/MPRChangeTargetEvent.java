package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRChangeTargetEvent.Serializer.class)
public class MPRChangeTargetEvent extends MPREvent {
	public final ChangeType changeType;

	public MPRChangeTargetEvent(ChangeType changeType, ResourceLocation id, Target target, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, applyProperties, conditions);
		this.changeType = changeType;
	}

	public void switchTarget(LivingEntity entity, @Nullable LivingEntity newTarget, @Nullable LivingEntity oldTarget) {
		switch (this.changeType) {
			case NEW_TARGET -> {
				if (oldTarget == null && newTarget != null)
					this.tryExecute(entity, newTarget);
			}
			case SWITCH_TARGET -> {
				if (oldTarget != null && newTarget != null && oldTarget != newTarget)
					this.tryExecute(entity, newTarget);
			}
			case LOST_TARGET -> {
				if (oldTarget != null && newTarget == null)
					this.tryExecute(entity, null);
			}
		}
	}

	public static void onTargetChange(LivingChangeTargetEvent event) {
		Mob mob = (Mob) event.getEntity();
		List<MPRChangeTargetEvent> events = getEvents(mob, MPRChangeTargetEvent.class);
		for (MPRChangeTargetEvent switchTargetEvent : events)
			switchTargetEvent.switchTarget(mob, event.getNewAboutToBeSetTarget(), mob.getTarget());
	}

	public static class Serializer implements JsonDeserializer<MPRChangeTargetEvent>, JsonSerializer<MPRChangeTargetEvent> {
		@Override
		public MPRChangeTargetEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			String id = GsonHelper.getAsString(jObject, "id");
			Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
			List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
			List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

			return new MPRChangeTargetEvent(GsonHelper.getAsObject(jObject, "type", context, ChangeType.class), ResourceLocation.parse(id), target, properties, conditions);
		}

		@Override
		public JsonElement serialize(MPRChangeTargetEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("type", context.serialize(src.changeType));
			return src.endSerialization(jObject, context, false);
		}
	}

	public enum ChangeType {
		@SerializedName("new_target")
		NEW_TARGET,
		@SerializedName("switch_target")
		SWITCH_TARGET,
		@SerializedName("lost_target")
		LOST_TARGET
	}
}
