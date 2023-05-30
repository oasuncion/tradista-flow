package finance.tradista.flow.test;

import finance.tradista.flow.exception.TradistaFlowBusinessException;

public class TestProcessKO extends finance.tradista.flow.model.Process {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5097243928471620584L;

	public TestProcessKO() {
		setTask(obj -> {
			throw new TradistaFlowBusinessException("Process KO");
		});
	}

}