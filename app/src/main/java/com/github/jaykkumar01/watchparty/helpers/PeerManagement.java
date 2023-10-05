package com.github.jaykkumar01.watchparty.helpers;

import static androidx.core.content.ContextCompat.getDrawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.interfaces.MessageListener;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.services.CallService;

public class PeerManagement {

    public static void start(Context context, Room room) {
        new PeerManagement(context, room);
    }

    public interface Listener{
        void onReceiveMessage(MessageModel messageModel);
        void onUpdatePeerCount(int count);

        void onToggleLayout(View view);

        void onSendMessage();
    }
    public static Listener listener;
    private final Context context;
    private final Activity activity;

    private final Room room;

    private int peerCount;
    private TextView messageET;
    private ConstraintLayout chatLayout,peerLayout;
    private ImageView circle;
    private TextView userCount;

    public PeerManagement(Context context,Room room) {
        this.context = context;
        activity = (Activity) context;
        this.room = room;
        listener = setListener();
        initViews();
    }

    private Listener setListener() {
        return new Listener() {
            @Override
            public void onReceiveMessage(MessageModel messageModel) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (chatLayout.getVisibility() != View.VISIBLE){
                            circle.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onUpdatePeerCount(int count) {
                activity.runOnUiThread(() -> {
                    if (peerLayout.getVisibility() != View.VISIBLE){
                        circle.setVisibility(View.VISIBLE);
                    }
                    peerCount = count;
                    userCount.setText("User Count: "+peerCount);
                });
            }

            @Override
            public void onToggleLayout(View view) {
                activity.runOnUiThread(() -> {
                    circle.setVisibility(View.GONE);
                    ImageView imageView = (ImageView) view;
                    if (peerLayout.getVisibility() == View.VISIBLE){
                        peerLayout.setVisibility(View.GONE);
                        chatLayout.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.baseline_people_24);
                    } else {
                        peerLayout.setVisibility(View.VISIBLE);
                        chatLayout.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.baseline_chat_bubble_24);
                    }
                });
            }

            @Override
            public void onSendMessage() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (messageET.getText() == null || messageET.getText().toString().isEmpty()){
                            return;
                        }
                        if (peerCount < 2){
                            Toast.makeText(context, "Invite Friends to chat!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String message = messageET.getText().toString();
                        MessageModel messageModel = new MessageModel(room.getUser().getUserId(),message);
                        messageModel.setName(room.getUser().getName());
                        messageModel.setTimeMillis(System.currentTimeMillis());
                        CallService.listener.sendMessage(messageModel);
                        messageET.setText("");
                        listener.onReceiveMessage(messageModel);
                        RecycleViewManagement.listener.onReceiveMessage(messageModel);
                    }
                });
            }
        };
    }

    public int getPeerCount() {
        return peerCount;
    }
    private void initViews(){
        chatLayout = findView(R.id.chatLayout);
        peerLayout = findView(R.id.peerLayout);
        circle = findView(R.id.circle);
        userCount = findView(R.id.userCount);
        messageET = findView(R.id.messageTXT);
    }

    private <T extends View> T findView(int viewId) {
        View view = activity.findViewById(viewId);
        //noinspection unchecked
        return (T) view;
    }

}
