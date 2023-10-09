let peer = null;
let connections = [];
let myId = null;

const playerTypes = ["Exo Player", "YouTube Player"];
let playerType = playerTypes[0]; // Default value is "Exo Player"

function setPlayerType(index) {
    if (index === 0 || index === 1) {
        playerType = playerTypes[index];
    } else {
        console.error("Invalid index. Please provide 0 for 'Exo Player' or 1 for 'YouTube Player'.");
    }
}


function init(userIdBytes) {
var userId = byteArrayToString(userIdBytes);
    peer = new Peer(userId, {
        port: 443,
        path: '/',
    });

    peer.on('open', () => {
        myId = peer.id;
        Android.onConnected(myId);
    });

    peer.on('connection', handleConnection);

    peer.on('close',function(){
        Android.onClose(peer.id);
    });
    peer.on('disconnected',function(){
        Android.send("user disconnected");
    })
}

function byteArrayToString(byteArray) {
    const decoder = new TextDecoder('utf-8');
    const utf8Text = decoder.decode(new Uint8Array(byteArray));
    return utf8Text;
}

function handleConnection(connection) {
    connections.push(connection); // Add the new connection to the list
    connection.on('data', handleData);
    //Android.send("Connected: " + connection.peer);
}

function connect(otherIdBytes) {
    var otherId = byteArrayToString(otherIdBytes);
    let conn = peer.connect(otherId, { reliable: true }); // Declare conn using 'let'

    conn.on('open', () => {
        handleConnection(conn);
    });
}

function sendMessage(name, message, millis) {
    var data = {
        type: 'message',
        id: myId,
        name: name,
        message: message,
        millis: millis
    };

    // Loop through all connections and send the JSON string to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendFile(bytes, read, millis, loudness) {
    var data = {
        type: 'file',
        id: myId,
        bytes: bytes,
        read: read,
        millis: millis,
        loudness: loudness
    };

    // Loop through all connections and send the file data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendSeekInfo(positionMs) {
    var data = {
        type: 'seekInfo',
        id: myId,
        positionMs: positionMs
    };

    // Loop through all connections and send the seek info data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendPlayPauseInfo(isPlaying) {
    var data = {
        type: 'playPauseInfo',
        id: myId,
        isPlaying: isPlaying
    };

    // Loop through all connections and send the play/pause info data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendPlaybackStateRequest(index) {
    setPlayerType(index);
    var data = {
        type: 'playbackStateRequest',
        id: myId,
        playerType,playerType
    };

    // Loop through all connections and send the playback state request data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendPlaybackState(idBytes, isPlaying, positionMs) {
    var id = byteArrayToString(idBytes);
    var data = {
        type: 'playbackState',
        id: myId,
        isPlaying: isPlaying,
        positionMs: positionMs
    };

    // Find the connection with the specified id and send the playback state data
    var connectionToTarget = connections.find(connection => connection.peer === id);
    if (connectionToTarget && connectionToTarget.open) {
        connectionToTarget.send(data);
    }
}

function sendActivityStopInfo(name, millis) {
    var data = {
        type: 'activityStopInfo',
        id: myId,
        name: name,
        millis: millis
    };

    // Loop through all connections and send the activity stop info data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function sendJoinedPartyAgain(name, millis) {
    var data = {
        type: 'joinedPartyAgain',
        id: myId,
        name: name,
        millis: millis
    };

    // Loop through all connections and send the 'joinedPartyAgain' info to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}

function handleData(data) {
    if (data.type === 'message') {
        Android.showMessage(data.id, data.name, data.message, data.millis);
    }
    else if (data.type === 'file') {
        Android.showFile(data.id, data.bytes, data.read, data.millis, data.loudness);
    }
    else if (data.type === 'seekInfo') {
        if (playerType === "Exo Player") {
            ExoPlayer.handleSeekInfo(data.id, data.positionMs);
        } else if (playerType === "YouTube Player") {
            YouTubePlayer.handleSeekInfo(data.id, data.positionMs);
        }
    }
    else if (data.type === 'playPauseInfo') {
        if (playerType === "Exo Player") {
            ExoPlayer.handlePlayPauseInfo(data.id, data.isPlaying);
        } else if (playerType === "YouTube Player") {
            YouTubePlayer.handlePlayPauseInfo(data.id, data.isPlaying);
        }
    }
else if (data.type === 'playbackStateRequest') {
if(data.playerType === playerType){
if (playerType === "Exo Player") {
            ExoPlayer.handlePlaybackStateRequest(data.id);
        } else if (playerType === "YouTube Player") {
            YouTubePlayer.handlePlaybackStateRequest(data.id);
        }
}

    }
    else if (data.type === 'playbackState') {
        if (playerType === "Exo Player") {
            ExoPlayer.handlePlaybackState(data.id, data.isPlaying, data.positionMs);
        } else if (playerType === "YouTube Player") {
            YouTubePlayer.handlePlaybackState(data.id, data.isPlaying, data.positionMs);
        }
    }
else if (data.type === 'activityStopInfo') {
        if (playerType === "Exo Player") {
            ExoPlayer.handleActivityStop(data.id, data.name, data.millis);
        } else if (playerType === "YouTube Player") {
            YouTubePlayer.handleActivityStop(data.id, data.name, data.millis);
        }
    } else if (data.type === 'joinedPartyAgain') {
        Android.handleJoinedPartyAgain(data.id, data.name, data.millis);
    }
}