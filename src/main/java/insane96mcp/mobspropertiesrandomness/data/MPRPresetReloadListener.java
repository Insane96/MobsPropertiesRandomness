package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.utils.FileUtils;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRPresetReloadListener extends ReloadListener<Void> {
	public static final List<MPRPreset> MPR_PRESETS = new ArrayList<>();

	public static final MPRPresetReloadListener INSTANCE;

	public static File presetsFolder;

	public MPRPresetReloadListener() {
		super();
	}

	@Override
	protected Void prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRPresetReloadListener();
	}

	@Override
	protected void apply(Void objectIn, IResourceManager iResourceManager, IProfiler iProfiler) {
		Logger.info("Reloading Presets");
		MPR_PRESETS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(presetsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;

			try {
				Logger.info(file.getName());
				FileReader fileReader = new FileReader(file);
				MPRPreset preset = gson.fromJson(fileReader, MPRPreset.class);
				preset.name = FilenameUtils.removeExtension(file.getName());
				Logger.debug(preset.toString());
				preset.validate(file);
				MPR_PRESETS.add(preset);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.error(Logger.getStackTrace(e));
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all Presets");
		else
			Logger.warn("Reloaded all Presets with error(s)");
	}
}
