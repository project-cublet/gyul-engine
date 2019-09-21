package io.gyul.flow.dsl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import io.gyul.flow.node.trigger.TimerNode;
import io.gyul.util.JsonUtils;

public class FlowDefinitionTest {

	@Test
	public void testBuilder() throws Exception {
		FlowDefinition f = FlowDefinition.builder()
			.id("A")
			.name("testFlow")
			.node(NodeDefinition.builder().id("N1").name("testNode1").nodeClazz(TimerNode.class).build())
			.node(NodeDefinition.builder().id("N2").name("testNode2").nodeClazz(TimerNode.class).build())
			.node(NodeDefinition.builder().id("N3").name("testNode3").nodeClazz(TimerNode.class).build())
			.wire(Wire.of("N1", "N2"))
			.wire(Wire.of("N2", "N3"))
			.param("testKey", "testValue")
			.build();
		assertThat(f.getId(), is("A"));
		assertThat(f.getNodes().size(), is(3));
		assertThat(f.getNodes().get(0).getId(), is("N1"));
		assertThat(f.getNodes().get(2).getId(), is("N3"));
		assertThat(f.getWires().size(), is(2));
		assertThat(f.getWires().get(0).getFromNode(), is("N1"));
		assertThat(f.getWires().get(0).getToNode(), is("N2"));
		assertThat(f.getParams().get("testKey").asText(), is("testValue"));
	}
	
	@Test
	public void testDeserialize() throws Exception {
		FlowDefinition src = FlowDefinition.builder()
			.id("A")
			.name("testFlow")
			.node(NodeDefinition.builder().id("N1").name("testNode").nodeClazz(TimerNode.class).build())
			.wire(Wire.of("A", "port1", "B"))
			.build();
		FlowDefinition f = JsonUtils.treeToValue(JsonUtils.toJsonNode(src), FlowDefinition.class);
		assertThat(f.getId(), is("A"));
		assertThat(f.getNodes().get(0).getId(), is("N1"));
		assertThat(f.getWires().get(0).getToNode(), is("B"));
	}
}
