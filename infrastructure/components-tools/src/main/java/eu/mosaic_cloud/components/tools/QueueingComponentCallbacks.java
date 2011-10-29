
package eu.mosaic_cloud.components.tools;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.callbacks.core.CallbackHandler;
import eu.mosaic_cloud.callbacks.core.CallbackReference;
import eu.mosaic_cloud.components.core.Component;
import eu.mosaic_cloud.components.core.ComponentCallReference;
import eu.mosaic_cloud.components.core.ComponentCallReply;
import eu.mosaic_cloud.components.core.ComponentCallRequest;
import eu.mosaic_cloud.components.core.ComponentCallbacks;
import eu.mosaic_cloud.components.core.ComponentCastRequest;
import eu.mosaic_cloud.components.core.ComponentMessage;


public final class QueueingComponentCallbacks
		extends Object
		implements
			ComponentCallbacks,
			CallbackHandler<ComponentCallbacks>
{
	private QueueingComponentCallbacks (final Component component, final BlockingQueue<ComponentMessage> queue)
	{
		super ();
		Preconditions.checkNotNull (component);
		Preconditions.checkNotNull (queue);
		this.component = component;
		this.queue = queue;
	}
	
	public final void assign ()
	{
		this.component.assign (this);
	}
	
	@Override
	public final CallbackReference called (final Component component, final ComponentCallRequest request)
	{
		Preconditions.checkArgument (this.component == component);
		Preconditions.checkNotNull (request);
		this.queue.add (request);
		return (null);
	}
	
	@Override
	public final CallbackReference callReturned (final Component component, final ComponentCallReply reply)
	{
		Preconditions.checkArgument (this.component == component);
		this.queue.add (reply);
		return (null);
	}
	
	@Override
	public final CallbackReference casted (final Component component, final ComponentCastRequest request)
	{
		Preconditions.checkArgument (this.component == component);
		Preconditions.checkNotNull (request);
		this.queue.add (request);
		return (null);
	}
	
	@Override
	public final void deassigned (final ComponentCallbacks trigger, final ComponentCallbacks newCallbacks)
	{
		Preconditions.checkState (false);
	}
	
	@Override
	public final CallbackReference failed (final Component component, final Throwable exception)
	{
		Preconditions.checkArgument (this.component == component);
		return (null);
	}
	
	@Override
	public final CallbackReference initialized (final Component component)
	{
		Preconditions.checkArgument (this.component == component);
		return (null);
	}
	
	@Override
	public final void reassigned (final ComponentCallbacks trigger, final ComponentCallbacks oldCallbacks)
	{
		Preconditions.checkState (false);
	}
	
	@Override
	public final void registered (final ComponentCallbacks trigger)
	{}
	
	@Override
	public final CallbackReference registerReturn (final Component component, final ComponentCallReference reference, final boolean ok)
	{
		throw (new UnsupportedOperationException ());
	}
	
	@Override
	public final CallbackReference terminated (final Component component)
	{
		Preconditions.checkArgument (this.component == component);
		return (null);
	}
	
	@Override
	public final void unregistered (final ComponentCallbacks trigger)
	{}
	
	public final BlockingQueue<ComponentMessage> queue;
	private final Component component;
	
	public static final QueueingComponentCallbacks create (final Component component)
	{
		return (new QueueingComponentCallbacks (component, new LinkedBlockingQueue<ComponentMessage> ()));
	}
	
	public static final QueueingComponentCallbacks create (final Component component, final BlockingQueue<ComponentMessage> queue)
	{
		return (new QueueingComponentCallbacks (component, queue));
	}
}