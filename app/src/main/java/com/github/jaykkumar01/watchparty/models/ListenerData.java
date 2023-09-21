package com.github.jaykkumar01.watchparty.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListenerData {
    String message;
    String errorMessage;
    List<UserModel> userList;
    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }

    public ListenerData() {
    }

    public ListenerData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ListenerData(String message, String errorMessage) {
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<UserModel> getUserList() {
        return userList;
    }

    public void setUserList(List<UserModel> userList) {
        this.userList = userList;
    }

    public ListenerData(String message, String errorMessage, List<UserModel> userList) {
        this.message = message;
        this.errorMessage = errorMessage;
        this.userList = userList;
    }


}
