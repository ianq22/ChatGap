package mongo;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

/**
 * MongoConnector is a driver for MongoClient to be used by the REST calls. The object is intended to be used
 * as a singleton and uses the static factory method in order to enforce this property.
 * 
 * @author ianq
 */
public class MongoConnector {
	private static MongoClient mongoClient = null;
	private static DB droidChat = null;
	
	//  Private so you can't instantiate it
	private MongoConnector() {}
	
	/**
	 * Creates MongoClient, connects to specified server and port number
	 * 
	 * @throws UnknownHostException
	 */
	public static MongoClient getMongoClient(String server, int portNumber) throws UnknownHostException {
		if(mongoClient == null){
			mongoClient = new MongoClient(server, portNumber);
		}
		
		return mongoClient;
	}
	
	/**
	 * @returns a mongoClient that connects to localhost:27017
	 */
	public static MongoClient getMongoClient() throws UnknownHostException {
		return getMongoClient("localhost", 27017);
	}
	
	/**
	 * @return DB connected to specified database name
	 */
	public static DB getDB(String dbName) {
		if(droidChat == null) {
			droidChat = mongoClient.getDB("droidChat");
		}
		
		return droidChat;
	}
	
	/**
	 * @return default DB connected to droidChat
	 */
	public static DB getDB() {
		return getDB("droidChat");
	}
	
	public static void closeConnection() {
		mongoClient.close();
	}
}
