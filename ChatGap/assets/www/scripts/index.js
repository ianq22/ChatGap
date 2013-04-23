/**
 * index.js script
 * 
 * ChatGap is designed around a single-page design. Therefore, all of the JS is in this here handy dandy script.
 * When I get lazy I code javascript that makes douglas crockford cry
 * author: @ianq
 */

/* Global Vars because I can */
var chatRoom;
var userName;
var messagesRead;

/* Init stuff for each page when it loads */
function initEnterChat(){
	$('#errorCR').hide();
	$("#chatRoom").val("").focus();
	
	$("#roomNextButton").on("click", function(){
		$('#errorCR').hide();
		
		if($("#chatRoom").val() !== null && $("#chatRoom").val() !== "" && $("#chatRoom").val() !== undefined){
			chatRoom = $("#chatRoom").val();
			$.mobile.changePage("#enterNamePage", {
				  transition: "slide"
			});
		}
		else{
			insertErrorMessage("Enter a topic you'd like to discuss!", "errorCR")
		}
	});
	
	$("#getChatting").on("click", function(){
		 //  We won't use the chatRoom var since the user may be checking to see how many people are in a chat room first
		usersChattingGET($("#chatRoom").val());
	});
	
	function usersChattingGET(chatRoom){
		$.ajax({
			url: "http://localhost:8080/DroidChatREST/rest/usersChatting",  //  hardcoded url lol
	        type: 'GET',  //  literally "get"ting the number of users chatting
	        beforeSend:
	        	function(header){
	        		header.setRequestHeader("chatRoom", chatRoom);
	        	},
	        dataType: 'json',
	        success:
	        	function(serverReached){
	        		if(serverReached[0] !== undefined){
	        			$("#numUsersText").html("The Number of Users Chatting in \"" + chatRoom + "\" is: " + serverReached[serverReached.length - 1].numUsers)
	        		}
	        		else{
	        			$("#numUsersText").html("The Number of Users Chatting in \"" + chatRoom + "\" is: 0")
	        		}
	        	},
	        error:
	        	function(error){
	        		console.log("Couldn't reach the server herp derp");  //  sometimes i amuse myself, other times i piss off others
	        	}
		});
	}
}

function initEnterName(){
	$('#errorEN').hide();
	$("#userName").val("").focus()
	
	$("#backToWelcome").on("click", function(){
		$.mobile.changePage("#chatRoomPage", {
			  transition: "slide",
			  reverse: true
		});
	});
	
	$("#chatTime").on("click", function(){
		userName = $("#userName").val();
		$('#errorEN').hide();
		createUserPOST();
	});
	
	function createUserPOST(){
		$.ajax({
			url: "http://localhost:8080/DroidChatREST/rest/createUser",  //  We're doing this locally for now
	        type: 'POST',
	        dataType: 'json',
	        data: "userName=" + userName + "&chatRoom=" + chatRoom,
	        success:
	        	function(serverReached){
	        		if(serverReached.success){
	        			$.mobile.changePage("#chatPage", {
	        				  transition: "slide"
	        			});
	        		}
	        		else{
	        			insertErrorMessage("Entered name is not available", "errorEN");
	        		}
	        	},
	        error:
	        	function(error){
	        		console.log("Couldn't reach the server herp derp");
	        	}
		});
	}
}

function initChatPage(){	
	$("#chatWindow").html($("#chatWindow").html() + '<p>Now chatting in: ' + chatRoom + '</p>');
	$("#chatBox").focus();
	
	$("#exitButton").on("click", function(){
		logoutPOST();
	});
	
	$("#chatBox").bind("enterKey", function(e){
		 sendMessagePOST();
	});
	
	$("#chatBox").keyup(function(e){
		if(e.keyCode == 13){
		  $(this).trigger("enterKey");
		}
	});
	
	function getMessages() {
		getMessagesGET(userName);
	    setTimeout(getMessages, 2500);
	}
	
	getMessages();
	
	function getMessagesGET(){
		if(userName !== null){
			$.ajax({
				url: "http://localhost:8080/DroidChatREST/rest/getMessages",  //  We're doing this locally for now
		        type: 'GET',
		        beforeSend:
		        	function(header){
		        		header.setRequestHeader("userName", userName);
		        		header.setRequestHeader("chatRoom", chatRoom);
		        	},
		        dataType: 'json',
		        success:
		        	function(serverReached){
		        		for(var i = 0; i < serverReached.length; i++){
		        			$("#chatWindow").html($("#chatWindow").html() + '<p class="otherMessage">' + serverReached[i].userName + ': ' + serverReached[i].message + '</p>');
		        		}
		    			$("#chatWindow").scrollTop($("#chatWindow").height()); // Scroll to bottom when new messages are added
		        	},
		        error:
		        	function(error){
		        		console.log("Couldn't reach the server herp derp");
		        	}
			});
		}
	}
	
	function sendMessagePOST(){
		$.ajax({
			url: "http://localhost:8080/DroidChatREST/rest/sendMessage",  //  We're doing this locally for now
	        type: 'POST',
	        dataType: 'json',
	        data: "newMessage=" + $("#chatBox").val() + "&chatRoom=" + chatRoom + "&userName=" + userName,
	        success:
	        	function(serverReached){
	        	 	var newMessage = $("#chatBox").val()
	        	
	        		if(serverReached.success){
	        			$("#chatWindow").html($("#chatWindow").html() + '<p class="userMessage">' + userName + ': ' + newMessage + '</p>');
	        			$("#chatWindow").scrollTop($("#chatWindow").height()); // Scroll to bottom when new messages are added
	        		}
	        		else if(serverReached.errorMessage !== undefined){
	        			$("#chatWindow").html($("#chatWindow").html() + '<p>' + serverReached.errorMessage + '</p>');
	        		}
	        		else{
	        			$("#chatWindow").html($("#chatWindow").html() + '<p>You have to type something in!</p>');
	        		}
	        		
	        		$("#chatBox").val("");
	        	},
	        error:
	        	function(error){
	        		console.log("Couldn't reach the server herp derp");
	        	}
		});
	}
	
	function logoutPOST(){
		$.ajax({
			url: "http://localhost:8080/DroidChatREST/rest/logout",  //  We're doing this locally for now
	        type: 'POST',
	        dataType: 'json',
	        data: "userName=" + userName + "&chatRoom=" + chatRoom,
	        success:
	        	function(serverReached){
	        		userName = null;
	        		
	        		$.mobile.changePage("#chatRoomPage", {
	      			  transition: "slide",
	      			  reverse: true
	        		});
	        	},
	        error:
	        	function(error){
	        		console.log("Couldn't reach the server herp derp");
	        	}
		});
	}
}

/* Global Functions */
function insertErrorMessage(message, idName){
	$('#' + idName).text(message).show();
}