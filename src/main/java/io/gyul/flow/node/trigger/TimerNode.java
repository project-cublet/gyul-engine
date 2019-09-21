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
package io.gyul.flow.node.trigger;

import static io.gyul.flow.node.trigger.TimerNodeConfig.ScheduleType.SIMPLE_INTERVAL;

import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.Node;
import io.gyul.flow.spi.FlowNode;

/**
 * TimerNode
 *  
 * @author sungjae
 */
@FlowNode(inPortEnabled = false)
public class TimerNode extends AbstractTriggerNode<TimerNodeConfig> implements Node<TimerNodeConfig> {

	@Autowired
	private ThreadPoolTaskScheduler flowScheduler;
	private ScheduledFuture<?> scheduledFuture;
	
	@Override
	public void init() {
		Trigger trigger = null;
		if (config.getScheduleType() == SIMPLE_INTERVAL) {
			trigger = new PeriodicTrigger(config.getInterval(), config.getTimeUnit());
		} else {
			trigger = new CronTrigger(config.getCronExpression());
		}
		scheduledFuture = flowScheduler.schedule(this::trigger, trigger);
	}
	
	@Override
	public void stop() {
		scheduledFuture.cancel(true);
	}
	
	public void trigger() {
		triggerGraphStage.push(Message.of(context.getParams()));
	}

}
