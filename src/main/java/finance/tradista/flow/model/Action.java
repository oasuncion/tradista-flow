package finance.tradista.flow.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
public abstract class Action<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = 6966194265379717568L;

	private String name;

	@SuppressWarnings("rawtypes")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Guard> guards;

	@SuppressWarnings("rawtypes")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workflow_id")
	private Workflow workflow;

	@SuppressWarnings("rawtypes")
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "departure_status_id")
	private Status departureStatus;

	protected Action(Workflow<X> workflow, String name, Status<X> departureStatus, Guard<X>... guards) {
		this();
		StringBuilder errMsg = new StringBuilder();
		if (departureStatus != null) {
			if (departureStatus.getWorkflow() == null || !departureStatus.getWorkflow().equals(workflow)) {
				errMsg.append(String.format("The departure status should have the same workflow %s", workflow));
			}
		}
		if (!errMsg.isEmpty()) {
			throw new IllegalArgumentException(errMsg.toString());
		}
		this.name = name;
		this.departureStatus = departureStatus;
		this.guards = new LinkedHashSet<>();
		if (guards != null) {
			Arrays.stream(guards).forEach(g -> this.guards.add(g));
		}
	}

	protected Action() {
	}

	public abstract boolean isConnectedToPseudoStatus();

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Status<X> getDepartureStatus() {
		return TradistaFlowUtil.clone(departureStatus);
	}

	@SuppressWarnings("unchecked")
	public Workflow<X> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow<X> workflow) {
		this.workflow = workflow;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDepartureStatus(Status<X> departureStatus) {
		this.departureStatus = departureStatus;
	}

	@SuppressWarnings("rawtypes")
	public Set<Guard> getGuards() {
		return guards;
	}

	@SuppressWarnings("rawtypes")
	public void setGuards(Set<Guard> guards) {
		this.guards = guards;
	}

	public abstract boolean isDepartureStatus(Status<X> status);

	@SuppressWarnings("unchecked")
	@Override
	public Action<X> clone() {
		Action<X> action = (Action<X>) super.clone();
		action.departureStatus = TradistaFlowUtil.clone(departureStatus);
		return action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(departureStatus, name, workflow);
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
		Action<X> other = (Action<X>) obj;
		return Objects.equals(departureStatus, other.departureStatus) && Objects.equals(name, other.name)
				&& Objects.equals(workflow, other.workflow);
	}

	@Override
	public String toString() {
		return name;
	}

}