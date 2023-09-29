package com.github.jaykkumar01.watchparty.models;

import com.github.jaykkumar01.watchparty.enums.RoomType;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    UserModel user;
    String code;
    RoomType roomType;
    boolean peerConnected;
    List<UserModel> userList;


    public Room() {
    }

    public Room(UserModel user, String code) {
        this.user = user;
        this.code = code;
    }

    public List<UserModel> getUserList() {
        return userList;
    }

    public void setUserList(List<UserModel> userList) {
        this.userList = userList;
    }

    public boolean isPeerConnected() {
        return peerConnected;
    }

    public void setPeerConnected(boolean peerConnected) {
        this.peerConnected = peerConnected;
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
