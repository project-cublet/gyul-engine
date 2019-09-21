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

import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.AsyncCallback;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import io.gyul.flow.engine.Message;
import io.gyul.flow.engine.NodeContext;
import lombok.extern.slf4j.Slf4j;

/**
 * TriggerGraphStage
 *  
 * @author sungjae
 */
@Slf4j
public class TriggerGraphStage extends GraphStage<SourceShape<Message>> {
		private static final int BUFFER_SIZE = 100;
		
		private final Outlet<Message> out;
		private final SourceShape<Message> shape;
		
		private NodeContext context;
		private AsyncCallback<Message> callback;
		
		public TriggerGraphStage(NodeContext context) {
			this.context = context;
			this.out =  Outlet.create(context.getNodeName() + ".out");
			this.shape = SourceShape.of(out);
		}
		
		public boolean push(Message message) {
			if(callback != null) {
				callback.invoke(message);
//				context.checkIn(message);
				return true;
			}
			return false;
		}
		
		@Override
		public SourceShape<Message> shape() {
			return shape;
		}
		
		@Override
		public GraphStageLogic createLogic(Attributes inheritedAttributes) throws Exception {
			return new GraphStageLogic(shape()) {
				private Queue<Message> buffer = new CircularFifoQueue<>(BUFFER_SIZE);
				private boolean downstreamWaiting = false;
				
				@Override
				public void preStart() {
					log.info("[{}] started.", context.displayName());
					callback = createAsyncCallback(m -> {
							boolean added = buffer.offer(m);
							if(!added) {
								log.warn("[{}] message buffer is full.", context.displayName());
//								context.markError(m, "Message buffer is full");
							}
							if (added && downstreamWaiting) {
								downstreamWaiting = false;
								Message bufferedMessage = buffer.poll();
								push(out, bufferedMessage);
//								context.checkOut();
							}
						}
					);
				}

				@Override
				public void postStop() {
					log.info("[{}] stopped.", context.displayName());
				}

				{
					setHandler(out, new AbstractOutHandler() {
						@Override
						public void onPull() throws Exception {
							if (buffer.isEmpty()) {
								downstreamWaiting = true;
							} else {
								Message message = buffer.poll();
								push(out, message);
//								context.checkOut();
							}
						}
					});
				}
			};
		}
}
