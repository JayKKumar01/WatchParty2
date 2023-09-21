package com.github.jaykkumar01.watchparty.models;

import com.github.jaykkumar01.watchparty.enums.RoomType;

import java.io.Serializable;

public class Room implements Serializable {
    UserModel user;
    String code;
    RoomType roomType;
    AgoraConfig agoraConfig;

    public Room() {
    }

    public Room(UserModel user, String code) {
        this.user = user;
        this.code = code;
    }

    public AgoraConfig getAgoraConfig() {
        return agoraConfig;
    }

    public void setAgoraConfig(AgoraConfig agoraConfig) {
        this.agoraConfig = agoraConfig;
    }

    public UserModel getUser() {
        return user;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
