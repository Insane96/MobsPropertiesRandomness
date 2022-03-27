package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.util.FileUtils;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRMobReloadListener extends SimplePreparableReloadListener<Void> {
	public static final List<MPRMob> MPR_MOBS = new ArrayList<>();

	public static final MPRMobReloadListener INSTANCE;

	public static File mobsFolder;

	public MPRMobReloadListener() {
		super();
	}

	@Override
	protected Void prepare(ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRMobReloadListener();
	}

	@Override
	protected void apply(Void objectIn, ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		Logger.info("Reloading Mobs");
		MPR_MOBS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(mobsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_") || file.getName().startsWith("."))
				continue;

			try {
				Logger.info(file.getName());
				FileReader fileReader = new FileReader(file);
				MPRMob mob = gson.fromJson(fileReader, MPRMob.class);
				Logger.debug(mob.toString());
				mob.validate(file);
				MPR_MOBS.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.error(Logger.getStackTrace(e));
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all Mobs");
		else
			Logger.warn("Reloaded all Mobs with error(s)");
	}
}
