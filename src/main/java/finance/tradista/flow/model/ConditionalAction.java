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

/*
 * Copyright 2023 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

/**
 * Class representing a conditional workflow action.
 * 
 * @author Olivier Asuncion
 *
 */

@Entity
public class ConditionalAction extends Action {

	private static final long serialVersionUID = -4235540145388029599L;

	@OneToOne(cascade = CascadeType.ALL)
	private Condition<WorkflowObject> condition;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Map<Integer, Status> conditionalRouting;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Map<Status, Process<WorkflowObject>> conditionalProcesses;

	@OneToOne(cascade = CascadeType.ALL)
	private Status choicePseudoStatus;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<SimpleAction> conditionalActions;

	private void init(Workflow workflow, Condition<WorkflowObject> condition, Map<Integer, Status> conditionalRouting,
			Status... arrivalStatus) {
		StringBuilder errMsg = new StringBuilder();
		for (Status status : arrivalStatus) {
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
		choicePseudoStatus = new PseudoStatus(workflow);
		this.conditionalActions = new HashSet<>(arrivalStatus.length);
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name,
			Condition<WorkflowObject> condition, Map<Integer, Status> conditionalRouting, Guard<WorkflowObject> guard,
			Status... arrivalStatus) {
		super(workflow, name, departureStatus, guard);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name,
			Condition<WorkflowObject> condition, Map<Integer, Status> conditionalRouting, Status... arrivalStatus) {
		super(workflow, name, departureStatus, null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		conditionalActions.add(new SimpleAction(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Set<SimpleAction> departureActions, Condition<WorkflowObject> condition,
			Map<Integer, Status> conditionalRouting, Status... arrivalStatus) {
		super(workflow, null, null, null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		for (SimpleAction sa : departureActions) {
			sa.setArrivalStatus(choicePseudoStatus);
			conditionalActions.add(sa);
		}
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name,
			Condition<WorkflowObject> condition, Map<Integer, Status> conditionalRouting,
			Map<Status, Process<WorkflowObject>> conditionalProcesses, Status... arrivalStatus) {
		super(workflow, name, departureStatus, null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		this.conditionalProcesses = conditionalProcesses;
		conditionalActions.add(new SimpleAction(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], conditionalProcesses.get(arrivalStatus[num])));
		}
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Set<SimpleAction> departureActions, Condition<WorkflowObject> condition,
			Map<Integer, Status> conditionalRouting, Map<Status, Process<WorkflowObject>> conditionalProcesses,
			Status... arrivalStatus) {
		super(workflow, null, null, null);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		this.conditionalProcesses = conditionalProcesses;
		for (SimpleAction sa : departureActions) {
			sa.setArrivalStatus(choicePseudoStatus);
			conditionalActions.add(sa);
		}
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], conditionalProcesses.get(arrivalStatus[num])));
		}
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name,
			Condition<WorkflowObject> condition, Map<Integer, Status> conditionalRouting, Guard<WorkflowObject> guard,
			Map<Status, Process<WorkflowObject>> conditionalProcesses, Status... arrivalStatus) {
		super(workflow, name, departureStatus, guard);
		init(workflow, condition, conditionalRouting, arrivalStatus);
		this.conditionalProcesses = conditionalProcesses;
		conditionalActions.add(new SimpleAction(workflow, name, departureStatus, choicePseudoStatus));
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], conditionalProcesses.get(arrivalStatus[num])));
		}
		workflow.addAction(this);
	}

	public ConditionalAction() {
	}

	public Condition<WorkflowObject> getCondition() {
		return condition;
	}

	public void setCondition(Condition<WorkflowObject> condition) {
		this.condition = condition;
	}

	public Map<Integer, Status> getConditionalRouting() {
		return conditionalRouting;
	}

	public void setConditionalRouting(Map<Integer, Status> conditionalRouting) {
		this.conditionalRouting = conditionalRouting;
	}

	public Status getArrivalStatusByResult(int res) {
		return conditionalRouting.get(res);
	}

	public Status getChoicePseudoStatus() {
		return choicePseudoStatus;
	}

	public void setChoicePseudoStatus(Status choicePseudoStatus) {
		this.choicePseudoStatus = choicePseudoStatus;
	}

	public Set<SimpleAction> getConditionalActions() {
		return conditionalActions;
	}

	public void setConditionalActions(Set<SimpleAction> conditionalActions) {
		this.conditionalActions = conditionalActions;
	}

	public Map<Status, Process<WorkflowObject>> getConditionalProcesses() {
		return conditionalProcesses;
	}

	public void setConditionalProcesses(Map<Status, Process<WorkflowObject>> conditionalProcesses) {
		this.conditionalProcesses = conditionalProcesses;
	}

	public Set<Action> getDepartureActions() {
		if (conditionalActions == null || conditionalActions.isEmpty()) {
			return null;
		}
		return conditionalActions.stream().filter(a -> a.getArrivalStatus().equals(choicePseudoStatus))
				.collect(Collectors.toSet());
	}

	public Guard<WorkflowObject> getGuardByActionName(String name) {
		if (conditionalActions == null || conditionalActions.isEmpty()) {
			return null;
		}
		Optional<SimpleAction> sa = conditionalActions.stream().filter(a -> a.getName().equals(name)).findAny();
		if (sa.isPresent()) {
			return sa.get().getGuard();
		}
		return null;
	}

	@Override
	public boolean isConnectedToPseudoStatus() {
		return false;
	}

	@Override
	public boolean isDepartureStatus(Status status) {
		return this.conditionalActions.stream().filter(a -> a.isDepartureStatus(status)).count() > 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(choicePseudoStatus, getWorkflow());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConditionalAction other = (ConditionalAction) obj;
		return Objects.equals(choicePseudoStatus, other.choicePseudoStatus)
				&& Objects.equals(getWorkflow(), other.getWorkflow());
	}

}