/*
 * #%L
 * mosaic-tools-exceptions
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

package eu.mosaic_cloud.tools.exceptions.core;


public final class IgnoredException
			extends CaughtException
{
	public IgnoredException (final Throwable exception) {
		super (ExceptionResolution.Ignored, exception);
	}
	
	public IgnoredException (final Throwable exception, final String message) {
		super (ExceptionResolution.Ignored, exception, message);
	}
	
	public IgnoredException (final Throwable exception, final String messageFormat, final Object ... messageArguments) {
		super (ExceptionResolution.Ignored, exception, messageFormat, messageArguments);
	}
	
	private static final long serialVersionUID = 1L;
}
