package insane96mcp.mobspropertiesrandomness.json;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;

import java.io.File;

public class MPRPreset extends MPRProperties implements IMPRObject {
	public transient String name;

	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	@Override
	public String toString() {
		return String.format("Preset{name: %s, conditions: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", name, this.conditions, potionEffects, attributes, equipment, creeper, ghast);
	}
}
