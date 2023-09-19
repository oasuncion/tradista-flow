package finance.tradista.flow.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.DirectedPseudograph;

import finance.tradista.flow.util.TradistaFlowUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

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
 * Class representing a workflow.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Workflow extends TradistaFlowObject {

	private static final long serialVersionUID = 3469347171038496805L;

	@Column(unique = true)
	private String name;

	private String description;

	@Transient
	private Graph<Status, Action> graph;

	@OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL)
	private Set<Action> actions;

	@OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL)
	private Set<Status> status;

	public Workflow() {
		actions = new HashSet<>();
		status = new HashSet<>();
		graph = new DirectedPseudograph<>(Action.class);
	}

	public Workflow(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("unchecked")
	public Set<Action> getActions() {
		return (Set<Action>) TradistaFlowUtil.deepCopy(actions);
	}

	public void setActions(Set<Action> actions) {
		graph.removeAllEdges(this.actions);
		this.actions = actions;
		if (actions != null) {
			for (Action action : actions) {
				if (action instanceof SimpleAction simpleAction) {
					graph.addEdge(action.getDepartureStatus(), simpleAction.getArrivalStatus(), action);
				} else {
					graph.addEdge(action.getDepartureStatus(), ((ConditionalAction) action).getChoicePseudoStatus(),
							action);
					for (SimpleAction condAction : ((ConditionalAction) action).getConditionalActions()) {
						graph.addEdge(((ConditionalAction) action).getChoicePseudoStatus(),
								condAction.getArrivalStatus(), condAction);
					}
				}
			}
		}
	}

	public void syncModel() {
		setStatus(status);
		setActions(actions);
	}

	@SuppressWarnings("unchecked")
	public Set<Status> getStatus() {
		return (Set<Status>) TradistaFlowUtil.deepCopy(status);
	}

	public void setStatus(Set<Status> status) {
		graph.removeAllVertices(this.status);
		this.status = status;
		if (status != null) {
			for (Status s : status) {
				graph.addVertex(s);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAction(Action action) {
		actions.add(action);
		action.setWorkflow(this);
		if (action instanceof SimpleAction simpleAction) {
			graph.addEdge(action.getDepartureStatus(), simpleAction.getArrivalStatus(), action);
		} else {
			graph.addEdge(action.getDepartureStatus(), ((ConditionalAction) action).getChoicePseudoStatus(), action);
			for (SimpleAction condAction : ((ConditionalAction) action).getConditionalActions()) {
				graph.addEdge(((ConditionalAction) action).getChoicePseudoStatus(), condAction.getArrivalStatus(),
						condAction);
			}
		}
	}

	public void addStatus(Status status) {
		this.status.add(status);
		graph.addVertex(status);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Workflow clone() {
		Workflow workflow = (Workflow) super.clone();
		workflow.status = (Set<Status>) TradistaFlowUtil.deepCopy(status);
		workflow.actions = (Set<Action>) TradistaFlowUtil.deepCopy(actions);
		return workflow;
	}

	@Transient
	public boolean isValid() {
		return GraphTests.isConnected(graph)
				&& graph.vertexSet().stream().filter(key -> graph.incomingEdgesOf(key).isEmpty()).count() == 1;
	}

	public boolean isInitialStatus(Status status) {
		return graph.inDegreeOf(status) == 0;
	}

	public boolean isFinalStatus(Status status) {
		return graph.outDegreeOf(status) == 0;
	}

	public Set<Action> getAvailableActionsFromStatus(Status status) {
		return graph.outgoingEdgesOf(status);
	}

	public Status getTargetStatus(Action action) {
		return graph.getEdgeTarget(action);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workflow other = (Workflow) obj;
		return Objects.equals(name, other.name);
	}

	public Status getInitialStatus() {
		Status status = null;
		if (this.status != null) {
			status = this.status.stream().filter(this::isInitialStatus).findFirst().get();
		}
		return status;
	}

	public Set<Status> getFinalStatus() {
		Set<Status> status = null;
		if (this.status != null) {
			status = this.status.stream().filter(this::isFinalStatus).collect(Collectors.toSet());
		}
		return status;
	}

}