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

import java.util.ArrayList;

import com.github.jknack.handlebars.Template;

import io.gyul.flow.data.FlowDebugEvent;
import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.SimpleNode;
import io.gyul.flow.node.AbstractNode;
import io.gyul.flow.spi.FlowNode;
import io.gyul.flow.util.HandlebarsUtils;

/**
 * DebugNode
 *  
 * @author sungjae
 */
@FlowNode
public class DebugNode extends AbstractNode<DebugNodeConfig> implements SimpleNode<DebugNodeConfig> {

	private Template messageTemplate;
	
	@Override
	public void init() {
		messageTemplate = HandlebarsUtils.compile(config.getMessageTemplate(), true);
	}
	
	public Message process(Message message) {
		FlowDebugEvent event = new FlowDebugEvent();
		event.setFlowId(context.getFlowId());
		event.setRootFlowId(context.getRootFlowId());
		event.setNodeId(context.getNodeId());
		event.setNodeName(context.getNodeName());
		event.setMessage(message);
		event.setLog(HandlebarsUtils.apply(messageTemplate, message));
		event.setLoggingLevel(config.getLoggingLevel().name());
		event.setNodePath(new ArrayList<String>(message.getNodePath()));
		context.publishEvent(event);
		return message;
	}

}
