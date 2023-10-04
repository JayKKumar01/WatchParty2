var player;
var interval;

function onYouTubeIframeAPIReady() {
//    createPlayer('R7aCOI4DuA0');
    createPlayer('b3-gdDnybIg'); // Default video ID
}

function createPlayer(videoId) {
    destroyPlayer(); // Call destroyPlayer before creating a new player

    var playerWidth = getWidth();
    var playerHeight = getHeight();

    player = new YT.Player('player', {
        height: playerHeight,
        width: playerWidth,
        videoId: videoId,
        playerVars: {
            'autoplay': 1,
            'controls': 0,
            'rel': 0,
            'modestbranding': 1,
            'fs': 0
        },
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

function getWidth() {
    return document.body.clientWidth;
}

function getHeight() {
    return document.body.clientHeight;
}

function onPlayerReady(event) {
    // This event fires when the player is ready to receive API calls.
    updateTotalDuration();
    // Start updating the current duration only if the player is playing
    interval = setInterval(function () {
        if (player.getPlayerState() === YT.PlayerState.PLAYING) {
            updateCurrentDuration();
        }
    }, 1000);
}

function onPlayerStateChange(event) {
    // This event fires whenever the player's state changes.
    console.log('Player state changed:', event.data);
}

function updateCurrentDuration() {
    // Get the current video duration only if the player is playing
    if (player.getPlayerState() === YT.PlayerState.PLAYING) {
        var currentDuration = player.getCurrentTime();
        Android.updateCurrentDuration(currentDuration);
    }
}

function formatTime(seconds) {
    var hours = Math.floor(seconds / 3600);
    var minutes = Math.floor((seconds % 3600) / 60);
    var remainingSeconds = Math.floor(seconds % 60);

    // Format the duration based on whether it's less than one hour
    if (hours > 0) {
        return pad(hours) + ':' + pad(minutes) + ':' + pad(remainingSeconds);
    } else {
        return pad(minutes) + ':' + pad(remainingSeconds);
    }
}

function pad(number) {
    // Pad the number with leading zero if it's less than 10
    return (number < 10 ? '0' : '') + number;
}


function destroyPlayer() {
    if (player) {
        player.destroy();
        clearInterval(interval); // Clear the interval when the player is destroyed
        console.log('Player destroyed.');
    }
}

// ... (your existing code)

function playPauseVideo(play) {
    if (play) {
        if (player.getPlayerState() !== YT.PlayerState.PLAYING) {
            player.playVideo();
        }
    } else {
        if (player.getPlayerState() === YT.PlayerState.PLAYING) {
            player.pauseVideo();
        }
    }
}

function updateTotalDuration() {
    var totalDuration = player.getDuration();
    Android.updateTotalDuration(totalDuration); // Send total duration to Android
}

// Inside script.js

function setCurrentDuration(currentDuration) {
    // Assuming you have a player variable defined in your script.js
    // For example: var player = new YT.Player(...);

    // Set the current duration of the player
    if (player && typeof player.seekTo === 'function') {
        player.seekTo(currentDuration, true);
    } else {
        console.error('Player not available or seekTo method not supported.');
    }
}

// Inside script.js

// Inside script.js

function setPlayerMute(isMute) {

    // Check the current mute status of the player and mute/unmute accordingly
    if (isMute && !player.isMuted()) {
        player.mute();
    } else if (!isMute && player.isMuted()) {
        player.unMute();
    }
}



