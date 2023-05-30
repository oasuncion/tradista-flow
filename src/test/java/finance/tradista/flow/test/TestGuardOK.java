package finance.tradista.flow.test;

import finance.tradista.flow.model.Guard;

public class TestGuardOK extends Guard {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestGuardOK() {
		setPredicate(obj -> {
			return true;
		});
	}

}