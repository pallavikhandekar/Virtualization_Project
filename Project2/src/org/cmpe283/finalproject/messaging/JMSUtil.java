/**
 * 
 */
package org.cmpe283.finalproject.messaging;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl;
import org.apache.qpid.amqp_1_0.jms.impl.QueueImpl;
import org.apache.qpid.amqp_1_0.jms.impl.TopicImpl;

/**
 * @author Administrator
 * 
 */
public class JMSUtil {
	private static Connection con = null;
	

	private static Connection getConnection() throws JMSException {
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(
				MessagingConfig.ACTIVEMQ_HOST, MessagingConfig.ACTIVEMQ_PORT,
				MessagingConfig.ACTIVEMQ_USER,
				MessagingConfig.ACTIVEMQ_PASSWORD);
		Connection connection = factory.createConnection(
				MessagingConfig.ACTIVEMQ_USER,
				MessagingConfig.ACTIVEMQ_PASSWORD);
		connection.start();
		connection.setExceptionListener((ExceptionListener) new DefaultExceptionListener());
		return connection;
	}

	public static Session getSession() throws JMSException {
		if (con == null)
			con = getConnection();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		return session;
	}
	
	public static MessageProducer getProducer(Session session, String queueName) throws JMSException{
		Destination dest = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		return producer;
	}
	
	public static MessageConsumer getConsumer(Session session, String queueName) throws JMSException{
		Destination dest = session.createQueue(queueName);
		return session.createConsumer(dest);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
