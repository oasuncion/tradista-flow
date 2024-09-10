package finance.tradista.flow.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import finance.tradista.flow.model.TradistaFlowObject;

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
 * An general Util class for the project.
 * 
 * @author Olivier Asuncion
 *
 */
public final class TradistaFlowUtil {

	private TradistaFlowUtil() {
	}

	private static UnaryOperator<TradistaFlowObject> clone = x -> x.clone();

	private static Function<Map.Entry<?, ?>, Object> cloneMapEntryKey = x -> x
			.getKey() instanceof TradistaFlowObject tradistaFlowObject ? tradistaFlowObject.clone() : x.getKey();

	private static Function<Map.Entry<?, ?>, Object> cloneMapEntryValue = x -> x
			.getValue() instanceof TradistaFlowObject tradistaFlowObject ? tradistaFlowObject.clone() : x.getValue();

	/**
	 * Creates a deep copy of a map.
	 * 
	 * @param originalMap the map to copy
	 * @return a deep copy of the map
	 */
	public static Map<?, ?> deepCopy(Map<?, ?> originalMap) {
		if (originalMap == null) {
			return null;
		}
		return originalMap.entrySet().stream().collect(Collectors.toMap(cloneMapEntryKey, cloneMapEntryValue));
	}

	/**
	 * Creates a deep copy of a TradistaFlowObject list.
	 * 
	 * @param originalList the list to copy
	 * @return a deep copy of the list
	 */
	public static List<? extends TradistaFlowObject> deepCopy(List<? extends TradistaFlowObject> originalList) {
		if (originalList == null) {
			return null;
		}
		return originalList.stream().map(clone).toList();
	}

	/**
	 * Creates a deep copy of a TradistaFlowObject set.
	 * 
	 * @param originalSet the set to copy
	 * @return a deep copy of the set
	 */
	public static Set<? extends TradistaFlowObject> deepCopy(Set<? extends TradistaFlowObject> originalSet) {
		if (originalSet == null) {
			return null;
		}
		return originalSet.stream().map(clone).collect(Collectors.toSet());
	}

	/**
	 * Clones a TradistaFlow object
	 * 
	 * @param tradistaFlowObject the TradistaFlow object to clone
	 * @return a clone of the TradistaFlow object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TradistaFlowObject> T clone(T tradistaFlowObject) {
		if (tradistaFlowObject == null) {
			return null;
		}
		return (T) tradistaFlowObject.clone();
	}

}