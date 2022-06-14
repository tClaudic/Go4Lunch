package com.example.go4lunch.ui.listView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;

public class RestaurantListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PlaceDetail> nearbyRestaurantList;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaceDetail placeDetail = nearbyRestaurantList.get(position);
        ViewHolder viewViewHolder = (ViewHolder) holder;
        viewViewHolder.tvRestaurantName.setText(placeDetail.getResult().getName());
        viewViewHolder.tvRestaurantAddress.setText(placeDetail.getResult().getFormattedAddress());
        viewViewHolder.tvRestaurantContributors.setText("2");
        viewViewHolder.tvRestaurantRange.setText("123meters");
        viewViewHolder.rbRestaurantRating.setRating(3);
        viewViewHolder.tvRestaurantOpenStatus.setText(placeDetail.getResult().getBusinessStatus());

    }

    @Override
    public int getItemCount() {
        if (nearbyRestaurantList != null) {
            return nearbyRestaurantList.size();
        }else return 0;
    }

    public void setNearbyRestaurantList(List<PlaceDetail> restaurantList){
        this.nearbyRestaurantList = restaurantList;
        notifyDataSetChanged();
    }


    public void updateNearbyRestaurantList(final List<PlaceDetail> nearbyRestaurantList) {
        this.nearbyRestaurantList.clear();
        this.nearbyRestaurantList = nearbyRestaurantList;
        notifyDataSetChanged();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantPicture;
        TextView tvRestaurantName;
        TextView tvRestaurantAddress;
        TextView tvRestaurantContributors;
        TextView tvRestaurantRange;
        RatingBar rbRestaurantRating;
        TextView tvRestaurantOpenStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRestaurantPicture = itemView.findViewById(R.id.iv_restaurant_picture);
            tvRestaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvRestaurantAddress = itemView.findViewById(R.id.tv_restaurant_description);
            tvRestaurantContributors = itemView.findViewById(R.id.tv_contributors);
            tvRestaurantRange = itemView.findViewById(R.id.tv_restaurant_range);
            rbRestaurantRating = itemView.findViewById(R.id.rb_restaurant_rating);
            tvRestaurantOpenStatus = itemView.findViewById(R.id.tv_restaurant_open_status);
        }
    }
}
