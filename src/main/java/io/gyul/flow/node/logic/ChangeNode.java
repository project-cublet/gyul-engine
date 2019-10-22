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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Template;

import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.SimpleNode;
import io.gyul.flow.node.AbstractNode;
import io.gyul.flow.node.logic.ChangeNodeConfig.ChangeRule;
import io.gyul.flow.node.logic.ChangeNodeConfig.ChangeRuleType;
import io.gyul.flow.spi.FlowNode;
import io.gyul.flow.util.HandlebarsUtils;
import io.gyul.util.JsonUtils;

/**
 * ChangeNode
 * 
 * @author sungjae
 */
@FlowNode
public class ChangeNode extends AbstractNode<ChangeNodeConfig> implements SimpleNode<ChangeNodeConfig> {

	private Map<String, Template> templateMap = new HashMap<>();

	@Override
	public void init() {
		config.getChangeRules().stream().filter(r -> r.getRuleType() == ChangeRuleType.SET)
				.forEach(r -> templateMap.put(r.getValueTemplate(), HandlebarsUtils.compile(r.getValueTemplate())));
	}

	@Override
	public Message process(Message message) {
		for(ChangeRule changeRule : config.getChangeRules()) {
			switch(changeRule.getRuleType()) {
			case SET:
				setValue(message, changeRule);
				break;
			case COPY:
				copyValue(message, changeRule);
				break;
			case MOVE:
				moveValue(message, changeRule);
				break;
			case REMOVE:
				removeValue(message, changeRule);
				break;
			}
		}
		return message;
	}

	protected void setValue(Message message, ChangeRule changeRule) {
		Object value = JsonUtils
				.tryJsonfy(HandlebarsUtils.apply(templateMap.get(changeRule.getValueTemplate()), message));
		message.putPayload(changeRule.getDestPath(), value);
	}

	protected void copyValue(Message message, ChangeRule changeRule) {
		JsonNode node = JsonUtils.path(message.getPayload(), changeRule.getSourcePath());
		if (!node.isMissingNode()) {
			message.putPayload(changeRule.getDestPath(), node.deepCopy());
		}
	}

	protected void moveValue(Message message, ChangeRule changeRule) {
		JsonNode node = JsonUtils.removePath(message.getPayload(), changeRule.getSourcePath());
		if (!node.isMissingNode()) {
			message.putPayload(changeRule.getDestPath(), node);
		}
	}

	protected void removeValue(Message message, ChangeRule changeRule) {
		JsonUtils.removePath(message.getPayload(), changeRule.getSourcePath());
	}

}
