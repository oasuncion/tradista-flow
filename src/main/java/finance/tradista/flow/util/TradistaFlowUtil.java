package finance.tradista.flow.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

public final class TradistaFlowUtil {

	private static Function<Object, Object> clone = x -> (x instanceof TradistaFlowObject)
			? ((TradistaFlowObject) x).clone()
			: x;

	private static Function<Map.Entry<?, ?>, Object> cloneMapEntryKey = x -> x.getKey() instanceof TradistaFlowObject
			? ((TradistaFlowObject) x.getKey()).clone()
			: x.getKey();

	private static Function<Map.Entry<?, ?>, Object> cloneMapEntryValue = x -> x
			.getValue() instanceof TradistaFlowObject ? ((TradistaFlowObject) x.getValue()).clone() : x.getValue();

	public static Map<?, ?> deepCopy(Map<?, ?> originalMap) {
		if (originalMap == null) {
			return null;
		}
		Map<?, ?> copy = originalMap.entrySet().stream()
				.collect(Collectors.toMap(cloneMapEntryKey, cloneMapEntryValue));
		return copy;
	}

	public static List<?> deepCopy(List<? extends TradistaFlowObject> originalList) {
		if (originalList == null) {
			return null;
		}
		List<?> copy = originalList.stream().map(clone).collect(Collectors.toList());
		return copy;
	}

	public static Set<?> deepCopy(Set<? extends TradistaFlowObject> originalSet) {
		if (originalSet == null) {
			return null;
		}
		Set<?> copy = originalSet.stream().map(clone).collect(Collectors.toSet());
		return copy;
	}

	@SuppressWarnings("unchecked")
	public static <T extends TradistaFlowObject> T clone(T tradistaFlowObject) {
		if (tradistaFlowObject == null) {
			return null;
		}
		return (T) tradistaFlowObject.clone();
	}

}