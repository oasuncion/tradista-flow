package finance.tradista.flow.model;

import java.util.UUID;

import jakarta.persistence.Entity;

/********************************************************************************
 * Copyright (c) 2024 Olivier Asuncion
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

@Entity
public class PseudoStatus<X extends WorkflowObject> extends Status<X> {

	private static final long serialVersionUID = -8956303865604695993L;

	public PseudoStatus() {
	}

	public PseudoStatus(Workflow<X> workflow) {
		super(workflow, UUID.randomUUID().toString());
	}

}