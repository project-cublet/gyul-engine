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
import java.util.stream.IntStream;

import akka.NotUsed;
import akka.stream.FlowShape;
import akka.stream.Graph;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.Shape;
import akka.stream.SourceShape;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL.Builder;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import io.gyul.flow.FlowException;
import lombok.Getter;

/**
 * NodeBuilderContext
 *  
 * @author sungjae
 */
public class NodeBuilderContext {

	private NodeContext context;
	@Getter
	private final Node<?> node;
	@Getter
	private final Graph<? extends Shape, NotUsed> graph;
	private int inletIndex;
	private boolean multiInPort;
	
	private Shape inputShape;
	private Shape outputShape;
	private Shape fanInShape;
	@Getter
	private int wiredInPorts;
	@Getter
	private final Map<Integer, Boolean> outPorts = new HashMap<>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NodeBuilderContext(NodeContext context, Node<?> node) {
		this.context = context;
		this.node = node;
		if(node instanceof GraphNode) {
			graph = ((GraphNode)node).buildGraph();
		} else if(node instanceof SimpleNode) {
			graph = Flow.of(Message.class).map(((SimpleNode)node)::process);
		} else if(node instanceof AsyncNode) {
			graph = Flow.of(Message.class).mapAsync(context.getParallelism(), ((AsyncNode)node)::processAsync);
		} else {
			throw new IllegalArgumentException("Invalid Node");	//TODO detail error message
		}
		IntStream.range(0, graph.shape().getOutlets().size()).forEach(i -> outPorts.put(i, false));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void preWiring(Builder builder) {
		Shape bodyShape = builder.add(graph);
		
		Graph<FlowShape<Message, Message>, NotUsed> checkInGraph = Flow.of(Message.class).map(m -> {
			m.addPath(context.getNodeId());
			return m;
		});
		Shape checkInShape = builder.add(checkInGraph);
		if(bodyShape instanceof SourceShape) {
			inputShape = bodyShape;
			outputShape = checkInShape;
		} else {
			inputShape = checkInShape;
			outputShape = bodyShape;
		}
		builder.from(inputShape.getOutlets().get(0)).toInlet(outputShape.getInlets().get(0));
		if (wiredInPorts > 1) {
			fanInShape = builder.add(Merge.create(wiredInPorts));
			builder.from(fanInShape.getOutlets().get(0)).toInlet(inputShape.getInlets().get(0));
			multiInPort = true;
		} 
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void wire(Builder builder, Outlet<?> fromOutlet) {
		Inlet<?> inlet = null;
		if (multiInPort) {
			inlet = fanInShape.getInlets().get(inletIndex);
			inletIndex += 1;
		} else {
			inlet = inputShape.getInlets().get(0);
		}
		builder.from(fromOutlet).toInlet(inlet);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void postWiring(Builder builder, FlowContext flowContext) {
		outPorts.entrySet().stream()
				.filter(e -> !e.getValue())
				.forEach(e -> {
					//wired 되지 않은 out port에 kill switch와 sink를 연결한다.
					Shape killSwitchShape = builder.add(flowContext.getKillSwitch().flow());
					builder.from(getOutlet(e.getKey())).toInlet(killSwitchShape.getInlets().get(0));
					Sink sink = Sink.foreach(m -> flowContext.completed((Message)m, false));
					builder.from(killSwitchShape.getOutlets().get(0)).toInlet(builder.add(sink).getInlets().get(0));
				});
	}

	protected void putWiredOutPort(String portName) {
		try {
			int port = node.getPortIndex(portName);
			Boolean p = outPorts.get(port);
			if(p == null) {
				throw new FlowException("[" + context.getNodeName() + "] Out port [" + (portName == null ? port : portName) + "] not exist.");
			} else if(p) {
				throw new FlowException("[" + context.getNodeName() + "] Out port [" + (portName == null ? port : portName) + "] already wired.");
			}
			outPorts.put(port, true);
		} catch(FlowException ex) {
			throw ex;
		} catch(RuntimeException ex) {
			throw new FlowException("[" + context.getNodeName() + "] Out port [" + portName +"] invalid!", ex);
		}
	}

	protected void addWiredInPort() {
		if(graph.shape() instanceof SourceShape) {
			throw new FlowException("[" + context.getNodeName() + "] Trigger node cannot wire in port.");
		}
		this.wiredInPorts += 1;
	}

	public Outlet<?> getOutlet(Integer index) {
		return outputShape.getOutlets().get(index);
	}
}
