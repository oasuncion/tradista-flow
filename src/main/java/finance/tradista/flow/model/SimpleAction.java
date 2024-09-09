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
public class SimpleAction<X extends WorkflowObject> extends Action<X> {

	private static final long serialVersionUID = 4747165809942693001L;

	@SuppressWarnings("rawtypes")
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "arrival_status_id")
	private Status arrivalStatus;

	@SuppressWarnings("rawtypes")
	@OneToOne(cascade = CascadeType.ALL)
	private Process process;

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
			Guard<X>[] guards, Process<X> process) {
		super(workflow, name, departureStatus, guards);
		init(workflow, arrivalStatus);
		this.process = process;
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Status<X> arrivalStatus,
			Process<X> process) {
		super(workflow, name, departureStatus, (Guard<X>[]) null);
		init(workflow, arrivalStatus);
		this.process = process;
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Process<X> process) {
		this(workflow, name, departureStatus, (Status<X>) null, process);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Guard<X>... guards) {
		this(workflow, name, departureStatus, null, guards);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus) {
		this(workflow, name, departureStatus, (Status<X>) null);
	}

	public SimpleAction(Workflow<X> workflow, String name, Status<X> departureStatus, Guard<X>[] guards,
			Process<X> process) {
		this(workflow, name, departureStatus, (Status<X>) null, guards, process);
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

	@SuppressWarnings("unchecked")
	public Process<X> getProcess() {
		return process;
	}

	public void setProcess(Process<X> process) {
		this.process = process;
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