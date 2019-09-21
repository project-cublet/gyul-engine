package io.gyul.flow.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;

import io.gyul.flow.engine.Message;
import io.gyul.util.JsonUtils;

public class HandlebarsUtilsTest {

	@Test
	public void testCompileError() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> HandlebarsUtils.compile(null));
		assertThrows(HandlebarsException.class, () -> HandlebarsUtils.compile("{{"));
	}

	@Test
	public void testCompileIgnoreEmpty() throws Exception {
		Template t = HandlebarsUtils.compile(null, true);
		assertThat(HandlebarsUtils.apply(t, Message.of(JsonUtils.objectNode())), is(""));
	}

	@Test
	public void testApplyString() throws Exception {
		ObjectNode payload = JsonUtils.objectNode();
		payload.put("name", "TestName");
		Message m = Message.of(JsonUtils.objectNode(), payload);
		assertThat(HandlebarsUtils.apply("name is {{message.payload.name}}", m), is("name is TestName"));
	}

	@Test
	public void testStringHelpers() throws Exception {
		System.out.println(HandlebarsUtils.compile("{{this}}: {{now}}").apply("now"));
	}
}
