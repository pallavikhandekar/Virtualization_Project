package org.cmpe283.finalproject.db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBUtil {
	static MongoClient client = null;
	static DB db = null;

	private static DB getDb() throws UnknownHostException {
		if (db != null)
			return db;
		if(client!=null)
			client = new MongoClient();
		db = client.getDB("vmstats");
		return db;
	}
	public static void saveCollection(DBObject row,String collectionname){
		DBCollection tbl;
		try {
			tbl = getDb().getCollection(collectionname);
			tbl.save(row);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
