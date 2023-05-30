package finance.tradista.flow.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Action;
import finance.tradista.flow.model.Condition;
import finance.tradista.flow.model.ConditionalAction;
import finance.tradista.flow.model.Guard;
import finance.tradista.flow.model.Process;
import finance.tradista.flow.model.SimpleAction;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.test.TestCondition;
import finance.tradista.flow.test.TestGuardKO;
import finance.tradista.flow.test.TestGuardOK;
import finance.tradista.flow.test.TestProcessKO;
import finance.tradista.flow.test.TestProcessOK;
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
		Process process = new TestProcessOK();
		new SimpleAction(wkf, "a1", s1, s2, process);
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Save valid workflow with a conditional action")
	void testSaveValidWorkflowConditional() {
		Workflow wkf = new Workflow("TestSaveValidWorkflowConditional");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
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
		new SimpleAction(wkf, "a1", s1, s2);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Check valid workflow with a conditional action")
	void testIsValidConditional() {
		Workflow wkf = new Workflow("TestIsValid");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
		;
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
		new SimpleAction(wkf, "a1", s1, s2);
		new Status(wkf, "s3");
		try {
			Assertions.assertFalse(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Check invalid workflow with a condition action")
	void testIsNotValidConditional() {
		Workflow wkf = new Workflow("TestIsNotValid");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
		;
		new Status(wkf, "s4");
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
		new SimpleAction(wkf, "a1", s1, s2);
		Status s3 = new Status(wkf, "s3");
		new SimpleAction(wkf, "a2", s2, s3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new SimpleAction(wkf, "a3", s3, s1);
		});
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Cycle detection with a conditional action")
	void testCycleCondition() {
		Workflow wkf = new Workflow("TestCycle");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Status s3 = new Status(wkf, "s3");
		new SimpleAction(wkf, "a2", s2, s3);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			TestCondition c1 = new TestCondition();
			Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
			conditionalRouting.put(1, s2);
			conditionalRouting.put(2, s3);
			new ConditionalAction(wkf, s3, "a1", c1, conditionalRouting, s1);
			;
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
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isInitialStatus(s1));
	}

	@Test
	@DisplayName("Final status")
	void testIsFinalStatus() {
		Workflow wkf = new Workflow("testIsFinalStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isFinalStatus(s2));
	}

	@Test
	@DisplayName("Apply valid action")
	void testApplyValidAction() {
		Workflow wkf = new Workflow("testApplyValidAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Action a1 = new SimpleAction(wkf, "a1", s1, s2);
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
	@DisplayName("Apply valid action with process OK")
	void testApplyValidActionWithProcessOK() {
		Workflow wkf = new Workflow("testApplyValidActionWithProcessOK");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		TestProcessOK process = new TestProcessOK();
		Action a1 = new SimpleAction(wkf, "a1", s1, s2, process);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		try {
			wkf = WorkflowManager.getWorkflowByName("testApplyValidActionWithProcessOK");
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		obj.setStatus(s1);
		obj.setWorkflow(wkf.getName());
		try {
			WorkflowManager.applyAction(obj, wkf.getActions().stream().findFirst().get());
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		Assertions.assertTrue(obj.getWorkflow().equals("Wkf"));
	}

	@Test
	@DisplayName("Apply valid action with process KO")
	void testApplyValidActionWithProcessKO() {
		Workflow wkf = new Workflow("testApplyValidActionWithProcessKO");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		TestProcessKO process = new TestProcessKO();
		Action a1 = new SimpleAction(wkf, "a1", s1, s2, process);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		try {
			final Workflow wkfTemp = WorkflowManager.getWorkflowByName("testApplyValidActionWithProcessKO");
			obj.setStatus(s1);
			obj.setWorkflow(wkf.getName());
			assertThrows(TradistaFlowBusinessException.class,
					() -> WorkflowManager.applyAction(obj, wkfTemp.getActions().stream().findFirst().get()));
			Assertions.assertFalse(obj.getStatus().equals(s2));
			Assertions.assertFalse(obj.getWorkflow().equals("Wkf"));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Apply action with guard OK")
	void testApplyActionGuardOK() {
		Workflow wkf = new Workflow("testApplyActionGuardOK");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Guard guardOK = new TestGuardOK();
		Action a1 = new SimpleAction(wkf, "a1", s1, s2, guardOK);
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
		Assertions.assertTrue(obj.getStatus().equals(s2));
	}

	@Test
	@DisplayName("Apply action with guard KO")
	void testApplyActionGuardKO() {
		Workflow wkf = new Workflow("testApplyActionGuardKO");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Guard guardKO = new TestGuardKO();
		Action a1 = new SimpleAction(wkf, "a1", s1, s2, guardKO);
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
		Assertions.assertTrue(obj.getStatus().equals(s1));
	}

	@Test
	@DisplayName("Apply invalid action")
	void testApplyInvalidAction() {
		Workflow wkf = new Workflow("testApplyInvalidAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Action a1 = new SimpleAction(wkf, "a1", s1, s2);
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

	@Test
	@DisplayName("Apply valid conditional action")
	void testApplyValidConditionalAction() {
		Workflow wkf = new Workflow("testApplyValidConditionalAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Action a1 = new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
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
		Assertions.assertTrue(obj.getStatus().equals(s2));
	}

	@Test
	@DisplayName("Apply valid conditional action with process OK")
	void testApplyValidConditionalActionWithProcessOK() {
		Workflow wkf = new Workflow("testApplyValidConditionalActionWithProcessOK");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		Condition c1 = new TestCondition();
		Process process = new TestProcessOK();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process> conditionalProcesses = new HashMap<Status, Process>();
		conditionalProcesses.put(s2, process);
		Action a1 = new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, conditionalProcesses, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		try {
			wkf = WorkflowManager.getWorkflowByName("testApplyValidConditionalActionWithProcessOK");
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
		Assertions.assertTrue(obj.getStatus().equals(s2));
		Assertions.assertTrue(obj.getWorkflow().equals("Wkf"));
	}

	@Test
	@DisplayName("Apply valid conditional action with process KO")
	void testApplyValidConditionalActionWithProcessKO() {
		Workflow wkf = new Workflow("testApplyValidConditionalActionWithProcessKO");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		Condition c1 = new TestCondition();
		Process process = new TestProcessKO();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process> conditionalProcesses = new HashMap<Status, Process>();
		conditionalProcesses.put(s2, process);
		Action a1 = new ConditionalAction(wkf, s1, "a1", c1, conditionalRouting, conditionalProcesses, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		obj.setStatus(s1);
		obj.setWorkflow(wkf.getName());
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, a1));
		Assertions.assertFalse(obj.getStatus().equals(s2));
		Assertions.assertFalse(obj.getWorkflow().equals("Wkf"));
	}

}