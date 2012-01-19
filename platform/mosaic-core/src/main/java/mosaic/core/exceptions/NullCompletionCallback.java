package mosaic.core.exceptions;

/**
 * Exception thrown when no operation completion callback is set.
 * 
 * @author Georgiana Macariu
 * 
 */
public class NullCompletionCallback extends Exception {

	private static final long serialVersionUID = -3388438945086356985L;

	/**
	 * Constructs a new exception with null as its detail message. The cause is
	 * not initialized, and may subsequently be initialized by a call to
	 * {@link Throwable#initCause(Throwable)}.
	 */
	public NullCompletionCallback() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link Throwable#initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link Throwable#getMessage()} method
	 */
	public NullCompletionCallback(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of (cause==null ? null : cause.toString()) (which typically contains the
	 * class and detail message of cause). This constructor is useful for
	 * exceptions that are little more than wrappers for other throwables.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A null value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public NullCompletionCallback(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * Note that the detail message associated with cause is not automatically
	 * incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link Throwable#getMessage()} method
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A null value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public NullCompletionCallback(String message, Throwable cause) {
		super(message, cause);
	}

}