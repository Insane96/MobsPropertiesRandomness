package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MPRPresetReloadListener extends SimpleJsonResourceReloadListener {
	public static final List<MPRPreset> MPR_PRESETS = new ArrayList<>();
	public static final MPRPresetReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();

	public MPRPresetReloadListener() {
		super(GSON, "mobs_properties_randomness/presets");
	}

	static {
		INSTANCE = new MPRPresetReloadListener();
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		MPR_PRESETS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				MPRPreset preset = GSON.fromJson(entry.getValue(), MPRPreset.class);
				preset.validate();
				preset.id = name;
				MPR_PRESETS.add(preset);
				Logger.info("Loaded Preset %s", entry.getKey());
			}
			catch (JsonValidationException e) {
				Logger.error("Validation error loading Preset %s: %s", entry.getKey(), e.getMessage());
			}
			catch (JsonSyntaxException e) {
				Logger.error("Parsing error loading Preset %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				Logger.error("Failed loading Preset %s: %s", entry.getKey(), e.getMessage());
			}
		}

		Logger.info("Loaded %s Presets", MPR_PRESETS.size());
	}
}
