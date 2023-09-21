package com.github.jaykkumar01.watchparty.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EventListenerData {
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    public EventListenerData(DatabaseReference databaseReference, ValueEventListener valueEventListener) {
        this.databaseReference = databaseReference;
        this.valueEventListener = valueEventListener;
    }

    public EventListenerData() {

    }

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
}
