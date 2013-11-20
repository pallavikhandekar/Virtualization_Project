package org.cmpe283.finalproject.messaging;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class Consumer {
	public static void main(String[] args) throws JMSException {
		Consumer c = new Consumer();
		c.startListening();
	}
	
	public void startListening() throws JMSException{
		Session session = JMSUtil.getSession();
		final MessageConsumer consumer = JMSUtil.getConsumer(session, "default");
		consumer.setMessageListener(new DefaultMessageListener());
	}
	
}
