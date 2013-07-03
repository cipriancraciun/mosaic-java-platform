/*
 * #%L
 * mosaic-drivers-stubs-riak
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

package eu.mosaic_cloud.drivers.kvstore.riak;


import eu.mosaic_cloud.drivers.ConfigProperties;
import eu.mosaic_cloud.drivers.ops.IOperationFactory;
import eu.mosaic_cloud.platform.implementations.v1.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.v1.core.configuration.IConfiguration;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;
import eu.mosaic_cloud.tools.transcript.core.Transcript;

import org.slf4j.Logger;


/**
 * Driver class for the Riak key-value database management systems.
 * 
 * Rest Interface
 * 
 * @author Carmine Di Biase
 * @deprecated
 */
@Deprecated
public final class RiakRestDriver
		extends AbstractKeyValueDriver
{
	/**
	 * Creates a new Riak driver.
	 * 
	 * @param noThreads
	 *            number of threads to be used for serving requests
	 * @param riakHost
	 *            the hostname of the Riak server
	 * @param riakPort
	 *            the port for the Riak server
	 */
	private RiakRestDriver (final ThreadingContext threading, final int noThreads, final String riakHost, final int riakPort)
	{
		super (threading, noThreads);
		this.riakHost = riakHost;
		this.riakPort = riakPort;
	}
	
	/**
	 * Destroys the driver. After this call no other method should be invoked on
	 * the driver object.
	 */
	@Override
	public synchronized void destroy ()
	{
		super.destroy ();
		RiakRestDriver.logger.trace ("RiakDriver destroyed."); // $NON-NLS-1$
	}
	
	/*
	 * Here is eventually possible add more particular operation for your key
	 * value store engine
	 */
	@Override
	protected IOperationFactory createOperationFactory (final Object ... params)
	{
		final String bucket = (String) params[0];
		final String clientId = (String) params[1];
		final IOperationFactory opFactory = RiakRestOperationFactory.getFactory (this.riakHost, this.riakPort, bucket, clientId);
		return opFactory;
	}
	
	/**
	 * Returns a Riak driver.
	 * 
	 * @param config
	 *            the configuration parameters required by the driver:
	 *            <ol>
	 *            <il>for each server to which the driver should connect there
	 *            should be two parameters: <i>host_&lt;server_number&gt;</i>
	 *            and <i>port_&lt;server_number&gt;</i> indicating the hostnames
	 *            and the ports where the servers are installed </il>
	 *            <il><i>memcached.driver_threads</i> specifies the maximum
	 *            number of threads that shall be created by the driver for
	 *            serving requests </il>
	 *            </ol>
	 * @return the driver
	 * @throws IOException
	 */
	public static RiakRestDriver create (final IConfiguration config, final ThreadingContext threading)
	{
		int port, noThreads;
		final String host = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_0, String.class, ""); // $NON-NLS-1$ $NON-NLS-2$
		port = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_1, Integer.class, 0); // $NON-NLS-1$
		noThreads = ConfigUtils.resolveParameter (config, ConfigProperties.KVStoreDriver_2, Integer.class, 1); // $NON-NLS-1$
		RiakRestDriver.logger.trace ("Created Riak REST driver for host " + host + ":" + port + " [threads=" + noThreads + "]");
		return new RiakRestDriver (threading, noThreads, host, port);
	}
	
	private final String riakHost;
	private final int riakPort;
	private static final Logger logger = Transcript.create (RiakRestDriver.class).adaptAs (Logger.class);
}
