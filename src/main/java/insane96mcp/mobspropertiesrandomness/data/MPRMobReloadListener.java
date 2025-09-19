package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRWeightedPreset;
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
import java.util.*;

public class MPRMobReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
	public static List<MPRMob> MPR_MOBS = new ArrayList<>();
	public static final MPRMobReloadListener INSTANCE;
	private static final Gson GSON;
	private final String directory;

	public MPRMobReloadListener() {
		this.directory = "mobs_properties_randomness/mobs";
	}

	static {
		GSON = MPR.createGson();
		INSTANCE = new MPRMobReloadListener();
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
				Logger.error("Error loading Mob %s: %s", key, exception.getMessage());
			}
		}

	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		MPR_MOBS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				Logger.info("Loading Mob %s", entry.getKey());
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				MPRMob mob = GSON.fromJson(entry.getValue(), MPRMob.class);
				MPR_MOBS.add(mob);
				Logger.info("Loaded Mob %s", entry.getKey());
			}
			catch (Exception e) {
				StringBuilder sb = new StringBuilder("Failed loading Mob " + entry.getKey() + ": " + e.getMessage());
				for (StackTraceElement s : e.getStackTrace())
					sb.append("\n").append(s.toString());
				Logger.error(sb.toString());
			}
		}
        MPR_MOBS.forEach(mob -> mob.getProperties()
                .forEach(property -> {
                    if (property instanceof MPRPresetsProperty mprPresetsProperty) {
                        mprPresetsProperty.weightedPresets.forEach(MPRWeightedPreset::resolve);
                    }
                }));

		MPR_MOBS.sort(Comparator.comparing(mob -> mob.priority, Comparator.reverseOrder()));

		Logger.info("Loaded %s Mob(s)", MPR_MOBS.size());
	}
}
