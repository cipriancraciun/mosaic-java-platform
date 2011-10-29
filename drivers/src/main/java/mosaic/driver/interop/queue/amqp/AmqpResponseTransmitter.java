package mosaic.driver.interop.queue.amqp;

import mosaic.core.log.MosaicLogger;
import mosaic.driver.interop.ResponseTransmitter;
import mosaic.driver.queue.amqp.AmqpInboundMessage;
import mosaic.driver.queue.amqp.AmqpOperations;
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

import com.google.protobuf.ByteString;

import eu.mosaic_cloud.interoperability.core.Message;
import eu.mosaic_cloud.interoperability.core.Session;

/**
 * Serializes responses for AMQP operation requests and sends them to the
 * connector proxy which requested the operations.
 * 
 * @author Georgiana Macariu
 * 
 */
public class AmqpResponseTransmitter extends ResponseTransmitter {

	/**
	 * Builds the result and sends it to the operation originator.
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * 
	 * @param token
	 *            the token identifying the operation
	 * @param operation
	 *            the identifier of the operation
	 * @param result
	 *            the result
	 * @param isError
	 *            <code>true</code> if the result is actual an error
	 */
	public void sendResponse(Session session, CompletionToken token,
			AmqpOperations operation, Object result, boolean isError) {
		Message message=null; // NOPMD by georgiana on 10/12/11 3:34 PM

		if (isError) {
			// create error message
			Error.Builder errorPayload = IdlCommon.Error.newBuilder();
			errorPayload.setToken(token);
			errorPayload.setErrorMessage(result.toString());
			message = new Message(AmqpMessage.ERROR, errorPayload.build());
		} else {
			switch (operation) {
			case DECLARE_EXCHANGE:
			case DECLARE_QUEUE:
			case BIND_QUEUE:
			case PUBLISH:
			case GET:
			case CANCEL:
			case ACK:
				boolean success = (Boolean) result;
				if (success) {
					Ok.Builder okPayload = IdlCommon.Ok.newBuilder();
					okPayload.setToken(token);
					message = new Message(AmqpMessage.OK, okPayload.build());
				} else {
					NotOk.Builder nokPayload = IdlCommon.NotOk.newBuilder();
					nokPayload.setToken(token);
					message = new Message(AmqpMessage.NOK, nokPayload.build());
				}
				break;
			case CONSUME:
				ConsumeReply.Builder consumePayload = AmqpPayloads.ConsumeReply
						.newBuilder();
				consumePayload.setToken(token);
				consumePayload.setConsumerTag((String) result);
				message = new Message(AmqpMessage.CONSUME_REPLY,
						consumePayload.build());
				break;
			default:
				break;
			}
		}

		// send response
		publishResponse(session, message);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter: sent response for " + operation
						+ " request " + token.getMessageId() + " client id "
						+ token.getClientId());
	}

	/**
	 * Builds the Cancel Ok message and sends it to the actual consumer.
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * @param consumerTag
	 *            the tag of the consumer
	 */
	public void sendCancelOk(Session session, String consumerTag) {
		AmqpPayloads.CancelOkMessage.Builder cancelPayload = CancelOkMessage
				.newBuilder();
		cancelPayload.setConsumerTag(consumerTag);
		Message message = new Message(AmqpMessage.CANCEL_OK,
				cancelPayload.build());

		// send response
		publishResponse(session, message);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter - Sent CANCEL ok message");
	}

	/**
	 * Builds the Cancel message and sends it to the actual consumer.
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * @param consumerTag
	 *            the tag of the consumer
	 */
	public void sendCancel(Session session, String consumerTag) {
		AmqpPayloads.ServerCancelRequest.Builder cancelPayload = ServerCancelRequest
				.newBuilder();
		cancelPayload.setConsumerTag(consumerTag);
		Message message = new Message(AmqpMessage.SERVER_CANCEL,
				cancelPayload.build());

		publishResponse(session, message);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter - Sent CANCEL message");
	}

	/**
	 * Builds the Consume Ok message and sends it to the actual consumer.
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * @param consumerTag
	 *            the tag of the consumer
	 */
	public void sendConsumeOk(Session session, String consumerTag) {
		AmqpPayloads.ConsumeOkMessage.Builder consumePayload = ConsumeOkMessage
				.newBuilder();
		consumePayload.setConsumerTag(consumerTag);
		Message message = new Message(AmqpMessage.CONSUME_OK,
				consumePayload.build());

		publishResponse(session, message);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter - Sent CONSUME Ok callback for consumer "
						+ consumerTag + ".");
	}

	/**
	 * Delivers a message to its consumer
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * @param message
	 *            the message contents and properties
	 */
	public void sendDelivery(Session session, AmqpInboundMessage message) {
		AmqpPayloads.DeliveryMessage.Builder deliveryPayload = DeliveryMessage
				.newBuilder();
		deliveryPayload.setConsumerTag(message.getConsumer());
		deliveryPayload.setDeliveryTag(message.getDelivery());
		deliveryPayload.setExchange(message.getExchange());
		deliveryPayload.setRoutingKey(message.getRoutingKey());
		deliveryPayload.setDeliveryMode(message.isDurable() ? 2 : 1);
		deliveryPayload.setData(ByteString.copyFrom(message.getData()));
		deliveryPayload.setContentType(message.getContentType());
		Message mssg = new Message(AmqpMessage.DELIVERY,
				deliveryPayload.build());

		// send response
		publishResponse(session, mssg);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter - Delivered message");
	}

	/**
	 * Builds the Shutdown message and sends it to the actual consumer.
	 * 
	 * @param session
	 *            the session to which the response message belongs
	 * @param consumerTag
	 *            the tag of the consumer
	 * @param errorMessage
	 *            a message about the shutdown cause
	 */
	public void sendShutdownSignal(Session session, String consumerTag,
			String errorMessage) {
		AmqpPayloads.ShutdownMessage.Builder downPayload = ShutdownMessage
				.newBuilder();
		downPayload.setConsumerTag(consumerTag);
		downPayload.setMessage(errorMessage);
		Message message = new Message(AmqpMessage.SHUTDOWN, downPayload.build());

		// send response
		publishResponse(session, message);
		MosaicLogger.getLogger().trace(
				"AmqpResponseTransmitter - Sent Shutdown message");
	}

}