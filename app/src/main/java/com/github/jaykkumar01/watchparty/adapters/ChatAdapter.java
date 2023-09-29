package com.github.jaykkumar01.watchparty.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.models.MessageModel;
import com.github.jaykkumar01.watchparty.models.Reply;
import com.github.jaykkumar01.watchparty.models.UserModel;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.UserViewHolder> {

    private List<MessageModel> chatList;
    Context context;
    private static final Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy",locale);

    public ChatAdapter(Context context,List<MessageModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    public List<MessageModel> getChatList() {
        return chatList;
    }

    public void setChatList(List<MessageModel> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatAdapter.UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        MessageModel message = chatList.get(position);
        holder.imageView.setImageDrawable(null);

        String timePattern = "hh:mm a";
        if (android.text.format.DateFormat.is24HourFormat(context)){
            timePattern = "HH:mm";
        }

        SimpleDateFormat format = new SimpleDateFormat(timePattern,locale);
        String time = format.format(message.getTimeMillis());

        holder.time.setText(time);


        String msgDate = dateFormat.format(message.getTimeMillis());
        holder.date.setText(msgDate);
        holder.date.setVisibility(View.VISIBLE);
        if (chatList.size()>1 && position>0){
            String preDate = dateFormat.format(chatList.get(position-1).getTimeMillis());
            if (msgDate.equals(preDate)){
                holder.date.setVisibility(View.GONE);
            }
        }

        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            holder.message.setText(message.getMessage());
            holder.message.setVisibility(View.VISIBLE);
        }
        else{
            holder.message.setVisibility(View.GONE);
        }
        Reply reply = message.getReply();
        if (reply != null){
            holder.replyName.setText(reply.getName());
            holder.replymsg.setText(reply.getMessage());

            holder.replyLayout.setVisibility(View.VISIBLE);
        }
        else{
            holder.replyLayout.setVisibility(View.GONE);
        }



    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView message,time,date,replymsg,replyName,fullWidth;
        View replyLayout;
        ImageView imageView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.item_msg);
            time = itemView.findViewById(R.id.item_time);
            date = itemView.findViewById(R.id.item_date);

            replyLayout = itemView.findViewById(R.id.replyLayout);
            replymsg = itemView.findViewById(R.id.item_reply_msg);
            replyName = itemView.findViewById(R.id.replyName);

            fullWidth = itemView.findViewById(R.id.fullWidth);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}