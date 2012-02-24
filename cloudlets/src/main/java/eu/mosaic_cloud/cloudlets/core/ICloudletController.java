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

package eu.mosaic_cloud.cloudlets.core;

import eu.mosaic_cloud.cloudlets.connectors.core.IConnectorsFactory;
import eu.mosaic_cloud.platform.core.configuration.IConfiguration;
import eu.mosaic_cloud.platform.core.ops.CompletionInvocationHandler;
import eu.mosaic_cloud.platform.core.ops.IOperationCompletionHandler;
import eu.mosaic_cloud.tools.callbacks.core.Callbacks;

/**
 * Interface for cloudlet control operations. Each cloudlet has access to an
 * object implementing this interface and uses it to ask for resources or
 * destroying them when they are not required anymore.
 * 
 * @author Georgiana Macariu
 * 
 * @param <Context>
 *            the type of the context of the cloudlet
 */
public interface ICloudletController<Context> extends Callbacks, IConnectorsFactory {

    <T> T buildCallbackInvoker(T callback, Class<T> callbackType);

    /**
     * Destroys the cloudlet.
     * 
     * @return <code>true</code> if cloudlet was successfully destroyed
     */
    boolean destroy();

    IConfiguration getConfiguration();

    <T> CompletionInvocationHandler<T> getResponseInvocationHandler(
            IOperationCompletionHandler<T> handler);

    /**
     * Indicates if the cloudlet is alive and can receive messages or not.
     * 
     * @return <code>true</code> if cloudlet is alive
     */
    boolean isActive();
}
