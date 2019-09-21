package io.gyul.flow.engine;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import akka.stream.Materializer;
import io.gyul.SpringTestConfiguration;
import io.gyul.flow.FlowException;
import io.gyul.flow.dsl.FlowDefinition;
import io.gyul.flow.dsl.NodeDefinition;
import io.gyul.flow.dsl.Wire;
import io.gyul.flow.node.output.DebugNode;
import io.gyul.flow.node.output.DebugNodeConfig;
import io.gyul.flow.node.output.DebugNodeConfig.LoggingLevel;
import io.gyul.flow.node.trigger.ButtonTriggerNode;
import io.gyul.flow.node.trigger.ButtonTriggerNodeConfig;
import io.gyul.flow.node.trigger.TimerNodeConfig;
import io.gyul.flow.service.VirtualButtonService;
import io.gyul.util.JsonUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringTestConfiguration.class})
public class FlowProcessorTest {
	
	@Autowired
	FlowProcessor processor;
	@Autowired
	VirtualButtonService buttonService;
	@Autowired
	Materializer materializer;
	
	@Test
	public void testStartFlow() throws Exception {
		FlowDefinition flow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder()
								.loggingLevel(LoggingLevel.INFO)
								.messageTemplate("payload: {{message.payload}}")
								.build())
						.build())
				.wire(Wire.of("b1", "d1"))
				.build();
		
		System.out.println(JsonUtils.toJsonNode(flow).toString());
		processor.startFlow(flow, "f1");
		
		buttonService.triggerVirtualButton("f1", "b1");
		processor.stopFlow("f1");
	}
	
	@Test
	public void testValidateFlowInvalidConfig() throws Exception {
		FlowDefinition invalidFlow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(TimerNodeConfig.builder().build())
						.build())
				.wire(Wire.of("b1", "d1"))
				.build();
		
		assertThrows(FlowException.class, () -> processor.validateFlow(invalidFlow));
	}
	
	@Test
	public void testValidateFlowCircular() throws Exception {
		FlowDefinition invalidFlow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d2")
						.name("debug2")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder().build())
						.build())
				.wire(Wire.of("b1", "d1"))
				.wire(Wire.of("d1", "d2"))
				.wire(Wire.of("d2", "d1"))
				.build();
		
		assertThrows(FlowException.class, () -> processor.validateFlow(invalidFlow));
	}
	
	@Test
	public void testValidateFlowIdUnwired() throws Exception {
		FlowDefinition invalidFlow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder().build())
						.build())
				.build();
		
		assertThrows(FlowException.class, () -> processor.validateFlow(invalidFlow));
	}
	
	@Test
	public void testValidateFlowWireInvalid() throws Exception {
		FlowDefinition invalidFlow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder().build())
						.build())
				.wire(Wire.of("b1", "d1"))
				.wire(Wire.of("d1", "d1"))
				.build();
		
		assertThrows(FlowException.class, () -> processor.validateFlow(invalidFlow));
	}
	
	@Test
	public void testValidateFlowWireInvalid2() throws Exception {
		FlowDefinition invalidFlow = FlowDefinition.builder()
				.id("f1")
				.name("testFlow")
				.node(NodeDefinition.builder()
						.id("b1")
						.name("button")
						.nodeClazz(ButtonTriggerNode.class)
						.configSrc(ButtonTriggerNodeConfig.builder().build())
						.build())
				.node(NodeDefinition.builder()
						.id("d1")
						.name("debug")
						.nodeClazz(DebugNode.class)
						.configSrc(DebugNodeConfig.builder().build())
						.build())
				.wire(Wire.of("b1", "d1"))
				.wire(Wire.of("d1", "d2"))
				.build();
		
		assertThrows(FlowException.class, () -> processor.validateFlow(invalidFlow));
	}
	
}
