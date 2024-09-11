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

/********************************************************************************
 * Copyright (c) 2023 Olivier Asuncion
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

/**
 * Class representing a workflow.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Workflow<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = 3469347171038496805L;

	@Column(unique = true)
	private String name;

	private String description;

	@SuppressWarnings("rawtypes")
	@Transient
	private Graph<Status, Action> graph;

	@SuppressWarnings("rawtypes")
	@OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL)
	private Set<Action> actions;

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Action> getActions() {
		return (Set<Action>) TradistaFlowUtil.deepCopy(actions);
	}

	@SuppressWarnings({ "unchecked"})
	public void setActions(@SuppressWarnings("rawtypes") Set<Action> actions) {
		graph.removeAllEdges(this.actions);
		this.actions = actions;
		if (actions != null) {
			for (Action<X> action : actions) {
				if (action instanceof SimpleAction<X> simpleAction) {
					graph.addEdge(simpleAction.getDepartureStatus(), simpleAction.getArrivalStatus(), simpleAction);
				} else {
					for (SimpleAction<X> condAction : ((ConditionalAction<X>) action).getConditionalActions()) {
						graph.addEdge(condAction.getDepartureStatus(), condAction.getArrivalStatus(), condAction);
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
	public Set<Status<X>> getStatus() {
		return (Set<Status<X>>) TradistaFlowUtil.deepCopy(status);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setStatus(Set<Status> status) {
		graph.removeAllVertices(this.status);
		this.status = status;
		if (status != null) {
			for (Status<X> s : status) {
				graph.addVertex(s);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public void addAction(Action<X> action) {
		if (action instanceof ConditionalAction<X>
				|| ((SimpleAction<X>) action).getArrivalStatus() != null && !action.isConnectedToPseudoStatus()) {
			actions.add(action);
			action.setWorkflow(this);
		}
		if (action instanceof SimpleAction<X> simpleAction) {
			graph.addEdge(simpleAction.getDepartureStatus(), simpleAction.getArrivalStatus(), simpleAction);
		} else {
			for (SimpleAction<X> condAction : ((ConditionalAction<X>) action).getConditionalActions()) {
				graph.addEdge(condAction.getDepartureStatus(), condAction.getArrivalStatus(), condAction);
			}
		}
	}

	public void addStatus(Status<X> status) {
		this.status.add(status);
		graph.addVertex(status);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Workflow<X> clone() {
		Workflow<X> workflow = (Workflow<X>) super.clone();
		workflow.status = (Set<Status>) TradistaFlowUtil.deepCopy(status);
		workflow.actions = (Set<Action>) TradistaFlowUtil.deepCopy(actions);
		return workflow;
	}

	@Transient
	public boolean isValid() {
		return GraphTests.isConnected(graph)
				&& graph.vertexSet().stream().filter(key -> graph.incomingEdgesOf(key).isEmpty()).count() == 1;
	}

	public boolean isInitialStatus(Status<X> status) {
		return graph.inDegreeOf(status) == 0;
	}

	public boolean isFinalStatus(Status<X> status) {
		return graph.outDegreeOf(status) == 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<String> getAvailableActionsFromStatus(Status<X> status) {
		Set<Action> actions = null;
		Set<String> actionNames = null;
		if (this.actions != null) {
			actions = this.actions.stream().filter(a -> a.isDepartureStatus(status)).collect(Collectors.toSet());
		}
		if (actions != null) {
			for (Action<X> a : actions) {
				if (actionNames == null) {
					actionNames = new HashSet<>();
				}
				if (a instanceof ConditionalAction<X> condAction) {
					Set<String> aNames = condAction.getConditionalActions().stream()
							.filter(action -> action.isDepartureStatus(status)).map(Action::getName)
							.collect(Collectors.toSet());
					actionNames.addAll(aNames);
				} else {
					actionNames.add(a.getName());
				}
			}
		}
		return actionNames;
	}

	@SuppressWarnings("unchecked")
	public Status<X> getTargetStatus(SimpleAction<X> action) {
		return TradistaFlowUtil.clone(graph.getEdgeTarget(action));
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workflow<X> other = (Workflow<X>) obj;
		return Objects.equals(name, other.name);
	}

	@SuppressWarnings("unchecked")
	public Status<X> getInitialStatus() {
		Status<X> status = null;
		if (this.status != null) {
			status = TradistaFlowUtil.clone(this.status.stream().filter(this::isInitialStatus).findFirst().get());
		}
		return status;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Status> getFinalStatus() {
		Set<Status> status = null;
		if (this.status != null) {
			status = (Set<Status>) TradistaFlowUtil
					.deepCopy(this.status.stream().filter(this::isFinalStatus).collect(Collectors.toSet()));
		}
		return status;
	}

	/**
	 * Gets an Action object by its departure status and name.
	 * 
	 * @param status     the status from where actions are searched
	 * @param actionName the name of the action to be searched
	 * @return an Action object.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Action<X> getActionByDepartureStatusAndName(Status<X> status, String actionName) {
		Set<Action> actions = null;
		if (this.actions != null) {
			actions = this.actions.stream().filter(a -> a.isDepartureStatus(status)).collect(Collectors.toSet());
		}
		if (actions != null) {
			for (Action<X> a : actions) {
				if (a instanceof ConditionalAction<X> condAction) {
					boolean exists = condAction.getConditionalActions().stream()
							.filter(act -> act.isDepartureStatus(status) && act.getName().equals(actionName))
							.count() > 0;
					if (exists) {
						return a.clone();
					}
				} else {
					if (a.getName().equals(actionName)) {
						return a.clone();
					}
				}
			}
		}
		return null;
	}

}