package finance.tradista.flow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Action;
import finance.tradista.flow.model.ConditionalAction;
import finance.tradista.flow.model.SimpleAction;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.model.WorkflowObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

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
 * 
 * Facade for Tradista Flow services.
 * 
 * @author Olivier Asuncion
 */

public final class WorkflowManager {

	public static final EntityManagerFactory entityManagerFactory = Persistence
			.createEntityManagerFactory("tradista-flow-persistence-unit");

	/**
	 * Saves a workflow.
	 * 
	 * @param workflow the workflow to be saved
	 * @return the id of the saved workflow
	 * @throws TradistaFlowBusinessException if the workflow is not valid
	 */
	public static long saveWorkflow(Workflow workflow) throws TradistaFlowBusinessException {
		if (!isValid(workflow)) {
			throw new TradistaFlowBusinessException(
					String.format("The workflow %s is not valid. Please check.", workflow.getName()));
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		if (isJTA(entityManager)) {
			entityManager.joinTransaction();
		} else {
			entityManager.getTransaction().begin();
		}
		entityManager.persist(workflow);
		if (!isJTA(entityManager)) {
			entityManager.getTransaction().commit();
		}
		entityManager.close();
		return workflow.getId();
	}

	/**
	 * Checks whether the entityManager is a JTA or a RESOURCE_LOCAL one.
	 * 
	 * @param entityManager the entityManager to be checked
	 * @return true if the entityManager is JTA, false otherwise
	 */
	private static boolean isJTA(EntityManager entityManager) {
		try {
			entityManager.getTransaction();
		} catch (IllegalStateException ise) {
			return true;
		}
		return false;
	}

	/**
	 * Gets all workflows of the system.
	 * 
	 * @return all workflows of the system in a set
	 */
	public static Set<Workflow> getAllWorkflows() {
		Set<Workflow> workflows = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<Workflow> res = entityManager.createQuery("Select w from Workflow w", Workflow.class).getResultList();
		if (res != null) {
			workflows = new HashSet<>(res);
			workflows.forEach(w -> w.syncModel());
		}
		entityManager.close();
		return workflows;
	}

	/**
	 * Deletes a workflow given its id.
	 * 
	 * @param id the id of the workflow to be deleted
	 * @throws TradistaFlowBusinessException if the workflow doesn't exist
	 */
	public static void deleteWorkflow(long id) throws TradistaFlowBusinessException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Workflow wkf = entityManager.find(Workflow.class, id);
		if (wkf == null) {
			throw new TradistaFlowBusinessException(String.format("The workflow %s doesn't exist.", id));
		}
		if (isJTA(entityManager)) {
			entityManager.joinTransaction();
		} else {
			entityManager.getTransaction().begin();
		}
		entityManager.remove(wkf);
		if (!isJTA(entityManager)) {
			entityManager.getTransaction().commit();
		}
		entityManager.close();
	}

	/**
	 * Checks if the workflow is valid. A valid workflow is a connected graph, with
	 * a single start status and no cycle
	 * 
	 * @param workflow the workflow to be checked
	 * @return true if the workflow is valid, false otherwise
	 * @throws TradistaFlowBusinessException if the workflow is null
	 */
	public static boolean isValid(Workflow workflow) throws TradistaFlowBusinessException {
		if (workflow == null) {
			throw new TradistaFlowBusinessException("The workflow cannot be null.");
		}
		return workflow.isValid();
	}

	/**
	 * Applies an action to an object.
	 * 
	 * @param object the object to update
	 * @param action the action to apply
	 * @throws TradistaFlowBusinessException if the object or action is null, if the
	 *                                       object workflow doesn't exist or if the
	 *                                       action is invalid.
	 */
	public static void applyAction(WorkflowObject object, Action action) throws TradistaFlowBusinessException {
		if (object == null) {
			throw new TradistaFlowBusinessException("The object is null");
		}
		Workflow wkf = getWorkflowByName(object.getWorkflow());
		StringBuilder errMsg = new StringBuilder();
		if (action == null) {
			errMsg.append("The action is null");
		}
		if (wkf == null) {
			errMsg.append(String.format("The workflow %s doesn't exist.", object.getWorkflow()));
		}
		if (errMsg.length() > 0) {
			throw new TradistaFlowBusinessException(errMsg.toString());
		}
		if (!isValidAction(wkf, object.getStatus(), action)) {
			throw new TradistaFlowBusinessException(
					String.format("The action %s is not a valid one from status %s in workflow %s.", action,
							object.getStatus(), object.getWorkflow()));
		}
		if (action.getGuard() != null) {
			if (!action.getGuard().test(object)) {
				return;
			}
		}

		if (action instanceof SimpleAction) {
			// Perform process
			finance.tradista.flow.model.Process<WorkflowObject> process = ((SimpleAction) action).getProcess();
			if (process != null) {
				process.apply(object);
			}
			object.setStatus(wkf.getTargetStatus(action));
		} else {
			ConditionalAction condAction = ((ConditionalAction) action);
			int res = ((ConditionalAction) action).getCondition().apply(object);
			Status arrivalStatus = condAction.getArrivalStatusByResult(res);
			// Perform process
			Map<Status, finance.tradista.flow.model.Process<WorkflowObject>> condProcesses = condAction.getConditionalProcesses();
			if (condProcesses != null) {
				finance.tradista.flow.model.Process<WorkflowObject> process = condProcesses.get(arrivalStatus);
				if (process != null) {
					process.apply(object);
				}
			}
			object.setStatus(arrivalStatus);
		}
	}

	/**
	 * Gets a workflow given its name.
	 * 
	 * @param name the name of the workflow to search
	 * @return the found workflow
	 * @throws TradistaFlowBusinessException if the name is empty
	 */
	public static Workflow getWorkflowByName(String name) throws TradistaFlowBusinessException {
		if (StringUtils.isEmpty(name)) {
			throw new TradistaFlowBusinessException("The name is mandatory.");
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Workflow res = entityManager.createQuery("Select w from Workflow w where w.name = :name", Workflow.class)
				.setParameter("name", name).getSingleResult();
		if (res != null) {
			res.syncModel();
		}
		entityManager.close();
		return res;
	}

	/**
	 * Checks whether an action is valid from a given status.
	 * 
	 * @param workflow the concerned workflow
	 * @param status   the start status
	 * @param action   the action to be checked
	 * @return true if an action is valid from the given status
	 */
	private static boolean isValidAction(Workflow workflow, Status status, Action action) {
		Set<Action> availableActions = null;
		try {
			availableActions = workflow.getAvailableActionsFromStatus(status);
		} catch (IllegalArgumentException iae) {
			return false;
		}
		return (availableActions != null && availableActions.contains(action));
	}

}