/*
 * #%L
 * mosaic-cloudlets
 * %%
 * Copyright (C) 2010 - 2013 Institute e-Austria Timisoara (Romania)
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

package eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.component;


import eu.mosaic_cloud.components.core.ComponentIdentifier;
import eu.mosaic_cloud.components.core.ComponentResourceDescriptor;
import eu.mosaic_cloud.components.core.ComponentResourceSpecification;
import eu.mosaic_cloud.platform.implementation.v2.cloudlets.connectors.core.BaseConnector;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnectorCallbacks;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnectorCallbacks.AcquireFailedArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnectorCallbacks.AcquireSucceededArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnectorCallbacks.CallFailedArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnectorCallbacks.CallSucceededArguments;
import eu.mosaic_cloud.platform.v2.cloudlets.core.CloudletController;
import eu.mosaic_cloud.platform.v2.configuration.Configuration;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletionObserver;


public class ComponentConnector<TContext, TExtra>
			extends BaseConnector<eu.mosaic_cloud.platform.v2.connectors.component.ComponentConnector, ComponentConnectorCallbacks<TContext, TExtra>, TContext>
			implements
				eu.mosaic_cloud.platform.v2.cloudlets.connectors.component.ComponentConnector<TExtra>
{
	public ComponentConnector (final CloudletController<?> cloudlet, final eu.mosaic_cloud.platform.v2.connectors.component.ComponentConnector connector, final Configuration configuration, final ComponentConnectorCallbacks<TContext, TExtra> callback, final TContext context) {
		super (cloudlet, connector, configuration, callback, context);
	}
	
	@Override
	public final CallbackCompletion<ComponentResourceDescriptor> acquire (final ComponentResourceSpecification resource) {
		return (this.connector.acquire (resource));
	}
	
	@Override
	public final CallbackCompletion<ComponentResourceDescriptor> acquire (final ComponentResourceSpecification resource, final TExtra extra) {
		this.transcript.traceDebugging ("acquiring the resource `%s`...", resource.identifier);
		final CallbackCompletion<ComponentResourceDescriptor> completion = this.connector.acquire (resource);
		if (this.callback != null) {
			completion.observe (new CallbackCompletionObserver () {
				@SuppressWarnings ("synthetic-access")
				@Override
				public CallbackCompletion<Void> completed (final CallbackCompletion<?> completion_) {
					assert (completion_ == completion);
					if (completion.getException () != null) {
						ComponentConnector.this.transcript.traceDebugging ("triggering the callback for acquire failure for resource `%s` and extra `%{object}`...", resource.identifier, extra);
						return ComponentConnector.this.callback.acquireFailed (ComponentConnector.this.context, new AcquireFailedArguments<TExtra> (ComponentConnector.this.cloudlet, ComponentConnector.this, resource, completion.getException (), extra));
					}
					ComponentConnector.this.transcript.traceDebugging ("triggering the callback for acquire success for resource `%s` and extra `%{object}`...", resource.identifier, extra);
					return ComponentConnector.this.callback.acquireSucceeded (ComponentConnector.this.context, new AcquireSucceededArguments<TExtra> (ComponentConnector.this.cloudlet, ComponentConnector.this, resource, completion.getOutcome (), extra));
				}
			});
		}
		return completion;
	}
	
	@Override
	public final <TInputs, TOutputs> CallbackCompletion<TOutputs> call (final ComponentIdentifier component, final String operation, final TInputs inputs, final Class<TOutputs> outputs) {
		return (this.connector.call (component, operation, inputs, outputs));
	}
	
	@Override
	public final <TInputs, TOutputs> CallbackCompletion<TOutputs> call (final ComponentIdentifier component, final String operation, final TInputs inputs, final Class<TOutputs> outputs, final TExtra extra) {
		this.transcript.traceDebugging ("calling to the resource `%s` with the operation `%s`...", component.string, operation);
		final CallbackCompletion<TOutputs> completion = this.connector.call (component, operation, inputs, outputs);
		if (this.callback != null) {
			completion.observe (new CallbackCompletionObserver () {
				@SuppressWarnings ("synthetic-access")
				@Override
				public CallbackCompletion<Void> completed (final CallbackCompletion<?> completion_) {
					assert (completion_ == completion);
					if (completion.getException () != null) {
						ComponentConnector.this.transcript.traceDebugging ("triggering the callback for call failure to the component `%s` with the operation `%s` and extra `%{object}`...", component.string, operation, extra);
						return ComponentConnector.this.callback.callFailed (ComponentConnector.this.context, new CallFailedArguments<TInputs, TOutputs, TExtra> (ComponentConnector.this.cloudlet, ComponentConnector.this, component, operation, inputs, outputs, completion.getException (), extra));
					}
					ComponentConnector.this.transcript.traceDebugging ("triggering the callback for call success to the component `%s` with the operation `%s` and extra `%{object}`...", component.string, operation, extra);
					return ComponentConnector.this.callback.callSucceeded (ComponentConnector.this.context, new CallSucceededArguments<TInputs, TOutputs, TExtra> (ComponentConnector.this.cloudlet, ComponentConnector.this, component, operation, inputs, outputs, completion.getOutcome (), extra));
				}
			});
		}
		return completion;
	}
	
	@Override
	public final <TInputs> CallbackCompletion<Void> cast (final ComponentIdentifier component, final String operation, final TInputs inputs) {
		this.transcript.traceDebugging ("casting to the component `%s` with the operation `%s`...", component.string, operation);
		final CallbackCompletion<Void> completion = this.connector.cast (component, operation, inputs);
		return completion;
	}
	
	@Override
	public CallbackCompletion<Void> destroy () {
		return this.destroy (false);
	}
	
	@Override
	public CallbackCompletion<Void> initialize () {
		return this.initialize (false);
	}
}
