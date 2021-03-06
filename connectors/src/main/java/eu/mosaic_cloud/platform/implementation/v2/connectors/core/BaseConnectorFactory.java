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

package eu.mosaic_cloud.platform.implementation.v2.connectors.core;


import eu.mosaic_cloud.platform.v2.connectors.core.Connector;
import eu.mosaic_cloud.platform.v2.connectors.core.ConnectorFactory;
import eu.mosaic_cloud.platform.v2.connectors.core.ConnectorsFactory;

import com.google.common.base.Preconditions;


public abstract class BaseConnectorFactory<TConnector extends Connector>
			extends Object
			implements
				ConnectorFactory<TConnector>
{
	protected BaseConnectorFactory (final ConnectorEnvironment environment, final ConnectorsFactory delegate) {
		super ();
		Preconditions.checkNotNull (environment);
		this.environment = environment;
		this.delegate = delegate;
	}
	
	protected final ConnectorsFactory delegate;
	protected final ConnectorEnvironment environment;
}
