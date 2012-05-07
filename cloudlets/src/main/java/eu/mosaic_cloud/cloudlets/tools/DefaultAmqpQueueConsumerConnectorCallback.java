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

package eu.mosaic_cloud.cloudlets.tools;


import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumeCallbackArguments;
import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.IAmqpQueueConsumerConnectorCallback;
import eu.mosaic_cloud.cloudlets.core.GenericCallbackCompletionArguments;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


/**
 * Default AMQP consumer callback.
 * 
 * @author Georgiana Macariu
 * 
 * @param <TContext>
 *            the context of the cloudlet
 * @param <TValue>
 *            the type of the values exchanged with the key-value store using
 *            this connector
 * @param <TExtra>
 *            the type of the extra data; as an example, this data can be used
 *            correlation
 */
public class DefaultAmqpQueueConsumerConnectorCallback<TContext, TValue, TExtra>
		extends DefaultAmqpQueueConnectorCallback<TContext>
		implements
			IAmqpQueueConsumerConnectorCallback<TContext, TValue, TExtra>
{
	@Override
	public CallbackCompletion<Void> acknowledgeFailed (final TContext context, final GenericCallbackCompletionArguments<TExtra> arguments)
	{
		return this.handleUnhandledCallback (arguments, "Acknowledge Failed", false, false);
	}
	
	@Override
	public CallbackCompletion<Void> acknowledgeSucceeded (final TContext context, final GenericCallbackCompletionArguments<TExtra> arguments)
	{
		return this.handleUnhandledCallback (arguments, "Acknowledge Succeeded", true, false);
	}
	
	@Override
	public CallbackCompletion<Void> consume (final TContext context, final AmqpQueueConsumeCallbackArguments<TValue> arguments)
	{
		return this.handleUnhandledCallback (arguments, "Consume", false, false);
	}
}
