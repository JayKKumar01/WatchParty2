let peer = null;
let connections = [];
let myId = null;

function init(userId) {
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

function handleConnection(connection) {
    connections.push(connection); // Add the new connection to the list
    connection.on('data', handleData);
    Android.sendToast("Connected: " + connection.peer);
}

function connect(otherId) {
    let conn = peer.connect(otherId, { reliable: true }); // Declare conn using 'let'

    conn.on('open', () => {
        handleConnection(conn);
    });
    
}


function handleData(data) {
    Android.play(data.id, data.bytes, data.read, data.millis, data.name, data.message); // Process the 'data' parameter using Android.play
}


function sendFile(bytes, read, millis, name, message) {
    var data = {
        id: myId,
        bytes: bytes,
        read: read,
        millis: millis,
        name: name,
        message: message
    };

    // Loop through all connections and send data to each one
    for (const connection of connections) {
        if (connection && connection.open) {
            connection.send(data);
        }
    }
}
