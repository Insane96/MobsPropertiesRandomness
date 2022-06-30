package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.io.IOException;
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
			throw new JsonValidationException("Invalid id for TiCon Modifier %s".formatted(this));
		if (!ModifierManager.INSTANCE.contains(this.modifierId))
			throw new JsonValidationException("Modifier does not exist. %s".formatted(this));

		if (this.level != null)
			this.level.validate();

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

	public static class SafeTypeAdapterFactory implements TypeAdapterFactory {
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
			return new TypeAdapter<T>() {
				@Override
				public void write(JsonWriter out, T value) throws IOException { }

				@Override
				public T read(JsonReader jsonReader) throws IOException {
					if (!ModList.get().isLoaded("tconstruct"))
						return null;

					return delegate.read(jsonReader);
				}
			};
		}
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
