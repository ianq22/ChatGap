package rest;

import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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

@Path("/sendMessage")
public class SendMessage {
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendMessage(@FormParam(value="newMessage") String message, @FormParam(value="chatRoom") String chatRoom, @FormParam(value="userName") String userName) throws UnknownHostException {
		MongoClient mongoClient = MongoConnector.getMongoClient();
		DB droidChat = mongoClient.getDB("droidChat");
		
		/* Error handling. Returns success = false and an error message if applicable */
		if(message == null || message.equals("")){
			return JSON.serialize(new BasicDBObject("success", false));  //  If no input is entered do nothing
		}
		if(message.length() > 140){ // 140 seems to be the standard length for ADD messages these days
			return JSON.serialize(new BasicDBObject("success", false)
										    .append("errorMessage", "Message exceeds length of 140 characters"));
		}
				
		/* We've determined the message is not null and is the proper length. 
		   Add it to the database and assign it a PK of the current number of messages++ */
		DBCollection messages = droidChat.getCollection("messages");
		BasicDBObject findNumMessages = new BasicDBObject().append("chatRoom", chatRoom);  //  Get all messages in a chatRoom		
		
		messages.insert(new BasicDBObject("userName", userName)
							      .append("message", message)
							      .append("chatRoom", chatRoom)
							      .append("messageNum", messages.find(findNumMessages).size() + 1));  //  Nth + 1 message in the table
				
		/*  message successfully added! */
		return JSON.serialize(new BasicDBObject("success", true));
	}
}
