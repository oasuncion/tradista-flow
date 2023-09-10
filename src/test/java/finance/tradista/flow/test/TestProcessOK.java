package finance.tradista.flow.test;

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
 * Process Test Class. This test process sets the WorkflowObject's workflow to
 * "Wkf".
 * 
 * @author OA
 *
 */
@Entity
public class TestProcessOK extends finance.tradista.flow.model.Process<WorkflowObject> {

	private static final long serialVersionUID = -5097243928471620584L;

	public TestProcessOK() {
		setTask(obj -> {
			((WorkflowTestObject) obj).setWorkflow("Wkf");
		});
	}

}