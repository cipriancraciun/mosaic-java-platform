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
package eu.mosaic_cloud.cloudlets.connectors.core;

import eu.mosaic_cloud.cloudlets.core.CallbackArguments;
import eu.mosaic_cloud.cloudlets.core.ICallback;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

/**
 * Basic interface for resource accessor callback classes.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            the type of the cloudlet context
 */
public interface IConnectorCallback<Context> extends ICallback<Context> {

	/**
	 * Called when resource accessor initialization succeeded.
	 * 
	 * @param context
	 *            cloudlet context
	 * @param arguments
	 *            callback arguments
	 */
	CallbackCompletion<Void> initializeSucceeded(Context context, CallbackArguments<Context> arguments);

	/**
	 * Called when resource accessor initialization failed.
	 * 
	 * @param context
	 *            cloudlet context
	 * @param arguments
	 *            callback arguments
	 */
	CallbackCompletion<Void> initializeFailed(Context context, CallbackArguments<Context> arguments);

	/**
	 * Called when resource accessor destruction succeeded.
	 * 
	 * @param context
	 *            cloudlet context
	 * @param arguments
	 *            callback arguments
	 */
	CallbackCompletion<Void> destroySucceeded(Context context, CallbackArguments<Context> arguments);

	/**
	 * Called when resource accessor destruction failed.
	 * 
	 * @param context
	 *            cloudlet context
	 * @param arguments
	 *            callback arguments
	 */
	CallbackCompletion<Void> destroyFailed(Context context, CallbackArguments<Context> arguments);
}