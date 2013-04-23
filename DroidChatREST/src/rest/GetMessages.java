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
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/getMessages")
public class GetMessages {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessage(@HeaderParam(value="userName") String userName, @HeaderParam(value="chatRoom") String chatRoom) throws UnknownHostException{
		Long userCount = (long) 0;
		DBCursor userCursor;
		DBCursor messagesCursor;
		
		MongoClient mongoClient = MongoConnector.getMongoClient();
		DB droidChat = mongoClient.getDB("droidChat");
		
		DBCollection userCollection = droidChat.getCollection("users");
		DBCollection messagesCollection = droidChat.getCollection("messages");
		
		//  Get current number of messages read
		userCursor = userCollection.find(new BasicDBObject("name", userName));
		DBObject user = userCursor.next();
		userCount = (Long) user.get("messagesRead");
		
		//  Find messages that are greater than the users read messages in the specified chat room
		BasicDBObject messageQuery = new BasicDBObject().append("messageNum", new BasicDBObject("$gt", userCount))
														.append("userName", new BasicDBObject("$ne", userName))  //  We don't want messages from the user, they've already seen them!
											   			.append("chatRoom", chatRoom);
		messagesCursor = messagesCollection.find(messageQuery);
		
		//  Create the messages query
		BasicDBObject findNumMessages = new BasicDBObject().append("chatRoom", chatRoom);
		Long increaseMessageRead = (long) messagesCollection.find(findNumMessages).size();
		
		//  Create the update objects
		BasicDBObject updateMessagesRead = new BasicDBObject().append("$set", new BasicDBObject()
															  .append("messagesRead",increaseMessageRead));
		BasicDBObject userDoc = new BasicDBObject().append("name", userName);
		
		userCollection.update(userDoc, updateMessagesRead);
		
		//  Return unread messages as a JSON array: { messages: <message array> }
		return JSON.serialize(messagesCursor);		
	}
}
