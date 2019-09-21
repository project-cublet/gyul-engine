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
package io.gyul.flow.service;

import static io.gyul.flow.util.FlowUtils.buildFlowNodeId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * VirtualButtonService
 *  
 * @author sungjae
 */
@Service
@Slf4j
public class VirtualButtonService {
	
	private Map<String, VirtualButtonListener> listenerMap = new ConcurrentHashMap<>();
	
	public void addVirtualButtonListener(String flowId, String nodeId, VirtualButtonListener virtualButtonNode) {
		String flowNodeId = buildFlowNodeId(flowId, nodeId);
		listenerMap.put(flowNodeId, virtualButtonNode);
		log.info("[{}] VirtualButtonListener added.", flowNodeId);
	}
	
	public void removeVirtualButtonListener(String flowId, String nodeId) {
		String flowNodeId = buildFlowNodeId(flowId, nodeId);
		if(listenerMap.remove(flowNodeId) != null) {
			log.info("[{}] VirtualButtonListener removed.", flowNodeId);	
		}
	}
	
	public void triggerVirtualButton(String flowId, String nodeId) {
		String flowNodeId = buildFlowNodeId(flowId, nodeId);
		@NonNull VirtualButtonListener listener = listenerMap.get(flowNodeId);
		listener.onTrigger();
		log.debug("[{}] VirtualButtonListener triggered.", flowNodeId);
	}
	
	public void triggerVirtualButton(String flowId, String nodeId, ObjectNode payload) {
		String flowNodeId = buildFlowNodeId(flowId, nodeId);
		@NonNull VirtualButtonListener listener = listenerMap.get(flowNodeId);
		listener.onTrigger(payload);
		log.debug("[{}] VirtualButtonListener triggered. payload={}", flowNodeId, payload);
	}
	
}
