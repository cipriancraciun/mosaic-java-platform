/*
 * #%L
 * mosaic-connectors
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

package eu.mosaic_cloud.platform.v2.connectors.kvstore;


import eu.mosaic_cloud.platform.v2.connectors.core.Connector;
import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;


/**
 * Interface for working with key-value stores.
 * 
 * @author Georgiana Macariu
 * @param <TValue>
 *            type of stored data
 */
public interface KvStoreConnector<TValue extends Object>
			extends
				Connector
{
	/**
	 * Deletes the given key.
	 * 
	 * @param key
	 *            the key to delete
	 * @return a result handle for the operation
	 */
	CallbackCompletion<Void> delete (String key);
	
	/**
	 * Gets data associated with a single key.
	 * 
	 * @param key
	 *            the key
	 * @return a result handle for the operation
	 */
	CallbackCompletion<TValue> get (String key);
	
	/**
	 * Stores the given data and associates it with the specified key.
	 * 
	 * @param key
	 *            the key under which this data should be stored
	 * @param data
	 *            the data
	 * @return a result handle for the operation
	 */
	CallbackCompletion<Void> set (String key, TValue data);
}
