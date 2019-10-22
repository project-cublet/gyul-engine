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

import java.util.Collection;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.gyul.util.JsonUtils;
import lombok.Value;

/**
 * Message
 *  
 * @author sungjae
 */
@Value(staticConstructor = "of")
public class Message {
	public static final String MESSAGE_ROOT = "message";
	private static final int MAX_PATH_SIZE = 100;
	
	private final long time;
	private ObjectNode params;
	private ObjectNode payload;
	@JsonIgnore
	private final Collection<String> nodePath = new CircularFifoQueue<>(MAX_PATH_SIZE);
	
	public static Message empty() {
		return Message.of(System.currentTimeMillis(), JsonUtils.objectNode(), JsonUtils.objectNode());
	}
	
	public static Message of(ObjectNode params) {
		return Message.of(System.currentTimeMillis(), params, JsonUtils.objectNode());
	}
	
	public static Message of(ObjectNode params, ObjectNode payload) {
		return Message.of(System.currentTimeMillis(), params, payload);
	}
	
	public void putPayload(String path, Object data) {
		String[] tokens = StringUtils.split(path.trim(), JsonUtils.PATH_SEPARATOR);
		String field = tokens[tokens.length - 1].trim();
		ObjectNode node = payload;
		for (int i = 0; i < tokens.length - 1; i++) {
			node = node.with(tokens[i].trim());
		}
		if (data instanceof JsonNode) {
			node.set(field, (JsonNode) data);
		} else {
			node.set(field, JsonUtils.toJsonNode(data));
		}
	}
	
	protected void addPath(String nodeId) {
		nodePath.add(nodeId);
	}
	
	/**
	 * 메시지가 출 체인 끝단에 도달해서 완료된 회수 기록
	 */
	protected void completed() {
		//TODO
	}
	
	/**
	 * 메시지가 룰 체인 내에서 실행이 완료되서 더 이상 진행해야 할 노드가 없는지 판단 
	 * @return
	 */
	protected boolean checkAllCompleted() {
		//TODO
		return false;
	}
}
