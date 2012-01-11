/*
 * #%L
 * mosaic-examples-realtime-feeds-indexer
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
package eu.mosaic_cloud.examples.feeds;

import java.util.HashMap;
import java.util.Map;

import eu.mosaic_cloud.examples.feeds.IndexerCloudlet.IndexerCloudletState;

import eu.mosaic_cloud.cloudlet.core.CallbackArguments;
import eu.mosaic_cloud.cloudlet.resources.kvstore.DefaultKeyValueAccessorCallback;
import eu.mosaic_cloud.cloudlet.resources.kvstore.KeyValueCallbackArguments;
import eu.mosaic_cloud.core.log.MosaicLogger;

public class TasksKVCallback extends
		DefaultKeyValueAccessorCallback<IndexerCloudletState> {

	private static final String BUCKET_NAME = "feed-tasks";

	@Override
	public void destroySucceeded(IndexerCloudletState state,
			CallbackArguments<IndexerCloudletState> arguments) {
		state.taskStore = null;
	}

	@Override
	public void setFailed(IndexerCloudletState state,
			KeyValueCallbackArguments<IndexerCloudletState> arguments) {
		handleError(arguments);
	}

	private void handleError(
			KeyValueCallbackArguments<IndexerCloudletState> arguments) {
		String key = arguments.getKey();
		MosaicLogger.getLogger().warn(
				"failed fetch (" + TasksKVCallback.BUCKET_NAME + "," + key
						+ ")");
		Map<String, String> errorMssg = new HashMap<String, String>(4);
		errorMssg.put("reason", "unexpected key-value store error");
		errorMssg.put("message", arguments.getValue().toString());
		errorMssg.put("bucket", TasksKVCallback.BUCKET_NAME);
		errorMssg.put("key", key);
		IndexWorkflow.onIndexError(errorMssg, arguments.getExtra());
	}
}
