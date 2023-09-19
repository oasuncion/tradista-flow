package finance.tradista.flow.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import finance.tradista.flow.model.TradistaFlowObject;

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