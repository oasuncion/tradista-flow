package finance.tradista.flow.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

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
 * Class representing a conditional workflow action.
 * 
 * @author Olivier Asuncion
 * @param <X>
 *
 */

@Entity
public class ConditionalAction<X extends WorkflowObject> extends Action<X> {

	private static final long serialVersionUID = -4235540145388029599L;

	@SuppressWarnings("rawtypes")
	@OneToOne(cascade = CascadeType.ALL)
	private Condition condition;

	@SuppressWarnings("rawtypes")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Map<Integer, Status> conditionalRouting;

	@SuppressWarnings("rawtypes")
	@OneToOne(cascade = CascadeType.ALL)
	private Status choicePseudoStatus;

	@SuppressWarnings("rawtypes")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<SimpleAction> conditionalActions;

	private void init(Workflow<X> workflow, Condition<X> condition,
			@SuppressWarnings("rawtypes") Map<Integer, Status> conditionalRouting, Status<X>... arrivalStatus) {
		StringBuilder errMsg = new StringBuilder();
		for (Status<X> status : arrivalStatus) {
			if (status.getWorkflow() == null || !status.getWorkflow().equals(workflow)) {
				errMsg.append(
						String.format("The arrival status %s should have the same workflow %s", status, workflow));
			}
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
		setWorkflow(workflow);
		this.condition = condition;
		this.conditionalRouting = conditionalRouting;
		choicePseudoStatus = new PseudoStatus<X>(workflow);
		this.conditionalActions = new HashSet<>(arrivalStatus.length);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ConditionalAction(Workflow<X> workflow, Status<X> departureStatus, String name, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Guard<X>[] guards, Status<X>... arrivalStatus) {
		super(workflow, name, departureStatus, guards);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction<>(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConditionalAction(Workflow<X> workflow, Status<X> departureStatus, String name, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Status<X>... arrivalStatus) {
		super(workflow, name, departureStatus, (Guard<X>[]) null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction<>(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConditionalAction(Workflow<X> workflow, Set<SimpleAction<X>> departureActions, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Status<X>... arrivalStatus) {
		super(workflow, null, null, (Guard<X>[]) null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		for (SimpleAction<X> sa : departureActions) {
			sa.setArrivalStatus(choicePseudoStatus);
			conditionalActions.add(sa);
		}
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConditionalAction(Workflow<X> workflow, Status<X> departureStatus, String name, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Map<Status, Set<Process>> conditionalProcesses,
			Status<X>... arrivalStatus) {
		super(workflow, name, departureStatus, (Guard<X>[]) null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction<>(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			Set<Process> processesSet = conditionalProcesses.get(arrivalStatus[num]);
			Process[] processes = null;
			if (processesSet != null) {
				processes = processesSet.toArray(new Process[0]);
			}
			conditionalActions.add(new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], processes));
		}
		workflow.addAction(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConditionalAction(Workflow<X> workflow, Set<SimpleAction<X>> departureActions, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Map<Status, Set<Process>> conditionalProcesses,
			Status<X>... arrivalStatus) {
		super(workflow, null, null, (Guard<X>[]) null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		for (SimpleAction<X> sa : departureActions) {
			sa.setArrivalStatus(choicePseudoStatus);
			conditionalActions.add(sa);
		}
		for (int num = 0; num < arrivalStatus.length; num++) {
			Set<Process> processesSet = conditionalProcesses.get(arrivalStatus[num]);
			Process[] processes = null;
			if (processesSet != null) {
				processes = processesSet.toArray(new Process[0]);
			}
			conditionalActions.add(new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], processes));
		}
		workflow.addAction(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ConditionalAction(Workflow<X> workflow, Status<X> departureStatus, String name, Condition<X> condition,
			Map<Integer, Status> conditionalRouting, Guard<X>[] guards, Map<Status, Set<Process>> conditionalProcesses,
			Status<X>... arrivalStatus) {
		super(workflow, name, departureStatus, guards);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction<>(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			Set<Process> processesSet = conditionalProcesses.get(arrivalStatus[num]);
			Process[] processes = null;
			if (processesSet != null) {
				processes = processesSet.toArray(new Process[0]);
			}
			conditionalActions.add(new SimpleAction<>(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], processes));
		}
		workflow.addAction(this);
	}

	public ConditionalAction() {
	}

	@SuppressWarnings("unchecked")
	public Condition<X> getCondition() {
		return condition;
	}

	public void setCondition(Condition<X> condition) {
		this.condition = condition;
	}

	@SuppressWarnings("rawtypes")
	public Map<Integer, Status> getConditionalRouting() {
		return conditionalRouting;
	}

	@SuppressWarnings("rawtypes")
	public void setConditionalRouting(Map<Integer, Status> conditionalRouting) {
		this.conditionalRouting = conditionalRouting;
	}

	@SuppressWarnings("unchecked")
	public Status<X> getArrivalStatusByResult(int res) {
		return conditionalRouting.get(res);
	}

	@SuppressWarnings("unchecked")
	public Status<X> getChoicePseudoStatus() {
		return choicePseudoStatus;
	}

	public void setChoicePseudoStatus(Status<X> choicePseudoStatus) {
		this.choicePseudoStatus = choicePseudoStatus;
	}

	@SuppressWarnings("rawtypes")
	public Set<SimpleAction> getConditionalActions() {
		return conditionalActions;
	}

	@SuppressWarnings("rawtypes")
	public void setConditionalActions(Set<SimpleAction> conditionalActions) {
		this.conditionalActions = conditionalActions;
	}

	public Set<Action<X>> getDepartureActions() {
		if (conditionalActions == null || conditionalActions.isEmpty()) {
			return null;
		}
		return conditionalActions.stream().filter(a -> a.getArrivalStatus().equals(choicePseudoStatus))
				.collect(Collectors.toSet());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<Guard> getGuardsByActionName(String name) {
		if (conditionalActions == null || conditionalActions.isEmpty()) {
			return null;
		}
		Optional<SimpleAction> sa = conditionalActions.stream().filter(a -> a.getName().equals(name)).findAny();
		if (sa.isPresent()) {
			return sa.get().getGuards();
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<Process> getProcessesByStatusName(String name) {
		if (conditionalActions == null || conditionalActions.isEmpty()) {
			return null;
		}
		Optional<SimpleAction> sa = conditionalActions.stream().filter(a -> a.getArrivalStatus().getName().equals(name))
				.findAny();
		if (sa.isPresent()) {
			return sa.get().getProcesses();
		}
		return null;
	}

	@Override
	public boolean isConnectedToPseudoStatus() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isDepartureStatus(Status<X> status) {
		return this.conditionalActions.stream().filter(a -> a.isDepartureStatus(status)).count() > 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(choicePseudoStatus, getWorkflow());
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
		ConditionalAction<X> other = (ConditionalAction<X>) obj;
		return Objects.equals(choicePseudoStatus, other.choicePseudoStatus)
				&& Objects.equals(getWorkflow(), other.getWorkflow());
	}

}