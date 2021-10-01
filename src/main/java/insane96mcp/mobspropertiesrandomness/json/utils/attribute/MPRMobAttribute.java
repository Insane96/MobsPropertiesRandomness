package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.UUID;

public class MPRMobAttribute extends MPRAttribute implements IMPRObject, IMPRAppliable {
	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (!this.shouldApply(entity, world))
			return;

		float min = this.amount.getMin(entity, world);
		float max = this.amount.getMax(entity, world);

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.id + " not found for the entity, skipping the attribute");
			return;
		}

		float amount = RandomHelper.getFloat(world.rand, min, max);

		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), this.modifierName, amount, operation);
		attributeInstance.applyPersistentModifier(modifier);

		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("MobAttribute{uuid: %s, id: %s, amount: %s, operation: %s, world_whitelist: %s}", uuid, id, amount, operation, worldWhitelist);
	}
}
