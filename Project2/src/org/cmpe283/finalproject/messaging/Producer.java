package org.cmpe283.finalproject.messaging;

import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.cmpe283.finalproject.domain.VMStatisticsDAO;

public class Producer {

	public interface WriteFunction{
		public void write(VMStatisticsDAO S) throws JMSException;
	}
	private WriteFunction func;
	public void connect() throws JMSException {
		final Session session = JMSUtil.getSession();
		final MessageProducer producer = JMSUtil.getProducer(session, "default");
		func =  new WriteFunction() {
			public void write(VMStatisticsDAO s) throws JMSException {
				MapMessage msg = session.createMapMessage();
				msg.setString(MessageFields.VMName.toString(), s.getVmname());
				msg.setString(MessageFields.Timestamp.toString(), s.getTimestamp().toString());
				msg.setString(MessageFields. powerstate.toString(), s.getPowerstate());
				msg.setString(MessageFields. connectionstate.toString(), s.getConnectionstate());
				msg.setString(MessageFields. host.toString(), s.getHost());
				msg.setDouble(MessageFields. cpuusage.toString(), s.getCPUUsage());
				msg.setDouble(MessageFields. memoryusage.toString(), s.getMemoryUsage());
				producer.send(msg);
			}
		};
	}
	
	public void beginSendingMessages(VMStatisticsDAO vm){
		try{func.write(vm);}catch(JMSException e){e.printStackTrace();}
		
		/*Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					func.write(vm);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, 1);*/
		
	}
	
	public static void main(String[] args) throws JMSException {
		Producer p = new Producer();
		WriteFunction func = p.connect();
		p.beginSendingMessages(func);
	}
	
}
