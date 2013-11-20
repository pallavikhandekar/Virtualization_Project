package org.cmpe283.finalproject.messaging;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class DefaultExceptionListener implements ExceptionListener{


	public void onException(JMSException e) {
		// TODO Auto-generated method stub
		e.printStackTrace();
	}

}
