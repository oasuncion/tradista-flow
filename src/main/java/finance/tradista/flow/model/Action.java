package finance.tradista.flow.model;

import java.util.Objects;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Abstract class representing a workflow action.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Action extends TradistaFlowObject {

	private static final long serialVersionUID = 6966194265379717568L;

	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	private Guard<WorkflowObject> guard;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_id")
	private Workflow workflow;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "departure_status_id")
	private Status departureStatus;

	public Action(Workflow workflow, String name, Status departureStatus, Guard<WorkflowObject> guard) {
		this();
		StringBuilder errMsg = new StringBuilder();
		if (departureStatus.getWorkflow() == null || !departureStatus.getWorkflow().equals(workflow)) {
			errMsg.append(String.format("The departure status should have the same workflow %s", workflow));
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
		this.workflow = workflow;
		this.name = name;
		this.departureStatus = departureStatus;
		this.guard = guard;
	}

	public Action() {
	}

	public String getName() {
		return name;
	}

	public Status getDepartureStatus() {
		return TradistaFlowUtil.clone(departureStatus);
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDepartureStatus(Status departureStatus) {
		this.departureStatus = departureStatus;
	}

	public Guard<WorkflowObject> getGuard() {
		return guard;
	}

	public void setGuard(Guard<WorkflowObject> guard) {
		this.guard = guard;
	}

	@Override
	public Action clone() {
		Action action = (Action) super.clone();
		action.departureStatus = TradistaFlowUtil.clone(departureStatus);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(departureStatus, name, workflow);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		return Objects.equals(departureStatus, other.departureStatus) && Objects.equals(name, other.name)
				&& Objects.equals(workflow, other.workflow);
	}

	@Override
	public String toString() {
		return name;
	}

}