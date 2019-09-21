package io.gyul.flow.node.output;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.gyul.flow.node.output.DebugNodeConfig.LoggingLevel;
import io.gyul.util.JsonUtils;
import lombok.val;

public class DebugNodeConfigTest {

	@Test
	public void testBuilder() throws Exception {
		val config = DebugNodeConfig.builder().loggingLevel(LoggingLevel.DEBUG).build();
		assertThat(config.getLoggingLevel(), is(LoggingLevel.DEBUG));
		assertThrows(IllegalArgumentException.class, () -> DebugNodeConfig.builder().loggingLevel(null).build());
	}
	
	@Test
	public void testDeserialize() throws Exception {
		val config = JsonUtils.readValue("{\"loggingLevel\":\"INFO\"}", DebugNodeConfig.class);
		assertThat(config.getLoggingLevel(), is(LoggingLevel.INFO));
		assertThrows(IllegalArgumentException.class, () -> JsonUtils.readValue("{\"loggingLevel\":null}", DebugNodeConfig.class));
//		assertThrows(IllegalArgumentException.class, () -> JsonUtils.readValue("{}", DebugNodeConfig.class));
	}
	
}
