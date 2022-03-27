package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import insane96mcp.mobspropertiesrandomness.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.util.FileUtils;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MPRGroupReloadListener extends SimplePreparableReloadListener<Void> {
	public static final List<MPRGroup> MPR_GROUPS = new ArrayList<>();

	public static final MPRGroupReloadListener INSTANCE;

	public static File groupsFolder;

	public MPRGroupReloadListener() {
		super();
	}

	@Override
	protected Void prepare(ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRGroupReloadListener();
	}

	@Override
	protected void apply(Void objectIn, ResourceManager iResourceManager, ProfilerFiller iProfiler) {
		Logger.info("Reloading Groups");
		MPR_GROUPS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(groupsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_") || file.getName().startsWith("."))
				continue;

			try {
				Logger.info(file.getName());
				FileReader fileReader = new FileReader(file);
				MPRGroup group = gson.fromJson(fileReader, MPRGroup.class);
				group.name = FilenameUtils.removeExtension(file.getName());
				Logger.debug(group.toString());
				group.validate(file);
				MPR_GROUPS.add(group);
			} catch (Exception e) {
				correctlyReloaded = false;
				//Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(Logger.getStackTrace(e));
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all Groups");
		else
			Logger.warn("Reloaded all Groups with error(s)");
	}

	public boolean doesGroupExist(String name) {
		for (MPRGroup group : MPR_GROUPS) {
			if (group.name.equals(name))
				return true;
		}
		return false;
	}
}
