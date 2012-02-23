/*
 * #%L
 * mosaic-cloudlets
 * %%
 * Copyright (C) 2010 - 2012 Institute e-Austria Timisoara (Romania)
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
package eu.mosaic_cloud.cloudlets.core;

import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

/**
 * Main interface for user cloudlets. All user cloudlets must implement this
 * interface.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            The type of the object encoding the context of the cloudlet.
 */
public interface ICloudletCallback<Context> extends ICallback<Context> {

	/**
	 * Initializes the user cloudlet.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> initialize(Context context, CloudletCallbackArguments<Context> arguments);

	/**
	 * Operation called after the cloudlet is successfully initialized.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> initializeSucceeded(Context context, CloudletCallbackCompletionArguments<Context> arguments);

	/**
	 * Operation called after the cloudlet is unsuccessfully initialized.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> initializeFailed(Context context, CloudletCallbackCompletionArguments<Context> arguments);

	/**
	 * Destrozs the user cloudlet.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> destroy(Context context, CloudletCallbackArguments<Context> arguments);

	/**
	 * Operation called after the cloudlet is successfully destroyed.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> destroySucceeded(Context context, CloudletCallbackCompletionArguments<Context> arguments);

	/**
	 * Operation called after the cloudlet is unsuccessfully destroyed.
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            here, this argument just gives access to the cloudlet
	 *            controller
	 */
	CallbackCompletion<Void> destroyFailed(Context context, CloudletCallbackCompletionArguments<Context> arguments);
}
