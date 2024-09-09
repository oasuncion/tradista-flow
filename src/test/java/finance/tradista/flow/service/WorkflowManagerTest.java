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
import finance.tradista.flow.test.TestGuardOKUpdateObject;
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestSaveInvalidWorkflow");
		new Status<WorkflowTestObject>(wkf, "s1");
		new Status<WorkflowTestObject>(wkf, "s2");
		try {
			WorkflowManager.saveWorkflow(wkf);
			Assertions.fail();
		} catch (TradistaFlowBusinessException tfbe) {
		}
	}

	@Test
	@DisplayName("Save valid workflow")
	void testSaveValidWorkflow() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestSaveValidWorkflow");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Process<WorkflowTestObject> process = new TestProcessOK();
		new SimpleAction<>(wkf, "a1", s1, s2, process);
		saveWorkflow(wkf);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Save valid workflow with a conditional action")
	void testSaveValidWorkflowConditional() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestSaveValidWorkflowConditional");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction<WorkflowTestObject>(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
		saveWorkflow(wkf);
	}

	@Test
	@DisplayName("Get valid workflow")
	void testGetValidWorkflow() {
		final String workflowName = "TestGetValidWorkflow";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Process<WorkflowTestObject> process = new TestProcessOK();
		Workflow<WorkflowTestObject> loadedWorklow;
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2, process);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestIsValid");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<>(wkf, "a1", s1, s2);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Check valid workflow with a conditional action")
	void testIsValidConditional() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestIsValid");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction<WorkflowTestObject>(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Check invalid workflow")
	void testIsNotValid() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestIsNotValid");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		new Status<>(wkf, "s3");
		try {
			Assertions.assertFalse(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Check invalid workflow with a condition action")
	void testIsNotValidConditional() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestIsNotValid");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction<WorkflowTestObject>(wkf, s1, "a1", c1, conditionalRouting, s2, s3);
		new Status<WorkflowTestObject>(wkf, "s4");
		try {
			Assertions.assertFalse(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Cycle detection")
	void testCycle() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestCycle");
		Status<WorkflowTestObject> s0 = new Status<>(wkf, "s0");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		new SimpleAction<WorkflowTestObject>(wkf, "a0", s0, s1);
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		new SimpleAction<WorkflowTestObject>(wkf, "a2", s2, s3);
		new SimpleAction<WorkflowTestObject>(wkf, "a3", s3, s1);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Cycle detection with a conditional action")
	void testCycleCondition() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("TestCycle");
		Status<WorkflowTestObject> s0 = new Status<>(wkf, "s0");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		new SimpleAction<WorkflowTestObject>(wkf, "a0", s0, s1);
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		new SimpleAction<WorkflowTestObject>(wkf, "a2", s2, s3);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		new ConditionalAction<WorkflowTestObject>(wkf, s3, "a1", c1, conditionalRouting, s1);
		try {
			Assertions.assertTrue(WorkflowManager.isValid(wkf));
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	@Test
	@DisplayName("Is initial status")
	void testIsInitialStatus() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testIsInitialStatus");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isInitialStatus(s1));
	}

	@Test
	@DisplayName("Is final status")
	void testIsFinalStatus() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testIsFinalStatus");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.isFinalStatus(s2));
	}

	@Test
	@DisplayName("Get initial status")
	void testGetInitialStatus() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetInitialStatus");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Assertions.assertEquals(s1, wkf.getInitialStatus());
	}

	@Test
	@DisplayName("Get final status")
	void testGetFinalStatus() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetFinalStatus");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		Assertions.assertTrue(wkf.getFinalStatus().contains(s2));
		Assertions.assertFalse(wkf.getFinalStatus().contains(s1));
		Assertions.assertEquals(1, wkf.getFinalStatus().size());

		wkf = new Workflow<>("testGetSeveralFinalStatus");
		s1 = new Status<>(wkf, "s1");
		s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s3);
		Assertions.assertTrue(wkf.getFinalStatus().contains(s2));
		Assertions.assertTrue(wkf.getFinalStatus().contains(s3));
		Assertions.assertFalse(wkf.getFinalStatus().contains(s1));
		Assertions.assertEquals(2, wkf.getFinalStatus().size());
	}

	@Test
	@DisplayName("Get no available action")
	void testGetNoAvailableAction() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetNoAvailableAction");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2);
		Assertions.assertNull(wkf.getAvailableActionsFromStatus(s2));
	}

	@Test
	@DisplayName("Get a simple available action")
	void testGetSimpleAvailableActionName() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetSimpleAvailableAction");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction<>(wkf, actionName, s1, s2);
		HashSet<String> resActionName = new HashSet<String>();
		resActionName.add(actionName);
		Assertions.assertEquals(resActionName, wkf.getAvailableActionsFromStatus(s1));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@DisplayName("Get a conditional available action")
	void testGetConditionalAvailableActionNames() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetConditionalAvailableAction");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, new TestCondition(), (Map<Integer, Status>) null,
				(Guard<WorkflowTestObject>[]) null, s2);
		HashSet<String> resActionName = new HashSet<String>();
		resActionName.add(actionName);
		Assertions.assertEquals(resActionName, wkf.getAvailableActionsFromStatus(s1));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@DisplayName("Get several available actions")
	void testGetSeveralAvailableActionNames() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetSeveralAvailableActions");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionNameOne = "a1";
		final String actionNameTwo = "a2";
		final String actionNameThree = "a3";
		Set<Action<WorkflowTestObject>> actions = new HashSet<>();
		actions.add(new ConditionalAction<WorkflowTestObject>(wkf, s1, actionNameOne, new TestCondition(),
				(Map<Integer, Status>) null, (Guard<WorkflowTestObject>[]) null, s2));
		actions.add(new ConditionalAction<WorkflowTestObject>(wkf, s1, actionNameTwo, new TestCondition(),
				(Map<Integer, Status>) null, (Guard<WorkflowTestObject>[]) null, s2));
		actions.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameThree, s1, s2));
		HashSet<String> resActionNames = new HashSet<String>();
		resActionNames.add(actionNameOne);
		resActionNames.add(actionNameTwo);
		resActionNames.add(actionNameThree);
		Assertions.assertEquals(resActionNames, wkf.getAvailableActionsFromStatus(s1));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Get several available actions from Conditional")
	void testGetSeveralAvailableActionNamesFromConditional() {
		Workflow<WorkflowTestObject> wkf = new Workflow<>("testGetSeveralAvailableActionsFromConditional");
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		final String actionNameOne = "a1";
		final String actionNameTwo = "a2";
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Set<SimpleAction<WorkflowTestObject>> actionsSet = new HashSet<>();
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameOne, s1));
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwo, s1));
		new ConditionalAction<WorkflowTestObject>(wkf, actionsSet, new TestCondition(), conditionalRouting, s2);
		HashSet<String> resActionNames = new HashSet<String>();
		resActionNames.add(actionNameOne);
		resActionNames.add(actionNameTwo);
		Assertions.assertEquals(resActionNames, wkf.getAvailableActionsFromStatus(s1));
	}

	@Test
	@DisplayName("Apply valid action")
	void testApplyValidAction() {
		String workflowName = "testApplyValidAction";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		TestProcessOK process = new TestProcessOK();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, process);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		TestProcessKOCheckedException process = new TestProcessKOCheckedException();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, process);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		TestProcessKORuntimeException process = new TestProcessKORuntimeException();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, process);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Guard<WorkflowTestObject> guardOK = new TestGuardOK();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, guardOK);
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
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Guard<WorkflowTestObject> guardKO = new TestGuardKO();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, guardKO);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		applyAction(obj, actionName);
		Assertions.assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply action with 2nd guard KO")
	void testApplyActionSecondGuardKO() {
		String workflowName = "testApplyActionSecondGuardKO";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Guard<WorkflowTestObject> guardOK = new TestGuardOK();
		Guard<WorkflowTestObject> guardKO = new TestGuardKO();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, guardOK, guardKO);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		applyAction(obj, actionName);
		Assertions.assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply action with guard OK and object modified")
	void testApplyActiondGuardOKObjectModified() {
		String workflowName = "testApplyActionGuardOKObjectModified";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Guard<WorkflowTestObject> guardOK = new TestGuardOKUpdateObject();
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2, guardOK);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		applyAction(obj, actionName);
		Assertions.assertNotEquals("AAA", obj.getWorkflow());
		Assertions.assertEquals(s1, obj.getStatus());
	}

	@Test
	@DisplayName("Apply invalid action")
	void testApplyInvalidAction() {
		String workflowName = "testApplyInvalidAction";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		final String actionName = "a1";
		new SimpleAction<WorkflowTestObject>(wkf, actionName, s1, s2);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid conditional action")
	void testApplyValidConditionalAction() {
		String workflowName = "testApplyValidConditionalAction";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertEquals(s2, res.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid junction action")
	void testApplyValidJunctionAction() {
		String workflowName = "testApplyValidJunctionAction";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s2b = new Status<>(wkf, "s2b");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		Status<WorkflowTestObject> s4 = new Status<>(wkf, "s4");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		new SimpleAction<WorkflowTestObject>(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction<WorkflowTestObject>> actionsSet = new HashSet<>();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwo, s2));
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwoB, s2b));
		new ConditionalAction<WorkflowTestObject>(wkf, actionsSet, c1, conditionalRouting, s3, s4);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid junction action with a guard OK")
	void testApplyValidJunctionActionWithGuardOK() {
		String workflowName = "testApplyValidJunctionActionWithGuardOK";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s2b = new Status<>(wkf, "s2b");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		Status<WorkflowTestObject> s4 = new Status<>(wkf, "s4");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		new SimpleAction<WorkflowTestObject>(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction<WorkflowTestObject>> actionsSet = new HashSet<>();
		Guard<WorkflowTestObject> guardOK = new TestGuardOK();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwo, s2, guardOK));
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwoB, s2b));
		new ConditionalAction<WorkflowTestObject>(wkf, actionsSet, c1, conditionalRouting, s3, s4);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid junction action with a guard KO")
	void testApplyValidJunctionActionWithGuardKO() {
		String workflowName = "testApplyValidJunctionActionWithGuardKO";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s2b = new Status<>(wkf, "s2b");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		Status<WorkflowTestObject> s4 = new Status<>(wkf, "s4");
		new SimpleAction<WorkflowTestObject>(wkf, "a1", s1, s2);
		new SimpleAction<WorkflowTestObject>(wkf, "a1b", s1, s2b);
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s3);
		conditionalRouting.put(2, s4);
		Set<SimpleAction<WorkflowTestObject>> actionsSet = new HashSet<>();
		Guard<WorkflowTestObject> guardKO = new TestGuardKO();
		final String actionNameTwo = "a2";
		final String actionNameTwoB = "a2b";
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwo, s2, guardKO));
		actionsSet.add(new SimpleAction<WorkflowTestObject>(wkf, actionNameTwoB, s2b));
		new ConditionalAction<WorkflowTestObject>(wkf, actionsSet, c1, conditionalRouting, s3, s4);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply conditional action with checked exception")
	void testApplyConditionalActionWithCheckedException() {
		String workflowName = "testConditionalActionWithCheckedException";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "sA");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestCondition c1 = new TestCondition();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, actionName));
		assertEquals(s1, obj.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply conditional action with runtime exception")
	void testApplyConditionalActionWithRuntimeException() {
		String workflowName = "testConditionalActionWithRuntimeException";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		TestConditionKORuntimeException c1 = new TestConditionKORuntimeException();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, c1, conditionalRouting, s2, s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowTechnicalException.class, () -> WorkflowManager.applyAction(obj, actionName));
		assertEquals(s1, obj.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid conditional action with process OK")
	void testApplyValidConditionalActionWithProcessOK() {
		String workflowName = "testApplyValidConditionalActionWithProcessOK";
		Workflow<WorkflowTestObject> wkf = new Workflow<WorkflowTestObject>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		Condition<WorkflowTestObject> c1 = new TestCondition();
		Process<WorkflowTestObject> process = new TestProcessOK();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process> conditionalProcesses = new HashMap<Status, Process>();
		conditionalProcesses.put(s2, process);
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, c1, conditionalRouting, conditionalProcesses, s2,
				s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		WorkflowObject res = null;
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		res = applyAction(obj, actionName);
		Assertions.assertEquals(s2, res.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	@DisplayName("Apply valid conditional action with process KO")
	void testApplyValidConditionalActionWithProcessKO() {
		String workflowName = "testApplyValidConditionalActionWithProcessKO";
		Workflow<WorkflowTestObject> wkf = new Workflow<>(workflowName);
		Status<WorkflowTestObject> s1 = new Status<>(wkf, "s1");
		Status<WorkflowTestObject> s2 = new Status<>(wkf, "s2");
		Status<WorkflowTestObject> s3 = new Status<>(wkf, "s3");
		Condition<WorkflowTestObject> c1 = new TestCondition();
		Process<WorkflowTestObject> process = new TestProcessKOCheckedException();
		Map<Integer, Status> conditionalRouting = new HashMap<Integer, Status>();
		conditionalRouting.put(1, s2);
		conditionalRouting.put(2, s3);
		Map<Status, Process> conditionalProcesses = new HashMap<Status, Process>();
		conditionalProcesses.put(s2, process);
		final String actionName = "a1";
		new ConditionalAction<WorkflowTestObject>(wkf, s1, actionName, c1, conditionalRouting, conditionalProcesses, s2,
				s3);
		WorkflowTestObject obj = new WorkflowTestObject();
		saveWorkflow(wkf);
		obj.setStatus(s1);
		obj.setWorkflow(workflowName);
		assertThrows(TradistaFlowBusinessException.class, () -> WorkflowManager.applyAction(obj, actionName));
		Assertions.assertNotEquals(s2, obj.getStatus());
		Assertions.assertNotEquals("Wkf", obj.getWorkflow());
	}

	private void saveWorkflow(Workflow<WorkflowTestObject> wkf) {
		try {
			WorkflowManager.saveWorkflow(wkf);
		} catch (TradistaFlowBusinessException tfbe) {
			Assertions.fail(tfbe);
		}
	}

	private Workflow<WorkflowTestObject> loadWorkflow(String workflowName) {
		Workflow<WorkflowTestObject> wkf = null;
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