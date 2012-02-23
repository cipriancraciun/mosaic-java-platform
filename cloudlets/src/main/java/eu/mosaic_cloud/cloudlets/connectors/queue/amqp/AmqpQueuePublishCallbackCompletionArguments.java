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

import eu.mosaic_cloud.cloudlets.core.CallbackCompletionArguments;
import eu.mosaic_cloud.cloudlets.core.ICloudletController;

/**
 * The arguments of the cloudlet callback methods for the publish request.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            the context of the cloudlet
 * @param <Message>
 *            the type of the published data
 */
public class AmqpQueuePublishCallbackCompletionArguments<Context, Message, Extra> extends
		CallbackCompletionArguments<Context> {

	private AmqpQueuePublishMessage<Message> message;
	private Extra extra;

	/**
	 * Creates a new callback argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param message
	 *            information about the publish request
	 */
	public AmqpQueuePublishCallbackCompletionArguments(ICloudletController<Context> cloudlet,
			AmqpQueuePublishMessage<Message> message, Extra extra) {
		super(cloudlet);
		this.message = message;
		this.extra = extra;
	}

	/**
	 * Returns information about the publish request.
	 * 
	 * @return information about the publish request
	 */
	public AmqpQueuePublishMessage<Message> getMessage() {
		return this.message;
	}

	public Extra getExtra() {
		return this.extra;
	}
}
