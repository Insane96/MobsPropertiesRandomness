package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.utils.FileUtils;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRPresetReloadListener extends ReloadListener<Void> {
	public static final List<MPRMob> MPR_PRESETS = new ArrayList<>();

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
				MPRMob mob = gson.fromJson(fileReader, MPRMob.class);
				Logger.debug(mob.toString());
				mob.validate(file);
				MPR_PRESETS.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				//Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all Presets");
		else
			Logger.warn("Reloaded all Presets with error(s)");
	}
}
