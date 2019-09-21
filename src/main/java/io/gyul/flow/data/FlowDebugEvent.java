/**
 * Copyright © 2019 The Project-gyul Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gyul.flow.data;

import java.util.List;
import java.util.UUID;

import io.gyul.flow.engine.Message;
import lombok.Data;

/**
 * FlowDebugEvent
 *  
 * @author sungjae
 */
@Data
public class FlowDebugEvent {
	private final String id;
	private final long timestamp;
	private String flowId;
	private String rootFlowId;
	private String nodeId;
	private String nodeName;
	private String loggingLevel;
	private String log;
	private Message message;
	private List<String> nodePath;

	public FlowDebugEvent() {
		id = UUID.randomUUID().toString();
		timestamp = System.currentTimeMillis();
	}
}
