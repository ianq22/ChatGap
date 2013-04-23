package rest;

import java.net.UnknownHostException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import mongo.MongoConnector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

@Path("/logout")
public class Logout {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void getMessage(@FormParam(value="userName") String userName, @FormParam(value="chatRoom") String chatRoom) throws UnknownHostException{
		MongoClient mongoClient = MongoConnector.getMongoClient();
		DB droidChat = mongoClient.getDB("droidChat");
		
		DBCollection chatRooms = droidChat.getCollection("rooms");
		DBCollection userCollection = droidChat.getCollection("users");
		
		//  Decrease the number of users
		BasicDBObject chatUpdateQuery = new BasicDBObject().append("$inc", new BasicDBObject()
		   												   .append("numUsers", -1));
		BasicDBObject searchChatRoom = new BasicDBObject().append("chatRoom", chatRoom);

		chatRooms.update(searchChatRoom, chatUpdateQuery);	
		
		//  Make it so the user is not chatting anymore
		BasicDBObject chattingFalseQuery = new BasicDBObject().append("$set", new BasicDBObject()
		  													  .append("name", null));
		BasicDBObject userDoc = new BasicDBObject().append("name", userName);
		
		userCollection.update(userDoc, chattingFalseQuery);

	}
}