package insane96mcp.mobspropertiesrandomness.json.util;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class MPRTiConModifier implements IMPRObject {

	public String id;
	public MPRRange level;
	public MPRModifiableValue chance;

	private transient ModifierId modifierId;

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
		toolStack.setMaterials(ToolBuildHandler.randomMaterials(toolStack.getDefinition().getData(), 1, false));
		return toolStack.createStack();
	}

	@Override
	public String toString() {
		return String.format("TiConModifier{id: %s, level: %s, chance: %s}", this.id, this.level, this.chance);
	}
}
