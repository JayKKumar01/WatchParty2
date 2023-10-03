package com.github.jaykkumar01.watchparty.utils;

import androidx.annotation.NonNull;

import com.github.jaykkumar01.watchparty.interfaces.FirebaseListener;
import com.github.jaykkumar01.watchparty.models.AppInfo;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.OnlineVideo;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FirebaseUtils {
    public static final String MEDIA_CALL = "MEDIA_CALL";
    private static final String USERS = "users";
    private static final String ONLINE_VIDEO = "online_video";

    private static DatabaseReference getDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(MEDIA_CALL);
    }

    public static void checkCodeExists(String code, FirebaseListener valueEventListener) {
        DatabaseReference databaseReference = getDatabaseReference().child(code);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueEventListener.onComplete(snapshot.exists(),new ListenerData("Code doesn't exist"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                valueEventListener.onComplete(false,new ListenerData("Something went wrong!"));
            }
        });
    }

    public static EventListenerData getUserList(String code, FirebaseListener listener) {


        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<UserModel> list = null;
                Set<String> idList = null;
                ListenerData data = new ListenerData();
                if (dataSnapshot.exists()) {
                    // Iterate through the children (users) and retrieve their data
                    list = new ArrayList<>();
                    idList = new HashSet<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        UserModel user = userSnapshot.getValue(UserModel.class);
                        if (user != null) {
                            list.add(user);
                            idList.add(user.getUserId());
                        }
                    }
                    if (list.isEmpty()){
                        data.setMessage("No Users found!");
                    }
                }else {
                    data.setErrorMessage("node deleted!");
                }

                data.setUserList(list);
                data.setIdList(idList);
                data.setValueEventListener(this);
                listener.onComplete(dataSnapshot.exists(),data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onComplete(false,new ListenerData("Something went wrong!"));
            }
        };
        DatabaseReference databaseReference = getDatabaseReference().child(code).child(USERS);
        databaseReference.addValueEventListener(eventListener);
        EventListenerData data = new EventListenerData();
        data.setDatabaseReference(databaseReference);
        data.setValueEventListener(eventListener);
        return data;
    }
    public static void updateUserData(String path, UserModel userModel, FirebaseListener listener) {
        DatabaseReference databaseReference = getDatabaseReference().child(path);
        String userId = userModel.getUserId();
        databaseReference.child(USERS).child(userId).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener == null){
                    return;
                }
                listener.onComplete(task.isSuccessful(),new ListenerData("Couldn't create User"));
            }
        });

    }

    public static EventListenerData getOnlineVideo(String code, FirebaseListener listener) {
        DatabaseReference databaseReference = getDatabaseReference().child(code).child(ONLINE_VIDEO);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListenerData data = new ListenerData();
                if (dataSnapshot.exists()) {
                    OnlineVideo onlineVideo = dataSnapshot.getValue(OnlineVideo.class);
                    if (onlineVideo != null) {
                        data.setOnlineVideo(onlineVideo);
                        listener.onComplete(true, data);
                    } else {
                        data.setErrorMessage("Failed to parse OnlineVideo data");
                        listener.onComplete(false, data);
                    }
                } else {
                    data.setErrorMessage("OnlineVideo not found");
                    listener.onComplete(false, data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ListenerData data = new ListenerData("Something went wrong!");
                listener.onComplete(false, data);
            }
        };

        databaseReference.addValueEventListener(eventListener);

        EventListenerData eventListenerData = new EventListenerData();
        eventListenerData.setDatabaseReference(databaseReference);
        eventListenerData.setValueEventListener(eventListener);
        return eventListenerData;
    }



    public static void updateOnlineVideo(String path, OnlineVideo onlineVideo, FirebaseListener listener) {
        DatabaseReference databaseReference = getDatabaseReference().child(path);
        databaseReference.child(ONLINE_VIDEO).setValue(onlineVideo).addOnCompleteListener(task -> {
            if (listener == null) {
                return;
            }
            if (task.isSuccessful()) {
                listener.onComplete(true, new ListenerData("Online video added successfully"));
            } else {
                listener.onComplete(false, new ListenerData("Failed to add online video"));
            }
        });
    }




    public static void updateUserData1(String path, UserModel userModel) {

        DatabaseReference databaseReference = getDatabaseReference().child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isUserAdded = false;
                    // Retrieve the existing list from the database
                    List<UserModel> userList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel existingUser = snapshot.getValue(UserModel.class);
                        assert existingUser != null;
                        if (existingUser.getUserId().equals(userModel.getUserId())){
                            userList.add(userModel);
                            isUserAdded = true;
                        }else {
                            userList.add(existingUser);
                        }
                    }

                    // Add the new user to the existing list
                    if (!isUserAdded){
                        userList.add(userModel);
                    }

                    // Update the database with the updated list
                    databaseReference.setValue(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if needed
            }
        });
    }
    public static void removeUserData(String path, UserModel userModel) {
        DatabaseReference databaseReference = getDatabaseReference().child(path).child(userModel.getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseReference.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if needed
            }
        });
    }

    public static void deleteData(String path) {
        DatabaseReference reference = getDatabaseReference().child(path);
        reference.removeValue();
    }

    public static void write(String okay) {
        getDatabaseReference().setValue(okay);
    }
}

