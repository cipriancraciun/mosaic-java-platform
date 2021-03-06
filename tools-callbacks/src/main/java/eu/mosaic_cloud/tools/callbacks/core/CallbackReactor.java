/*
 * #%L
 * mosaic-tools-callbacks
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

package eu.mosaic_cloud.tools.callbacks.core;


import eu.mosaic_cloud.tools.threading.core.Joinable;


public interface CallbackReactor
			extends
				Joinable
{
	public abstract <_Callbacks_ extends Callbacks> CallbackCompletion<Void> assignDelegate (final _Callbacks_ proxy, _Callbacks_ delegate);
	
	public abstract <_Callbacks_ extends Callbacks> CallbackCompletion<Void> assignHandler (final _Callbacks_ proxy, final CallbackHandler handler, final CallbackIsolate isolate);
	
	public abstract CallbackIsolate createIsolate ();
	
	public abstract <_Callbacks_ extends Callbacks> _Callbacks_ createProxy (final Class<_Callbacks_> specification);
	
	public abstract CallbackCompletion<Void> destroyIsolate (final CallbackIsolate isolate);
	
	public abstract <_Callbacks_ extends Callbacks> CallbackCompletion<Void> destroyProxy (final _Callbacks_ proxy);
}
