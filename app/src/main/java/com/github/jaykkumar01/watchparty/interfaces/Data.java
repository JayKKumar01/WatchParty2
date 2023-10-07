package com.github.jaykkumar01.watchparty.interfaces;

import android.media.AudioFormat;

public interface Data {
    String CHANNEL_ID = "watchparty_channel_id";
    String CHANNEL_NAME = "watchparty_channel_name";
    int NOTIFICATION_ID = 1;
    int REQUEST_CODE_MUTE = 100;
    int REQUEST_CODE_HANGUP = 200;
    int REQUEST_CODE_DEAFEN = 300;
    int REQUEST_CODE_CONTENT = 0;

    int SAMPLE_RATE = 22000;
    int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    int BYTES_PER_SAMPLE = 2; // 16-bit audio (2 bytes per sample)
    //    int BUFFER_SIZE_IN_BYTES = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    int BUFFER_SIZE_IN_BYTES = 8 * 1024;
    float MAX_AMP = 32768;
}
