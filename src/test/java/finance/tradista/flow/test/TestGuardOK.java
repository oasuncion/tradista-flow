package finance.tradista.flow.test;

import finance.tradista.flow.model.Guard;
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
 * Guard Test Class. This test guard return true, so never blocks.
 * 
 * @author OA
 *
 */
@Entity
public class TestGuardOK extends Guard<WorkflowTestObject> {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestGuardOK() {
		setPredicate(obj -> {
			return true;
		});
	}

}