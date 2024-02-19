package finance.tradista.flow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.exception.TradistaFlowTechnicalException;
import finance.tradista.flow.model.Action;
import finance.tradista.flow.model.Condition;
import finance.tradista.flow.model.ConditionalAction;
import finance.tradista.flow.model.Guard;
import finance.tradista.flow.model.Process;
import finance.tradista.flow.model.SimpleAction;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.model.WorkflowObject;
import finance.tradista.flow.test.TestCondition;
import finance.tradista.flow.test.TestConditionKORuntimeException;
import finance.tradista.flow.test.TestGuardKO;
import finance.tradista.flow.test.TestGuardOK;
import finance.tradista.flow.test.TestProcessKOCheckedException;
import finance.tradista.flow.test.TestProcessKORuntimeException;
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
		Process<WorkflowObject> process = new TestProcessOK();
		new SimpleAction(wkf, "a1", s1, s2, process);
		saveWorkflow(wkf);
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
		saveWorkflow(wkf);
	}

	@Test
	@DisplayName("Get valid workflow")
	void testGetValidWorkflow() {
		final String workflowName = "TestGetValidWorkflow";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Process<WorkflowObject> process = new TestProcessOK();
		Workflow loadedWorklow;
		new SimpleAction(wkf, "a1", s1, s2, process);
		saveWorkflow(wkf);
		loadedWorklow = loadWorkflow(workflowName);
		Assertions.assertEquals(wkf.getActions(), loadedWorklow.getActions());
		Assertions.assertEquals(wkf.getStatus(), loadedWorklow.getStatus());
		Assertions.assertEquals(wkf.getName(), loadedWorklow.getName());
	}

	@Test
	@DisplayName("Workflow doesn't exist")
	void testWorkflowDoesNotExist() {
		try {
			WorkflowManager.getWorkflowByName("DoesNotExist");
			Assertions.fail();
		} catch (TradistaFlowBusinessException tfbe) {
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
		Status s0 = new Status(wkf, "s0");
		Status s1 = new Status(wkf, "s1");
		new SimpleAction(wkf, "a0", s0, s1);
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Status s3 = new Status(wkf, "s3");
		new SimpleAction(wkf, "a2", s2, s3);
		new SimpleAction(wkf, "a3", s3, s1);
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
		Status s0 = new Status(wkf, "s0");
		Status s1 = new Status(wkf, "s1");
		new SimpleAction(wkf, "a0", s0, s1);
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Status s3 = new Status(wkf, "s3");
		new SimpleAction(wkf, "a2", s2, s3);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction(wkf, s3, "a1", c1, conditionalRouting, s1);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Is initial status")
	void testIsInitialStatus() {
		Workflow wkf = new Workflow("testIsInitialStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isInitialStatus(s1));
	}

	@Test
	@DisplayName("Is final status")
	void testIsFinalStatus() {
		Workflow wkf = new Workflow("testIsFinalStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isFinalStatus(s2));
	}

	@Test
	@DisplayName("Get initial status")
	void testGetInitialStatus() {
		Workflow wkf = new Workflow("testGetInitialStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertEquals(s1, wkf.getInitialStatus());
	}

	@Test
	@DisplayName("Get final status")
	void testGetFinalStatus() {
		Workflow wkf = new Workflow("testGetFinalStatus");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		new SimpleAction(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.getFinalStatus().contains(s2));
		Assertions.assertFalse(wkf.getFinalStatus().contains(s1));
		Assertions.assertEquals(1, wkf.getFinalStatus().size());

		wkf = new Workflow("testGetSeveralFinalStatus");
		s1 = new Status(wkf, "s1");
		s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		new SimpleAction(wkf, "a1", s1, s2);
		new SimpleAction(wkf, "a1", s1, s3);
		Assertions.assertTrue(wkf.getFinalStatus().contains(s2));
		Assertions.assertTrue(wkf.getFinalStatus().contains(s3));
		Assertions.assertFalse(wkf.getFinalStatus().contains(s1));
		Assertions.assertEquals(2, wkf.getFinalStatus().size());
	}

	@Test
	@DisplayName("Get no available action")
	void testGetNoAvailableAction() {
		Workflow wkf = new Workflow("testGetNoAvailableAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2);
		Assertions.assertNull(wkf.getAvailableActionsFromStatus(s2));
	}

	@Test
	@DisplayName("Get a simple available action")
	void testGetSimpleAvailableActionName() {
		Workflow wkf = new Workflow("testGetSimpleAvailableAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2);
		HashSet<String> resActionName = new HashSet<String>();
		resActionName.add(actionName);
		Assertions.assertEquals(resActionName, wkf.getAvailableActionsFromStatus(s1));
	}

	@Test
	@DisplayName("Get a conditional available action")
	void testGetConditionalAvailableActionNames() {
		Workflow wkf = new Workflow("testGetConditionalAvailableAction");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, new TestCondition(), (Map<Integer, Status>) null,
				(Guard<WorkflowObject>) null, s2);
		HashSet<String> resActionName = new HashSet<String>();
		resActionName.add(actionName);
		Assertions.assertEquals(resActionName, wkf.getAvailableActionsFromStatus(s1));
	}

	@Test
	@DisplayName("Get several available actions")
	void testGetSeveralAvailableActionNames() {
		Workflow wkf = new Workflow("testGetSeveralAvailableActions");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionNameOne = "a1";
		final String actionNameTwo = "a2";
		final String actionNameThree = "a3";
		Set<Action> actions = new HashSet<>();
		actions.add(new ConditionalAction(wkf, s1, actionNameOne, new TestCondition(), (Map<Integer, Status>) null,
				(Guard<WorkflowObject>) null, s2));
		actions.add(new ConditionalAction(wkf, s1, actionNameTwo, new TestCondition(), (Map<Integer, Status>) null,
				(Guard<WorkflowObject>) null, s2));
		actions.add(new SimpleAction(wkf, actionNameThree, s1, s2));
		HashSet<String> resActionNames = new HashSet<String>();
		resActionNames.add(actionNameOne);
		resActionNames.add(actionNameTwo);
		resActionNames.add(actionNameThree);
		Assertions.assertEquals(resActionNames, wkf.getAvailableActionsFromStatus(s1));
	}

	@Test
	@DisplayName("Get several available actions from Conditional")
	void testGetSeveralAvailableActionNamesFromConditional() {
		Workflow wkf = new Workflow("testGetSeveralAvailableActionsFromConditional");
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		final String actionNameOne = "a1";
		final String actionNameTwo = "a2";
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Set<SimpleAction> actionsSet = new HashSet<>();
		actionsSet.add(new SimpleAction(wkf, actionNameOne, s1));
		actionsSet.add(new SimpleAction(wkf, actionNameTwo, s1));
		new ConditionalAction(wkf, actionsSet, new TestCondition(), conditionalRouting, s2);
		HashSet<String> resActionNames = new HashSet<String>();
		resActionNames.add(actionNameOne);
		resActionNames.add(actionNameTwo);
		Assertions.assertEquals(resActionNames, wkf.getAvailableActionsFromStatus(s1));
	}

	@Test
	@DisplayName("Apply valid action")
	void testApplyValidAction() {
		String workflowName = "testApplyValidAction";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		applyAction(obj, actionName);
	}

	@Test
	@DisplayName("Apply valid action with process OK")
	void testApplyValidActionWithProcessOK() {
		String workflowName = "testApplyValidActionWithProcessOK";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		TestProcessOK process = new TestProcessOK();
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2, process);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertEquals("Wkf", res.getWorkflow());
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertEquals(s2, res.getStatus());
	}

	@Test
	@DisplayName("Apply valid action with process KO")
	void testApplyValidActionWithProcessKO() {
		String workflowName = "testApplyValidActionWithProcessKO";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		TestProcessKOCheckedException process = new TestProcessKOCheckedException();
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2, process);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, actionName));
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertNotEquals("Wkf", obj.getWorkflow());

		assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply valid action with process KO Runtime Exception")
	void testApplyValidActionWithProcessKORuntimeException() {
		String workflowName = "testApplyValidActionWithProcessKORuntimeException";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		TestProcessKORuntimeException process = new TestProcessKORuntimeException();
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2, process);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowTechnicalException.class, () -> WorkflowManager.applyAction(obj, actionName));
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertNotEquals("Wkf", obj.getWorkflow());

		assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply action with guard OK")
	void testApplyActionGuardOK() {
		String workflowName = "testApplyActionGuardOK";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Guard<WorkflowObject> guardOK = new TestGuardOK();
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2, guardOK);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertEquals(s2, res.getStatus());
	}

	@Test
	@DisplayName("Apply action with guard KO")
	void testApplyActionGuardKO() {
		String workflowName = "testApplyActionGuardKO";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Guard<WorkflowObject> guardKO = new TestGuardKO();
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2, guardKO);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		applyAction(obj, actionName);
		Assertions.assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply invalid action")
	void testApplyInvalidAction() {
		String workflowName = "testApplyInvalidAction";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction(wkf, actionName, s1, s2);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setWorkflow(workflowName);
		obj.setStatus(s2);
		try {
			WorkflowManager.applyAction(obj, actionName);
			Assertions.fail();
		} catch (TradistaFlowBusinessException tfbe) {
		}
	}

	@Test
	@DisplayName("Apply valid conditional action")
	void testApplyValidConditionalAction() {
		String workflowName = "testApplyValidConditionalAction";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertEquals(s2, res.getStatus());
	}

	@Test
	@DisplayName("Apply valid junction action")
	void testApplyValidJunctionAction() {
		String workflowName = "testApplyValidJunctionAction";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s2b = new Status(wkf, "s2b");
		Status s3 = new Status(wkf, "s3");
		Status s4 = new Status(wkf, "s4");
		new SimpleAction(wkf, "a1", s1, s2);
		new SimpleAction(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction> actionsSet = new HashSet<>();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction(wkf, actionNameTwo, s2));
		actionsSet.add(new SimpleAction(wkf, actionNameTwoB, s2b));
		new ConditionalAction(wkf, actionsSet, c1, conditionalRouting, s3, s4);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s2);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionNameTwo);
		Assertions.assertEquals(s4, res.getStatus());
		obj.setStatus(s2b);
		res = applyAction(obj, actionNameTwoB);
		Assertions.assertEquals(s4, res.getStatus());
	}

	@Test
	@DisplayName("Apply valid junction action with a guard OK")
	void testApplyValidJunctionActionWithGuardOK() {
		String workflowName = "testApplyValidJunctionActionWithGuardOK";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s2b = new Status(wkf, "s2b");
		Status s3 = new Status(wkf, "s3");
		Status s4 = new Status(wkf, "s4");
		new SimpleAction(wkf, "a1", s1, s2);
		new SimpleAction(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction> actionsSet = new HashSet<>();
		Guard<WorkflowObject> guardOK = new TestGuardOK();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction(wkf, actionNameTwo, s2, guardOK));
		actionsSet.add(new SimpleAction(wkf, actionNameTwoB, s2b));
		new ConditionalAction(wkf, actionsSet, c1, conditionalRouting, s3, s4);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s2);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionNameTwo);
		Assertions.assertEquals(s4, res.getStatus());
		obj.setStatus(s2b);
		res = applyAction(obj, actionNameTwoB);
		Assertions.assertEquals(s4, res.getStatus());
	}

	@Test
	@DisplayName("Apply valid junction action with a guard KO")
	void testApplyValidJunctionActionWithGuardKO() {
		String workflowName = "testApplyValidJunctionActionWithGuardKO";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s2b = new Status(wkf, "s2b");
		Status s3 = new Status(wkf, "s3");
		Status s4 = new Status(wkf, "s4");
		new SimpleAction(wkf, "a1", s1, s2);
		new SimpleAction(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction> actionsSet = new HashSet<>();
		Guard<WorkflowObject> guardKO = new TestGuardKO();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction(wkf, actionNameTwo, s2, guardKO));
		actionsSet.add(new SimpleAction(wkf, actionNameTwoB, s2b));
		new ConditionalAction(wkf, actionsSet, c1, conditionalRouting, s3, s4);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s2);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionNameTwo);
		Assertions.assertEquals(s2, res.getStatus());
		obj.setStatus(s2b);
		res = applyAction(obj, actionNameTwoB);
		Assertions.assertEquals(s4, res.getStatus());
	}

	@Test
	@DisplayName("Apply conditional action with checked exception")
	void testApplyConditionalActionWithCheckedException() {
		String workflowName = "testConditionalActionWithCheckedException";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "sA");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, actionName));
		assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply conditional action with runtime exception")
	void testApplyConditionalActionWithRuntimeException() {
		String workflowName = "testConditionalActionWithRuntimeException";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		TestConditionKORuntimeException c1 = new TestConditionKORuntimeException();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowTechnicalException.class, () -> WorkflowManager.applyAction(obj, actionName));
		assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply valid conditional action with process OK")
	void testApplyValidConditionalActionWithProcessOK() {
		String workflowName = "testApplyValidConditionalActionWithProcessOK";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		Condition<WorkflowObject> c1 = new TestCondition();
		Process<WorkflowObject> process = new TestProcessOK();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process<WorkflowObject>> conditionalProcesses = new HashMap<Status, Process<WorkflowObject>>();
		conditionalProcesses.put(s2, process);
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, c1, conditionalRouting, conditionalProcesses, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertEquals(s2, res.getStatus());
	}

	@Test
	@DisplayName("Apply valid conditional action with process KO")
	void testApplyValidConditionalActionWithProcessKO() {
		String workflowName = "testApplyValidConditionalActionWithProcessKO";
		Workflow wkf = new Workflow(workflowName);
		Status s1 = new Status(wkf, "s1");
		Status s2 = new Status(wkf, "s2");
		Status s3 = new Status(wkf, "s3");
		Condition<WorkflowObject> c1 = new TestCondition();
		Process<WorkflowObject> process = new TestProcessKOCheckedException();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process<WorkflowObject>> conditionalProcesses = new HashMap<Status, Process<WorkflowObject>>();
		conditionalProcesses.put(s2, process);
		final String actionName = "a1";
		new ConditionalAction(wkf, s1, actionName, c1, conditionalRouting, conditionalProcesses, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, actionName));
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertNotEquals("Wkf", obj.getWorkflow());
	}

	private void saveWorkflow(Workflow wkf) {
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	private Workflow loadWorkflow(String workflowName) {
		Workflow wkf = null;
		try {
			wkf = WorkflowManager.getWorkflowByName(workflowName);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		return wkf;
	}

	private WorkflowObject applyAction(WorkflowTestObject obj, String actionToApply) {
		try {
			obj = WorkflowManager.applyAction(obj, actionToApply);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
		return obj;
	}

}