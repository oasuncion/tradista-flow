package finance.tradista.flow.test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Condition;
import jakarta.persistence.Entity;

/********************************************************************************
 * Copyright (c) 2023 Olivier Asuncion
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

/**
 * Condition Test Class. This test condition returns the second letter of the
 * object status as an integer. Example: if the object status is "s1", the
 * condition returns 1. If the second letter of the object status cannot be
 * parsed as a number, a TradistaFlowBusinessException is thrown.
 * 
 * @author OA
 *
 */
@Entity
public class TestCondition extends Condition<WorkflowTestObject> {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestCondition() {
		setFunction(obj -> {
			int ret = 0;
			try {
				ret = Integer.parseInt(obj.getStatus().getName().substring(1, 2));
			} catch (NumberFormatException nfe) {
				throw new TradistaFlowBusinessException(
						String.format("Condition %s - Could not parse the second letter of the object status : '%s'",
								getName(), obj.getStatus()));
			}

			return ret;
		});
	}

}