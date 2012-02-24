/*
 * #%L
 * mosaic-cloudlets
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

package eu.mosaic_cloud.cloudlets.runtime;

import java.util.ArrayList;
import java.util.List;

import eu.mosaic_cloud.cloudlets.core.CloudletException;
import eu.mosaic_cloud.cloudlets.core.ICloudletCallback;
import eu.mosaic_cloud.cloudlets.tools.ConfigProperties;
import eu.mosaic_cloud.platform.core.configuration.ConfigUtils;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.configuration.PropertyTypeConfiguration;
import eu.mosaic_cloud.platform.core.exceptions.ExceptionTracer;
import eu.mosaic_cloud.platform.core.log.MosaicLogger;
import eu.mosaic_cloud.tools.miscellaneous.Monitor;
import eu.mosaic_cloud.tools.threading.core.ThreadingContext;

/**
 * Implements a container holding a list of cloudlet instances. All instances
 * have the same cloudlet type.
 * 
 * @author Georgiana Macariu
 * 
 */
public class CloudletManager {

    /**
     * Pool containing all cloudlet instances of the cloudlet. Accessed only
     * when holding mainLock.
     */
    private List<Cloudlet<?>> cloudletPool;

    private ThreadingContext threading;

    private ClassLoader classLoader;

    private final Monitor monitor = Monitor.create(this);

    private IConfiguration configuration;

    private static MosaicLogger logger = MosaicLogger.createLogger(CloudletManager.class);

    /**
     * Creates a new container.
     * 
     * @param classLoader
     *            class loader to be used for loading the classes of the
     *            cloudlet
     * @param configuration
     *            configuration object of the cloudlet
     */
    public CloudletManager(ThreadingContext threading, ClassLoader classLoader,
            IConfiguration configuration) {
        super();
        synchronized (this.monitor) {
            this.threading = threading;
            this.classLoader = classLoader;
            this.configuration = configuration;
            this.cloudletPool = new ArrayList<Cloudlet<?>>();
            // FIXME
        }
    }

    /**
     * Creates a new cloudlet instance.
     * 
     * @throws CloudletException
     *             if the cloudlet instance cannot be created
     */
    private void createCloudletInstance() throws CloudletException {
        Class<?> handlerClasz;
        Class<?> stateClasz;
        IConfiguration resourceConfig;
        try {
            final String cloudletClass = ConfigUtils.resolveParameter(this.configuration,
                    ConfigProperties.getString("CloudletComponentCallbacks.8"), String.class, ""); //$NON-NLS-1$ //$NON-NLS-2$
            if (cloudletClass.equals("")) {
                throw new CloudletException("The configuration file " //$NON-NLS-1$
                        + this.configuration.toString()
                        + " does not specify a handler class for cloudlet " //$NON-NLS-1$
                        + cloudletClass + "."); //$NON-NLS-1$
            }
            final String cloudletStateClass = ConfigUtils.resolveParameter(this.configuration,
                    ConfigProperties.getString("CloudletComponentCallbacks.8"), String.class, ""); //$NON-NLS-1$ //$NON-NLS-2$
            if (cloudletStateClass.equals("")) {
                throw new CloudletException("The configuration file " //$NON-NLS-1$
                        + this.configuration.toString()
                        + " does not specify a context class for cloudlet " //$NON-NLS-1$
                        + cloudletClass + "."); //$NON-NLS-1$
            }
            final String resourceFile = ConfigUtils.resolveParameter(this.configuration,
                    ConfigProperties.getString("CloudletComponentCallbacks.10"), String.class, ""); //$NON-NLS-1$ //$NON-NLS-2$
            handlerClasz = this.classLoader.loadClass(cloudletClass);
            stateClasz = this.classLoader.loadClass(cloudletStateClass);
            resourceConfig = PropertyTypeConfiguration.create(this.classLoader, resourceFile);
            final ICloudletCallback<?> cloudlerHandler = (ICloudletCallback<?>) createHandler(handlerClasz);
            final Object cloudletState = invokeConstructor(stateClasz);
            final Cloudlet<?> cloudlet = new Cloudlet(cloudletState, cloudlerHandler,
                    this.threading, this.classLoader);
            cloudlet.initialize(resourceConfig);
            this.cloudletPool.add(cloudlet);
        } catch (final ClassNotFoundException e) {
            ExceptionTracer.traceDeferred(e);
            CloudletManager.logger.error("Could not resolve class: " + e.getMessage()); //$NON-NLS-1$
            throw new IllegalArgumentException(e);
        }
    }

    private <T> T createHandler(final Class<T> clasz) {
        T instance = null;
        final boolean isCallback = ICloudletCallback.class.isAssignableFrom(clasz);
        if (!isCallback) {
            CloudletManager.logger.error("Missmatched object class: `" + clasz.getName() + "`"); //$NON-NLS-1$ //$NON-NLS-2$
            throw new IllegalArgumentException();
        }
        try {
            instance = clasz.newInstance();
        } catch (final Throwable exception) {
            ExceptionTracer.traceDeferred(exception);
            CloudletManager.logger.error("Could not instantiate class: `" + clasz + "`"); //$NON-NLS-1$ //$NON-NLS-2$
            throw new IllegalArgumentException(exception);
        }
        return instance;
    }

    private final Object invokeConstructor(final Class<?> clasz) {
        Object instance;
        try {
            instance = clasz.newInstance();
        } catch (final Throwable exception) {
            CloudletManager.logger.error("Could not instantiate class: `" + clasz + "`"); //$NON-NLS-1$ //$NON-NLS-2$
            ExceptionTracer.traceIgnored(exception);
            throw new IllegalArgumentException();
        }
        return (instance);
    }

    /**
     * Starts the container.
     * 
     * @throws CloudletException
     */
    public void start() throws CloudletException {
        synchronized (this.monitor) {
            createCloudletInstance();
        }
    }

    /**
     * Stops the container and destroys all hosted cloudlets.
     */
    public final void stop() {
        synchronized (this.monitor) {
            for (final Cloudlet<?> cloudlet : this.cloudletPool) {
                if (cloudlet.isActive()) {
                    cloudlet.destroy();
                }
            }
        }
    }
}
