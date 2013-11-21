package org.cmpe283.finalproject.messaging;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.cmpe283.finalproject.db.MongoDBUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultMessageListener implements MessageListener{

	
	public DefaultMessageListener(){
	}

	public void onMessage(Message msg) {
		if(msg instanceof MapMessage){
			MapMessage m = (MapMessage) msg;
			try {
				System.out.println( m.getString("CPU Cycles"));
				DBObject row = new BasicDBObject();
				row.put(MessageFields.VMName.toString(), m.getString(MessageFields.VMName.toString()));
				row.put(MessageFields.Timestamp.toString(), m.getString(MessageFields.Timestamp.toString()));
				row.put(MessageFields.powerstate.toString(), m.getString(MessageFields.powerstate.toString()));
				row.put(MessageFields.connectionstate.toString(), m.getString(MessageFields.connectionstate.toString()));
				row.put(MessageFields.host.toString(), m.getString(MessageFields.host.toString()));
				row.put(MessageFields.cpuusage.toString(), m.getString(MessageFields.cpuusage.toString()));
				row.put(MessageFields.memoryusage.toString(), m.getString(MessageFields.memoryusage.toString()));
				MongoDBUtil.saveCollection(row, "vmstatistics");
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			System.err.println("Expecting MapMessage");
	}
}
