package insane96mcp.mobspropertiesrandomness.json;

import insane96mcp.insanelib.exception.JsonValidationException;

public class MPRPreset extends MPRProperties implements IMPRObject {
	public transient String name;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}

	@Override
	public String toString() {
		return String.format("Preset{name: %s, conditions: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", name, this.conditions, potionEffects, attributes, equipment, creeper, ghast);
	}
}
