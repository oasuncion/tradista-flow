package finance.tradista.flow.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

/**
 * Class representing a workflow status.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Status<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = -2572527032257168988L;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	private Workflow workflow;

	public Workflow<X> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow<X> workflow) {
		this.workflow = workflow;
	}

	public Status(Workflow<X> workflow, String name) {
		this();
		this.workflow = workflow;
		this.name = name;
		workflow.addStatus(this);
	}

	public Status() {
	}

	public String getName() {
		return name;
	}

	public boolean isInitialStatus(Workflow<X> workflow) {
		return workflow.isInitialStatus(this);
	}

	public boolean isFinalStatus(Workflow<X> workflow) {
		return workflow.isFinalStatus(this);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, workflow);
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
		Status<X> other = (Status<X>) obj;
		return Objects.equals(name, other.name) && Objects.equals(workflow, other.workflow);
	}

	@Override
	public String toString() {
		return name;
	}

}