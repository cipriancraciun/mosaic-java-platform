/*
 * #%L
 * mosaic-tools-transcript
 * %%
 * Copyright (C) 2010 - 2012 eAustria Research Institute (Timisoara, Romania)
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

package eu.mosaic_cloud.transcript.core;


import eu.mosaic_cloud.exceptions.core.ExceptionTracer;


public interface TranscriptBackend
		extends
			ExceptionTracer
{
	public abstract void trace (final TranscriptTraceType type, final String message);
	
	public abstract void trace (final TranscriptTraceType type, final String format, final Object ... tokens);
}
