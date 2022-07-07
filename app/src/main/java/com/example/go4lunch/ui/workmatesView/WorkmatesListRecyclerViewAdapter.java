package com.example.go4lunch.ui.workmatesView;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

import java.util.List;

public class WorkmatesListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final RequestManager glide;
    List<User> usersList;

    public WorkmatesListRecyclerViewAdapter(RequestManager glide) {
        this.glide = glide;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        Log.e("userListRecycler", String.valueOf(usersList.size()));
        notifyDataSetChanged();
    }

    public boolean getUserChoice(User user){
        if (user.restaurantChoiceName.isEmpty() || user.restaurantChoiceName == null){
            return false;
        }else return true;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmates_list_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        User user = usersList.get(position);
        Log.e("urlTest", user.urlPicture);
        glide.load(user.urlPicture).into(viewHolder.ivUserPicture);
        if (getUserChoice(user)){
            String restaurantChoiceString = ((ViewHolder) holder).tvUserChoice.getContext().getString(R.string.workmate_restaurant_choice);
              viewHolder.tvUserChoice.setText(user.name + " " + restaurantChoiceString + " " + user.restaurantChoiceName);
              viewHolder.tvUserChoice.setTypeface(null, Typeface.BOLD);
        }else {
            String restaurantNoChoice = viewHolder.tvUserChoice.getContext().getString(R.string.workmate_restaurant_no_choice);
            viewHolder.tvUserChoice.setText(user.name + restaurantNoChoice);
            viewHolder.tvUserChoice.setTypeface(null,Typeface.ITALIC);
        }


    }

    @Override
    public int getItemCount() {
        if (usersList != null) {
            return usersList.size();
        } else return 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPicture;
        TextView tvUserChoice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPicture = itemView.findViewById(R.id.iv_workmate_picture_item);
            tvUserChoice = itemView.findViewById(R.id.tv_workmate_status);
        }
    }
}


