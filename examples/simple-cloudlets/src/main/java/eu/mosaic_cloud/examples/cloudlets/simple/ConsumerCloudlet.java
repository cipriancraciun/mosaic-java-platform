/*
 * #%L
 * mosaic-examples-simple-cloudlets
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
package eu.mosaic_cloud.examples.cloudlets.simple;

import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumeCallbackArguments;
import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumeMessage;
import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.AmqpQueueConsumerConnector;
import eu.mosaic_cloud.cloudlets.connectors.queue.amqp.IAmqpQueueConsumerConnector;
import eu.mosaic_cloud.cloudlets.core.CallbackArguments;
import eu.mosaic_cloud.cloudlets.core.ICallback;
import eu.mosaic_cloud.cloudlets.core.ICloudletController;
import eu.mosaic_cloud.cloudlets.tools.DefaultAmqpQueueConsumerConnectorCallback;
import eu.mosaic_cloud.cloudlets.tools.DefaultCloudletCallback;
import eu.mosaic_cloud.platform.core.configuration.ConfigurationIdentifier;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.utils.DataEncoder;
import eu.mosaic_cloud.platform.core.utils.PojoDataEncoder;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;

public class ConsumerCloudlet {

	public static final class LifeCycleHandler extends
			DefaultCloudletCallback<ConsumerCloudletContext> {

		@Override
		public CallbackCompletion<Void> initialize(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet is being initialized.");
			ICloudletController<ConsumerCloudletContext> cloudlet = arguments
					.getCloudlet();
			IConfiguration configuration = cloudlet.getConfiguration();
			IConfiguration queueConfiguration = configuration
					.spliceConfiguration(ConfigurationIdentifier
							.resolveAbsolute("queue"));
			DataEncoder<String> encoder = new PojoDataEncoder<String>(
					String.class);
			context.consumer = new AmqpQueueConsumerConnector<ConsumerCloudlet.ConsumerCloudletContext, String>(
					queueConfiguration, cloudlet, String.class, encoder);
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> initializeSucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet initialized successfully.");
			ICloudletController<ConsumerCloudletContext> cloudlet = arguments
					.getCloudlet();
			cloudlet.initializeResource(context.consumer,
					new AmqpConsumerCallback(), context);
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> destroy(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet is being destroyed.");
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> destroySucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"Consumer cloudlet was destroyed successfully.");
			return ICallback.SUCCESS;
		}

	}

	public static final class AmqpConsumerCallback extends
			DefaultAmqpQueueConsumerConnectorCallback<ConsumerCloudletContext, String> {

		@Override
		public CallbackCompletion<Void> registerSucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet consumer registered successfully.");
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> unregisterSucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet consumer unregistered successfully.");
			// if unregistered as consumer is successful then destroy resource
			ICloudletController<ConsumerCloudletContext> cloudlet = arguments
					.getCloudlet();
			cloudlet.destroyResource(context.consumer, this);
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> initializeSucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			// if resource initialized successfully then just register as a
			// consumer
			context.consumer.register();
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> destroySucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			this.logger.info(
					"ConsumerCloudlet consumer was destroyed successfully.");
			context.consumer = null;
			arguments.getCloudlet().destroy();
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> acknowledgeSucceeded(ConsumerCloudletContext context,
				CallbackArguments<ConsumerCloudletContext> arguments) {
			context.consumer.unregister();
			return ICallback.SUCCESS;
		}

		@Override
		public CallbackCompletion<Void> consume(
				ConsumerCloudletContext context,
				AmqpQueueConsumeCallbackArguments<ConsumerCloudletContext, String> arguments) {

			AmqpQueueConsumeMessage<String> message = arguments.getMessage();
			String data = message.getData();
			this.logger.info(
					"ConsumerCloudlet received logging message for user "
							+ data);
			message.acknowledge();
			return ICallback.SUCCESS;
		}

	}

	public static final class ConsumerCloudletContext {

		IAmqpQueueConsumerConnector<ConsumerCloudletContext, String> consumer;
	}
}
