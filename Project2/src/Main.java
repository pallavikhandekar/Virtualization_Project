import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.*;



public class Main {
	static final String MONGO_HOST = "localhost";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			DB db = mongoClient.getDB( "VMStats" );
			Set<String> colls = db.getCollectionNames();

			for (String s : colls) {
			    System.out.println(s);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
