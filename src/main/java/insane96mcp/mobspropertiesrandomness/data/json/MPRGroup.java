package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.insanelib.exception.JsonValidationException;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MPRGroup implements IMPRObject {

	public transient ResourceLocation id;
	public List<String> mobs;

	public static final MPRGroup EMPTY = new MPRGroup();

	public MPRGroup() {
		this.mobs = new ArrayList<>();
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.mobs == null || this.mobs.isEmpty()) {
			throw new JsonValidationException("Group " + this.id + " is missing mobs or has no mobs in the list");
		}
	}

	@Override
	public String toString() {
		return String.format("Group{id: %s, mobs: %s}", this.id, this.mobs);
	}
}
