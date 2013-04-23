package rest;

import java.net.UnknownHostException;
import java.util.Date;

import javax.ws.rs.Consumes;
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
import com.mongodb.util.JSON;
import com.sun.jersey.multipart.FormDataParam;

@Path("/createUser")
public class CreateUser {
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String createUser(@FormParam(value="userName") String userName, @FormParam(value="chatRoom") String chatRoom) throws UnknownHostException {
		Date date = new Date();
		
		MongoClient mongoClient = MongoConnector.getMongoClient();
		DB droidChat = mongoClient.getDB("droidChat");
		
		DBCollection userCollection = droidChat.getCollection("users");
		//  Get number of messages so far so we know what messages to load first
		DBCollection messages = droidChat.getCollection("messages");
		DBCollection chatRooms = droidChat.getCollection("rooms");
		
		if(!(chatRooms.find(new BasicDBObject().append("chatRoom", chatRoom)).hasNext())){
			chatRooms.insert(new BasicDBObject().append("chatRoom", chatRoom).append("numUsers", 1));
		}
		else{
			BasicDBObject chatUpdateQuery = new BasicDBObject().append("$inc", new BasicDBObject()
			  												   .append("numUsers", 1));
			BasicDBObject searchChatRoom = new BasicDBObject().append("chatRoom", chatRoom);
			
			chatRooms.update(searchChatRoom, chatUpdateQuery);
		}
						
		//  If user name is in database return an error message
		if(userCollection.find(new BasicDBObject().append("name", userName)).hasNext()){
			return JSON.serialize(new BasicDBObject("success", false)
							 				.append("errorMessage", "Entered name is not available."));
		}
		else{
			//  If name is free create new user
			BasicDBObject newUser = new BasicDBObject("name", userName)
											  .append("date", date)
											  .append("chatting", true)
											  .append("messagesRead", messages.getCount())
											  .append("chatRoom", chatRoom);

			//  Insert new user into DB
			userCollection.insert(newUser);
						
			//  Return JSON indicating a successful insertion and the data that was added
			return JSON.serialize(new BasicDBObject("success", true)
											.append("dataAdded", newUser));
		}
	}
}
