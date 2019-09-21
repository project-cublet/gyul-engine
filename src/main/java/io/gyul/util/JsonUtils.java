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
package io.gyul.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.NonNull;

/**
 * JsonUtils
 *  
 * @author sungjae
 */
public class JsonUtils {
	public static final char PATH_SEPARATOR = '.';
	
	public static final ObjectMapper mapper = new ObjectMapper();

	public static <T> T readValue(String string, @NonNull Class<T> clazz) {
		try {
			return mapper.readValue(string, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T readValue(String string, @NonNull TypeReference valueTypeRef) {
		try {
			return mapper.readValue(string, valueTypeRef);
		} catch (IOException e) {
			throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object", e);
		}
	}

	public static <T> T treeToValue(JsonNode jsonNode, @NonNull Class<T> clazz) {
		try {
			return mapper.treeToValue(jsonNode, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException("The given json: " + jsonNode + " cannot be transformed to Json object", e);
		}
	}
	
	public static String writeValueAsString(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String", e);
		}
	}

	public static JsonNode readTree(String jsonContents) {
		if (jsonContents == null || jsonContents.isEmpty()) {
			return null;
		}
		try {
			return mapper.readTree(jsonContents);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	
	public static Object tryJsonfy(String contents) {
		if (contents == null || contents.isEmpty()) {
			return null;
		}
		try {
			return mapper.readTree(contents);
		} catch(IOException ignore) {
			return contents;
		}
	}
	
	public static JsonNode toJsonNode(Object fromValue) {
		return mapper.valueToTree(fromValue);
	}
	
	/**
	 * 경로 표현식을 통한 JsonNode 경로 찾기 
	 * @param node
	 * @param paths 경로 표현식 ex) data.anArray.[1]
	 * @return 경로의 JsonNode. 일치하는 경로가 없는  경우 MissingNode가 반환됨
	 */
	public static JsonNode path(JsonNode node, String paths) {
		if(StringUtils.isEmpty(paths)) {
			return node;
		}
		paths = paths.trim();
		JsonNode current = node;
		String[] pathList = StringUtils.split(paths, PATH_SEPARATOR);
		for(String path : pathList) {
			current = findPath(current, path);
		}
		return current;
	}
	
	/**
	 * 경로 표현식을 통해 JsonNode의 필드를 삭제
	 * @param node
	 * @param paths 경로 표현식. ex) data.anArray.[1]
	 * @return 삭제된 JsonNode. 경로가 일치하지 않은 경우 MissingNode가 반환됨
	 */
	public static JsonNode removePath(JsonNode node, String paths) {
		if(StringUtils.isEmpty(paths)) {
			return MissingNode.getInstance();
		}
		paths = paths.trim();
		JsonNode current = node;
		String[] pathList = StringUtils.split(paths, PATH_SEPARATOR);
		for(int i = 0; i < pathList.length - 1; i++) {
			String path = pathList[i];
			current = findPath(current, path);
		}
		String path = StringUtils.strip(pathList[pathList.length - 1], "[ ]");
		if (current.isArray()) {
			if (StringUtils.isNumeric(path)) {
				return ((ArrayNode)current).remove(Integer.parseInt(path));
			}
		} else if(current.isObject()) {
			return ((ObjectNode)current).remove(path);
		}
		return MissingNode.getInstance();
	}
	
	private static JsonNode findPath(JsonNode node, String path) {
		path = StringUtils.strip(path, "[ ]");
		if (StringUtils.isNumeric(path)) {
			return node.path(Integer.parseInt(path));
		} else {
			return node.path(path);
		}
	}
	
	public static String toReadableString(JsonNode node) {
		if(node == null) {
			return null;
		}
		if(node.isNull() || node.isMissingNode() ) {
			return null;
		}
		if(node.isValueNode()) {
			return node.asText();
		}
		return node.toString();
	}
	
	public static ObjectNode objectNode() {
		return JsonNodeFactory.instance.objectNode();
	}
}
