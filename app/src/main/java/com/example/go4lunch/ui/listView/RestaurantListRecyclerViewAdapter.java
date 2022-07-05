package com.example.go4lunch.ui.listView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.util.RestaurantListHelper;
import com.google.firebase.database.collection.LLRBNode;

import java.util.List;

public class RestaurantListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PlaceDetail> nearbyRestaurantList;
    final RequestManager glide;
    Location userLocation;
    List<User> usersList;

    public RestaurantListRecyclerViewAdapter(RequestManager glide) {
        this.glide = glide;
    }


    public PlaceDetail getPlaceDetail(int position){
        return nearbyRestaurantList.get(position);
    }
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
        viewViewHolder.tvRestaurantRange.setText(RestaurantListHelper.sumDistanceBetweenTwoLocation(userLocation, placeDetail));
        viewViewHolder.rbRestaurantRating.setRating(RestaurantListHelper.divideRatingResultBy3(placeDetail));
        viewViewHolder.tvRestaurantContributors.setText(RestaurantListHelper.howManyUsersLunchAtThisRestaurant(usersList,placeDetail));
        if (RestaurantListHelper.formatCloseHour(placeDetail).contains("Closing soon")){
            viewViewHolder.tvRestaurantOpenStatus.setTypeface(null,Typeface.BOLD);
            viewViewHolder.tvRestaurantOpenStatus.setTextColor(Color.parseColor("#c00000"));
        }
        viewViewHolder.tvRestaurantOpenStatus.setText(RestaurantListHelper.formatCloseHour(placeDetail));

        if (placeDetail.getResult().getPhotos() != null) {
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetail.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY).centerCrop().into(viewViewHolder.ivRestaurantPicture);
        } else {
            glide.load(R.drawable.ic_baseline_image_not_supported_24).into(viewViewHolder.ivRestaurantPicture);
        }
    }


    @Override
    public int getItemCount() {
        if (nearbyRestaurantList != null) {
            return nearbyRestaurantList.size();
        } else return 0;
    }

    public void setNearbyRestaurantList(List<PlaceDetail> restaurantList) {
        this.nearbyRestaurantList = restaurantList;
        notifyDataSetChanged();
    }

    public void setUsersList(List<User> usersList){
        this.usersList = usersList;
        notifyDataSetChanged();

    }



    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
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
