package com.example.go4lunch.ui.restaurantDetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;

public class RestaurantDetailFragment extends Fragment {

    FragmentRestaurantDetailBinding binding;
    RestaurantListViewModel restaurantListViewModel;
    RestaurantDetailViewModel restaurantDetailViewModel;
    User currentUser;
    PlaceDetail placeDetail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        initViewModel();
        return binding.getRoot();
    }


    private void initViewModel() {
        restaurantDetailViewModel = new ViewModelProvider(requireActivity()).get(RestaurantDetailViewModel.class);
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);
        restaurantDetailViewModel.init();
        restaurantListViewModel.getSelected().observe(getViewLifecycleOwner(),
                new Observer<PlaceDetail>() {
                    @Override
                    public void onChanged(PlaceDetail placeDetail1) {
                        RestaurantDetailFragment.this.updateLayoutWithRestaurantDetailData(placeDetail1);
                        placeDetail = placeDetail1;
                        Log.e("placedetail", placeDetail1.getResult().getPlaceId());
                    }
                });
        restaurantDetailViewModel.authenticatedUser.observe(getViewLifecycleOwner(),
                new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        currentUser = user;
                        Log.e("usercolpme", user.likes.get(4));
                        updateStarColor(user, placeDetail);
                    }
                });
    }

    private void updateLayoutWithRestaurantDetailData(PlaceDetail placeDetail) {
        if (placeDetail.getResult().getPhotos() != null) {
            Glide.with(binding.getRoot()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetail.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY).centerCrop().into(binding.ivDetailRestaurantPicture);
        } else {
            Glide.with(binding.getRoot()).load(R.drawable.ic_baseline_image_not_supported_24).into(binding.ivDetailRestaurantPicture);
        }
        binding.tvDetailRestaurantName.setText(placeDetail.getResult().getName());
        binding.tvDetailRestaurantAdress.setText(placeDetail.getResult().getFormattedAddress());
        setCallBtn(placeDetail);
        setWebsiteBtn(placeDetail);
        setLikeBtn(placeDetail);
    }

    private void setCallBtn(PlaceDetail placeDetail) {
        binding.btnCallDetail.setOnClickListener(view -> {
            if (placeDetail.getResult().getFormattedPhoneNumber() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", placeDetail.getResult().getFormattedPhoneNumber(), null));
                startActivity(intent);
            }
        });
    }

    private void updateStarColor(User user, PlaceDetail placeDetail) {
        if (user.likes.contains(placeDetail.getResult().getPlaceId())) {
            binding.ivDetailRestaurantLike.setImageResource(R.drawable.ic_restaurant_liked_star);
        } else {
            binding.ivDetailRestaurantLike.setImageResource(R.drawable.ic_unlike_restaurant_star);
        }

    }

    private void setWebsiteBtn(PlaceDetail placeDetail) {
        binding.btnWebsiteDetail.setOnClickListener(view -> {
            if (placeDetail.getResult().getWebsite() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetail.getResult().getWebsite()));
                startActivity(intent);
            }
        });
    }

    private void setLikeBtn(PlaceDetail placeDetail) {
        binding.btnLikeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restaurantDetailViewModel.addUserRestaurantLike(currentUser.uid, placeDetail.getResult().getPlaceId());
                binding.ivDetailRestaurantLike.setColorFilter(R.color.ratingColorStar);

            }
        });
    }
}
