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
package gov.nasa.arc.mct.scenario.util;

import java.text.ParseException;

public class DurationFormatter {
	private static final long MS_IN_SEC = 1000L;
	private static final long MS_IN_MIN = 	60L * MS_IN_SEC;
	private static final long MS_IN_HOUR = 	60L * MS_IN_MIN;
	private static final long MS_IN_DAY = 24L * MS_IN_HOUR;
	
	
	public static long parse(String duration) throws ParseException {
		long ms = 0;
		try {
			String[] dayTime = duration.split(" ");
			if (dayTime.length > 1) {
				return Long.parseLong(dayTime[0]) * MS_IN_DAY + parse(dayTime[1]);
			} else {
				String[] hms = dayTime[0].split(":");
				for (int i = 0; i < 3; i++) {
					ms *= 60L;
					if (hms.length > i) {
						ms += Long.parseLong(hms[i]) * 1000L;
					}
				}
			}
		} catch (Exception e) {
			throw new ParseException("Could not parse " + duration, 0);
		}
		return ms;		
	}
	
	public static String formatDuration(long duration) {
		StringBuilder builder = new StringBuilder();
		if (duration > MS_IN_DAY) {
			builder.append(duration / MS_IN_DAY);
			builder.append(' ');
		}
		
		duration %= MS_IN_DAY;
		appendTwoDigits(builder, duration / MS_IN_HOUR);
		builder.append(':');
		
		duration %= MS_IN_HOUR;
		appendTwoDigits(builder, duration / MS_IN_MIN);
		
		
		duration %= MS_IN_MIN;
		if (duration > 0) {
			builder.append(':');
			appendTwoDigits(builder, duration / MS_IN_SEC);
		}
		
		return builder.toString();
	}
	
	private static void appendTwoDigits(StringBuilder b, long d) {
		if (d < 10) {
			b.append('0');
		}
		b.append(d);
	}
	
	public static void main(String[] arghs) { 
		System.out.println(formatDuration(310 * MS_IN_DAY + 30 * 60 * 60 * 1000L));
	}
}
