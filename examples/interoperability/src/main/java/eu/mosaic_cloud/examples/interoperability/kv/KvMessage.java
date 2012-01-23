/*
 * #%L
 * mosaic-examples-interoperability
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

package eu.mosaic_cloud.examples.interoperability.kv;


import java.io.Serializable;

import eu.mosaic_cloud.interoperability.core.MessageSpecification;
import eu.mosaic_cloud.interoperability.core.MessageType;
import eu.mosaic_cloud.interoperability.core.PayloadCoder;
import eu.mosaic_cloud.interoperability.tools.DefaultJavaSerializationPayloadCoder;
import eu.mosaic_cloud.interoperability.tools.Identifiers;


public enum KvMessage
		implements
			MessageSpecification
{
	Aborted (MessageType.Termination, null),
	Access (MessageType.Initiation, null),
	Error (MessageType.Exchange, Error.class),
	GetReply (MessageType.Exchange, KvPayloads.GetReply.class),
	GetRequest (MessageType.Exchange, KvPayloads.GetRequest.class),
	Ok (MessageType.Exchange, KvPayloads.Ok.class),
	PutRequest (MessageType.Exchange, KvPayloads.PutRequest.class);
	KvMessage (final MessageType type, final Class<? extends Serializable> clasz)
	{
		this.identifier = Identifiers.generate (this);
		this.type = type;
		if (clasz != null)
			this.coder = DefaultJavaSerializationPayloadCoder.create (clasz, false);
		else
			this.coder = null;
	}
	
	@Override
	public String getIdentifier ()
	{
		return (this.identifier);
	}
	
	@Override
	public PayloadCoder getPayloadCoder ()
	{
		return (this.coder);
	}
	
	@Override
	public String getQualifiedName ()
	{
		return (Identifiers.generateName (this));
	}
	
	@Override
	public MessageType getType ()
	{
		return (this.type);
	}
	
	public final PayloadCoder coder;
	public final String identifier;
	public final MessageType type;
}
