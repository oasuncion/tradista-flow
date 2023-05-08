package finance.tradista.flow.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Action;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.test.WorkflowTestObject;

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

public class WorkflowManagerTest {

	@Test
	@DisplayName("Save invalid workflow")
	void testSaveInvalidWorkflow() {
		Workflow wkf = new Workflow("TestSaveInvalidWorkflow");
		new Status(wkf, "s1");
		new Status(wkf, "s2");
		try {
			WorkflowManager.saveWorkflow(wkf);
			Assertions.fail();
		} catch (TradistaFlowBusinessException tfbe) {
		}
	}

	@Test
	@DisplayName("Save valid workflow")
	void testSaveValidWorkflow() {
		Workflow wkf = new Workflow("TestSaveValidWorkflow");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Check valid workflow")
	void testIsValid() {
		Workflow wkf = new Workflow("TestIsValid");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Check invalid workflow")
	void testIsNotValid() {
		Workflow wkf = new Workflow("TestIsNotValid");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		new Status(wkf, "s3");
		try {
			Assertions.assertFalse(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Cycle detection")
	void testCycle() {
		Workflow wkf = new Workflow("TestCycle");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		Status s3 = new Status(wkf, "s3");
		new Action(wkf, "a2", s2, s3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Action(wkf, "a3", s3, s1);
		});
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Initial status")
	void testIsInitialStatus() {
		Workflow wkf = new Workflow("testIsInitialStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isInitialStatus(s1));
	}

	@Test
	@DisplayName("Final status")
	void testIsFinalStatus() {
		Workflow wkf = new Workflow("testIsFinalStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new Action(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isFinalStatus(s2));
	}

	@Test
	@DisplayName("Apply valid action")
	void testApplyValidAction() {
		Workflow wkf = new Workflow("testApplyValidAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Action a1 = new Action(wkf, "a1", s1, s2);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		obj.setStatus(s1);
		obj.setWorkflow(wkf.getName());
		try {
			WorkflowManager.applyAction(obj, a1);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Apply invalid action")
	void testApplyInvalidAction() {
		Workflow wkf = new Workflow("testApplyInvalidAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Action a1 = new Action(wkf, "a1", s1, s2);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		obj.setWorkflow(wkf.getName());
		obj.setStatus(s2);
		try {
			WorkflowManager.applyAction(obj, a1);
			Assertions.fail();
		} catch (TradistaFlowBusinessException tfbe) {
		}
	}

}