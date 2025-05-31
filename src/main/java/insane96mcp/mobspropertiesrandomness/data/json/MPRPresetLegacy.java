package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import net.minecraft.resources.ResourceLocation;

public class MPRPresetLegacy extends MPRPropertiesLegacy implements IMPRObject {
	@JsonAdapter(ResourceLocation.Serializer.class)
	public transient ResourceLocation id;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}
}
