package finance.tradista.flow.test;

import finance.tradista.flow.model.Guard;
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
 * Guard Test Class. This test guard return false, so always blocks.
 * 
 * @author OA
 *
 */
@Entity
public class TestGuardKO extends Guard<WorkflowTestObject> {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestGuardKO() {
		setPredicate(obj -> {
			return false;
		});
	}

}