package finance.tradista.flow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import finance.tradista.flow.exception.TradistaFlowBusinessException;
import finance.tradista.flow.model.Action;
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

public final class WorkflowManager {

	public static final EntityManagerFactory entityManagerFactory = Persistence
			.createEntityManagerFactory("tradista-flow-persistence-unit");

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

	private static boolean isJTA(EntityManager entityManager) {
		try {
			entityManager.getTransaction();
		} catch (IllegalStateException ise) {
			return true;
		}
		return false;
	}

	public static Set<Workflow> getAllWorkflows() {
		Set<Workflow> workflows = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<Workflow> res = entityManager.createQuery("Select w from Workflow w", Workflow.class).getResultList();
		if (res != null) {
			workflows = new HashSet<>(res);
			workflows.forEach(w -> w.syncGraph());
		}
		entityManager.close();
		return workflows;
	}

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

	public static boolean isValid(Workflow wkf) throws TradistaFlowBusinessException {
		if (wkf == null) {
			throw new TradistaFlowBusinessException("The workflow cannot be null.");
		}
		return wkf.isValid();
	}

	public static void applyAction(WorkflowObject object, Action action) throws TradistaFlowBusinessException {
		Workflow wkf = getWorkflowByName(object.getWorkflow());
		if (wkf == null) {
			throw new TradistaFlowBusinessException(
					String.format("The workflow %s doesn't exist.", object.getWorkflow()));
		}
		if (!isValidAction(wkf, object.getStatus(), action)) {
			throw new TradistaFlowBusinessException(
					String.format("The action %s is not a valid one from status %s in workflow %s.", action,
							object.getStatus(), object.getWorkflow()));
		}
		object.setStatus(wkf.getTargetStatus(action));
	}

	private static Workflow getWorkflowByName(String name) throws TradistaFlowBusinessException {
		if (StringUtils.isEmpty(name)) {
			throw new TradistaFlowBusinessException("The name is mandatory.");
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Workflow res = entityManager.createQuery("Select w from Workflow w where w.name = :name", Workflow.class)
				.setParameter("name", name).getSingleResult();
		if (res != null) {
			res.syncGraph();
		}
		entityManager.close();
		return res;
	}

	private static boolean isValidAction(Workflow workflow, Status status, Action action) {
		Set<Action> availableActions = null;
		try {
			availableActions = workflow.availableActionsFromStatus(status);
		} catch (IllegalArgumentException iae) {
			return false;
		}
		return (availableActions != null && availableActions.contains(action));
	}

}