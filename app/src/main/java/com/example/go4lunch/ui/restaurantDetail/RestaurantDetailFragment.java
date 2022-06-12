package com.example.go4lunch.ui.restaurantDetail;

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
import com.example.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

public class RestaurantDetailFragment extends Fragment {

    RestaurantDetailViewModel restaurantDetailViewModel;
    FragmentRestaurantDetailBinding binding;
    String placeID = "ChIJN1t_tDeuEmsRUsoyG83frY4";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        initViewModel();
        return binding.getRoot();

    }

    private void initViewModel(){
        restaurantDetailViewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);
        restaurantDetailViewModel.init();
        restaurantDetailViewModel.searchRestaurantDetail(placeID);
        restaurantDetailViewModel.restaurantDetail.observe(getViewLifecycleOwner(), new Observer<PlaceDetail>() {
            @Override
            public void onChanged(PlaceDetail placeDetail) {
                if (placeDetail != null){
                    updateLayoutWithRestaurantDetailData(placeDetail);
                }
            }
        });
    }

    private void updateLayoutWithRestaurantDetailData(PlaceDetail placeDetail){
       Glide.with(this).load(placeDetail.getResult().getPhotos().get(0)).into(binding.ivDetailRestaurantPicture);
       binding.tvDetailRestaurantName.setText(placeDetail.getResult().getName());

    }
}
