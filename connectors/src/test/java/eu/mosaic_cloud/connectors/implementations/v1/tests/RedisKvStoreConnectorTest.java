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

package eu.mosaic_cloud.connectors.implementations.v1.tests;


import eu.mosaic_cloud.connectors.implementations.v1.core.ConnectorConfiguration;
import eu.mosaic_cloud.connectors.implementations.v1.kvstore.generic.GenericKvStoreConnector;
import eu.mosaic_cloud.drivers.kvstore.interop.KeyValueStub;
import eu.mosaic_cloud.platform.implementations.v1.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.implementations.v1.serialization.PlainTextDataEncoder;
import eu.mosaic_cloud.platform.interop.specs.kvstore.KeyValueSession;
import eu.mosaic_cloud.platform.v1.core.configuration.IConfiguration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;


@Ignore
public class RedisKvStoreConnectorTest
		extends BaseKvStoreConnectorTest<GenericKvStoreConnector<String>>
{
	@Override
	public void setUp ()
	{
		this.scenario = RedisKvStoreConnectorTest.scenario_;
		final ConnectorConfiguration configuration = ConnectorConfiguration.create (this.scenario.getConfiguration (), this.scenario.getEnvironment ());
		this.connector = GenericKvStoreConnector.create (configuration, PlainTextDataEncoder.DEFAULT_INSTANCE);
	}
	
	@BeforeClass
	public static void setUpBeforeClass ()
	{
		final String host = System.getProperty (RedisKvStoreConnectorTest.MOSAIC_REDIS_HOST, RedisKvStoreConnectorTest.MOSAIC_REDIS_HOST_DEFAULT);
		final Integer port = Integer.valueOf (System.getProperty (RedisKvStoreConnectorTest.MOSAIC_REDIS_PORT, RedisKvStoreConnectorTest.MOSAIC_REDIS_PORT_DEFAULT));
		final IConfiguration configuration = PropertyTypeConfiguration.create ();
		configuration.addParameter ("interop.driver.endpoint", "inproc://98eceebc-fd87-4ef3-84cf-3feca6044e5a");
		configuration.addParameter ("interop.driver.identity", "98eceebc-fd87-4ef3-84cf-3feca6044e5a");
		configuration.addParameter ("kvstore.host", host);
		configuration.addParameter ("kvstore.port", port);
		configuration.addParameter ("kvstore.driver_name", "REDIS");
		configuration.addParameter ("kvstore.driver_threads", 1);
		configuration.addParameter ("kvstore.bucket", "10");
		final Scenario scenario = new Scenario (RedisKvStoreConnectorTest.class, configuration);
		scenario.registerDriverRole (KeyValueSession.DRIVER);
		BaseConnectorTest.driverStub = KeyValueStub.createDetached (configuration, scenario.getThreading (), scenario.getDriverChannel ());
		RedisKvStoreConnectorTest.scenario_ = scenario;
	}
	
	@AfterClass
	public static void tearDownAfterClass ()
	{
		BaseConnectorTest.tearDownScenario (RedisKvStoreConnectorTest.scenario_);
	}
	
	private static final String MOSAIC_REDIS_HOST = "mosaic.tests.resources.redis.host";
	private static final String MOSAIC_REDIS_HOST_DEFAULT = "127.0.0.1";
	private static final String MOSAIC_REDIS_PORT = "mosaic.tests.resources.redis.port";
	private static final String MOSAIC_REDIS_PORT_DEFAULT = "6379";
	private static Scenario scenario_;
}