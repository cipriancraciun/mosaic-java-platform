/*
 * #%L
 * mosaic-platform-interop
 * %%
 * Copyright (C) 2010 - 2013 Institute e-Austria Timisoara (Romania)
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

package eu.mosaic_cloud.platform.interop.specs.amqp;


import eu.mosaic_cloud.interoperability.core.MessageSpecification;
import eu.mosaic_cloud.interoperability.core.MessageType;
import eu.mosaic_cloud.interoperability.core.PayloadCoder;
import eu.mosaic_cloud.interoperability.tools.Identifiers;
import eu.mosaic_cloud.platform.interop.idl.IdlCommon;
import eu.mosaic_cloud.platform.interop.idl.amqp.AmqpPayloads;
import eu.mosaic_cloud.platform.interop.tools.DefaultPBPayloadCoder;

import com.google.protobuf.GeneratedMessage;


/**
 * Enum containing all possible AMQP connector-driver messages.
 * 
 * @author Georgiana Macariu
 */
public enum AmqpMessage
			implements
				MessageSpecification
{
	ABORTED (MessageType.Termination, null),
	ACCESS (MessageType.Initiation, null),
	ACK (MessageType.Exchange, AmqpPayloads.Ack.class),
	BIND_QUEUE_REQUEST (MessageType.Exchange, AmqpPayloads.BindQueueRequest.class),
	CANCEL_OK (MessageType.Exchange, AmqpPayloads.CancelOkMessage.class),
	CANCEL_REQUEST (MessageType.Exchange, AmqpPayloads.CancelRequest.class),
	CONSUME_OK (MessageType.Exchange, AmqpPayloads.ConsumeOkMessage.class),
	CONSUME_REPLY (MessageType.Exchange, AmqpPayloads.ConsumeReply.class),
	CONSUME_REQUEST (MessageType.Exchange, AmqpPayloads.ConsumeRequest.class),
	DECL_EXCHANGE_REQUEST (MessageType.Exchange, AmqpPayloads.DeclareExchangeRequest.class),
	DECL_QUEUE_REQUEST (MessageType.Exchange, AmqpPayloads.DeclareQueueRequest.class),
	DELIVERY (MessageType.Exchange, AmqpPayloads.DeliveryMessage.class),
	ERROR (MessageType.Exchange, IdlCommon.Error.class),
	GET_REQUEST (MessageType.Exchange, AmqpPayloads.GetRequest.class),
	NOK (MessageType.Exchange, IdlCommon.NotOk.class),
	OK (MessageType.Exchange, IdlCommon.Ok.class),
	PUBLISH_REQUEST (MessageType.Exchange, AmqpPayloads.PublishRequest.class),
	SERVER_CANCEL (MessageType.Exchange, AmqpPayloads.ServerCancelRequest.class),
	SHUTDOWN (MessageType.Exchange, AmqpPayloads.ShutdownMessage.class);
	/**
	 * Creates a new AMQP message.
	 * 
	 * @param type
	 *            the type of the message (initiation, exchange or termination)
	 * @param clasz
	 *            the class containing the payload of the message
	 */
	AmqpMessage (final MessageType type, final Class<? extends GeneratedMessage> clasz) {
		this.identifier = Identifiers.generate (this);
		this.type = type;
		if (clasz != null) {
			this.coder = new DefaultPBPayloadCoder (clasz, false);
		}
	}
	
	@Override
	public String getIdentifier () {
		return this.identifier;
	}
	
	@Override
	public PayloadCoder getPayloadCoder () {
		return this.coder;
	}
	
	@Override
	public String getQualifiedName () {
		return (Identifiers.generateName (this));
	}
	
	@Override
	public MessageType getType () {
		return this.type;
	}
	
	public PayloadCoder coder = null;
	public final String identifier;
	public final MessageType type;
}
