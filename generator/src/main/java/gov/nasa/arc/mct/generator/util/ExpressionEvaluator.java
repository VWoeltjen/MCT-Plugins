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

/**
 * Utility class for evaluating user-provided mathematical 
 * expressions.
 * 
 * Defers most functionality to the JEval library. 
 * http://jeval.sourceforge.net/
 * 
 * @author vwoeltje
 */
public class ExpressionEvaluator {
	/**
	 * The JEval evaluator which will be used to evaluate this expression.
	 */
	private Evaluator evaluator = new Evaluator();
	
	/**
	 * The expression to be evaluated. 
	 */
	private final String expression;
	
	/**
	 * Create a new evaluator for the given expression. The variable 
	 * t in the expression shall have its value assigned to be the 
	 * current system time, in seconds since the UNIX epoch, at the 
	 * time subsequent evaluation is performed.
	 * 
	 * @param expression the expression to be evaluted
	 */
	public ExpressionEvaluator(String expression) {
		// Express the variable "t" as JEval expects it
		this.expression = expression.replaceAll("\\bt\\b", "#{t}");
		
		// Resolve variable t to the current system time
		evaluator.setVariableResolver(TIME_RESOLVER);
		
		// Throw an exception if the expression cannot be parsed.
		// This avoids trying to generate data for bad expressions.
		try {
			evaluator.parse(expression);
		} catch (EvaluationException e) {
			throw new IllegalArgumentException(e);
		}			
	}
	
	/**
	 * Evaluate the expression provided during instantiation, 
	 * at the current time.
	 * @return the value of the expression, at the current time
	 */
	public double evaluate() {
		try {
			// Defer to JEval
			return evaluator.getNumberResult(expression);
		} catch (EvaluationException e) {
			// Not expected, as expression parsed successfully 
			// during the constructor call.
			return Double.NaN;
		}
	}
	
	/**
	 * Provides current system time for variable "t", expressed 
	 * as seconds since the UNIX epoch.
	 */
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
