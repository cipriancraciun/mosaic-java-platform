/*
 * #%L
 * mosaic-cloudlets
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

package eu.mosaic_cloud.cloudlets.v1.connectors.kvstore;


import java.util.Arrays;
import java.util.List;

import eu.mosaic_cloud.cloudlets.v1.cloudlets.ICloudletController;
import eu.mosaic_cloud.cloudlets.v1.core.CallbackCompletionArguments;


/**
 * The arguments of the cloudlet callback methods for the operations on
 * key-value storages.
 * 
 * @author Georgiana Macariu
 * 
 * @param <TContext>
 *            the context of the cloudlet
 * @param <TValue>
 *            the type of the values exchanged with the key-value store using
 *            this connector
 * @param <TExtra>
 *            the type of the extra data; as an example, this data can be used
 *            correlation
 */
public class KvStoreCallbackCompletionArguments<TValue, TExtra>
		extends CallbackCompletionArguments
{
	/**
	 * Creates a new argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param keys
	 *            the keys used in the operation
	 * @param error
	 *            the exception thrown by the operation
	 * @param extra
	 *            some application specific object
	 */
	public KvStoreCallbackCompletionArguments (final ICloudletController<?> cloudlet, final List<String> keys, final Throwable error, final TExtra extra)
	{
		super (cloudlet, error);
		this.keys = keys;
		this.value = null;
		this.extra = extra;
	}
	
	/**
	 * Creates a new argument for the callbacks of operations using more than
	 * one key.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param keys
	 *            the keys used in the operation
	 * @param value
	 *            the value associated with the key (if this callback is used
	 *            for failed operations this value should contain the error)
	 * @param extra
	 *            some application specific object
	 */
	public KvStoreCallbackCompletionArguments (final ICloudletController<?> cloudlet, final List<String> keys, final TValue value, final TExtra extra)
	{
		super (cloudlet);
		this.keys = keys;
		this.value = value;
		this.extra = extra;
	}
	
	/**
	 * Creates a new argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param key
	 *            the key used in the operation
	 * @param error
	 *            the exception thrown by the operation
	 */
	public KvStoreCallbackCompletionArguments (final ICloudletController<?> cloudlet, final String key, final Throwable error)
	{
		super (cloudlet, error);
		this.keys = Arrays.asList (key);
		this.value = null;
		this.extra = null;
	}
	
	/**
	 * Creates a new argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param key
	 *            the key used in the operation
	 * @param error
	 *            the exception thrown by the operation
	 * @param extra
	 *            some application specific object
	 */
	public KvStoreCallbackCompletionArguments (final ICloudletController<?> cloudlet, final String key, final Throwable error, final TExtra extra)
	{
		super (cloudlet, error);
		this.keys = Arrays.asList (key);
		this.value = null;
		this.extra = extra;
	}
	
	/**
	 * Creates a new argument.
	 * 
	 * @param cloudlet
	 *            the cloudlet
	 * @param key
	 *            the key used in the operation
	 * @param value
	 *            the value associated with the key (if this callback is used
	 *            for failed operations this value should contain the error)
	 * @param extra
	 *            some application specific object
	 */
	public KvStoreCallbackCompletionArguments (final ICloudletController<?> cloudlet, final String key, final TValue value, final TExtra extra)
	{
		super (cloudlet);
		this.keys = Arrays.asList (key);
		this.value = value;
		this.extra = extra;
	}
	
	/**
	 * Returns any application specific data used for the key-value store
	 * operation.
	 * 
	 * @return any application specific data used for the key-value store
	 *         operation
	 */
	public TExtra getExtra ()
	{
		return this.extra;
	}
	
	/**
	 * Returns the key used in single-key operations.
	 * 
	 * @return the key used in single-key operations
	 */
	public String getKey ()
	{
		return this.keys.get (0);
	}
	
	/**
	 * Returns the keys used in multiple-key operations.
	 * 
	 * @return the key used in multiple-key operations
	 */
	public List<String> getKeys ()
	{
		return this.keys;
	}
	
	/**
	 * Returns the value field of the argument.
	 * 
	 * @return the value field of the argument
	 */
	public TValue getValue ()
	{
		return this.value;
	}
	
	private final TExtra extra;
	private final List<String> keys;
	private final TValue value;
}