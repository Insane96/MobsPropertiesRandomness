package insane96mcp.mobspropertiesrandomness.data.json.properties;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class MPRCustomName implements IMPRObject {

	public MPRModifiableValue chance;
	public List<String> overrides;
	public List<String> prefixes;
	public List<String> suffixes;

	@Override
	public void validate() throws JsonValidationException {
		if (this.chance != null)
			this.chance.validate();
		if ((this.overrides == null || this.overrides.size() == 0) && (this.prefixes == null || this.prefixes.size() == 0) && (this.suffixes == null || this.suffixes.size() == 0))
			throw new JsonValidationException("No overrides, prefixes or suffixes specified for Custom Name");
	}

	public void applyCustomName(LivingEntity entity) {
		if (this.chance != null && entity.level().random.nextDouble() >= this.chance.getValue(entity))
			return;

		String prefix = "";
		if (this.prefixes != null && this.prefixes.size() > 0)
			prefix = this.prefixes.get(entity.getRandom().nextInt(this.prefixes.size()));
		String suffix = "";
		if (this.suffixes != null && this.suffixes.size() > 0)
			suffix = this.suffixes.get(entity.getRandom().nextInt(this.suffixes.size()));

		Component component;
		if (this.overrides != null && this.overrides.size() > 0)
			component = Component.literal(prefix + this.overrides.get(entity.getRandom().nextInt(this.overrides.size())) + suffix);
		else
			component = Component.literal(prefix).append(entity.getName()).append(suffix);

		entity.setCustomName(component);
	}

	@Override
	public String toString() {
		return String.format("CustomName{chance: %s, overrides: %s, prefixes: %s, suffixes: %s}", chance, overrides, this.prefixes, this.suffixes);
	}
}
