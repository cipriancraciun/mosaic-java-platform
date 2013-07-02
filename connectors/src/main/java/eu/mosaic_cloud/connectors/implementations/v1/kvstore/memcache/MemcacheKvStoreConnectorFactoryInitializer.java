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

package eu.mosaic_cloud.connectors.implementations.v1.kvstore.memcache;


import eu.mosaic_cloud.connectors.implementations.v1.core.BaseConnectorsFactoryInitializer;
import eu.mosaic_cloud.connectors.implementations.v1.core.ConnectorConfiguration;
import eu.mosaic_cloud.connectors.implementations.v1.core.ConnectorEnvironment;
import eu.mosaic_cloud.connectors.v1.core.IConnectorsFactory;
import eu.mosaic_cloud.connectors.v1.core.IConnectorsFactoryBuilder;
import eu.mosaic_cloud.connectors.v1.kvstore.memcache.IMemcacheKvStoreConnector;
import eu.mosaic_cloud.connectors.v1.kvstore.memcache.IMemcacheKvStoreConnectorFactory;
import eu.mosaic_cloud.platform.v1.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.v1.core.serialization.DataEncoder;


public final class MemcacheKvStoreConnectorFactoryInitializer
		extends BaseConnectorsFactoryInitializer
{
	@Override
	protected void initialize_1 (final IConnectorsFactoryBuilder builder, final ConnectorEnvironment environment, final IConnectorsFactory delegate)
	{
		builder.register (IMemcacheKvStoreConnectorFactory.class, new IMemcacheKvStoreConnectorFactory () {
			@Override
			public <TValue> IMemcacheKvStoreConnector<TValue> create (final IConfiguration configuration, final Class<TValue> valueClass, final DataEncoder<TValue> valueEncoder)
			{
				return MemcacheKvStoreConnector.create (ConnectorConfiguration.create (configuration, environment), valueEncoder);
			}
		});
	}
	
	public static final MemcacheKvStoreConnectorFactoryInitializer defaultInstance = new MemcacheKvStoreConnectorFactoryInitializer ();
}