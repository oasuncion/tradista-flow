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
 * Class representing a condition.
 * 
 * @author Olivier Asuncion
 *
 */
@Entity
public class Condition<X extends WorkflowObject> extends TradistaFlowObject {

	private static final long serialVersionUID = -8970069804519725007L;

	@FunctionalInterface
	protected interface EXFunction<T, E extends Exception> {
		Integer apply(T t) throws E;
	}

	@Transient
	private EXFunction<X, Exception> function;

	public Condition() {
	}

	public final String getName() {
		return getClass().getSimpleName();
	}

	public void setFunction(EXFunction<X, Exception> function) {
		this.function = function;
	}

	public String toString() {
		return getName();
	}

	public int apply(X obj) throws Exception {
		return function.apply(obj);
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
		Condition<?> other = (Condition<?>) obj;
		return Objects.equals(getName(), other.getName());
	}

}