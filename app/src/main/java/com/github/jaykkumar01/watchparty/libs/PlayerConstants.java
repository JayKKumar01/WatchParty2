package com.github.jaykkumar01.watchparty.libs;

import androidx.annotation.NonNull;

import java.util.Random;

public class PlayerConstants {

    public enum PlayerState {
        UNKNOWN, UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, VIDEO_CUED
    }

    public enum PlaybackQuality {
        SMALL("Small", "small"),
        MEDIUM("Medium", "medium"),
        LARGE("Large", "large"),
        HD720("HD 720p", "720p"),
        HD1080("HD 1080p", "1080p"),
        HIGH_RES("High Resolution", "highres");

        private final String displayName;
        private final String value;

        PlaybackQuality(String displayName, String value) {
            this.displayName = displayName;
            this.value = value;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }


    public enum PlayerError {
        UNKNOWN, INVALID_PARAMETER_IN_REQUEST, HTML_5_PLAYER, VIDEO_NOT_FOUND, VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
    }

    public enum PlaybackRate {
        UNKNOWN("Unknown"), RATE_0_25("0.25x"), RATE_0_5("0.5x"), RATE_1("1x (normal)"), RATE_1_25("1.25x"), RATE_1_5("1.5x"), RATE_2("2x");

        private final String displayString;

        PlaybackRate(String displayString) {
            this.displayString = displayString;
        }

        @Override
        public String toString() {
            return displayString;
        }

        public float toFloat() {
            switch (this) {
                case UNKNOWN:
                case RATE_1:
                    return 1f;
                case RATE_0_25:
                    return 0.25f;
                case RATE_0_5:
                    return 0.5f;
                case RATE_1_5:
                    return 1.5f;
                case RATE_2:
                    return 2f;
                default:
                    return 1f; // Default to 1 if unknown value is encountered
            }
        }
    }




}
