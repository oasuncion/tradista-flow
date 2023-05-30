package finance.tradista.flow.test;

public class TestProcessOK extends finance.tradista.flow.model.Process {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5097243928471620584L;

	public TestProcessOK() {
		setTask(obj -> {
			((WorkflowTestObject)obj).setWorkflow("Wkf");
		});
	}

}