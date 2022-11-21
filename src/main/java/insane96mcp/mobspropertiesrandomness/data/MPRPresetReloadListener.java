package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.FileUtils;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRPresetReloadListener extends SimplePreparableReloadListener<Void> {
	public static final List<MPRPreset> MPR_PRESETS = new ArrayList<>();

	public static final MPRPresetReloadListener INSTANCE;

	public static File presetsFolder;

	public MPRPresetReloadListener() {
		super();
	}

	@Override
	protected Void prepare(ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRPresetReloadListener();
	}

	@Override
	protected void apply(Void objectIn, ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		Logger.info("Reloading Presets");
		MPR_PRESETS.clear();

		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(presetsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_' or comma '.'
			if (file.getName().startsWith("_") || file.getName().startsWith("."))
				continue;

			try {
				FileReader fileReader = new FileReader(file);
				MPRPreset preset = gson.fromJson(fileReader, MPRPreset.class);
				preset.name = FilenameUtils.removeExtension(file.getName());
				preset.validate();
				MPR_PRESETS.add(preset);
			}
			catch (JsonValidationException e) {
				Logger.error("Validation error loading Preset %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
			catch (JsonSyntaxException e) {
				Logger.error("Parsing error loading Preset %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
			catch (Exception e) {
				Logger.error("Failed loading Preset %s: %s", FilenameUtils.removeExtension(file.getName()), e.getMessage());
			}
		}

		Logger.info("Loaded %s Presets", MPR_PRESETS.size());
	}
}
