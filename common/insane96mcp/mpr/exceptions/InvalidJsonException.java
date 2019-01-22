package insane96mcp.mpr.exceptions;

import java.io.File;

public class InvalidJsonException extends Exception {
	private String message;
	private File file;
	
	public InvalidJsonException(String message, File file) {
		this.message = message;
		this.file = file;
	}
	
    public String getMessage()
    {
        return "Failed to parse JSON (File: " + file.getName() + "): " + this.message;
    }
}
