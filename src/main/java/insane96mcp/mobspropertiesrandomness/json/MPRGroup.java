package insane96mcp.mobspropertiesrandomness.json;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;

import java.io.File;
import java.util.List;

public class MPRGroup implements IMPRObject {

	public transient String name;
	public List<String> mobs;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (mobs == null || mobs.isEmpty()) {
			throw new InvalidJsonException("Group " + this.name + " is missing mobs or has no mobs in the list", file);
		}
	}

	@Override
	public String toString() {
		return String.format("Group{name: %s, mobs: %s}", name, mobs);
	}
}
