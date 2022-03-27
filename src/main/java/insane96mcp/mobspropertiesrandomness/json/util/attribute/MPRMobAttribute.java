package insane96mcp.mobspropertiesrandomness.json.util.attribute;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.UUID;

public class MPRMobAttribute extends MPRAttribute implements IMPRObject, IMPRAppliable {
	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	@Override
	public void apply(LivingEntity entity, Level world) {
		if (!this.shouldApply(entity, world))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		AttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.id + " not found for the entity, skipping the attribute");
			return;
		}

		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), this.modifierName, this.amount.getFloatBetween(entity, world), this.operation.get());
		attributeInstance.addPermanentModifier(modifier);

		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("MobAttribute{uuid: %s, id: %s, amount: %s, operation: %s, world_whitelist: %s}", uuid, id, amount, operation, worldWhitelist);
	}
}
