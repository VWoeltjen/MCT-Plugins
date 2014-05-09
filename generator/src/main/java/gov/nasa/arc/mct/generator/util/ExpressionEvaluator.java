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
package gov.nasa.arc.mct.generator.util;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.VariableResolver;
import net.sourceforge.jeval.function.FunctionException;

public class ExpressionEvaluator {
	private Evaluator evaluator = new Evaluator(); 
	private final String expression;
	
	public ExpressionEvaluator(String expression) {
		this.expression = expression;
		
		evaluator.setVariableResolver(TIME_RESOLVER);
		
		try {
			evaluator.parse(expression);
		} catch (EvaluationException e) {
			throw new IllegalArgumentException(e);
		}			
	}
	
	public double evaluate(String variable, double value) {
		try {
			return evaluator.getNumberResult(expression);
		} catch (EvaluationException e) {
			return Double.NaN;
		}
	}
	
	private static final VariableResolver TIME_RESOLVER = new VariableResolver() {
		@Override
		public String resolveVariable(String a) throws FunctionException {
			if (a.equals("t")) {
				return Long.toString(System.currentTimeMillis() / 1000L);
			}
			return null;
		}
	};
}
