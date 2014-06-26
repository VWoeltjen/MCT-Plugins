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

import gov.nasa.arc.mct.components.FeedInfoProvider;
import gov.nasa.arc.mct.components.FeedProvider;

import java.util.HashMap;
import java.util.Map;

public class GeneratorFeedInfoProvider implements FeedInfoProvider {
	private Map<String, FeedInfo> feedInfos = new HashMap<String, FeedInfo>();
	
	public GeneratorFeedInfoProvider(String expression) {
		super();
		feedInfos.put(expression, GeneratorFeedInfo.NORMAL);
		feedInfos.put("-("+expression+")", GeneratorFeedInfo.INVERTED);
	}

	@Override
	public FeedInfo getFeedInfo(FeedProvider fp) {		
		return feedInfos.get(fp.getCanonicalName());
	}

	private static enum GeneratorFeedInfo implements FeedInfo {
		NORMAL,
		INVERTED
		;		
		
		@Override
		public String getTypeId() {
			return name();
		}

		@Override
		public String getTypeName() {
			return name();
		}
	}
}
