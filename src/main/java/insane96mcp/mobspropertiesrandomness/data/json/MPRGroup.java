package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.insanelib.exception.JsonValidationException;

import java.util.List;

public class MPRGroup implements IMPRObject {

	public transient String name;
	public List<String> mobs;

	@Override
	public void validate() throws JsonValidationException {
		if (mobs == null || mobs.isEmpty()) {
			throw new JsonValidationException("Group " + this.name + " is missing mobs or has no mobs in the list");
		}
	}

	@Override
	public String toString() {
		return String.format("Group{name: %s, mobs: %s}", name, mobs);
	}
}
