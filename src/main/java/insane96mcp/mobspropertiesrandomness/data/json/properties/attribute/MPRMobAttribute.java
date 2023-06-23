package insane96mcp.mobspropertiesrandomness.data.json.properties.attribute;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class MPRMobAttribute extends MPRAttribute implements IMPRObject {
	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}

	public void apply(LivingEntity entity) {
		if (!this.shouldApply(entity))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		AttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.id + " not found for the entity, skipping the attribute");
			return;
		}

		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), this.modifierName, this.amount.getFloatBetween(entity), this.operation.get());
		attributeInstance.addPermanentModifier(modifier);

		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("MobAttribute{uuid: %s, id: %s, amount: %s, operation: %s, conditions: %s}", uuid, id, amount, operation, conditions);
	}
}
