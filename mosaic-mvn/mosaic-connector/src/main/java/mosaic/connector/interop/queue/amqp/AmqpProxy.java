package mosaic.connector.interop.queue.amqp;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import mosaic.connector.interop.ConnectorProxy;
import mosaic.connector.queue.amqp.AmqpConnector;
import mosaic.connector.queue.amqp.IAmqpConsumerCallback;
import mosaic.core.configuration.IConfiguration;
import mosaic.core.exceptions.ConnectionException;
import mosaic.core.exceptions.ExceptionTracer;
import mosaic.core.log.MosaicLogger;
import mosaic.core.ops.IOperationCompletionHandler;
import mosaic.core.utils.SerDesUtils;
import mosaic.driver.queue.amqp.AmqpExchangeType;
import mosaic.driver.queue.amqp.AmqpOutboundMessage;
import mosaic.interop.amqp.AmqpMessage;
import mosaic.interop.amqp.AmqpSession;
import mosaic.interop.idl.IdlCommon.CompletionToken;
import mosaic.interop.idl.amqp.AmqpPayloads;
import mosaic.interop.idl.amqp.AmqpPayloads.DeclareExchangeRequest.ExchangeType;

import com.google.protobuf.ByteString;

import eu.mosaic_cloud.interoperability.core.Message;
import eu.mosaic_cloud.interoperability.implementations.zeromq.ZeroMqChannel;

/**
 * Proxy for the driver for queuing systems implementing the AMQP protocol. This
 * is used by the {@link AmqpConnector} to communicate with a AMQP driver.
 * 
 * @author Georgiana Macariu
 * 
 */
public class AmqpProxy extends ConnectorProxy {

	/**
	 * Creates a proxy for AMQP queuing systems.
	 * 
	 * @param config
	 *            the configurations required to initialize the proxy
	 * @param connectorId
	 *            the identifier of this connector's proxy
	 * @param reactor
	 *            the response reactor
	 * @param channel
	 *            the channel on which to communicate with the driver
	 * @throws Throwable
	 */
	private AmqpProxy(IConfiguration config, String connectorId,
			AmqpConnectorReactor reactor, ZeroMqChannel channel)
			throws Throwable {
		super(config, connectorId, reactor, channel);
	}

	/**
	 * Returns a proxy for AMQP queuing systems.
	 * 
	 * @param config
	 *            the configurations required to initialize the proxy
	 * @param connectorIdentifier
	 *            the identifier of this connector
	 * @param driverIdentifier
	 *            the identifier of the driver to which request will be sent
	 * @param channel
	 *            the channel on which to communicate with the driver
	 * @return the proxy
	 * @throws Throwable
	 */
	public static AmqpProxy create(IConfiguration config,
			String connectorIdentifier, String driverIdentifier,
			ZeroMqChannel channel) throws Throwable {
		String connectorId = connectorIdentifier;

		AmqpConnectorReactor reactor = new AmqpConnectorReactor(config);
		AmqpProxy proxy = new AmqpProxy(config, connectorId, reactor, channel);

		proxy.connect(driverIdentifier, AmqpSession.CONNECTOR, new Message(
				AmqpMessage.ACCESS, null));
		return proxy;
	}

