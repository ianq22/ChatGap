package rest;

import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import mongo.MongoConnector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/usersChatting")
public class UsersChatting {
	
	/**
	 * Get the number of users chatting in a specified chat room
	 * 
	 * @return chat room document as JSON
	 * @throws UnknownHostException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessage(@HeaderParam(value="chatRoom") String chatRoom) throws UnknownHostException{
		MongoClient mongoClient = MongoConnector.getMongoClient();
		DB droidChat = mongoClient.getDB("droidChat");
		
		DBCollection chatRooms = droidChat.getCollection("rooms");
		BasicDBObject findRoomQuery = new BasicDBObject().append("chatRoom", chatRoom);		
		DBCursor queryResult = chatRooms.find(findRoomQuery);
				
		return JSON.serialize(queryResult);  //  Return it as serialized JSON
	}
}
