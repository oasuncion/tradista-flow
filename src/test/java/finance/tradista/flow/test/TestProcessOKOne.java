package finance.tradista.flow.test;

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
 * Process Test Class. This test process sets the WorkflowObject's workflow to
 * "Wkf". If the WorkflowObject's workflow was already set to "Wkf", it sets it
 * to ""AnotherWkf".
 * 
 * @author OA
 *
 */
@Entity
public class TestProcessOKOne extends finance.tradista.flow.model.Process<WorkflowTestObject> {

	private static final long serialVersionUID = -5097243928471620584L;

	public TestProcessOKOne() {
		setTask(obj -> {
			final String WKF = "Wkf";
			if (!obj.getWorkflow().equals(WKF)) {
				obj.setWorkflow(WKF);
			} else {
				obj.setWorkflow("AnotherWkf");
			}
		});
	}

}