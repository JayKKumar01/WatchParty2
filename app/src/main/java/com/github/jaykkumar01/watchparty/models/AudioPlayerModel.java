package com.github.jaykkumar01.watchparty.models;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.github.jaykkumar01.watchparty.interfaces.Data;

public class AudioPlayerModel implements Data{

    AudioTrack audioTrack;

    int offset;
    String id;

    public AudioPlayerModel(String id,long millis) {
        this.id = id;
        offset = (int) (System.currentTimeMillis() - millis);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AUDIO_FORMAT,
                BUFFER_SIZE_IN_BYTES,
                AudioTrack.MODE_STREAM);
        audioTrack.play();
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
