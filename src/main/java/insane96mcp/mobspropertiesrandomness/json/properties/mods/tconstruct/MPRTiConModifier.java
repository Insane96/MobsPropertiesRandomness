package insane96mcp.mobspropertiesrandomness.json.properties.mods.tconstruct;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.lang.reflect.Type;

@JsonAdapter(MPRTiConModifier.Deserializer.class)
public class MPRTiConModifier implements IMPRObject {

	public String id;
	public MPRRange level;
	public MPRModifiableValue chance;

	private transient ModifierId modifierId;

	private MPRTiConModifier(String id, MPRRange level, MPRModifiableValue chance) {
		this.id = id;
		this.level = level;
		this.chance = chance;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id for TiCon Modifier %s".formatted(this));
		this.modifierId = ModifierId.tryParse(this.id);
		if (modifierId == null)
			throw new JsonValidationException("Invalid id for TiCon Modifier %s".formatted(this.id));
		if (!ModifierManager.INSTANCE.contains(this.modifierId))
			throw new JsonValidationException("Modifier does not exist. %s".formatted(this.id));

		if (this.level != null)
			this.level.validate();
		else
			this.level = new MPRRange(1);

		if (this.chance != null)
			this.chance.validate();
	}

	public ItemStack applyToStack(LivingEntity entity, Level level, ItemStack itemStack) {
		if (this.chance != null && level.random.nextFloat() >= this.chance.getValue(entity, level))
			return itemStack;

		ToolStack toolStack = ToolStack.copyFrom(itemStack);
		toolStack.addModifier(this.modifierId, this.level.getInt(entity, level));
		return toolStack.createStack();
	}

	public static class Deserializer implements JsonDeserializer<MPRTiConModifier> {
		@Override
		public MPRTiConModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			//Deserialize only if tinkers construct is loaded
			if (!ModList.get().isLoaded("tconstruct"))
				throw new JsonParseException("Tinkers' Construct is not present. This object can't be used: %s.".formatted(json));

			return new MPRTiConModifier(json.getAsJsonObject().get("id").getAsString(), context.deserialize(json.getAsJsonObject().get("level"), MPRRange.class), context.deserialize(json.getAsJsonObject().get("chance"), MPRModifiableValue.class));
		}
	}
}
