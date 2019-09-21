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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import akka.stream.ClosedShape;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import io.gyul.flow.FlowException;
import io.gyul.flow.dsl.FlowDefinition;
import io.gyul.flow.dsl.NodeDefinition;
import io.gyul.flow.dsl.Wire;
import io.gyul.flow.util.FlowCycleDetector;
import io.gyul.util.GenericUtils;
import io.gyul.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * FlowProcessor
 *  
 * @author sungjae
 */
@Component
@Slf4j
public class FlowProcessor {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private Materializer materializer;
	
	private ConcurrentMap<String, FlowContext> contextMap = new ConcurrentHashMap<>();
	
	@PreDestroy
	public void close() {
		contextMap.values().forEach(context -> {
			context.getKillSwitch().shutdown();
			log.info("[{}] flow stopped", context.getName());
		});
	}
	
	public FlowContext getFlowContext(String flowId) {
		return contextMap.get(flowId);
	}
	
	public void startFlow(FlowDefinition def) {
		startFlow(def, def.getId());
	}
	
	public void startFlow(FlowDefinition def, String rootFlowId) {
		try {
			log.info("[{}] start flow", def.getName());
			FlowContext context = buildFlow(def, rootFlowId);
			contextMap.put(context.getId(), context);
			context.getRunnableGraph().run(materializer);
		} catch(FlowException ex) {
			log.warn("Flow start error! " + def, ex);
			throw ex;
		} catch(Exception ex) {
			log.warn("Flow start error! " + def, ex);
			throw new FlowException(ex.getMessage(), ex);
		}
	}
	
	public void validateFlow(FlowDefinition def) {
		try {
			FlowContext context = buildFlow(def, def.getId());
			context.stop();
		} catch(FlowException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new FlowException(ex.getMessage(), ex);
		}
	}
	
	public void stopFlow(String flowId) {
		FlowContext context = contextMap.remove(flowId);
		if(context != null) {
			log.info("[{}] stop flow", context.getName());
			context.stop();
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected FlowContext buildFlow(FlowDefinition def, String rootFlowId) throws Exception {
		FlowContext flowContext = new FlowContext(def.getId(), rootFlowId);
		flowContext.setName(def.getName());
		flowContext.setKillSwitch(KillSwitches.shared(def.getId()));
		flowContext.setEventPublisher(applicationContext);
		flowContext.setParams(def.getParams() == null ? JsonNodeFactory.instance.objectNode() : def.getParams().deepCopy());
		Map<String, NodeBuilderContext> nodeContexts = buildNodes(flowContext, def);
		flowContext.setNodes(nodeContexts.values().stream().map(NodeBuilderContext::getNode).collect(Collectors.toList()));
//		flowContext.initStatistics(nodeContexts.keySet());
		try {
			if (FlowCycleDetector.detectCycle(def)) {
				throw new FlowException("Flow contains cycle!");
			}
			RunnableGraph runnableGraph = RunnableGraph.fromGraph(GraphDSL.create(builder -> {
				// after init
				nodeContexts.values().forEach(context -> {
					context.preWiring(builder);
				});

				// wire
				for (Wire wire : def.getWires()) {
					NodeBuilderContext from = nodeContexts.get(wire.getFromNode());
					NodeBuilderContext to = nodeContexts.get(wire.getToNode());
					int outletIndex = from.getNode().getPortIndex(wire.getFromPort());
					to.wire(builder, from.getOutlet(outletIndex));
				}

				// before running
				nodeContexts.values().forEach(context -> context.postWiring(builder, flowContext));
				return ClosedShape.getInstance();
			}));
			flowContext.setRunnableGraph(runnableGraph);
		} catch (IllegalStateException ex) {
			throw new FlowException("Unconnected input ports exist or the node configuration is invalid.", ex);
		} catch (Exception ex) {
			flowContext.stop();
			throw ex;
		}
		return flowContext;
	}

	protected Map<String, NodeBuilderContext> buildNodes(FlowContext flowContext, FlowDefinition def) throws Exception {
		Map<String, NodeBuilderContext> contexts = new HashMap<>();
		try {
			for (NodeDefinition nodeDef : def.getNodes()) {
				NodeBuilderContext nodeContext = buildNode(flowContext, nodeDef);
				contexts.put(nodeDef.getId(), nodeContext);
				log.debug("Flow node initialized: {}", def);
			}
	
			for (Wire wire : def.getWires()) {
				NodeBuilderContext fromNodeContext = contexts.get(wire.getFromNode());
				if(fromNodeContext == null) {
					throw new FlowException("From node [" + wire.getFromNode() + "] not exist.");
				}
				NodeBuilderContext toNodeContext = contexts.get(wire.getToNode());
				if(toNodeContext == null) {
					throw new FlowException("To node [" + wire.getToNode() + "] not exist.");
				}
				if(fromNodeContext == toNodeContext) {
					throw new FlowException("[" + wire.getFromNode() + "] From and to nodes are same.");
				}
				fromNodeContext.putWiredOutPort(wire.getFromPort());
				toNodeContext.addWiredInPort();
			}
			return contexts;
		} catch(Exception ex) {
			//init된 node를 중지 시킨다.
			contexts.values().stream().forEach(c -> {
				try {
					c.getNode().stop();
				} catch(Exception ignore) {
					// ignore error
				}
			});
			throw ex;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected NodeBuilderContext buildNode(FlowContext flowContext, NodeDefinition def) {
		NodeContext context = new NodeContext(flowContext, def.getId(), def.getName());
		try {
			Node node = applicationContext.getBean(def.getNodeClazz());
			Class<?> configClazz = GenericUtils.getInterfacesGenericType(node.getClass(), Node.class);
			
			Object config = JsonUtils.treeToValue(def.getConfig(), configClazz);
			node.configure(context, config);
			node.init();
			return new NodeBuilderContext(context, node);
		} catch(Exception ex) {
			throw new FlowException("[" + context.displayName() + "] Flow node initializing fail. " + ex.getMessage(), ex);
		}
	}

}
