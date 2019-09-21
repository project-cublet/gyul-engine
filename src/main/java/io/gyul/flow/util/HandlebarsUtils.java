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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;

import io.gyul.flow.engine.Message;
import io.gyul.util.JsonUtils;
import lombok.NonNull;

/**
 * HandlebarsUtils
 * 
 * @author sungjae
 */
public class HandlebarsUtils {

	private static final int TEMPLATE_CACHE_SIZE = 1000;
	
	private static Handlebars handlebars;
	private static Template emptyTemplate;
	private static Map<String, Template> templateCache;

	static {
		handlebars = new Handlebars();
		handlebars.with(EscapingStrategy.NOOP); // ignore HTML escape strategy
		handlebars.registerHelpers(StringHelpers.class);
		handlebars.registerHelpers(ConditionalHelpers.class);
		handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
		templateCache = Collections.synchronizedMap(new LRUMap<>(TEMPLATE_CACHE_SIZE));
		emptyTemplate = compile("");
	}
	
	public static Template compile(@NonNull String input) {
		try {
			return handlebars.compileInline(input.trim());
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static Template compile(String input, boolean ignoreEmpty) {
		if(ignoreEmpty && StringUtils.isEmpty(input)) {
			return emptyTemplate;
		}
		return compile(input);
	}

	public static String apply(Template template, Message message) {
		try {
			return template.apply(buildContext(message));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static String apply(String input, Message message) {
		try {
			return createOrGetTemplate(input).apply(buildContext(message));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	protected static Template createOrGetTemplate(String input) {
		String s = input == null ? "" : input.trim();
		return templateCache.computeIfAbsent(s, HandlebarsUtils::compile);
	}

	public static Context buildContext(Message message) {
		ObjectNode messageNode = JsonUtils.objectNode();
		messageNode.set(Message.MESSAGE_ROOT, JsonUtils.toJsonNode(message));
		return Context.newBuilder(messageNode)
				.resolver(JsonNodeValueResolver.INSTANCE)
				.build();
	}
}
