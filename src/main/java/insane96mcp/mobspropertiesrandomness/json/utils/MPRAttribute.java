package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.difficulty.MPRDifficultyModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MPRAttribute implements IMPRObject {
	public String uuid;
	@SerializedName("attribute_id")
	public String attributeId;
	@SerializedName("modifier_name")
	public String modifierName;
	public MPRRange amount;
	public AttributeModifier.Operation operation;
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;
	//TODO Add MPRPosModifier

	//TODO Move to MPRWorld
	protected List<String> dimensions;
	public transient List<ResourceLocation> dimensionsList = new ArrayList<>();

	protected List<String> biomes;
	public transient List<ResourceLocation> biomesList = new ArrayList<>();

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (uuid == null)
			uuid = UUID.randomUUID().toString();

		if (attributeId == null)
			throw new InvalidJsonException("Missing Attribute Id for " + this, file);

		if (modifierName == null)
			throw new InvalidJsonException("Missing Modifier Name for " + this, file);

		if (amount == null)
			throw new InvalidJsonException("Missing Amount (Min/Max) for " + this, file);
		amount.validate(file);

		if (operation == null)
			throw new InvalidJsonException("Missing Operation for " + this, file);

		if (difficultyModifier != null)
			difficultyModifier.validate(file);

		dimensionsList.clear();
		if (dimensions != null) {
			for (String dimension : dimensions) {
				ResourceLocation dimensionRL = new ResourceLocation(dimension);
				dimensionsList.add(dimensionRL);
			}
		}

		biomesList.clear();
		if (biomes != null) {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				biomesList.add(biomeLoc);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Attribute{uuid: %s, attribute_id: %s, modifier_name: %s, amount: %s, operation: %s, difficulty_modifier: %s, dimensions: %s, biomes: %s}", uuid, attributeId, modifierName, amount, operation, difficultyModifier, dimensions, biomes);
	}
}