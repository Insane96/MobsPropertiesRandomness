package insane96mcp.mpr.json;

import java.io.File;

import insane96mcp.mpr.exceptions.InvalidJsonException;

public interface IJsonObject {
	void validate(final File file) throws InvalidJsonException;
}
