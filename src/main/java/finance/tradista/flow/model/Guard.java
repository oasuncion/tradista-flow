package finance.tradista.flow.model;

import java.util.Objects;

import jakarta.persistence.Entity;
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
 * Class representing a guard.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Guard<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = 3817044564143531144L;

	@FunctionalInterface
	protected interface EXPredicate<T, E extends Exception> {
		Boolean test(T t) throws E;
	}

	@Transient
	private EXPredicate<X, Exception> predicate;

	public Guard() {
	}

	public final String getName() {
		return getClass().getSimpleName();
	}

	public void setPredicate(EXPredicate<X, Exception> predicate) {
		this.predicate = predicate;
	}

	public String toString() {
		return getName();
	}

	public boolean test(X obj) throws Exception {
		return predicate.test(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Guard<?> other = (Guard<?>) obj;
		return Objects.equals(getName(), other.getName());
	}

}