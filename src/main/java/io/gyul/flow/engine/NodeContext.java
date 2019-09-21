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
package io.gyul.flow.engine;

import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Value;

/**
 * NodeContext
 *  
 * @author sungjae
 */
@Value
public class NodeContext {
	private FlowContext flowContext;
	private String nodeId;
	private String nodeName;
	
	public String getFlowId() {
		return flowContext.getId();
	}
	
	public String getRootFlowId() {
		return flowContext.getRootFlowId();
	}
	
	public ObjectNode getParams() {
		return flowContext.getParams();
	}
	
	public int getParallelism() {
		return flowContext.getParallelism();
	}
	
	public String id() {
		return flowContext.getId() + ":" + nodeId;
	}
	
	public String displayName() {
		return flowContext.getName() + ":" + nodeName;
	}
	
	public void checkIn(Message message) {
		message.addPath(nodeId);
	}
	
	public void checkOut() {
		//TODO
	}
	
	public void markError(Message message, Throwable error) {
		markError(message, null, error);
	}
	
	public void markError(Message message, String errorName, Throwable error) {
		markError(message, buildErrorMessage(errorName, error));
	}
	
	protected String buildErrorMessage(String errorName, Throwable error) {
		Throwable cause = error;
		if(error.getCause() != null && error instanceof CompletionException) {
			cause = error.getCause();
		}

		String detailMessage = null;
		if(cause.getLocalizedMessage() == null) {
			detailMessage = cause.getClass().getSimpleName();
		} else if(StringUtils.contains(cause.getLocalizedMessage(), "Exception: ")){
			detailMessage = StringUtils.substringAfterLast(cause.getLocalizedMessage(), "Exception: ");
		} else {
			detailMessage = cause.getLocalizedMessage();
		}
		return errorName == null ? detailMessage : errorName + ": " + detailMessage;
	}
	
	public void markError(Message message, String error) {
//		flowContext.incrementError(nodeId);
//		if(flowContext.getDebugNodeService() != null) {
//			DebugData debugData = new DebugData();
//			debugData.setRuleChainId(flowContext.getId());
//			debugData.setNodeId(nodeId);
//			debugData.setNodeName(nodeName);
//			debugData.setLoggingLevel(LoggingLevel.ERROR.name());
//			debugData.setTimestamp(System.currentTimeMillis());
//			debugData.setMessage(message);
//			debugData.setNodePath(new ArrayList<>(message.getNodePath()));
//			debugData.setLog(error);
//			flowContext.getDebugNodeService().onDebug(debugData);
//		}
		flowContext.completedWithError(message, error);
	}
	
	public void addFlowContextListener(FlowCompletionListener listener) {
		flowContext.addListener(listener);
	}
	
	public void removeFlowContextListener(FlowCompletionListener listener) {
		flowContext.removeListener(listener);
	}
	
	public void completed(Message message, boolean isBreak) {
		flowContext.completed(message, isBreak);
	}
	
	public void publishEvent(Object event) {
		flowContext.publishEvent(event);
	}
}
