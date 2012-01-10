/*
 * #%L
 * mosaic-platform-core
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
package eu.mosaic_cloud.core.ops;

/**
 * Factory class which builds the asynchronous calls for the operations
 * supported by a specific resource. This interface should be implemented for
 * each resource kind supported by the platform.
 * 
 * @author Georgiana Macariu
 * 
 */
public interface IOperationFactory {

	/**
	 * Builds the asynchronous operation.
	 * 
	 * @param type
	 *            the type of the operation
	 * @param parameters
	 *            the parameters of the operation
	 * @return the operation
	 */
	IOperation<?> getOperation(IOperationType type, Object... parameters);

	/**
	 * Destroys a facory..
	 */
	void destroy();

}
