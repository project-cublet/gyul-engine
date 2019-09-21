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
package io.gyul.flow.engine;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.stream.SharedKillSwitch;
import akka.stream.javadsl.RunnableGraph;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * FlowContext
 *  
 * @author sungjae
 */
@Data
@Slf4j
public class FlowContext {
	
	private final int parallelism = ForkJoinPool.getCommonPoolParallelism();
	
	private final String id;
	private final String rootFlowId;
	private String name;
	@SuppressWarnings("rawtypes")
	private RunnableGraph runnableGraph;
	private SharedKillSwitch killSwitch;
	private List<Node<?>> nodes;
	private ObjectNode params;
	private ApplicationEventPublisher eventPublisher;
	
	private List<FlowCompletionListener> listeners = new CopyOnWriteArrayList<>();
	
	public void stop() {
		killSwitch.shutdown();
		nodes.forEach(node -> {
			try {
				node.stop();
			} catch(Exception ex) {
				log.warn("[" + name + "]'s node stop error! ", ex);
			}
		});
	}
	
	public void addListener(FlowCompletionListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(FlowCompletionListener listener) {
		listeners.remove(listener);
	}
	
	public void completed(Message message, boolean isBreak) {
		message.completed();
		if(isBreak || message.checkAllCompleted()) {
			listeners.forEach(l -> {
				try {
					l.onComplete(message, isBreak);
				} catch(Exception ex) {
					log.warn("Message onComplete callback error. {}", message, ex);
				}
			});
		}
	}
	
	public void completedWithError(Message message, String error) {
		message.completed();
		if(message.checkAllCompleted()) {
			listeners.forEach(l -> {
				try {
					l.onError(message, error);
				} catch(Exception ex) {
					log.warn("Message onError callback error. {}", message, ex);
				}
			});
		}
	}
	
	public void publishEvent(Object event) {
		eventPublisher.publishEvent(event);
	}
}
