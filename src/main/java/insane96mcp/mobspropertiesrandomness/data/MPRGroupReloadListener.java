package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MPRGroupReloadListener extends SimpleJsonResourceReloadListener {

	public static final MPRGroupReloadListener INSTANCE;
	public static final List<MPRGroup> MPR_GROUPS = new ArrayList<>();
	private static final Gson GSON = new GsonBuilder().create();

	public MPRGroupReloadListener() {
		super(GSON, "mobs_properties_randomness/groups");
	}

	static {
		INSTANCE = new MPRGroupReloadListener();
	}

	@Override
	protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
		MPR_GROUPS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				MPRGroup group = GSON.fromJson(entry.getValue(), MPRGroup.class);
				group.validate();
				group.id = name;
				MPR_GROUPS.add(group);
			}
			catch (JsonValidationException e) {
				Logger.error("Validation error loading Group %s: %s", entry.getKey(), e.getMessage());
			}
			catch (JsonSyntaxException e) {
				Logger.error("Parsing error loading Group %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				Logger.error("Failed loading Group %s: %s", entry.getKey(), e.getMessage());
			}
		}

		Logger.info("Loaded %s Groups", MPR_GROUPS.size());
	}
}
