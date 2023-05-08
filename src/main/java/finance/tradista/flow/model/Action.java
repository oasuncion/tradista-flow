package finance.tradista.flow.model;

import java.util.Objects;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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

@Entity
public class Action extends TradistaFlowObject {

	private static final long serialVersionUID = 6966194265379717568L;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_id")
	private Workflow workflow;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "departure_status_id")
	private Status departureStatus;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "arrival_status_id")
	private Status arrivalStatus;

	public Action(Workflow workflow, String name, Status departureStatus, Status arrivalStatus) {
		this();
		StringBuilder errMsg = new StringBuilder();
		if (departureStatus.getWorkflow() == null || !departureStatus.getWorkflow().equals(workflow)) {
			errMsg.append(String.format("The departure status should have the same workflow %s", workflow));
		}
		if (arrivalStatus.getWorkflow() == null || !arrivalStatus.getWorkflow().equals(workflow)) {
			errMsg.append(String.format("The arrival status should have the same workflow %s", workflow));
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
		this.workflow = workflow;
		this.name = name;
		this.departureStatus = departureStatus;
		this.arrivalStatus = arrivalStatus;
		workflow.addAction(this);
	}

	public Action() {
	}

	public String getName() {
		return name;
	}

	public Status getDepartureStatus() {
		return TradistaFlowUtil.clone(departureStatus);
	}

	public Status getArrivalStatus() {
		return TradistaFlowUtil.clone(arrivalStatus);
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

	public void setArrivalStatus(Status arrivalStatus) {
		this.arrivalStatus = arrivalStatus;
	}

	@Override
	public Action clone() {
		Action action = (Action) super.clone();
		action.departureStatus = TradistaFlowUtil.clone(departureStatus);
		action.arrivalStatus = TradistaFlowUtil.clone(arrivalStatus);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrivalStatus, departureStatus, name, workflow);
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
		return Objects.equals(arrivalStatus, other.arrivalStatus)
				&& Objects.equals(departureStatus, other.departureStatus) && Objects.equals(name, other.name)
				&& Objects.equals(workflow, other.workflow);
	}

	@Override
	public String toString() {
		return name;
	}

}