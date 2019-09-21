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
package io.gyul.flow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gyul.flow.dsl.FlowDefinition;
import lombok.Data;

/**
 * FlowCycleDetector
 *  
 * @author sungjae
 */
public class FlowCycleDetector {

	public static boolean detectCycle(FlowDefinition flow) {
		Graph graph = new Graph();
		flow.getNodes().forEach(n -> graph.addNode(n.getId()));
		flow.getWires().forEach(l -> graph.addWire(l.getFromNode(), l.getToNode()));
		return graph.isCyclic();
	}

	private static class Graph {
		private Map<String, GraphNode> nodes = new HashMap<>();

		private void addNode(String name) {
			nodes.put(name, new GraphNode(name));
		}
		
		private void addWire(String from, String to) {
			nodes.get(from).addLink(to);
		}
		
		private boolean isCyclic(GraphNode node) {
			// Mark the current node as visited and part of recursion stack
			if (node.isRecStack()) {
				return true;
			}

			if (node.isVisited()) {
				return false;
			}

			node.setVisited(true);
			node.setRecStack(true);

			for(String s : node.getAdjacencies()) {
				if(isCyclic(nodes.get(s))) {
					return true;
				}
			}
			node.setRecStack(false);

			return false;
		}

		private boolean isCyclic() {
			// Call the recursive helper function to detect cycle in different DFS trees
			for(GraphNode node : nodes.values()) {
				if(isCyclic(node)) {
					return true;
				}
			}
			return false;
		}
	}
	
	@Data
	private static class GraphNode {
		private String name;
		private boolean visited;
		private boolean recStack;
		private List<String> adjacencies = new ArrayList<>();
		
		private GraphNode(String name) {
			this.name = name;
		}
		private void addLink(String edge) {
			adjacencies.add(edge);
		}
	}

}
