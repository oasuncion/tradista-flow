package finance.tradista.flow.model;

import java.util.Objects;
import java.util.function.Function;

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
 * Class representing a condition.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Condition<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = -8970069804519725007L;

	@Transient
	private Function<X, Integer> function;

	public Condition() {
	}

	public final String getName() {
		return getClass().getSimpleName();
	}

	public Function<X, Integer> getFunction() {
		return function;
	}

	public void setFunction(Function<X, Integer> function) {
		this.function = function;
	}

	public String toString() {
		return getName();
	}

	public int apply(X obj) {
		return function.apply(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition<?> other = (Condition<?>) obj;
		return Objects.equals(getName(), other.getName());
	}

}