package com.github.jaykkumar01.watchparty.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.adapters.ChatAdapter;
import com.github.jaykkumar01.watchparty.adapters.UserAdapter;
import com.github.jaykkumar01.watchparty.interfaces.MessageListener;
import com.github.jaykkumar01.watchparty.models.EventListenerData;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.services.CallService;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecycleViewManagement {

    public interface Listener{
        void onReceiveMessage(MessageModel messageModel);
    }

    public static Listener listener;
    private final Context context;
    private final Activity activity;
    private final Room room;
    private final UserModel userModel;

    private RecyclerView chatListRV;
    private ChatAdapter chatAdapter;
    private UserAdapter userAdapter;

    private List<UserModel> userList = new ArrayList<>();


    public RecycleViewManagement(Context context,Room room) {
        this.context = context;
        activity = (Activity) context;
        this.room = room;
        userModel = room.getUser();
        initViews();
        listener = setListener();
    }

    @SuppressLint("NotifyDataSetChanged")
    private Listener setListener() {
        return new Listener() {
            @Override
            public void onReceiveMessage(MessageModel messageModel) {
                activity.runOnUiThread(() -> {
                    chatAdapter.addMessage(messageModel);
                    chatAdapter.notifyDataSetChanged();
                    if (chatListRV.getScrollState() == RecyclerView.SCROLL_STATE_IDLE){
                        scrollToTop(chatListRV);
                    }
                });
            }
        };
    }

    private void scrollToTop(RecyclerView recyclerView) {
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }
    private void initViews(){
        RecyclerView userListRV = activity.findViewById(R.id.recyclerViewUsers);
        setupUsersRecycleView(userListRV);
        chatListRV = activity.findViewById(R.id.recyclerViewChats);
        setupChatsRecycleView(chatListRV);
    }

    private void setupChatsRecycleView(RecyclerView recyclerView) {
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        chatAdapter = new ChatAdapter(context,userModel.getUserId());
        recyclerView.setAdapter(chatAdapter);
    }

    private void setupUsersRecycleView(RecyclerView userListRV) {
        userListRV.setLayoutManager(new LinearLayoutManager(context));
        userAdapter = new UserAdapter(userList);
        userListRV.setAdapter(userAdapter);


        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        EventListenerData listenerData = FirebaseUtils.getUserList(room.getCode(), (successful, data) -> {
            if (successful){
                Set<String> idList = data.getIdList();
                for (UserModel user : userList){
                    if (!user.getUserId().equals(userModel.getUserId()) && !idList.contains(user.getUserId())){
                        Toast.makeText(context, user.getName()+" got disconnected!", Toast.LENGTH_SHORT).show();
                    }
                }
                userList = data.getUserList();

                if (!room.isPeerConnected()){
                    room.setPeerConnected(true);
                    room.setUserList(userList);
                    Intent serviceIntent = new Intent(context, CallService.class);
                    serviceIntent.putExtra(context.getString(R.string.room),room);
                    context.startService(serviceIntent);
                }

                PeerManagement.listener.onUpdatePeerCount(userList.size());

                userAdapter.setList(userList);
                userAdapter.notifyDataSetChanged();

                if (userList.isEmpty()){
                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, data.getErrorMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
        HandleEventListener.add(context,listenerData);


    }


}
