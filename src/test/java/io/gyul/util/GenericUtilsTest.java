package io.gyul.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import akka.NotUsed;
import akka.stream.Graph;
import akka.stream.Shape;
import io.gyul.flow.engine.GraphNode;
import io.gyul.flow.engine.Node;
import io.gyul.flow.engine.NodeContext;
import io.gyul.flow.node.EmptyNodeConfig;
import io.gyul.flow.node.trigger.TimerNode;
import io.gyul.flow.node.trigger.TimerNodeConfig;

public class GenericUtilsTest {
	@Test
	public void testgetInterfacesGenericType() throws Exception {
		assertEquals(EmptyNodeConfig.class, GenericUtils.getInterfacesGenericType(DummyNode.class, Node.class));
		assertEquals(EmptyNodeConfig.class, GenericUtils.getInterfacesGenericType(DummyNode2.class, Node.class));
		assertEquals(TimerNodeConfig.class, GenericUtils.getInterfacesGenericType(TimerNode.class, Node.class));
		assertNull(GenericUtils.getInterfacesGenericType(GenericUtilsTest.class, Node.class));
	}

	class DummyNode implements Serializable, GraphNode<EmptyNodeConfig> {

		@Override
		public void configure(NodeContext context, EmptyNodeConfig config) {
		}

		@Override
		public void init() {
		}

		@Override
		public Graph<? extends Shape, NotUsed> buildGraph() {
			return null;
		}
	}

	class DummyNode2 extends DummyNode {
		
	}
}
