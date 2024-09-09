package finance.tradista.flow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.exception.TradistaFlowTechnicalException;
import finance.tradista.flow.model.Action;
import finance.tradista.flow.model.ConditionalAction;
import finance.tradista.flow.model.Guard;
import finance.tradista.flow.model.SimpleAction;
import finance.tradista.flow.model.Status;
import finance.tradista.flow.model.Workflow;
import finance.tradista.flow.model.WorkflowObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
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

	private WorkflowManager() {
	}

	public static final EntityManagerFactory entityManagerFactory = Persistence
			.createEntityManagerFactory("tradista-flow-persistence-unit");

	/**
	 * Saves a workflow.
	 * 
	 * @param workflow the workflow to be saved
	 * @return the id of the saved workflow
	 * @throws TradistaFlowBusinessException if the workflow is not valid
	 */
	public static long saveWorkflow(Workflow<? extends WorkflowObject> workflow) throws TradistaFlowBusinessException {
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
	@SuppressWarnings("rawtypes")
	public static <X extends WorkflowObject> Set<Workflow<X>> getAllWorkflows() {
		Set<Workflow<X>> workflows = null;
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
	@SuppressWarnings("unchecked")
	public static void deleteWorkflow(long id) throws TradistaFlowBusinessException {
		try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			Workflow<? extends WorkflowObject> wkf = entityManager.find(Workflow.class, id);
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
		}
	}

	/**
	 * Checks if the workflow is valid. A valid workflow is a connected graph, with
	 * a single start status and no cycle
	 * 
	 * @param workflow the workflow to be checked
	 * @return true if the workflow is valid, false otherwise
	 * @throws TradistaFlowBusinessException if the workflow is null
	 */
	public static boolean isValid(Workflow<? extends WorkflowObject> workflow) throws TradistaFlowBusinessException {
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
	 * @throws TradistaFlowBusinessException  if the object is null, the action is
	 *                                        null or empty, if the object workflow
	 *                                        doesn't exist, if the action is
	 *                                        invalid or if a
	 *                                        condition/guard/process raised a
	 *                                        checked exception.
	 * @throws TradistaFlowTechnicalException if a condition/guard/process raised a
	 *                                        runtime exception.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <X extends WorkflowObject> X applyAction(X object, String action)
			throws TradistaFlowBusinessException {
		if (object == null) {
			throw new TradistaFlowBusinessException("The object is null");
		}
		Workflow<X> wkf = getWorkflowByName(object.getWorkflow());
		StringBuilder errMsg = new StringBuilder();
		X objectDeepCopy = null;
		if (StringUtils.isEmpty(action)) {
			errMsg.append("The action is null or empty.");
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
		try {
			Action<X> actionObject = wkf.getActionByDepartureStatusAndName(object.getStatus(), action);
			objectDeepCopy = (X) object.clone();
			Set<Guard> guards = actionObject.getGuards();
			if (!ObjectUtils.isEmpty(guards)) {
				for (Guard<X> guard : guards) {
					if (!guard.test(objectDeepCopy)) {
						return object;
					}
					// Reinitializing objectDeepCopy in case it has been modified by a guard
					objectDeepCopy = (X) object.clone();
				}
			}

			if (actionObject instanceof SimpleAction simpleAction) {
				// Perform process
				finance.tradista.flow.model.Process<WorkflowObject> process = simpleAction.getProcess();
				if (process != null) {
					process.apply(objectDeepCopy);
				}
				objectDeepCopy.setStatus(wkf.getTargetStatus(simpleAction));
			} else {
				ConditionalAction<X> condAction = ((ConditionalAction<X>) actionObject);
				guards = condAction.getGuardsByActionName(action);
				if (!ObjectUtils.isEmpty(guards)) {
					for (Guard<X> guard : guards) {
						if (!guard.test(objectDeepCopy)) {
							return object;
						}
						// Reinitializing objectDeepCopy in case it has been modified by a guard
						objectDeepCopy = (X) object.clone();
					}
				}
				int res = condAction.getCondition().apply(objectDeepCopy);
				Status<X> arrivalStatus = condAction.getArrivalStatusByResult(res);
				// Perform process
				Map<Status, finance.tradista.flow.model.Process> condProcesses = condAction.getConditionalProcesses();
				if (condProcesses != null) {
					finance.tradista.flow.model.Process<X> process = condProcesses.get(arrivalStatus);
					if (process != null) {
						process.apply(objectDeepCopy);
					}
				}
				objectDeepCopy.setStatus(arrivalStatus);
			}
		} catch (RuntimeException | CloneNotSupportedException ex) {
			throw new TradistaFlowTechnicalException(ex);
		} catch (Exception ex) {
			throw new TradistaFlowBusinessException(ex);
		}
		return objectDeepCopy;
	}

	/**
	 * Gets a workflow given its name.
	 * 
	 * @param name the name of the workflow to search
	 * @return the found workflow
	 * @throws TradistaFlowBusinessException if the name is empty
	 */
	@SuppressWarnings("unchecked")
	public static <X extends WorkflowObject> Workflow<X> getWorkflowByName(String name)
			throws TradistaFlowBusinessException {
		if (StringUtils.isEmpty(name)) {
			throw new TradistaFlowBusinessException("The name is mandatory.");
		}
		Workflow<X> res;
		try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
			res = entityManager.createQuery("Select w from Workflow w where w.name = :name", Workflow.class)
					.setParameter("name", name).getSingleResult();
			if (res != null) {
				res.syncModel();
			}
		} catch (NoResultException nre) {
			throw new TradistaFlowBusinessException(String.format("The workflow named %s doesn't exist.", name));
		}

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
	private static <X extends WorkflowObject> boolean isValidAction(Workflow<X> workflow, Status<X> status,
			String action) {
		Set<String> availableActions = null;
		try {
			availableActions = workflow.getAvailableActionsFromStatus(status);
		} catch (IllegalArgumentException iae) {
			return false;
		}
		return (availableActions != null && availableActions.contains(action));
	}

}