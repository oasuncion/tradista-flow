package finance.tradista.flow.test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Condition;
import finance.tradista.flow.model.WorkflowObject;
import jakarta.persistence.Entity;

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
 * Condition Test Class. This test condition returns the second letter of the
 * object status as an integer. Example: if the object status is "s1", the
 * condition returns 1. If the second letter of the object status cannot be
 * parsed as a number, a TradistaFlowBusinessException is thrown.
 * 
 * @author OA
 *
 */
@Entity
public class TestCondition extends Condition<WorkflowObject> {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestCondition() {
		setFunction(obj -> {
			int ret = 0;
			try {
				ret = Integer.parseInt(obj.getStatus().getName().substring(1, 2));
			} catch (NumberFormatException nfe) {
				throw new TradistaFlowBusinessException(
						String.format("Condition %s - Could not parse the second letter of the object status : '%s'",
								getName(), obj.getStatus()));
			}

			return ret;
		});
	}

}