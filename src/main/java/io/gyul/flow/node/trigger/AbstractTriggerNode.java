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

import akka.NotUsed;
import akka.stream.Graph;
import akka.stream.SourceShape;
import io.gyul.flow.engine.GraphNode;
import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.NodeContext;
import io.gyul.flow.node.AbstractNode;

/**
 * AbstractTriggerNode
 *  
 * @author sungjae
 */
public abstract class AbstractTriggerNode<T> extends AbstractNode<T> implements GraphNode<T> {
	
	protected TriggerGraphStage triggerGraphStage;
	
	@Override
	public void configure(NodeContext context, T config) {
		super.configure(context, config);
		triggerGraphStage = new TriggerGraphStage(context);
	}
	
	public Graph<SourceShape<Message>, NotUsed> buildGraph() {
		return triggerGraphStage;
	}
}
