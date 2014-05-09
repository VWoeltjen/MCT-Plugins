/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.generator;

import gov.nasa.arc.mct.api.feed.BufferFullException;
import gov.nasa.arc.mct.api.feed.FeedDataArchive;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.event.services.EventProvider;
import gov.nasa.arc.mct.generator.util.ExpressionEvaluator;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GeneratorEventProvider implements EventProvider {
	public static final String GENERATOR_FEED_PREFIX = "generator:";
	private static final String COMPLETE_PREFIX = EventProvider.TELEMETRY_TOPIC_PREFIX + GENERATOR_FEED_PREFIX;
	
	private AtomicReference<FeedDataArchive> archive = new AtomicReference<FeedDataArchive>();
	private Timer timer;
	
	private final EvaluatorTask task = new EvaluatorTask();
	private static long latestTimestamp;
	
	
	@Override
	public Collection<String> subscribeTopics(String... topic) {
		Collection<String> accepted = filter(topic);		
		task.add(accepted);		
		return accepted;
	}

	@Override
	public void unsubscribeTopics(String... topic) {
		task.remove(filter(topic));
	}

	@Override
	public void refresh() {
		
	}
	
	public void bind(FeedDataArchive archive) {
		this.archive.set(archive);
		timer = new Timer("Data generator");
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public void unbind(FeedDataArchive archive) {
		this.archive.set(null);
		clearTimer();
	}
	
	private void clearTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		} 
	}
	
	private Collection<String> filter(String[] subscriptions) {
		List<String> filtered = new ArrayList<String>();
		for (String subscription : subscriptions) {
			if (subscription.startsWith(COMPLETE_PREFIX)) {
				filtered.add(subscription);
			}
		}
		return filtered;
	}
	
	public static final TimeService TIME_SERVICE = new TimeService() {
		@Override
		public long getCurrentTime() {
			return latestTimestamp;
		}		
	};

	private class EvaluatorTask extends TimerTask {
		private Queue<String> toAdd = new ConcurrentLinkedQueue<String>();
		private Queue<String> toRemove = new ConcurrentLinkedQueue<String>();

		private Map<String, ExpressionEvaluator> evaluators = 
				new HashMap<String, ExpressionEvaluator>();
		
		@Override
		public void run() {
			// Update list of expressions
			while (!toAdd.isEmpty()) {
				performAdd(toAdd.poll());
			}
			while (!toRemove.isEmpty()) {
				performRemove(toRemove.poll());
			}
			
			// Stop working if there are no more subscribed feeds
			if (evaluators.isEmpty()) {
				return;
			}
			
			// Generate data points
			FeedDataArchive archive = GeneratorEventProvider.this.archive.get();
			if (archive != null) {
				long timestamp = System.currentTimeMillis();
				for (Entry<String, ExpressionEvaluator> entry : evaluators.entrySet()) {
					double value = entry.getValue().evaluate();
					if (!Double.isNaN(value)) {
						try {
							archive.putData(entry.getKey().substring(
									EventProvider.TELEMETRY_TOPIC_PREFIX.length()), 
									TimeUnit.MILLISECONDS, 
									timestamp, 
									makeDatum(timestamp, value));
						} catch (BufferFullException e) {
							// TODO Log?
						}
					}
				}
				latestTimestamp = timestamp;
			}
		}
		
		private Map<String, String> makeDatum(long time, double value) {
			Map<String, String> datum = new HashMap<String, String>();
			datum.put(FeedProvider.NORMALIZED_IS_VALID_KEY, Boolean.toString(true));
			String status = " ";
			Color c = Color.GREEN;
			RenderingInfo ri = new RenderingInfo(Double.toString(value), c, status, c, true);
			ri.setPlottable(true);
			datum.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
			
			datum.put(FeedProvider.NORMALIZED_TIME_KEY, Long.toString((long) time));
			datum.put(FeedProvider.NORMALIZED_VALUE_KEY, Double.toString(value));
			datum.put(FeedProvider.NORMALIZED_TELEMETRY_STATUS_CLASS_KEY, "1");

			return datum;
		}
		
		private void performAdd(String feedId) {
			if (feedId.startsWith(COMPLETE_PREFIX)) {
				String expr = feedId.substring(COMPLETE_PREFIX.length());
				try {
					evaluators.put(feedId, new ExpressionEvaluator(expr));	
				} catch (IllegalArgumentException iae) {
					// TODO log
				}				
			}
		}
		
		private void performRemove(String feedId) {
			evaluators.remove(feedId);
		}
		
		public void add(Collection<String> feedIds) {
			toAdd.addAll(feedIds);
		}
		
		public void remove(Collection<String> feedIds) {
			toRemove.addAll(feedIds);
		}
		
	}
	
}
