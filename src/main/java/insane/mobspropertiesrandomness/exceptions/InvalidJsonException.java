package insane.mobspropertiesrandomness.exceptions;

import java.io.File;

public class InvalidJsonException extends Exception {
	private String message;
	private String objectFault;

	public InvalidJsonException(String message, Object fault) {
		this.message = message;
		this.objectFault = fault.toString();
	}

	public String getMessage()
	{
		return String.format("%s", String.format(this.message, this.objectFault));
	}
}
