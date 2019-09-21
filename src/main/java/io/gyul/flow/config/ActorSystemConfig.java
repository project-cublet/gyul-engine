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
package io.gyul.flow.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.Materializer;
import akka.stream.Supervision;

/**
 * ActorSystemConfig
 *  
 * @author sungjae
 */
@Configuration
public class ActorSystemConfig {
	public static final String ACTOR_SYSTEM_NAME = "actor-system";
	public static final String ACTOR_SYSTEM_CONF_FILE_NAME = "/io/gyul/flow/config/actor-system.conf";

	@Bean
	public ActorSystem actorSystem() throws IOException {
		Resource configResource = new ClassPathResource(ACTOR_SYSTEM_CONF_FILE_NAME);
		Config config = ConfigFactory.parseURL(configResource.getURL());
		return ActorSystem.create(ACTOR_SYSTEM_NAME, config);
	}

	@Bean
	public Materializer materializer(ActorSystem actorSystem) {
		// 기본 Supervision 전략을 Resume으로 한다
		final Function<Throwable, Supervision.Directive> decider = exc -> Supervision.resume();
		return ActorMaterializer.create(ActorMaterializerSettings.create(actorSystem).withSupervisionStrategy(decider),
				actorSystem);
	}
}
