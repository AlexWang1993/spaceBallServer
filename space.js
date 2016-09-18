var SERVER_MSG_TYPE = {
	INIT : 1,
	BIRTH : 2,
	LIST_PLAYERS : 3,
	LIST_FOOD : 4,
	LEADER_BOARD : 5,
	ERROR : 99
};

var CLIENT_MSG_TYPE = {
	BIRTH : 1,
	MOVEMENT : 2,
	EAT_FOOD : 3,
	EAT_PLAYER : 4,
	DEATH : 99
};

var client = {

	// establish the communication channel over a websocket
	ws : new WebSocket("ws://104.196.140.95:8080/socket"),

	// appends logText to log text area
	appendLog : function(logText) {
	    var log = document.getElementById("log");
	    log.value = log.value + logText + "\n";
	},

	sendBirth : function(color, style, name) {
		this.sendToServer(getDelimitedFields([CLIENT_MSG_TYPE.BIRTH, color, style, name], ","));
	},

	sendMovement: function(x, y, z) {
		this.sendToServer(getDelimitedFields([CLIENT_MSG_TYPE.MOVEMENT,x, y, z], ","));
	},

	sendEatFood: function(food_id, new_size, new_score) {
		this.sendToServer(getDelimitedFields([CLIENT_MSG_TYPE.EAT_FOOD,food_id, new_size, new_score], ","));
	},

	sendEatPlayer: function(player_id, new_size, new_score) {
		this.sendToServer(getDelimitedFields([CLIENT_MSG_TYPE.EAT_PLAYER,player_id, new_size, new_score], ","));
	},

	sendDeath: function() {
		this.sendToServer(getDelimitedFields([CLIENT_MSG_TYPE.DEATH], ","));
	},
	 
	// sends msg to the server over websocket
	sendToServer : function(msg) {
		console.log(msg);
	    this.ws.send(msg);
	}

}


	 
// called when socket connection established
client.ws.onopen = function() {
    client.appendLog("Connected to stock service! Press 'Start' to generate player.")
};
 
// called when a message received from server
client.ws.onmessage = function (evt) {
	var fields = evt.data.split(",");
	var msg_type = parseInt(fields[0]);
	console.log(msg_type===SERVER_MSG_TYPE.ERROR);
	switch(msg_type) {
		case SERVER_MSG_TYPE.INIT:
			break;
		case SERVER_MSG_TYPE.BIRTH:
			var id = parseInt(fields[1]);
			var x = parseFloat(fields[2]);
			var y = parseFloat(fields[3]);
			var z = parseFloat(fields[4]);
			var size = parseFloat(fields[5]); 
			break;
		case SERVER_MSG_TYPE.LIST_PLAYERS:
			var players = {};
			for (i = 1; i < fields.length; i++) {
				var subfields = fields[i].split(";");
				var id = parseInt(subfields[0]);
				var x = parseFloat(subfields[1]);
				var y = parseFloat(subfields[2]);
				var z = parseFloat(subfields[3]);
				var color = parseInt(subfields[4]);
				var style = parseInt(subfields[5]);
				var size = parseFloat(subfields[6]);
				var name = subfields[7];
				players[id] = {
					x : x,
					y : y,
					z : z,
					color : 0xff0000,
					style : style,
					size : size,
					name : name
				}
			}
			data.players = players;
			break;
		case SERVER_MSG_TYPE.LIST_FOOD:
			var foods = {};
			for (i = 1; i < fields.length; i++) {
				var subfields = fields[i].split(";");
				var id = parseInt(subfields[0]);
				var x = parseFloat(subfields[1]);
				var y = parseFloat(subfields[2]);
				var z = parseFloat(subfields[3]);
				var color = parseInt(subfields[4]);
				var style = parseInt(subfields[5]);
				var size = parseFloat(subfields[6]);
				foods[id] = {
					x : x,
					y : y,
					z : z,
					color : randomColor(),
					style : style,
					size : size
				}
			}
			data.foods = foods;
			break;
		case SERVER_MSG_TYPE.LEADER_BOARD:
			for (i = 1; i < fields.length; i++) {
				var subfields = fields[i].split(";");
				var id = parseInt(subfields[0]);
				var name = subfields[1];
				var size = parseFloat(subfields[2]);
			}
			break;
		case SERVER_MSG_TYPE.ERROR:
			console.log("here");
			alert(fields[1]);
			break;
	}
	client.appendLog("msg_type:" + msg_type);
    client.appendLog(evt.data)
};
 
// called when socket connection closed
client.ws.onclose = function() {
    appendLog("Disconnected from stock service!")
};
 
// called in case of an error
client.ws.onerror = function(err) {
    console.log("ERROR!", err )
};

var getDelimitedFields = function(fields, delimiter) {
	var msg = "";
	for (i = 0 ; i < fields.length; i++) {
		msg = msg + fields[i];
		if (i < fields.length - 1) {
			msg = msg + delimiter;
		}
	}

	return msg;
}

