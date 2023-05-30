package finance.tradista.flow.model;

import java.util.Objects;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

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
 * Class representing a process.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Process extends TradistaFlowObject {

	private static final long serialVersionUID = -9106790274567211638L;

	private String name;

	@FunctionalInterface
	public interface Task<W extends WorkflowObject> {
		void apply(W obj) throws TradistaFlowBusinessException;
	}

	@Transient
	private Task<WorkflowObject> task;

	public Process() {
		this.name = this.getClass().getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Task<WorkflowObject> getPredicate() {
		return task;
	}

	public void setTask(Task<WorkflowObject> task) {
		this.task = task;
	}

	public String toString() {
		return name;
	}

	public void apply(WorkflowObject obj) throws TradistaFlowBusinessException {
		task.apply(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Process other = (Process) obj;
		return Objects.equals(name, other.name);
	}

}