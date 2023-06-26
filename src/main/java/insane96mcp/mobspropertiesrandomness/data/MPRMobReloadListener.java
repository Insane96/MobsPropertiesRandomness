package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MPRMobReloadListener extends SimpleJsonResourceReloadListener {
	public static List<MPRMob> MPR_MOBS = new ArrayList<>();
	public static final MPRMobReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();

	public MPRMobReloadListener() {
		super(GSON, "mobs_properties_randomness/mobs");
	}

	static {
		INSTANCE = new MPRMobReloadListener();
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
				mob.validate();
				MPR_MOBS.add(mob);
				Logger.info("Loaded Mob %s", entry.getKey());
			}
			catch (JsonValidationException e) {
				Logger.error("Validation error loading Mob %s: %s", entry.getKey(), e.getMessage());
			}
			catch (JsonSyntaxException e) {
				Logger.error("Parsing error loading Mob %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				Logger.error("Failed loading Mob %s: %s", entry.getKey(), e.getMessage());
			}
		}

		MPR_MOBS = MPR_MOBS.stream().sorted(Comparator.comparing(mob -> mob.priority)).collect(Collectors.toList());

		Logger.info("Loaded %s Mob(s)", MPR_MOBS.size());
	}
}
