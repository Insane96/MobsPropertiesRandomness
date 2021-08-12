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

public class MPRMobReloadListener extends ReloadListener<Void> {
	public static final List<MPRMob> MPR_MOBS = new ArrayList<>();

	public static final MPRMobReloadListener INSTANCE;

	public static File jsonFolder;

	public MPRMobReloadListener() {
		super();
	}

	@Override
	protected Void prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRMobReloadListener();
	}

	@Override
	protected void apply(Void objectIn, IResourceManager iResourceManager, IProfiler iProfiler) {
		MPR_MOBS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(jsonFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;

			try {
				Logger.info("Loading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				MPRMob mob = gson.fromJson(fileReader, MPRMob.class);
				Logger.debug(mob.toString());
				mob.validate(file);
				MPR_MOBS.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				//Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all JSONs");
		else
			Logger.info("Reloaded all JSONs with error(s)");
	}
}
