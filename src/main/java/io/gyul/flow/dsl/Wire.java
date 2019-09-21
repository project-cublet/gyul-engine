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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Wire
 * 
 * @author sungjae
 */
@Value
@AllArgsConstructor(staticName = "of")
@JsonDeserialize(builder = Wire.WireBuilder.class)
@Builder
public class Wire {

	@NonNull
	private String fromNode;
	private String fromPort;
	@NonNull
	private String toNode;

	public static Wire of(String fromNode, String toNode) {
		return of(fromNode, null, toNode);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class WireBuilder {
	}
}
