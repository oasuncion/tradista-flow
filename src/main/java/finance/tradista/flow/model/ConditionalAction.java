package finance.tradista.flow.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
	private Condition condition;

	@OneToMany(cascade = CascadeType.ALL)
	private Map<Integer, Status> conditionalRouting;

	@OneToMany(cascade = CascadeType.ALL)
	private Map<Status, Process> conditionalProcesses;

	@OneToOne(cascade = CascadeType.ALL)
	private Status choicePseudoStatus;

	@OneToMany(cascade = CascadeType.ALL)
	private Set<SimpleAction> conditionalActions;

	private void init(Workflow workflow, Status departureStatus, String name, Condition condition,
			Map<Integer, Status> conditionalRouting, Status... arrivalStatus) {
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
		this.condition = condition;
		this.conditionalRouting = conditionalRouting;
		choicePseudoStatus = new Status(workflow, UUID.randomUUID().toString());
		this.conditionalActions = new HashSet<SimpleAction>(arrivalStatus.length);
		workflow.addAction(this);
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name, Condition condition,
			Map<Integer, Status> conditionalRouting, Guard guard, Status... arrivalStatus) {
		super(workflow, name, departureStatus, guard);
		init(workflow, departureStatus, name, condition, conditionalRouting, arrivalStatus);
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name, Condition condition,
			Map<Integer, Status> conditionalRouting, Status... arrivalStatus) {
		super(workflow, name, departureStatus, null);
		init(workflow, departureStatus, name, condition, conditionalRouting, arrivalStatus);
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(
					new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus, arrivalStatus[num]));
		}
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name, Condition condition,
			Map<Integer, Status> conditionalRouting, Map<Status, Process> conditionalProcesses,
			Status... arrivalStatus) {
		super(workflow, name, departureStatus, null);
		init(workflow, departureStatus, name, condition, conditionalRouting, arrivalStatus);
		this.conditionalProcesses = conditionalProcesses;
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], conditionalProcesses.get(arrivalStatus[num])));
		}
	}

	public ConditionalAction(Workflow workflow, Status departureStatus, String name, Condition condition,
			Map<Integer, Status> conditionalRouting, Guard guard, Map<Status, Process> conditionalProcesses,
			Status... arrivalStatus) {
		super(workflow, name, departureStatus, guard);
		init(workflow, departureStatus, name, condition, conditionalRouting, arrivalStatus);
		this.conditionalProcesses = conditionalProcesses;
		for (int num = 0; num < arrivalStatus.length; num++) {
			conditionalActions.add(new SimpleAction(workflow, UUID.randomUUID().toString(), choicePseudoStatus,
					arrivalStatus[num], conditionalProcesses.get(arrivalStatus[num])));
		}
	}

	public ConditionalAction() {
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
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

	public Map<Status, Process> getConditionalProcesses() {
		return conditionalProcesses;
	}

	public void setConditionalProcesses(Map<Status, Process> conditionalProcesses) {
		this.conditionalProcesses = conditionalProcesses;
	}

}