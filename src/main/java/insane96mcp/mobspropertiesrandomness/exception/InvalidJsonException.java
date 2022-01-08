package insane96mcp.mobspropertiesrandomness.exception;

import java.io.File;

//TODO Probably scrap this
public class InvalidJsonException extends Exception {
	private final String message;
	private final File file;

	public InvalidJsonException(String message, File file) {
		this.message = message;
		this.file = file;
	}

	public String getMessage() {
		return "Failed to parse JSON (File: " + file.getName() + "): " + this.message;
	}
}
