
package eu.mosaic_cloud.transcript.implementations.slf4j;


import com.google.common.base.Preconditions;
import eu.mosaic_cloud.exceptions.core.ExceptionResolution;
import eu.mosaic_cloud.tools.ExtendedFormatter;
import eu.mosaic_cloud.transcript.core.TranscriptBackend;
import eu.mosaic_cloud.transcript.core.TranscriptTraceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Slf4jTranscriptBackend
		extends Object
		implements
			TranscriptBackend
{
	Slf4jTranscriptBackend (final Logger logger, final ExtendedFormatter formatter)
	{
		super ();
		Preconditions.checkNotNull (logger);
		Preconditions.checkNotNull (formatter);
		this.logger = logger;
		this.formatter = formatter;
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception)
	{
		this.trace (this.map (resolution), null, null, exception);
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception, final String message)
	{
		this.trace (this.map (resolution), message, null, exception);
	}
	
	@Override
	public final void trace (final ExceptionResolution resolution, final Throwable exception, final String format, final Object ... tokens)
	{
		this.trace (this.map (resolution), format, tokens, exception);
	}
	
	@Override
	public final void trace (final TranscriptTraceType type, final String message)
	{
		this.trace (type, message, null, null);
	}
	
	@Override
	public final void trace (final TranscriptTraceType type, final String format, final Object ... tokens)
	{
		this.trace (type, format, tokens, null);
	}
	
	private final String format (final String format, final Object[] tokens)
	{
		if (format == null) {
			if (tokens != null)
				throw (new IllegalArgumentException ());
			return ("");
		}
		if (tokens == null)
			return (format);
		return (this.formatter.format (format, tokens));
	}
	
	private final TranscriptTraceType map (final ExceptionResolution resolution)
	{
		switch (resolution) {
			case Handled :
				return (TranscriptTraceType.Debugging);
			case Deferred :
				return (TranscriptTraceType.Debugging);
			case Ignored :
				return (TranscriptTraceType.Warning);
		}
		return (TranscriptTraceType.Error);
	}
	
	private final void trace (final TranscriptTraceType type, final String format, final Object[] tokens, final Throwable exception)
	{
		Preconditions.checkNotNull (type);
		switch (type) {
			case Information :
				if (this.logger.isInfoEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.info (message, exception);
					else
						this.logger.info (message);
				}
				break;
			case Warning :
				if (this.logger.isWarnEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.warn (message, exception);
					else
						this.logger.warn (message);
				}
				break;
			case Error :
				if (this.logger.isErrorEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.error (message, exception);
					else
						this.logger.error (message);
				}
				break;
			case Debugging :
				if (this.logger.isDebugEnabled ()) {
					final String message = this.format (format, tokens);
					if (exception != null)
						this.logger.debug (message, exception);
					else
						this.logger.debug (message);
				}
				break;
		}
	}
	
	private final ExtendedFormatter formatter;
	private final Logger logger;
	
	public static final Slf4jTranscriptBackend create (final Class<?> owner)
	{
		Preconditions.checkNotNull (owner);
		return (new Slf4jTranscriptBackend (LoggerFactory.getLogger (owner), ExtendedFormatter.defaultInstance));
	}
	
	public static final Slf4jTranscriptBackend create (final Object owner)
	{
		Preconditions.checkNotNull (owner);
		return (new Slf4jTranscriptBackend (LoggerFactory.getLogger (owner.getClass ()), ExtendedFormatter.defaultInstance));
	}
}
