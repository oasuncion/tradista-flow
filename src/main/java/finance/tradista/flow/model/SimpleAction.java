package finance.tradista.flow.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

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
 * Class representing a simple workflow action.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class SimpleAction<X extends WorkflowObject> extends Action<X> {

	private static final long serialVersionUID = 4747165809942693001L;

	@SuppressWarnings("rawtypes")
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "arrival_status_id")
	private Status arrivalStatus;

	@SuppressWarnings("rawtypes")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@OrderBy
	private Set<Process> processes;

	private void init(Workflow<X> workflow, Status<X> arrivalStatus) {
		StringBuilder errMsg = new StringBuilder();
		if (arrivalStatus != null) {
			if (arrivalStatus.getWorkflow() == null || !arrivalStatus.getWorkflow().equals(workflow)) {
				errMsg.append(String.format("The arrival status should have the same workflow %s", workflow));
			}
			this.arrivalStatus = arrivalStatus;
			if (!isConnectedToPseudoStatus()) {
				workflow.addAction(this);
				setWorkflow(workflow);
			}
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Status<X> arrivalStatus,
			Guard<X>... guards) {
		super(workflow, name, departureStatus, guards);
		init(workflow, arrivalStatus);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Status<X> arrivalStatus) {
		super(workflow, name, departureStatus, (Guard<X>[]) null);
		init(workflow, arrivalStatus);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Status<X> arrivalStatus,
			Guard<X>[] guards, Process<X>... processes) {
		super(workflow, name, departureStatus, guards);
		init(workflow, arrivalStatus);
		this.processes = new LinkedHashSet<>();
		if (processes != null) {
			Arrays.stream(processes).forEach(p -> this.processes.add(p));
		}
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Status<X> arrivalStatus,
			Process<X>... processes) {
		super(workflow, name, departureStatus, (Guard<X>[]) null);
		init(workflow, arrivalStatus);
		this.processes = new LinkedHashSet<>();
		if (processes != null) {
			Arrays.stream(processes).forEach(p -> this.processes.add(p));
		}
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Process<X>... processes) {
		this(workflow, name, departureStatus, (Status<X>) null, processes);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Guard<X>... guards) {
		this(workflow, name, departureStatus, null, guards);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus) {
		this(workflow, name, departureStatus, (Status<X>) null);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Guard<X>[] guards,
			Process<X>... processes) {
		this(workflow, name, departureStatus, (Status<X>) null, guards, processes);
	}

	public SimpleAction() {
	}

	@SuppressWarnings("unchecked")
	public Status<X> getArrivalStatus() {
		return TradistaFlowUtil.clone(arrivalStatus);
	}

	public void setArrivalStatus(Status<X> arrivalStatus) {
		this.arrivalStatus = arrivalStatus;
	}

	@SuppressWarnings("rawtypes")
	public Set<Process> getProcesses() {
		return processes;
	}

	@SuppressWarnings("rawtypes")
	public void setProcesses(Set<Process> processes) {
		this.processes = processes;
	}

	@Override
	public SimpleAction<X> clone() {
		SimpleAction<X> action = (SimpleAction<X>) super.clone();
		action.arrivalStatus = TradistaFlowUtil.clone(arrivalStatus);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrivalStatus, getDepartureStatus(), getName(), getWorkflow());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleAction<X> other = (SimpleAction<X>) obj;
		return Objects.equals(arrivalStatus, other.arrivalStatus)
				&& Objects.equals(getDepartureStatus(), other.getDepartureStatus())
				&& Objects.equals(getName(), other.getName()) && Objects.equals(getWorkflow(), other.getWorkflow());
	}

	@Override
	public boolean isConnectedToPseudoStatus() {
		return (this.getDepartureStatus() instanceof PseudoStatus || this.getArrivalStatus() instanceof PseudoStatus);
	}

	@Override
	public boolean isDepartureStatus(Status<X> status) {
		return getDepartureStatus().equals(status);
	}

}