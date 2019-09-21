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
package io.gyul.flow.node.output;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * DebugNodeConfig
 * 
 * @author sungjae
 */
@Value
@JsonDeserialize(builder = DebugNodeConfig.DebugNodeConfigBuilder.class)
@Builder
public class DebugNodeConfig {

	public enum LoggingLevel {
		DEBUG, INFO, WARN, ERROR
	}

	@NonNull
	@Builder.Default
	private LoggingLevel loggingLevel = LoggingLevel.INFO;
	
	private String messageTemplate;
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class DebugNodeConfigBuilder {

	}
}
