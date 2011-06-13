
package eu.mosaic_cloud.components.examples.abacus;


import java.nio.ByteBuffer;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import eu.mosaic_cloud.callbacks.core.CallbackHandler;
import eu.mosaic_cloud.callbacks.core.CallbackReference;
import eu.mosaic_cloud.components.core.Component;
import eu.mosaic_cloud.components.core.ComponentCallReply;
import eu.mosaic_cloud.components.core.ComponentCallRequest;
import eu.mosaic_cloud.components.core.ComponentCallbacks;
import eu.mosaic_cloud.components.core.ComponentCastRequest;
import eu.mosaic_cloud.json.tools.DefaultJsonMapper;
import eu.mosaic_cloud.transcript.core.Transcript;
import eu.mosaic_cloud.transcript.tools.TranscriptExceptionTracer;


public final class AbacusComponentCallbacks
		extends Object
		implements
			ComponentCallbacks,
			CallbackHandler<ComponentCallbacks>
{
	public AbacusComponentCallbacks ()
	{
		super ();
		this.transcript = Transcript.create (this);
		this.exceptions = TranscriptExceptionTracer.create (this.transcript);
		this.component = null;
		this.status = Status.Created;
	}
	
	@Override
	public final CallbackReference called (final Component component, final ComponentCallRequest request)
	{
		Preconditions.checkState (this.status == Status.Initialized);
		Preconditions.checkState (this.component == component);
		Preconditions.checkArgument (request.data.remaining () == 0);
		if ("+".equals (request.operation)) {
			final List<?> operands;
			try {
				operands = DefaultJsonMapper.defaultInstance.decode (request.inputs, List.class);
				Preconditions.checkNotNull (operands);
			} catch (final Throwable exception) {
				this.exceptions.traceIgnoredException (exception);
				return (null);
			}
			this.transcript.traceInformation ("called `%s` with `%s`", request.operation, Joiner.on ("`, `").join (operands));
			double outcome;
			if ("+".equals (request.operation)) {
				outcome = 0;
				try {
					for (final Object operand : operands)
						outcome += ((Number) operand).doubleValue ();
				} catch (final Throwable exception) {
					this.exceptions.traceIgnoredException (exception);
					return (null);
				}
			} else
				throw (new IllegalArgumentException ());
			final ComponentCallReply reply = ComponentCallReply.create (true, Double.valueOf (outcome), ByteBuffer.allocate (0), request.reference);
			component.reply (reply);
		}
		return (null);
	}
	
	@Override
	public final CallbackReference casted (final Component component, final ComponentCastRequest request)
	{
		Preconditions.checkState (this.status == Status.Initialized);
		Preconditions.checkState (this.component == component);
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final void deassigned (final ComponentCallbacks trigger, final ComponentCallbacks newCallbacks)
	{
		throw (new IllegalStateException ());
	}
	
	@Override
	public final CallbackReference failed (final Component component, final Throwable exception)
	{
		Preconditions.checkState (this.status == Status.Initialized);
		Preconditions.checkState (this.component == component);
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final CallbackReference initialized (final Component component)
	{
		Preconditions.checkState (this.status == Status.Registered);
		Preconditions.checkState (this.component == null);
		this.component = component;
		this.status = Status.Initialized;
		this.transcript.traceInformation ("initialized;");
		return (null);
	}
	
	@Override
	public final void reassigned (final ComponentCallbacks trigger, final ComponentCallbacks oldCallbacks)
	{
		throw (new IllegalStateException ());
	}
	
	@Override
	public final void registered (final ComponentCallbacks trigger)
	{
		Preconditions.checkState (this.status == Status.Created);
		Preconditions.checkState (this.component == null);
		this.status = Status.Registered;
	}
	
	@Override
	public final CallbackReference replied (final Component component, final ComponentCallReply reply)
	{
		Preconditions.checkState (this.status == Status.Initialized);
		Preconditions.checkState (this.component == component);
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final CallbackReference terminated (final Component component)
	{
		Preconditions.checkState (this.status == Status.Initialized);
		Preconditions.checkState (this.component == component);
		this.component = null;
		this.status = Status.Terminated;
		this.transcript.traceInformation ("terminated;");
		return (null);
	}
	
	@Override
	public final void unregistered (final ComponentCallbacks trigger)
	{
		Preconditions.checkState ((this.status == Status.Registered) || (this.status == Status.Initialized) || (this.status == Status.Terminated));
		this.component = null;
		this.status = Status.Unregistered;
	}
	
	private Component component;
	private final TranscriptExceptionTracer exceptions;
	private Status status;
	private final Transcript transcript;
	
	private static enum Status
	{
		Created,
		Initialized,
		Registered,
		Terminated,
		Unregistered;
	}
}
