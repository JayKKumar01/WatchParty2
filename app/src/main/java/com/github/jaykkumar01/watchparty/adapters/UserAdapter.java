package com.github.jaykkumar01.watchparty.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jaykkumar01.watchparty.R;
import com.github.jaykkumar01.watchparty.models.UserModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserModel> userList;

    public UserAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user2, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.tvUserName.setText(user.getName());

        if (user.isDeafen()){
            holder.deafen.setVisibility(View.VISIBLE);
            holder.deafen.setImageResource(R.drawable.deafen_on_small);
            holder.mic.setImageResource(R.drawable.mic_off_small);
        }
        else {
            holder.deafen.setVisibility(View.GONE);
            holder.deafen.setImageResource(R.drawable.deafen_off_small);
            if (user.isMute()){
                holder.mic.setImageResource(R.drawable.mic_off_small);
            }else {
                holder.mic.setImageResource(R.drawable.mic_on_small);
            }
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setList(List<UserModel> userList) {
        this.userList = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        ImageView mic,deafen;
        View view;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.item_name);
            mic = itemView.findViewById(R.id.mic);
            deafen = itemView.findViewById(R.id.deafen);
            view = itemView.findViewById(R.id.item_view);
        }
    }
}

