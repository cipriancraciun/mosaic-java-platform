package mosaic.driver;

import mosaic.core.configuration.ConfigUtils;
import mosaic.core.configuration.IConfiguration;
import mosaic.core.exceptions.ExceptionTracer;
import mosaic.core.log.MosaicLogger;
import mosaic.driver.interop.AbstractDriverStub;

import com.google.common.base.Preconditions;

import eu.mosaic_cloud.callbacks.core.CallbackHandler;
import eu.mosaic_cloud.callbacks.core.CallbackReference;
import eu.mosaic_cloud.components.core.Component;
import eu.mosaic_cloud.components.core.ComponentCallReference;
import eu.mosaic_cloud.components.core.ComponentCallbacks;
import eu.mosaic_cloud.components.core.ComponentCastRequest;
import eu.mosaic_cloud.components.core.ComponentIdentifier;
import eu.mosaic_cloud.exceptions.tools.AbortingExceptionTracer;
import eu.mosaic_cloud.interoperability.core.SessionSpecification;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;
import eu.mosaic_cloud.tools.Monitor;

/**
 * This callback class enables a resource driver to be exposed as a component.
 * Upon initialization it will look for the resource and will create a driver
 * object for the resource.
 * 
 * @author Georgiana Macariu
 * 
 */
public abstract class AbstractDriverComponentCallbacks implements
		ComponentCallbacks, CallbackHandler<ComponentCallbacks> {

	protected static enum Status {
		Created, Registered, Terminated, Unregistered, WaitingResourceResolved;
	}

	protected Status status;
	protected Component component;
	protected Monitor monitor;
	protected ComponentCallReference pendingReference;
	protected AbstractDriverStub stub;
	protected ComponentIdentifier resourceGroup;
	protected ComponentIdentifier selfGroup;
	protected IConfiguration driverConfiguration;

	public void terminate() {
		synchronized (this.monitor) {
			Preconditions.checkState(this.component != null);
			this.component.terminate();
		}
	}

	@Override
	public CallbackReference casted(Component component,
			ComponentCastRequest request) {
		synchronized (this.monitor) {
			Preconditions.checkState(this.component == component);
			Preconditions.checkState((this.status != Status.Terminated)
					&& (this.status != Status.Unregistered));
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public CallbackReference failed(Component component, Throwable exception) {
		MosaicLogger.getLogger().trace("AMQP driver callback failed.");
		synchronized (this.monitor) {
			Preconditions.checkState(this.component == component);
			Preconditions.checkState((this.status != Status.Terminated)
					&& (this.status != Status.Unregistered));
			if (this.stub != null) {
				this.stub.destroy();
			}
			this.component = null; // NOPMD by georgiana on 10/10/11 1:56 PM
			this.status = Status.Terminated;
			ExceptionTracer.traceIgnored(exception);
		}
		return null;
	}

	@Override
	public CallbackReference terminated(Component component) {
		synchronized (this.monitor) {
			Preconditions.checkState(this.component == component);
			Preconditions.checkState((this.status != Status.Terminated)
					&& (this.status != Status.Unregistered));
			if (this.stub != null) {
				this.stub.destroy();
				MosaicLogger.getLogger().trace("Driver callbacks terminated.");
			}
			this.component = null; // NOPMD by georgiana on 10/10/11 1:56 PM
			this.status = Status.Terminated;
		}
		return null;
	}

	public abstract void deassigned(ComponentCallbacks trigger,
			ComponentCallbacks newCallbacks);

	public abstract void reassigned(ComponentCallbacks trigger,
			ComponentCallbacks oldCallbacks);

	public abstract void registered(ComponentCallbacks trigger);

	public abstract void unregistered(ComponentCallbacks trigger);

	protected ZeroMqChannel createDriverChannel(String channelIdentifierProp,
			String channelEndpointProp, SessionSpecification role) {
		// create stub and interop channel
		Preconditions.checkNotNull(this.driverConfiguration);
		ZeroMqChannel driverChannel = new ZeroMqChannel(
				ConfigUtils.resolveParameter(this.driverConfiguration,
						channelIdentifierProp, String.class, ""),
				AbortingExceptionTracer.defaultInstance);
		driverChannel.register(role);
		driverChannel.accept(ConfigUtils
				.resolveParameter(this.driverConfiguration,
						channelEndpointProp, String.class, ""));
		return driverChannel;
	}

	protected IConfiguration getDriverConfiguration() {
		return driverConfiguration;
	}

	protected void setDriverConfiguration(IConfiguration driverConfiguration) {
		this.driverConfiguration = driverConfiguration;
	}

}