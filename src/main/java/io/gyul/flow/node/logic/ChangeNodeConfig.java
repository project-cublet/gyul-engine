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
package io.gyul.flow.node.logic;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

/**
 * ChangeNodeConfig
 *  
 * @author sungjae
 */
@Value
@JsonDeserialize(builder = ChangeNodeConfig.ChangeNodeConfigBuilder.class)
@Builder
public class ChangeNodeConfig {
	
	private List<ChangeRule> changeRules;
	
	enum ChangeRuleType { SET, REMOVE, COPY, MOVE }
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class ChangeNodeConfigBuilder {

	}
	
	@Data
	static class ChangeRule {
		private ChangeRuleType ruleType;
		private String valueTemplate;
		private String sourcePath;
		private String destPath;
	}
}
