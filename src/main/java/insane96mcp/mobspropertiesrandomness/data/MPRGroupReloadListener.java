package insane96mcp.mobspropertiesrandomness.data;

import com.google.gson.Gson;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.json.MPRGroup;
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

public class MPRGroupReloadListener extends ReloadListener<Void> {
	public static final List<MPRGroup> MPR_GROUPS = new ArrayList<>();

	public static final MPRGroupReloadListener INSTANCE;

	public MPRGroupReloadListener() {
		super();
	}

	@Override
	protected Void prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
		return null;
	}

	static {
		INSTANCE = new MPRGroupReloadListener();
	}

	@Override
	protected void apply(Void objectIn, IResourceManager iResourceManager, IProfiler iProfiler) {
		File groupsFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/groups");
		if (!groupsFolder.exists())
			groupsFolder.mkdir();

		MPR_GROUPS.clear();

		boolean correctlyReloaded = true;
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = FileUtils.ListFilesForFolder(groupsFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;

			try {
				Logger.info("Loading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				MPRGroup group = gson.fromJson(fileReader, MPRGroup.class);
				group.name = FilenameUtils.removeExtension(file.getName());
				Logger.debug(group.toString());
				group.validate(file);
				MPR_GROUPS.add(group);
			} catch (Exception e) {
				correctlyReloaded = false;
				//Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}

		if (correctlyReloaded)
			Logger.info("Correctly reloaded all Groups");
		else
			Logger.info("Reloaded all Groups with error(s)");
	}

	public boolean doesGroupExist(String name) {
		for (MPRGroup group : MPR_GROUPS) {
			if (group.name.equals(name))
				return true;
		}
		return false;
	}
}
