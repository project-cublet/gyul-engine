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
package io.gyul.flow.dsl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.gyul.flow.engine.Node;
import io.gyul.util.JsonUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Node
 *  
 * @author sungjae
 */
@Value
@JsonDeserialize(builder = NodeDefinition.NodeDefinitionBuilder.class)
@Builder
public class NodeDefinition {
	@NonNull
	private String id;
	private String name;
	private String description;
	@NonNull
	private Class<? extends Node<?>> nodeClazz;
	private ObjectNode config;
	private int x;
	private int y;
	
    @JsonPOJOBuilder(withPrefix = "")
    public static class NodeDefinitionBuilder {
    	public NodeDefinitionBuilder configSrc(Object configSrc) {
    		return config((ObjectNode)JsonUtils.toJsonNode(configSrc));
    	}
    }
}
