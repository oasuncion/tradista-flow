package finance.tradista.flow.model;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

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
 * Abstract class for classes part of the Tradista Flow model.
 * 
 * @author Olivier Asuncion
 *
 */

@MappedSuperclass
public abstract class TradistaFlowObject implements Serializable, Cloneable {

	private static final long serialVersionUID = 1547063575791139565L;

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Gets the id of this TradistaFlowObject. The persistence provider should auto
	 * generate a unique id for new TradistaFlowObjects.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the id of this TradistaFlowObject to the specified value.
	 * 
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public TradistaFlowObject clone() {
		TradistaFlowObject clone = null;
		try {
			clone = (TradistaFlowObject) super.clone();
		} catch (CloneNotSupportedException e) {
			// Not expected, TradistaFlowObjects are subclasses are Cloneable
		}
		return clone;
	}

}