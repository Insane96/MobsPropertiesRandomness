package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPREvent;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRWeightedPreset;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MPRPresetReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
	public static final Map<ResourceLocation, MPRProperties> PRESETS = new HashMap<>();
	public static final MPRPresetReloadListener INSTANCE;
	private static final Gson GSON;
	private final String directory;

	public MPRPresetReloadListener() {
		this.directory = "mobs_properties_randomness/presets";
	}

	static {
		GSON = MPR.createGson();
		INSTANCE = new MPRPresetReloadListener();
	}

	@Override
	protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		Map<ResourceLocation, JsonElement> map = new HashMap<>();
		scanDirectory(resourceManager, this.directory, GSON, map);
		return map;
	}

	public static void scanDirectory(ResourceManager resourceManager, String directory, Gson gson, Map<ResourceLocation, JsonElement> map) {
		FileToIdConverter filetoidconverter = FileToIdConverter.json(directory);
		MPREvent.LOADED_EVENTS.clear();

		for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
			ResourceLocation key = entry.getKey();
			ResourceLocation id = filetoidconverter.fileToId(key);

			try (Reader reader = entry.getValue().openAsReader()) {
				JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
				JsonElement duplicated = map.put(id, jsonElement);
				if (duplicated != null)
					throw new IllegalStateException("Duplicate data file with ID " + id);
			}
			catch (Exception exception) {
				MPRLogger.error("Error loading Preset %s: %s", key, exception.getMessage());
			}
		}

	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		PRESETS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				MPRLogger.info("Loading Preset %s", entry.getKey());
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				MPRProperties preset = GSON.fromJson(entry.getValue(), MPRProperties.class);
                if (preset.getProperties().isEmpty())
                    throw new IllegalStateException("Preset has no properties");
				PRESETS.put(name, preset);
			}
			catch (JsonSyntaxException e) {
				MPRLogger.error("Parsing error loading Preset %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				MPRLogger.error("Failed loading Preset %s: %s", entry.getKey(), e.getMessage());
			}
		}
        PRESETS.forEach((id, preset) -> preset.getProperties()
                .forEach(property -> {
                    if (property instanceof MPRPresetsProperty mprPresetsProperty) {
                        MPRLogger.info("Resolving presets-in-presets for %s", id);
                        mprPresetsProperty.weightedPresets.forEach(MPRWeightedPreset::resolve);
                    }
                }));

		//Logger.info("Loaded %s Presets", PRESETS.size());
	}

	@Nullable
	public static ResourceLocation getKey(@NotNull MPRProperties preset) {
		for (Map.Entry<ResourceLocation, MPRProperties> entry : PRESETS.entrySet()) {
			if (entry.getValue().equals(preset))
				return entry.getKey();
		}
		return null;
	}
}
