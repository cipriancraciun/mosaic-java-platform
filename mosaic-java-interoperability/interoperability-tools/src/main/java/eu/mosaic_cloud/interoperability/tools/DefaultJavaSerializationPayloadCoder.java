
package eu.mosaic_cloud.interoperability.tools;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.interoperability.core.PayloadCoder;


public final class DefaultJavaSerializationPayloadCoder
		extends Object
		implements
			PayloadCoder
{
	public DefaultJavaSerializationPayloadCoder (final Class<? extends Serializable> clasz, final boolean nullAllowed)
	{
		super ();
		Preconditions.checkNotNull (clasz);
		Preconditions.checkArgument (Serializable.class.isAssignableFrom (clasz));
		this.clasz = clasz;
		this.nullAllowed = nullAllowed;
	}
	
	@Override
	public Object decode (final ByteBuffer buffer)
			throws Throwable
	{
		final ByteArrayInputStream bufferStream = new ByteArrayInputStream (buffer.array (), buffer.arrayOffset () + buffer.position (), buffer.remaining ());
		final ObjectInputStream objectStream = new ObjectInputStream (bufferStream);
		final Object object = objectStream.readObject ();
		if (!this.nullAllowed && (object == null))
			throw (new IOException ("unexpected null object"));
		if (!this.clasz.isInstance (object))
			throw (new IOException (String.format ("unexpected object class: `%s`", object.getClass ())));
		if (objectStream.available () > 0)
			throw (new IOException ("trailing garbage after object"));
		return (this.clasz.cast (object));
	}
	
	@Override
	public ByteBuffer encode (final Object object)
			throws Throwable
	{
		if (!this.nullAllowed)
			Preconditions.checkNotNull (object);
		Preconditions.checkArgument (this.clasz.isInstance (object));
		final ByteArrayOutputStream bufferStream = new ByteArrayOutputStream ();
		final ObjectOutputStream objectStream = new ObjectOutputStream (bufferStream);
		objectStream.writeObject (object);
		objectStream.close ();
		final ByteBuffer buffer = ByteBuffer.wrap (bufferStream.toByteArray ());
		return (buffer);
	}
	
	private final Class<? extends Serializable> clasz;
	private final boolean nullAllowed;
}
