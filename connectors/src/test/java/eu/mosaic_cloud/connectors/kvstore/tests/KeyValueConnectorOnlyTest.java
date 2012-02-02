/*
 * #%L
 * mosaic-connectors
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

package eu.mosaic_cloud.connectors.kvstore.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import eu.mosaic_cloud.connectors.kvstore.KeyValueStoreConnector;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.platform.core.ops.IResult;
import eu.mosaic_cloud.platform.core.tests.TestLoggingHandler;
import eu.mosaic_cloud.platform.core.utils.PojoDataEncoder;
import eu.mosaic_cloud.tools.exceptions.tools.NullExceptionTracer;
import eu.mosaic_cloud.tools.exceptions.tools.QueueingExceptionTracer;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingContext;
import eu.mosaic_cloud.tools.threading.implementations.basic.BasicThreadingSecurityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyValueConnectorOnlyTest {

	private KeyValueStoreConnector<String> connector;
	private BasicThreadingContext threadingContext;

	private static String keyPrefix;
	private static final long timeout = 1000 * 1000;

	@BeforeClass
	public static void setUpBeforeClass() throws Throwable {
		KeyValueConnectorOnlyTest.keyPrefix = UUID.randomUUID().toString();
	}

	@Before
	public void setUp() throws Throwable {
		QueueingExceptionTracer exceptions = QueueingExceptionTracer
				.create(NullExceptionTracer.defaultInstance);
		BasicThreadingSecurityManager.initialize();
		this.threadingContext = BasicThreadingContext.create(this,
				exceptions.catcher);
		final IConfiguration config = PropertyTypeConfiguration.create(
				KeyValueConnectorOnlyTest.class.getClassLoader(),
				"kv-test.prop");
		this.connector = KeyValueStoreConnector.create(config,
				new PojoDataEncoder<String>(String.class),
				this.threadingContext);
		KeyValueConnectorOnlyTest.keyPrefix = UUID.randomUUID().toString();
	}

	@After
	public void tearDown() throws Throwable {
		this.connector.destroy();
		this.threadingContext.destroy();
	}

	private static <T> List<IOperationCompletionHandler<T>> getHandlers(
			final String testName) {
		final IOperationCompletionHandler<T> handler = new TestLoggingHandler<T>(
				testName);
		final List<IOperationCompletionHandler<T>> list = new ArrayList<IOperationCompletionHandler<T>>();
		list.add(handler);
		return list;
	}

	public void testConnection() {
		Assert.assertNotNull(this.connector);
	}

	@Test
	public void testConnector() throws IOException, ClassNotFoundException {
		this.testConnection();
		this.testSet();
		this.testGet();
		this.testDelete();
	}

	public void testDelete() {
		final String k1 = KeyValueConnectorOnlyTest.keyPrefix
				+ "_key_fantastic";
		final List<IOperationCompletionHandler<Boolean>> handlers = KeyValueConnectorOnlyTest
				.getHandlers("delete");
		final IResult<Boolean> r1 = this.connector.delete(k1, handlers, null);
		try {
			Assert.assertTrue(r1.getResult(KeyValueConnectorOnlyTest.timeout,
					TimeUnit.MILLISECONDS));
		} catch (final Exception e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
		final List<IOperationCompletionHandler<String>> handlers1 = KeyValueConnectorOnlyTest
				.getHandlers("get after delete");
		final IResult<String> r2 = this.connector.get(k1, handlers1, null);
		try {
			Assert.assertNull(r2.getResult(KeyValueConnectorOnlyTest.timeout,
					TimeUnit.MILLISECONDS));
		} catch (final Exception e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public void testGet() throws IOException, ClassNotFoundException {
		final String k1 = KeyValueConnectorOnlyTest.keyPrefix
				+ "_key_fantastic";
		final List<IOperationCompletionHandler<String>> handlers = KeyValueConnectorOnlyTest
				.getHandlers("get");
		final IResult<String> r1 = this.connector.get(k1, handlers, null);
		try {
			Assert.assertEquals(
					"fantastic",
					r1.getResult(KeyValueConnectorOnlyTest.timeout,
							TimeUnit.MILLISECONDS).toString());
		} catch (final Exception e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public void testSet() throws IOException {
		final String k1 = KeyValueConnectorOnlyTest.keyPrefix
				+ "_key_fantastic";
		final List<IOperationCompletionHandler<Boolean>> handlers1 = KeyValueConnectorOnlyTest
				.getHandlers("set 1");
		final IResult<Boolean> r1 = this.connector.set(k1, "fantastic",
				handlers1, null);
		Assert.assertNotNull(r1);
		final String k2 = KeyValueConnectorOnlyTest.keyPrefix + "_key_famous";
		final List<IOperationCompletionHandler<Boolean>> handlers2 = KeyValueConnectorOnlyTest
				.getHandlers("set 2");
		final IResult<Boolean> r2 = this.connector.set(k2, "famous", handlers2,
				null);
		Assert.assertNotNull(r2);
		try {
			Assert.assertTrue(r1.getResult(KeyValueConnectorOnlyTest.timeout,
					TimeUnit.MILLISECONDS));
			Assert.assertTrue(r2.getResult(KeyValueConnectorOnlyTest.timeout,
					TimeUnit.MILLISECONDS));
		} catch (final Exception e) {
			ExceptionTracer.traceIgnored(e);
			Assert.fail();
		}
	}

	public static void main(final String[] arguments) throws Throwable {
		Preconditions.checkArgument((arguments != null)
				&& (arguments.length == 0));
		KeyValueConnectorOnlyTest.setUpBeforeClass();
		KeyValueConnectorOnlyTest test = new KeyValueConnectorOnlyTest();
		test.setUp();
		test.testConnector();
		test.tearDown();
	}

}
