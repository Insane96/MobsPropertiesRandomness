package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPresetLegacy;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MPRPresetReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
	public static final List<MPRPresetLegacy> MPR_PRESETS = new ArrayList<>();
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

		for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
			ResourceLocation key = entry.getKey();
			ResourceLocation id = filetoidconverter.fileToId(key);

			try (Reader reader = entry.getValue().openAsReader()) {
				JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
				JsonElement duplicated = map.put(id, jsonElement);
				if (duplicated != null)
					throw new IllegalStateException("Duplicate data file ignored with ID " + id);
			}
			catch (IllegalArgumentException | IOException | JsonParseException exception) {
				Logger.error("Error loading Preset %s: %s", key, exception.getMessage());
			}
		}

	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		MPR_PRESETS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				Logger.info("Loading Preset %s", entry.getKey());
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				MPRPresetLegacy preset = GSON.fromJson(entry.getValue(), MPRPresetLegacy.class);
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
