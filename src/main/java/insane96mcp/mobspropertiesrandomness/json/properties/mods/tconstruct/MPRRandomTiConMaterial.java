package insane96mcp.mobspropertiesrandomness.json.properties.mods.tconstruct;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRModifiableValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class MPRRandomTiConMaterial implements IMPRObject {

	@SerializedName("max_tier")
	public MPRModifiableValue maxTier;

	public MPRRandomTiConMaterial(MPRModifiableValue maxTier) {
		this.maxTier = maxTier;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.maxTier == null) {
			throw new JsonValidationException("Missing tier for random TiCon Material %s".formatted(this));
		}
	}

	@Nullable
	public MaterialNBT getMaterials(LivingEntity entity, Level level, ItemStack itemStack) {
		ToolStack toolStack = ToolStack.copyFrom(itemStack);
		return ToolBuildHandler.randomMaterials(toolStack.getDefinition().getData(), (int) this.maxTier.getValue(entity, level), false);
	}

	@Override
	public String toString() {
		return String.format("RandomTiConMaterial{tier: %s}", maxTier);
	}

	public static class Deserializer implements JsonDeserializer<MPRRandomTiConMaterial> {
		@Override
		public MPRRandomTiConMaterial deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (!ModList.get().isLoaded("tconstruct"))
				throw new JsonParseException("Tinkers' Construct is not present. This object can't be used: %s.".formatted(json));

			return new Gson().fromJson(json, MPRRandomTiConMaterial.class);
		}
	}
}
