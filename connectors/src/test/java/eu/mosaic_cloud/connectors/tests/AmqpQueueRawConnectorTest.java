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

package eu.mosaic_cloud.connectors.tests;

import eu.mosaic_cloud.connectors.queue.amqp.AmqpQueueRawConnector;
import eu.mosaic_cloud.drivers.interop.queue.amqp.AmqpStub;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.interop.common.amqp.AmqpExchangeType;
import eu.mosaic_cloud.platform.interop.specs.amqp.AmqpSession;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public class AmqpQueueRawConnectorTest extends
        BaseConnectorTest<AmqpQueueRawConnector, BaseScenario> {

    private static BaseScenario scenario_;

    @BeforeClass
    public static void setUpBeforeClass() {
        final BaseScenario scenario = new BaseScenario(
                AmqpQueueRawConnectorTest.class,
                "amqp-queue-driver-test.properties");
        BaseConnectorTest.setUpScenario(AmqpQueueRawConnectorTest.class);
        scenario.registerDriverRole(AmqpSession.DRIVER);
        driverStub = AmqpStub.createDetached(scenario.getConfiguration(),
                scenario.getDriverChannel(), scenario.getThreading());
        AmqpQueueRawConnectorTest.scenario_ = scenario;
    }

    @AfterClass
    public static void tearDownAfterClass() {
        BaseConnectorTest.tearDownScenario(AmqpQueueRawConnectorTest.scenario_);
    }

    @Override
    @Before
    public void setUp() {
        this.scenario = AmqpQueueRawConnectorTest.scenario_;
        this.connector = AmqpQueueRawConnector.create(
                this.scenario.getConfiguration(),
                this.scenario.getEnvironment());
    }

    @Override
    public void test() {
        this.testConnector();
        this.testDeclareExchange();
        this.testDeclareQueue();
        this.testBindQueue();
    }

    protected void testBindQueue() {
        IConfiguration configuration = this.scenario.getConfiguration();
        final String exchange = ConfigUtils.resolveParameter(configuration,
                "publisher.amqp.exchange", String.class, "");
        final String routingKey = ConfigUtils.resolveParameter(configuration,
                "publisher.amqp.routing_key", String.class, "");
        final String queue = ConfigUtils.resolveParameter(configuration,
                "consumer.amqp.queue", String.class, "");
        Assert.assertTrue(this.awaitSuccess(this.connector.bindQueue(exchange,
                queue, routingKey)));
    }

    protected void testDeclareExchange() {
        final String exchange = ConfigUtils.resolveParameter(
                this.scenario.getConfiguration(), "publisher.amqp.exchange",
                String.class, "");
        Assert.assertTrue(this.awaitSuccess(this.connector.declareExchange(
                exchange, AmqpExchangeType.DIRECT, false, false, false)));
    }

    protected void testDeclareQueue() {
        final String queue = ConfigUtils.resolveParameter(
                this.scenario.getConfiguration(), "consumer.amqp.queue",
                String.class, "");
        Assert.assertTrue(this.awaitSuccess(this.connector.declareQueue(queue,
                true, false, true, false)));
    }
}