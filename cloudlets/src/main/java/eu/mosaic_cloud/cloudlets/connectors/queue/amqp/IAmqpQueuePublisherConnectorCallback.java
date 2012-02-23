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
package eu.mosaic_cloud.cloudlets.connectors.queue.amqp;

import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

/**
 * Interface for AMQP queue publishers. This will be implemented by cloudlets
 * which need to send messages to an exchange.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            the type of the cloudlet context
 * @param <Message>
 *            the type of published data
 */
public interface IAmqpQueuePublisherConnectorCallback<Context, Message, Extra> extends
		IAmqpQueueConnectorCallback<Context> {

	/**
	 * Called when the publisher receives confirmation that the message
	 * publishing finished successfully.
	 * 
	 * @param <D>
	 *            the type of the published message
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            the arguments of the callback
	 */
	CallbackCompletion<Void> publishSucceeded(Context context,
			AmqpQueuePublishCallbackCompletionArguments<Context, Message, Extra> arguments);

	/**
	 * Called when the publisher receives notification that the message
	 * publishing could not be finished with success.
	 * 
	 * 
	 * @param context
	 *            the context of the cloudlet
	 * @param arguments
	 *            the arguments of the callback
	 */
	CallbackCompletion<Void> publishFailed(Context context,
			AmqpQueuePublishCallbackCompletionArguments<Context, Message, Extra> arguments);
}
