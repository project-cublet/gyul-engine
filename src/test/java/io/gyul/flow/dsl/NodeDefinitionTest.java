package io.gyul.flow.dsl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.gyul.flow.node.trigger.TimerNode;
import io.gyul.flow.node.trigger.TimerNodeConfig;
import io.gyul.util.JsonUtils;

public class NodeDefinitionTest {

	@Test
	public void testBuilder() {
		NodeDefinition def = NodeDefinition.builder()
				.id("1")
				.name("test")
				.nodeClazz(TimerNode.class)
				.configSrc(TimerNodeConfig.builder().interval(10).build())
				.build();
		JsonNode node = JsonUtils.toJsonNode(def);
		NodeDefinition result = JsonUtils.treeToValue(node, NodeDefinition.class);
		assertThat(result.getId(), is("1"));
		assertThat(result.getName(), is("test"));
		assertThat(result.getConfig().get("interval").asInt(), is(10));
	}

}
