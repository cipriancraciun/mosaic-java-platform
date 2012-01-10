/*
 * #%L
 * mosaic-drivers
 * %%
 * Copyright (C) 2010 - 2012 eAustria Research Institute (Timisoara, Romania)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.mosaic_cloud.driver;


import com.google.common.base.Preconditions;

import eu.mosaic_cloud.callbacks.core.CallbackHandler;
import eu.mosaic_cloud.callbacks.core.CallbackReference;
import eu.mosaic_cloud.components.core.Component;
import eu.mosaic_cloud.components.core.ComponentCallReference;
import eu.mosaic_cloud.components.core.ComponentCallbacks;
import eu.mosaic_cloud.components.core.ComponentCastRequest;
import eu.mosaic_cloud.components.core.ComponentIdentifier;
import eu.mosaic_cloud.core.configuration.ConfigUtils;
import eu.mosaic_cloud.core.configuration.IConfiguration;
import eu.mosaic_cloud.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.core.log.MosaicLogger;
import eu.mosaic_cloud.driver.interop.AbstractDriverStub;
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