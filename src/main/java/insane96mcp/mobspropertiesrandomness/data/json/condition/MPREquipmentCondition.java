package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREquipmentCondition.Serializer.class)
public class MPREquipmentCondition extends MPRCondition {
	public EquipmentSlot slot;
	public List<Item> items;

	public MPREquipmentCondition(EquipmentSlot slot, List<Item> items, boolean inverted) {
		super(inverted);
		this.slot = slot;
		this.items = items;
	}

	@Override
	protected boolean conditionCheck(LivingEntity living) {
		for (Item item : this.items) {
			if (living.getItemBySlot(this.slot).is(item))
				return true;
		}
		return false;
	}

	public static class Serializer implements JsonDeserializer<MPREquipmentCondition>, JsonSerializer<MPREquipmentCondition> {
		@Override
		public MPREquipmentCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			return new MPREquipmentCondition(
					GsonHelper.getAsObject(jObject, "slot", context, EquipmentSlot.class), SerializerUtils.deserializeRegistryObjectList(jObject, "items", context, Registries.ITEM), MPRCondition.deserializeInverted(jObject));
		}

		@Override
		public JsonElement serialize(MPREquipmentCondition src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("slot", context.serialize(src.slot));
			jObject.add("items", SerializerUtils.serializeRegistryObjectList(jObject, src.items, context, Registries.ITEM));
			return src.endSerialization(jObject);
		}
	}

}
