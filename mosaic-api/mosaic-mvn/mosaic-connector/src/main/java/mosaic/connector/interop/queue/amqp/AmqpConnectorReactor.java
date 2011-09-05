package mosaic.connector.interop.queue.amqp;

import java.io.IOException;
import java.util.List;

import mosaic.connector.interop.AbstractConnectorReactor;
import mosaic.connector.queue.amqp.AmqpCallbacksMap;
import mosaic.connector.queue.amqp.IAmqpConsumerCallback;
import mosaic.core.configuration.IConfiguration;
import mosaic.core.log.MosaicLogger;
import mosaic.core.ops.IOperationCompletionHandler;
import mosaic.driver.queue.amqp.AmqpInboundMessage;
import mosaic.interop.amqp.AmqpMessage;
import mosaic.interop.idl.IdlCommon;
import mosaic.interop.idl.IdlCommon.CompletionToken;
import mosaic.interop.idl.IdlCommon.Error;
import mosaic.interop.idl.IdlCommon.NotOk;
import mosaic.interop.idl.IdlCommon.Ok;
import mosaic.interop.idl.amqp.AmqpPayloads;
import mosaic.interop.idl.amqp.AmqpPayloads.CancelOkMessage;
import mosaic.interop.idl.amqp.AmqpPayloads.ConsumeOkMessage;
import mosaic.interop.idl.amqp.AmqpPayloads.ConsumeReply;
import mosaic.interop.idl.amqp.AmqpPayloads.DeliveryMessage;
import mosaic.interop.idl.amqp.AmqpPayloads.ServerCancelRequest;
import mosaic.interop.idl.amqp.AmqpPayloads.ShutdownMessage;

import com.google.common.base.Preconditions;

import eu.mosaic_cloud.interoperability.core.Message;

/**
 * Implements a reactor for processing asynchronous requests issued by the AMQP
 * connector.
 * 
 * @author Georgiana Macariu
 * 
 */
public class AmqpConnectorReactor extends AbstractConnectorReactor {

	private AmqpCallbacksMap callbacksMap;

	/**
	 * Creates the reactor for the AMQP connector proxy.
	 * 
	 * @param config
	 *            the configurations required to initialize the proxy
	 * @throws Throwable
	 */
	public AmqpConnectorReactor(IConfiguration config) throws Throwable {
		super(config);
		this.callbacksMap = new AmqpCallbacksMap();
	}

	/**
	 * Maps the consume callback to the Consume request. When the response from
	 * the Consume request arrives this entry in the map will be replaced with
	 * another, mapping the consumerTag to the callback.
	 * 
	 * @param requestId
	 *            the request identifier
	 * @param callback
	 *            the callback
	 */
	protected synchronized void addCallback(String requestId,
			IAmqpConsumerCallback callback) {
		this.callbacksMap.addHandlers(requestId, callback);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void processResponse(Message message) throws IOException {
		Preconditions
				.checkArgument(message.specification instanceof AmqpMessage);

		AmqpMessage amqpMessage = (AmqpMessage) message.specification;
		CompletionToken token = null;
		String consumerId;
		IAmqpConsumerCallback callback;
		List<IOperationCompletionHandler<?>> handlers;

		switch (amqpMessage) {
		case OK:
			IdlCommon.Ok okPayload = (Ok) message.payload;
			token = okPayload.getToken();

			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received response "
							+ amqpMessage.toString() + " for request id "
							+ token.getMessageId());

			handlers = getHandlers(token);
			if (handlers != null) {
				for (IOperationCompletionHandler<?> handler : handlers) {
					((IOperationCompletionHandler<Boolean>) handler)
							.onSuccess(true);
				}
			}
			break;
		case NOK:
			IdlCommon.NotOk nokPayload = (NotOk) message.payload;
			token = nokPayload.getToken();

			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received response "
							+ amqpMessage.toString() + " for request id "
							+ token.getMessageId());

			handlers = getHandlers(token);
			if (handlers != null) {
				for (IOperationCompletionHandler<?> handler : handlers) {
					((IOperationCompletionHandler<Boolean>) handler)
							.onSuccess(false);
				}
			}
			break;
		case ERROR:
			IdlCommon.Error errorPayload = (Error) message.payload;
			token = errorPayload.getToken();

			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received response "
							+ amqpMessage.toString() + " for request id "
							+ token.getMessageId());

			handlers = getHandlers(token);
			if (handlers != null) {
				for (IOperationCompletionHandler<?> handler : handlers) {
					handler.onFailure(new Exception(errorPayload
							.getErrorMessage()));
				}
			}
			break;
		case CONSUME_REPLY:
			AmqpPayloads.ConsumeReply consumePayload = (ConsumeReply) message.payload;
			token = consumePayload.getToken();

			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received response "
							+ amqpMessage.toString() + " for request id "
							+ token.getMessageId());

			handlers = getHandlers(token);
			if (handlers != null) {
				String resultStr = consumePayload.getConsumerTag();
				for (IOperationCompletionHandler<?> handler : handlers) {
					((IOperationCompletionHandler<String>) handler)
							.onSuccess(resultStr);
				}
			}
			break;
		case CANCEL_OK:
			AmqpPayloads.CancelOkMessage cancelOkPayload = (CancelOkMessage) message.payload;
			consumerId = cancelOkPayload.getConsumerTag();
			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received CANCEL Ok "
							+ " for consumer " + consumerId);
			callback = this.callbacksMap.removeConsumerCallback(consumerId);
			callback.handleCancelOk(consumerId);
			break;
		case SERVER_CANCEL:
			AmqpPayloads.ServerCancelRequest scancelPayload = (ServerCancelRequest) message.payload;
			consumerId = scancelPayload.getConsumerTag();
			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received SERVER CANCEL "
							+ " for consumer " + consumerId);
			callback = this.callbacksMap.removeConsumerCallback(consumerId);
			callback.handleCancelOk(consumerId); // FIXME
			break;
		case CONSUME_OK:
			AmqpPayloads.ConsumeOkMessage consumeOkPayload = (ConsumeOkMessage) message.payload;
			consumerId = consumeOkPayload.getConsumerTag();
			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received CONSUME Ok "
							+ " for consumer " + consumerId);
			callback = this.callbacksMap.getRequestHandlers(consumerId);
			callback.handleConsumeOk(consumerId);
			break;
		case DELIVERY:
			AmqpPayloads.DeliveryMessage delivery = (DeliveryMessage) message.payload;
			consumerId = delivery.getConsumerTag();

			MosaicLogger.getLogger().trace(
					"AmqpConnectorReactor - Received delivery "
							+ " for consumer " + consumerId);

			long deliveryTag = delivery.getDeliveryTag();
			String exchange = delivery.getExchange();
			String routingKey = delivery.getRoutingKey();
			int deliveryMode = delivery.getDeliveryMode();
			byte[] data = delivery.getData().toByteArray();
			AmqpInboundMessage mssg = new AmqpInboundMessage(consumerId,
					deliveryTag, exchange, routingKey, data, deliveryMode == 2,
					delivery.getContentType());
			callback = this.callbacksMap.getRequestHandlers(consumerId);
			callback.handleDelivery(mssg);
			break;
		case SHUTDOWN:
			AmqpPayloads.ShutdownMessage downPayload = (ShutdownMessage) message.payload;
			consumerId = downPayload.getConsumerTag();
			String signalMssg = downPayload.getMessage();
			callback = this.callbacksMap.getRequestHandlers(consumerId);
			callback.handleShutdownSignal(consumerId, signalMssg);
			break;
		default:
			break;
		}

	}
}
