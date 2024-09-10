package finance.tradista.flow.test;

import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.WorkflowObject;

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
 * Test Class only.
 * 
 * @author OA
 *
 */
public class WorkflowTestObject implements WorkflowObject {

	private Status<? extends WorkflowObject> status;

	private String workflow;

	@Override
	public void setStatus(Status<? extends WorkflowObject> status) {
		this.status = status;
	}

	@Override
	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String name) {
		this.workflow = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Status<? extends WorkflowObject> getStatus() {
		return status;
	}

	@Override
	public WorkflowTestObject clone() throws CloneNotSupportedException {
		return (WorkflowTestObject) super.clone();
	}

}