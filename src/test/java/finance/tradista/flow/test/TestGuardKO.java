package finance.tradista.flow.test;

import finance.tradista.flow.model.Guard;

public class TestGuardKO extends Guard {

	private static final long serialVersionUID = -4945718662266443702L;

	public TestGuardKO() {
		setPredicate(obj -> {
			return false;
		});
	}

}