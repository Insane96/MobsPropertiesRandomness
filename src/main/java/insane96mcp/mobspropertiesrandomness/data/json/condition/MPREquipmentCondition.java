package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREquipmentCondition.Serializer.class)
public class MPREquipmentCondition extends MPRCondition {
	public EquipmentSlot slot;
	@Nullable
	public List<Item> items;
	@Nullable
	public TagKey<Item> itemTag;

	public MPREquipmentCondition(EquipmentSlot slot, @Nullable List<Item> items, @Nullable TagKey<Item> itemTag, boolean inverted) {
		super(inverted);
		this.slot = slot;
		this.items = items;
		this.itemTag = itemTag;
	}

	@Override
	protected boolean conditionCheck(LivingEntity living) {
		ItemStack itemBySlot = living.getItemBySlot(this.slot);
		if (this.itemTag != null)
			return itemBySlot.is(this.itemTag);
		if (this.items != null) {
			for (Item item : this.items) {
				if (itemBySlot.is(item))
					return true;
			}
		}
		return false;
	}

	public static class Serializer implements JsonDeserializer<MPREquipmentCondition>, JsonSerializer<MPREquipmentCondition> {
		@Override
		public MPREquipmentCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			if (!jObject.has("items"))
				throw new JsonParseException("missing 'items'");

			EquipmentSlot slot = GsonHelper.getAsObject(jObject, "slot", context, EquipmentSlot.class);
			boolean inverted = MPRCondition.deserializeInverted(jObject);
			JsonElement itemsElement = jObject.get("items");

			if (itemsElement.isJsonPrimitive()) {
				String itemsString = itemsElement.getAsString();
				if (itemsString.startsWith("#")) {
					TagKey<Item> itemTag = TagKey.create(Registries.ITEM, ResourceLocation.parse(itemsString.substring(1)));
					return new MPREquipmentCondition(slot, null, itemTag, inverted);
				}
				else {
					//noinspection DataFlowIssue
					List<Item> items = List.of(SerializerUtils.deserializeRegistryObject(jObject, "items", Registries.ITEM));
					return new MPREquipmentCondition(slot, items, null, inverted);
				}
			}
			else if (itemsElement.isJsonArray()) {
				List<Item> items = SerializerUtils.deserializeRegistryObjectList(jObject, "items", context, Registries.ITEM);
				return new MPREquipmentCondition(slot, items, null, inverted);
			}
			else {
				throw new JsonParseException("'items' must be a string or an array");
			}
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
