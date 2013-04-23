package rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class Test {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloWorld() {
	    return "Hello Ian!";
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String testPost(@FormParam("thing") String thing) {
		return "You posted: " + thing;
	}
}