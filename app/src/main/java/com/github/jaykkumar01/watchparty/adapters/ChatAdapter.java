package com.github.jaykkumar01.watchparty.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.PlayerActivity;
import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.models.MessageModel;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter {

    private List<MessageModel> chatList;
    Context context;
    String id;
    int isSender = 1;
    String timePattern = "hh:mm a";
    SimpleDateFormat format;
    private final Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy",locale);

    public ChatAdapter(Context context,String id) {
        this.context = context;
        chatList = new ArrayList<>();
        this.id = id;
        if (android.text.format.DateFormat.is24HourFormat(context)){
            timePattern = "HH:mm";
        }
        format = new SimpleDateFormat(timePattern,locale);
    }

    public List<MessageModel> getChatList() {
        return chatList;
    }

    @Override
    public int getItemViewType(int position) {
        String userId = chatList.get(position).getUserId();
        if (userId.equals(id)){
            return 1;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == isSender){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_send,parent,false);
            return new SendViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_receive,parent,false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MessageModel message = chatList.get(position);




        if (viewHolder.getClass() == SendViewHolder.class){
            SendViewHolder holder = (SendViewHolder)viewHolder;
            String msgDate = dateFormat.format(message.getTimeMillis());
            holder.date.setText(msgDate);
            holder.date.setVisibility(View.VISIBLE);
            if (chatList.size()>1 && position>0){
                String preDate = dateFormat.format(chatList.get(position-1).getTimeMillis());
                if (msgDate.equals(preDate)){
                    holder.date.setVisibility(View.GONE);
                }
            }

            String time = format.format(message.getTimeMillis());
            holder.time.setText(time);

            if (message.getMessage() != null && !message.getMessage().isEmpty()) {
                holder.message.setText(message.getMessage());
                holder.message.setVisibility(View.VISIBLE);
            }
            else{
                holder.message.setVisibility(View.GONE);
            }


        }else{
            ReceiveViewHolder holder = (ReceiveViewHolder) viewHolder;
            message.setTimeMillis(System.currentTimeMillis());
            String msgDate = dateFormat.format(message.getTimeMillis());
            holder.date.setText(msgDate);
            holder.date.setVisibility(View.VISIBLE);
            if (chatList.size()>1 && position>0){
                String preDate = dateFormat.format(chatList.get(position-1).getTimeMillis());
                if (msgDate.equals(preDate)){
                    holder.date.setVisibility(View.GONE);
                }
            }





            String time = format.format(message.getTimeMillis());
            holder.time.setText(time);


            if (message.getMessage() != null && !message.getMessage().isEmpty()) {
                holder.message.setText(message.getMessage());
                holder.name.setText(message.getName());
                holder.message.setVisibility(View.VISIBLE);
            }
            else{
                holder.message.setVisibility(View.GONE);
            }
        }


    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void addMessage(MessageModel messageModel) {
        chatList.add(messageModel);
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView message,time,date,name;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.item_msg);
            time = itemView.findViewById(R.id.item_time);
            date = itemView.findViewById(R.id.item_date);
            name = itemView.findViewById(R.id.item_name);
        }
    }
    public static class SendViewHolder extends RecyclerView.ViewHolder {
        TextView message,time,date;
        ImageView sent;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.item_msg);
            time = itemView.findViewById(R.id.item_time);
            date = itemView.findViewById(R.id.item_date);
            sent = itemView.findViewById(R.id.item_sent);
        }
    }
}
