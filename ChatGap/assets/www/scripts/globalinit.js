/* Initialize each page when it loads */

var serverURL = 

$(document).delegate("#chatRoomPage", 'pageinit', function(){
	initEnterChat();
});

$(document).delegate("#enterNamePage", 'pageinit', function(){
	initEnterName();
});

$(document).delegate("#chatPage", 'pageinit', function(){
	initChatPage();
});