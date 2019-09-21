package io.gyul.flow.node.output;

import org.junit.jupiter.api.Test;

import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.NodeContextTestUtils;
import io.gyul.flow.node.output.DebugNodeConfig.LoggingLevel;

public class DebugNodeTest {
	
	@Test
	public void testDebugNode() throws Exception {
		DebugNode node = new DebugNode();
		node.configure(NodeContextTestUtils.buildTestContext("f1", "n1"), DebugNodeConfig.builder().loggingLevel(LoggingLevel.WARN).build());
		node.init();
		node.process(Message.empty());
	}

}
