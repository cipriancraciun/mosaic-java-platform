/*
 * #%L
 * mosaic-examples-realtime-feeds-indexer
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
package eu.mosaic_cloud.examples.realtime_feeds.indexer;

import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumeCallbackArguments;
import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumeMessage;
import eu.mosaic_cloud.cloudlets.core.CallbackArguments;
import eu.mosaic_cloud.cloudlets.core.ICallback;
import eu.mosaic_cloud.cloudlets.tools.DefaultAmqpQueueConsumerConnectorCallback;
import eu.mosaic_cloud.examples.realtime_feeds.indexer.IndexerCloudlet.IndexerCloudletContext;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import org.json.JSONObject;

public class QueueConsumerCallback extends
		DefaultAmqpQueueConsumerConnectorCallback<IndexerCloudletContext, JSONObject, Void> {

	@Override
	public CallbackCompletion<Void> registerSucceeded(IndexerCloudletContext context,
			CallbackArguments<IndexerCloudletContext> arguments) {
		this.logger.info(
				"Index Message consumer registered successfully.");
		return ICallback.SUCCESS;
	}

	@Override
	public CallbackCompletion<Void> consume(
			IndexerCloudletContext context,
			AmqpQueueConsumeCallbackArguments<IndexerCloudletContext, JSONObject, Void> arguments) {
		AmqpQueueConsumeMessage<JSONObject> message = arguments.getMessage();

		IndexWorkflow.indexNewFeed(context, message);
		message.acknowledge();
		return ICallback.SUCCESS;
	}

}