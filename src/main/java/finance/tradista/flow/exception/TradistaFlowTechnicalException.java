package finance.tradista.flow.exception;

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
 * Exception used for Technical errors in Tradista Flow.
 * 
 * @author Olivier Asuncion
 *
 */
public class TradistaFlowTechnicalException extends RuntimeException {

	private static final long serialVersionUID = -3974129445297394798L;

	public TradistaFlowTechnicalException(String msg) {
		super(msg);
	}

	public TradistaFlowTechnicalException(Exception e) {
		super(e);
	}

}