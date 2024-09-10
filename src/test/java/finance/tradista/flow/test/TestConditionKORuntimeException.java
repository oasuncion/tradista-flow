package finance.tradista.flow.test;

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
 * Condition Test Class. This test condition throws a RuntimeException
 * 
 * @author OA
 *
 */
@Entity
public class TestConditionKORuntimeException extends Condition<WorkflowTestObject> {

	private static final long serialVersionUID = 4059753355897169736L;

	public TestConditionKORuntimeException() {
		setFunction(obj -> {
			throw new RuntimeException("Condition KO");
		});
	}

}