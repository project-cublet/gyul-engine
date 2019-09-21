package io.gyul.flow.node.trigger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import io.gyul.flow.engine.NodeContextTestUtils;
import io.gyul.flow.node.trigger.TimerNodeConfig.ScheduleType;

@ExtendWith(MockitoExtension.class)
public class TimerNodeTest {

	@Spy
	ThreadPoolTaskScheduler flowScheduler = new ThreadPoolTaskScheduler();
	
	@InjectMocks
	TimerNode node;
	
	@BeforeEach
	public void setUp() {
		flowScheduler.afterPropertiesSet();
	}
	
	@Test
	public void testSimpleInterval() throws Exception {
		node.configure(NodeContextTestUtils.buildTestContext("f1", "n1"), 
				TimerNodeConfig.builder()
					.scheduleType(ScheduleType.SIMPLE_INTERVAL)
					.interval(10)
					.timeUnit(TimeUnit.SECONDS)
					.build()
				);
		node.init();
		node.stop();
		verify(flowScheduler).schedule(any(), any(PeriodicTrigger.class));
	}

	@Test
	public void testCron() throws Exception {
		node.configure(NodeContextTestUtils.buildTestContext("f1", "n1"), 
				TimerNodeConfig.builder()
					.scheduleType(ScheduleType.CRON)
					.cronExpression("0 0 * * * *")
					.build()
				);
		node.init();
		node.stop();
		verify(flowScheduler).schedule(any(), any(CronTrigger.class));
	}
}
