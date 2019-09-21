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
package io.gyul.flow.node.trigger;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.Node;
import io.gyul.flow.service.VirtualButtonListener;
import io.gyul.flow.service.VirtualButtonService;
import io.gyul.flow.spi.FlowNode;

/**
 * VirtualButtonTriggerNode
 *  
 * @author sungjae
 */
@FlowNode(inPortEnabled = false)
public class ButtonTriggerNode extends AbstractTriggerNode<ButtonTriggerNodeConfig> implements Node<ButtonTriggerNodeConfig>, VirtualButtonListener {

	@Autowired
	protected VirtualButtonService buttonService;
	
	@Override
	public void init() {
		buttonService.addVirtualButtonListener(context.getFlowId(), context.getNodeId(), this);
	}
	
	@Override
	public void stop() {
		buttonService.removeVirtualButtonListener(context.getFlowId(), context.getNodeId());
	}
	
	@Override
	public void onTrigger() {
		onTrigger(config.getPayload());
	}
	
	@Override
	public void onTrigger(ObjectNode payload) {
		triggerGraphStage.push(Message.of(context.getParams(), payload));
	}

}
