package finance.tradista.flow.test;

import finance.tradista.flow.model.Condition;

public class TestCondition extends Condition {

	private static final long serialVersionUID = -4945718662266443702L;
	
	public TestCondition() {
		setFunction(obj -> {
			int ret = 0;
			try { 
				ret = Integer.parseInt(obj.getStatus().getName().substring(1, 2));
			} catch (NumberFormatException nfe) {
			}
			
			return ret;	
		});
	}

}