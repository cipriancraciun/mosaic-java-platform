/*
 * #%L
 * mosaic-cloudlets
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
package eu.mosaic_cloud.cloudlet.resources.amqp;

import java.util.ArrayList;
import java.util.List;

import eu.mosaic_cloud.cloudlet.core.CallbackArguments;
import eu.mosaic_cloud.cloudlet.core.ICloudletController;
import eu.mosaic_cloud.cloudlet.core.OperationResultCallbackArguments;
import eu.mosaic_cloud.cloudlet.resources.IResourceAccessorCallback;
import eu.mosaic_cloud.core.configuration.IConfiguration;
import eu.mosaic_cloud.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.core.log.MosaicLogger;
import eu.mosaic_cloud.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.core.utils.DataEncoder;
import eu.mosaic_cloud.driver.queue.amqp.AmqpOutboundMessage;


/**
 * This class provides access for cloudlets to an AMQP-based queueing system as
 * a message publisher.
 * 
 * @author Georgiana Macariu
 * 
 * @param <S>
 *            the type of the cloudlet state object
 * @param <D>
 *            the type of the messages published by the cloudlet
 */
public class AmqpQueuePublisher<S, D extends Object> extends
		AmqpQueueAccessor<S, D> implements IAmqpQueuePublisher<S, D> {

	private IAmqpQueuePublisherCallback<S, D> callback;

	/**
	 * Creates a new AMQP publisher.
	 * 
	 * @param config
	 *            configuration data required by the accessor:
	 *            <ul>
	 *            <li>amqp.publisher.exchange - the exchange to publish the
	 *            messages to</li>
	 *            <li>amqp.publisher.routing_key - the routing key of the
	 *            messages</li>
	 *            <li>amqp.publisher.manadatory - true if we are requesting a
	 *            mandatory publish</li>
	 *            <li>amqp.publisher.immediate - true if we are requesting an
	 *            immediate publish</li>
	 *            <li>amqp.publisher.durable - true if messages must not be lost
	 *            even if server shutdowns unexpectedly</li>
	 *            </ul>
	 * @param cloudlet
	 *            the cloudlet controller of the cloudlet using the accessor
	 * @param dataClass
	 *            the type of the published messages
	 * @param encoder
	 *            encoder used for serializing data
	 */
	public AmqpQueuePublisher(IConfiguration config,
			ICloudletController<S> cloudlet, Class<D> dataClass,
			DataEncoder<D> encoder) {
		super(config, cloudlet, dataClass, false, encoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.mosaic_cloud.cloudlet.resources.amqp.AmqpQueueAccessor#initialize(eu.mosaic_cloud.cloudlet
	 * .resources.IResourceAccessorCallback, java.lang.Object)
	 */
	@Override
	public void initialize(IResourceAccessorCallback<S> callback, S state) {
		synchronized (this) {
			if (callback instanceof IAmqpQueuePublisherCallback) {
				super.initialize(callback, state);
				this.callback = (IAmqpQueuePublisherCallback<S, D>) callback;
			} else {
				IllegalArgumentException e = new IllegalArgumentException(
						"The callback argument must be of type " //$NON-NLS-1$
								+ IAmqpQueuePublisherCallback.class
										.getCanonicalName());
				@SuppressWarnings("unchecked")
				IAmqpQueuePublisherCallback<S, D> proxy = this.cloudlet
						.buildCallbackInvoker(this.callback,
								IAmqpQueuePublisherCallback.class);
				CallbackArguments<S> arguments = new OperationResultCallbackArguments<S, Boolean>(
						AmqpQueuePublisher.this.cloudlet, e);
				proxy.initializeFailed(state, arguments);
				throw e;
			}
		}
	}

	@Override
	public void register() {
		// declare queue and in case of success register as consumer
		synchronized (this) {
			startRegister(this.callback);
		}
	}

	@Override
	protected void finishRegister(IAmqpQueueAccessorCallback<S> callback) {
		if (AmqpQueuePublisher.super.registered)
			return;
		CallbackArguments<S> arguments = new CallbackArguments<S>(
				AmqpQueuePublisher.super.cloudlet);
		this.callback.registerSucceeded(AmqpQueuePublisher.this.cloudletState,
				arguments);
		synchronized (AmqpQueuePublisher.this) {
			AmqpQueuePublisher.super.registered = true;
		}

	}

	@Override
	public void unregister() {
		synchronized (this) {
			if (!AmqpQueuePublisher.super.registered)
				return;
			AmqpQueuePublisher.super.registered = false;
		}
		CallbackArguments<S> arguments = new CallbackArguments<S>(
				AmqpQueuePublisher.super.cloudlet);
		this.callback.unregisterSucceeded(
				AmqpQueuePublisher.this.cloudletState, arguments);
	}

	@Override
	public void publish(D data, final Object token, String contentType) {
		try {
			byte[] sData = this.dataEncoder.encode(data);
			final AmqpOutboundMessage message = new AmqpOutboundMessage(
					this.exchange, this.routingKey, sData, true, true, false,
					contentType);

			IOperationCompletionHandler<Boolean> cHandler = new PublishCompletionHandler(
					message, token);
			List<IOperationCompletionHandler<Boolean>> handlers = new ArrayList<IOperationCompletionHandler<Boolean>>();
			handlers.add(cHandler);

			super.getConnector().publish(message, handlers,
					this.cloudlet.getResponseInvocationHandler(cHandler));
			MosaicLogger.getLogger().trace(
					"AmqpQueuePublisher - published message " + data);
		} catch (Exception e) {
			ExceptionTracer.traceDeferred(e);
			@SuppressWarnings("unchecked")
			IAmqpQueuePublisherCallback<S, D> proxy = this.cloudlet
					.buildCallbackInvoker(this.callback,
							IAmqpQueuePublisherCallback.class);
			AmqpQueuePublishMessage<D> pMessage = new AmqpQueuePublishMessage<D>(
					AmqpQueuePublisher.this, null, token);
			AmqpQueuePublishCallbackArguments<S, D> arguments = new AmqpQueuePublishCallbackArguments<S, D>(
					AmqpQueuePublisher.this.cloudlet, pMessage);
			proxy.publishFailed(AmqpQueuePublisher.this.cloudletState,
					arguments);
		}

	}

	@Override
	public void publish(D data, final Object token, String contentType,
			String selector) {
		try {
			// FIXME this is a hack
			String routingKey = (selector != null) ? this.queue + "."
					+ selector : this.routingKey;
			String exchange = (selector != null) ? "" : this.exchange;
			byte[] sData = this.dataEncoder.encode(data);
			final AmqpOutboundMessage message = new AmqpOutboundMessage(
					exchange, routingKey, sData, true, true, false, null, null,
					contentType, null, null);

			IOperationCompletionHandler<Boolean> cHandler = new PublishCompletionHandler(
					message, token);
			List<IOperationCompletionHandler<Boolean>> handlers = new ArrayList<IOperationCompletionHandler<Boolean>>();
			handlers.add(cHandler);

			super.getConnector().publish(message, handlers,
					this.cloudlet.getResponseInvocationHandler(cHandler));
			MosaicLogger.getLogger().trace(
					"AmqpQueuePublisher - published message " + data);
		} catch (Exception e) {
			ExceptionTracer.traceDeferred(e);
			@SuppressWarnings("unchecked")
			IAmqpQueuePublisherCallback<S, D> proxy = this.cloudlet
					.buildCallbackInvoker(this.callback,
							IAmqpQueuePublisherCallback.class);
			AmqpQueuePublishMessage<D> pMessage = new AmqpQueuePublishMessage<D>(
					AmqpQueuePublisher.this, null, token);
			AmqpQueuePublishCallbackArguments<S, D> arguments = new AmqpQueuePublishCallbackArguments<S, D>(
					AmqpQueuePublisher.this.cloudlet, pMessage);
			proxy.publishFailed(AmqpQueuePublisher.this.cloudletState,
					arguments);
		}

	}

	final class PublishCompletionHandler implements
			IOperationCompletionHandler<Boolean> {

		private AmqpQueuePublishCallbackArguments<S, D> arguments;

		public PublishCompletionHandler(AmqpOutboundMessage message,
				Object token) {
			AmqpQueuePublishMessage<D> pMessage = new AmqpQueuePublishMessage<D>(
					AmqpQueuePublisher.this, message, token);
			this.arguments = new AmqpQueuePublishCallbackArguments<S, D>(
					AmqpQueuePublisher.super.cloudlet, pMessage);
		}

		@Override
		public void onSuccess(Boolean result) {
			AmqpQueuePublisher.this.callback.publishSucceeded(
					AmqpQueuePublisher.super.cloudletState, this.arguments);
		}

		@Override
		public <E extends Throwable> void onFailure(E error) {
			AmqpQueuePublisher.this.callback.publishFailed(
					AmqpQueuePublisher.super.cloudletState, this.arguments);
		}
	}

}