	public synchronized void declareExchange(String name,
			AmqpExchangeType type, boolean durable, boolean autoDelete,
			boolean passive, List<IOperationCompletionHandler<Boolean>> handlers) {
		ExchangeType eType = AmqpPayloads.DeclareExchangeRequest.ExchangeType
				.valueOf(type.toString().toUpperCase());
		CompletionToken token = generateToken();

		AmqpPayloads.DeclareExchangeRequest.Builder requestBuilder = AmqpPayloads.DeclareExchangeRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setExchange(name);
		requestBuilder.setType(eType);
		requestBuilder.setDurable(durable);
		requestBuilder.setAutoDelete(autoDelete);
		requestBuilder.setPassive(passive);

		Message message = new Message(AmqpMessage.DECL_EXCHANGE_REQUEST,
				requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	public synchronized void declareQueue(String queue, boolean exclusive,
			boolean durable, boolean autoDelete, boolean passive,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.DeclareQueueRequest.Builder requestBuilder = AmqpPayloads.DeclareQueueRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setQueue(queue);
		requestBuilder.setExclusive(exclusive);
		requestBuilder.setDurable(durable);
		requestBuilder.setAutoDelete(autoDelete);
		requestBuilder.setPassive(passive);

		Message message = new Message(AmqpMessage.DECL_QUEUE_REQUEST,
				requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	public synchronized void bindQueue(String exchange, String queue,
			String routingKey,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.BindQueueRequest.Builder requestBuilder = AmqpPayloads.BindQueueRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setQueue(queue);
		requestBuilder.setExchange(exchange);
		requestBuilder.setRoutingKey(routingKey);

		Message message = new Message(AmqpMessage.BIND_QUEUE_REQUEST,
				requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	public synchronized void publish(AmqpOutboundMessage message,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.PublishRequest.Builder requestBuilder = AmqpPayloads.PublishRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setExchange(message.getExchange());
		requestBuilder.setData(ByteString.copyFrom(message.getData()));
		requestBuilder.setDurable(message.isDurable());
		requestBuilder.setImmediate(message.isImmediate());
		requestBuilder.setMandatory(message.isMandatory());
		requestBuilder.setRoutingKey(message.getRoutingKey());

		Message mssg = new Message(AmqpMessage.DECL_QUEUE_REQUEST,
				requestBuilder.build());

		sendMessage(mssg, token, handlers);
	}

	public synchronized void consume(String queue, String consumer,
			boolean exclusive, boolean autoAck, Object extra,
			List<IOperationCompletionHandler<String>> handlers,
			IAmqpConsumerCallback consumerCallback) {

		CompletionToken token = generateToken();

		AmqpPayloads.ConsumeRequest.Builder requestBuilder = AmqpPayloads.ConsumeRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setQueue(queue);
		requestBuilder.setExclusive(exclusive);
		requestBuilder.setAutoAck(autoAck);
		try {
			requestBuilder.setExtra(ByteString.copyFrom(SerDesUtils
					.toBytes(extra)));

			AmqpConnectorReactor reactor = super
					.getResponseReactor(AmqpConnectorReactor.class);
			if (consumer.equals("")) {
				requestBuilder.setConsumer(token.getMessageId());
				reactor.addCallback(token.getMessageId(), consumerCallback);
			} else {
				requestBuilder.setConsumer(consumer);
				reactor.addCallback(consumer, consumerCallback);
			}

			Message mssg = new Message(AmqpMessage.CONSUME_REQUEST,
					requestBuilder.build());

			sendMessage(mssg, token, handlers);
		} catch (IOException e) {
			for (IOperationCompletionHandler<String> handler : handlers) {
				handler.onFailure(e);
			}
			ExceptionTracer.traceDeferred(new ConnectionException(
					"Cannot send consume request to driver: " + e.getMessage(),
					e));
		}
	}

	public synchronized void cancel(String consumer,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.CancelRequest.Builder requestBuilder = AmqpPayloads.CancelRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setConsumer(consumer);

		Message message = new Message(AmqpMessage.CANCEL_REQUEST,
				requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	public synchronized void get(String queue, boolean autoAck,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.GetRequest.Builder requestBuilder = AmqpPayloads.GetRequest
				.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setQueue(queue);
		requestBuilder.setAutoAck(autoAck);

		Message message = new Message(AmqpMessage.GET_REQUEST,
				requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	public synchronized void ack(long delivery, boolean multiple,
			List<IOperationCompletionHandler<Boolean>> handlers) {
		CompletionToken token = generateToken();

		AmqpPayloads.Ack.Builder requestBuilder = AmqpPayloads.Ack.newBuilder();
		requestBuilder.setToken(token);
		requestBuilder.setDelivery(delivery);
		requestBuilder.setMultiple(multiple);

		Message message = new Message(AmqpMessage.ACK, requestBuilder.build());

		sendMessage(message, token, handlers);
	}

	private <V extends Object> void sendMessage(Message message,
			CompletionToken token, List<IOperationCompletionHandler<V>> handlers) {
		try {
			// store token and completion handlers
			super.registerHandlers(token.getMessageId(), handlers);
			super.sendRequest(getResponseReactor(AmqpConnectorReactor.class)
					.getSession(), message);

			MosaicLogger.getLogger().trace(
					"AmqpProxy - Sent " + message.specification.toString()
							+ " request [" + token.getMessageId() + "]...");
		} catch (IOException e) {
			for (IOperationCompletionHandler<V> handler : handlers) {
				handler.onFailure(e);
			}
			ExceptionTracer.traceDeferred(new ConnectionException(
					"Cannot send " + message.specification.toString()
							+ " request to driver: " + e.getMessage(), e));
		}
	}

	private CompletionToken generateToken() {
		String id = UUID.randomUUID().toString();
		CompletionToken.Builder tokenBuilder = CompletionToken.newBuilder();
		tokenBuilder.setMessageId(id);
		tokenBuilder.setClientId(getConnectorId());
		return tokenBuilder.build();
	}

}
