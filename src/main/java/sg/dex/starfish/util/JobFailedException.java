package sg.dex.starfish.util;

/**
 * Exception class representing a failure during invoke job execution.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public class JobFailedException extends RuntimeException {

	public JobFailedException(String message, Exception e) {
		super(message,e);
	}

}