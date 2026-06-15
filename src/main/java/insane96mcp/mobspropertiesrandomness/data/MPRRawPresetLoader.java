package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MPRRawPresetLoader extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {

    private static final Gson GSON = new Gson();

    public static final Map<ResourceLocation, JsonElement> MODIFIER_PRESETS = new HashMap<>();
    public static final Map<ResourceLocation, JsonElement> CONDITION_PRESETS = new HashMap<>();
    public static final Map<ResourceLocation, JsonElement> FUNCTION_PRESETS = new HashMap<>();

    public static final MPRRawPresetLoader MODIFIER_LOADER =
            new MPRRawPresetLoader("mobs_properties_randomness/presets/modifiers", MODIFIER_PRESETS);
    public static final MPRRawPresetLoader CONDITION_LOADER =
            new MPRRawPresetLoader("mobs_properties_randomness/presets/conditions", CONDITION_PRESETS);
    public static final MPRRawPresetLoader FUNCTION_LOADER =
            new MPRRawPresetLoader("mobs_properties_randomness/presets/functions", FUNCTION_PRESETS);

    private final String directory;
    private final Map<ResourceLocation, JsonElement> target;

    private MPRRawPresetLoader(String directory, Map<ResourceLocation, JsonElement> target) {
        this.directory = directory;
        this.target = target;
    }

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        FileToIdConverter converter = FileToIdConverter.json(this.directory);

        for (Map.Entry<ResourceLocation, Resource> entry : converter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation key = entry.getKey();
            ResourceLocation id = converter.fileToId(key);
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                JsonElement duplicate = map.put(id, json);
                if (duplicate != null)
                    throw new IllegalStateException("Duplicate data file with ID " + id);
            } catch (Exception e) {
                MPRLogger.error("Error loading preset %s: %s", key, e.getMessage());
            }
        }
        return map;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.target.clear();
        this.target.putAll(map);
    }
}
