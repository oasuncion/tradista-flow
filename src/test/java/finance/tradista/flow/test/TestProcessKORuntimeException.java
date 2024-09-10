package finance.tradista.flow.test;

import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
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
 * Process Test Class. This test process performs some updates on the object and
 * then throws a RuntimeException.
 * 
 * @author OA
 *
 */
@Entity
public class TestProcessKORuntimeException extends finance.tradista.flow.model.Process<WorkflowTestObject> {

	private static final long serialVersionUID = -7542450271283408548L;

	public TestProcessKORuntimeException() {
		setTask(obj -> {
			// The changes below are expected to be ignored as an exception is thrown.
			final String WKF = "Wkf";
			if (!obj.getWorkflow().equals(WKF)) {
				obj.setWorkflow(WKF);
			} else {
				obj.setWorkflow("AnotherWkf");
			}
			obj.setStatus(new Status<>(new Workflow<WorkflowTestObject>("dummyWorkkflow"), "dummyStatus"));
			throw new RuntimeException("Process KO");
		});
	}

}