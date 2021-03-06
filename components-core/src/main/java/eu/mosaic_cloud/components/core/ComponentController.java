/*
 * #%L
 * mosaic-components-core
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

package eu.mosaic_cloud.components.core;


import eu.mosaic_cloud.tools.callbacks.core.CallbackCompletion;
import eu.mosaic_cloud.tools.callbacks.core.Callbacks;


public interface ComponentController
			extends
				Callbacks
{
	public abstract CallbackCompletion<Void> acquire (final ComponentAcquireRequest request);
	
	public abstract CallbackCompletion<Void> bind (final ComponentCallbacks callbacks, final ChannelController channel);
	
	public abstract CallbackCompletion<Void> call (final ComponentIdentifier component, final ComponentCallRequest request);
	
	public abstract CallbackCompletion<Void> callReturn (final ComponentCallReply reply);
	
	public abstract CallbackCompletion<Void> cast (final ComponentIdentifier component, final ComponentCastRequest request);
	
	public abstract CallbackCompletion<Void> register (final ComponentIdentifier group, final ComponentCallReference reference);
	
	public abstract CallbackCompletion<Void> terminate ();
}
