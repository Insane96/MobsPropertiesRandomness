package insane96mcp.mobspropertiesrandomness.data.json.properties.mods.tconstruct;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonAdapter(MPRTiConMaterials.Deserializer.class)
public class MPRTiConMaterials implements IMPRObject {

	public List<String> materials;
	public MPRRandomTiConMaterial random;

	private final transient List<IMaterial> _materials;

	private MPRTiConMaterials(@Nullable String[] materials, @Nullable MPRRandomTiConMaterial random) {
		if (materials == null)
			this.materials = null;
		else
			this.materials = Arrays.asList(materials);
		this._materials = new ArrayList<>();
		this.random = random;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.materials == null && this.random == null)
			throw new JsonParseException("Missing materials or random for TiCon Materials %s".formatted(this));

		if (this.materials != null) {
			for (String material : this.materials) {
				MaterialId materialId = MaterialId.tryParse(material);
				if (materialId == null)
					throw new JsonValidationException("%s is not a valid material. Must be a resource location namespace:entry_id.".formatted(material));
				IMaterial iMaterial = MaterialRegistry.getMaterial(materialId);
				if (iMaterial == IMaterial.UNKNOWN)
					throw new JsonParseException("%s material does not exist.".formatted(material));
				this._materials.add(iMaterial);
			}
		}

		if (this.random != null) {
			this.random.validate();
		}
	}

	public ItemStack applyToStack(LivingEntity entity, Level level, ItemStack itemStack) {
		ToolStack toolStack = ToolStack.copyFrom(itemStack);
		MaterialNBT.Builder builder = MaterialNBT.builder();
		for (IMaterial material : this._materials) {
			builder.add(material);
		}
		MaterialNBT materialNBT = builder.build();
		if (this.random != null){
			materialNBT = this.random.getMaterials(entity, level, itemStack);
		}
		toolStack.setMaterials(materialNBT);
		return toolStack.createStack();
	}

	@Override
	public String toString() {
		return String.format("TiConMaterials{materials: %s}", _materials);
	}

	public static class Deserializer implements JsonDeserializer<MPRTiConMaterials> {
		@Override
		public MPRTiConMaterials deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			//Deserialize only if tinkers construct is loaded
			if (!ModList.get().isLoaded("tconstruct"))
				throw new JsonParseException("Tinkers' Construct is not present. This object can't be used: %s.".formatted(json));

			return new MPRTiConMaterials(context.deserialize(json.getAsJsonObject().get("materials"), String[].class), context.deserialize(json.getAsJsonObject().get("random"), MPRRandomTiConMaterial.class));
		}
	}
}
