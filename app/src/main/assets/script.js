var player;
var interval;

function onYouTubeIframeAPIReady() {
    Android.onReady();
//    createPlayer('R7aCOI4DuA0');
    //createPlayer('b3-gdDnybIg'); // Default video ID
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
            'controls': 1,
            'rel': 0,
            'modestbranding': 1,
            'fs': 1
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

function updatePlaybackState(id) {
    var isPlaying = player.getPlayerState() === YT.PlayerState.PLAYING;
    var currentPosition = player.getCurrentTime();
    Android.onPlaybackUpdate(id,isPlaying, currentPosition);
}

function updatePlayback(isPlaying, currentPosition){
    playPauseVideo(isPlaying);
    if(isPlaying){
        currentPosition++;
    }
    setCurrentDuration(currentPosition);
}

function onPlayerReady(event) {
    Android.onPlayerReady();
}

var pauseTimeout;

function onPlayerStateChange(event) {
    // Clear any previous pauseTimeout to prevent multiple pause events
    clearTimeout(pauseTimeout);

    // event.data holds the state of the player
    if (event.data == YT.PlayerState.PLAYING) {
        Android.onPlayPause(true,player.getCurrentTime());
    } else {
        // Pause event will be fired after 1 second delay
        pauseTimeout = setTimeout(function() {
            // Check the player state again to confirm it's still paused
            if (player.getPlayerState() === YT.PlayerState.PAUSED) {
                Android.onPlayPause(false,player.getCurrentTime());
            }
        }, 500);
    }
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
        player.playVideo();
        //if (player.getPlayerState() !== YT.PlayerState.PLAYING) {
            //player.playVideo();

        //}
    } else {
        player.pauseVideo();
//        if (player.getPlayerState() === YT.PlayerState.PLAYING) {
//            player.pauseVideo();
//        }
    }
}

function updateTotalDuration() {
    var totalDuration = player.getDuration();
    Android.updateTotalDuration(totalDuration); // Send total duration to Android
}

// Inside script.js

function setCurrentDuration(currentDuration) {
    player.seekTo(currentDuration, true);
}

// Inside script.js

function setPlayerMute(isMute) {

    // Check the current mute status of the player and mute/unmute accordingly
    if (isMute && !player.isMuted()) {
        player.mute();
    } else if (!isMute && player.isMuted()) {
        player.unMute();
    }
}

function setPlaybackRate(rate) {// Assuming you have a function to get the YouTube player instance
    player.setPlaybackRate(rate);
}

function setPlaybackQuality(quality) {
    player.setPlaybackQuality(quality);
}


function clean() {
    if (player.getPlayerState() === YT.PlayerState.PLAYING) {
        player.pauseVideo();
        setTimeout(function() {
            player.playVideo();
        }, 500);
    }
}






