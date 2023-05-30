package finance.tradista.flow.model;

import java.util.Objects;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
 * Class representing a simple workflow action.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class SimpleAction extends Action {

	private static final long serialVersionUID = 4747165809942693001L;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "arrival_status_id")
	private Status arrivalStatus;

	@OneToOne(cascade = CascadeType.ALL)
	private Process process;

	private void init(Workflow workflow, Status arrivalStatus) {
		StringBuilder errMsg = new StringBuilder();
		if (arrivalStatus.getWorkflow() == null || !arrivalStatus.getWorkflow().equals(workflow)) {
			errMsg.append(String.format("The arrival status should have the same workflow %s", workflow));
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
		this.arrivalStatus = arrivalStatus;
		workflow.addAction(this);
	}

	public SimpleAction(Workflow workflow, String name, Status departureStatus, Status arrivalStatus, Guard guard) {
		super(workflow, name, departureStatus, guard);
		init(workflow, arrivalStatus);
	}

	public SimpleAction(Workflow workflow, String name, Status departureStatus, Status arrivalStatus) {
		super(workflow, name, departureStatus, null);
		init(workflow, arrivalStatus);
	}

	public SimpleAction(Workflow workflow, String name, Status departureStatus, Status arrivalStatus, Guard guard,
			Process process) {
		super(workflow, name, departureStatus, guard);
		init(workflow, arrivalStatus);
		this.process = process;
	}

	public SimpleAction(Workflow workflow, String name, Status departureStatus, Status arrivalStatus, Process process) {
		super(workflow, name, departureStatus, null);
		init(workflow, arrivalStatus);
		this.process = process;
	}

	public SimpleAction() {
	}

	public Status getArrivalStatus() {
		return TradistaFlowUtil.clone(arrivalStatus);
	}

	public void setArrivalStatus(Status arrivalStatus) {
		this.arrivalStatus = arrivalStatus;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	@Override
	public SimpleAction clone() {
		SimpleAction action = (SimpleAction) super.clone();
		action.arrivalStatus = TradistaFlowUtil.clone(arrivalStatus);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrivalStatus, getDepartureStatus(), getName(), getWorkflow());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleAction other = (SimpleAction) obj;
		return Objects.equals(arrivalStatus, other.arrivalStatus)
				&& Objects.equals(getDepartureStatus(), other.getDepartureStatus())
				&& Objects.equals(getName(), other.getName()) && Objects.equals(getWorkflow(), other.getWorkflow());
	}

}