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
 * Exception used for Business errors in Tradista Flow.
 * 
 * @author Olivier Asuncion
 *
 */
public class TradistaFlowBusinessException extends Exception {

	private static final long serialVersionUID = 376484241701427322L;

	public TradistaFlowBusinessException(String msg) {
		super(msg);
	}

	public TradistaFlowBusinessException(Exception ex) {
		super(ex);
	}

}