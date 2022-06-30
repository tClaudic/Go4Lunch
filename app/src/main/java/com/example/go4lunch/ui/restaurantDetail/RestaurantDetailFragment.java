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
import com.example.go4lunch.ui.listView.RestaurantListViewModel;

public class RestaurantDetailFragment extends Fragment {

    FragmentRestaurantDetailBinding binding;
    RestaurantListViewModel restaurantListViewModel;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        initViewModel();
        return binding.getRoot();
    }

    private void initViewModel(){
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);
        restaurantListViewModel.getSelected().observe(getViewLifecycleOwner(), this::updateLayoutWithRestaurantDetailData);
    }

    private void updateLayoutWithRestaurantDetailData(PlaceDetail placeDetail){
        if (placeDetail.getResult().getPhotos() != null) {
            Glide.with(binding.getRoot()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetail.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.MAPS_API_KEY).centerCrop().into(binding.ivDetailRestaurantPicture);
        } else {
            Glide.with(binding.getRoot()).load(R.drawable.ic_baseline_image_not_supported_24).into(binding.ivDetailRestaurantPicture);
        }
        binding.tvDetailRestaurantName.setText(placeDetail.getResult().getName());
        binding.tvDetailRestaurantAdress.setText(placeDetail.getResult().getFormattedAddress());
        Log.e("detailtest",placeDetail.getResult().getName());
        setCallBtn(placeDetail);
        setWebsiteBtn(placeDetail);
    }

    private void setCallBtn(PlaceDetail placeDetail){
        binding.btnCallDetail.setOnClickListener(view -> {
            if (placeDetail.getResult().getFormattedPhoneNumber() != null){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",placeDetail.getResult().getFormattedPhoneNumber(),null));
                startActivity(intent);
            }
        });
    }
    private void setWebsiteBtn(PlaceDetail placeDetail){
        binding.btnWebsiteDetail.setOnClickListener(view -> {
            if (placeDetail.getResult().getWebsite() != null){
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(placeDetail.getResult().getWebsite()));
                startActivity(intent);
            }
        });
    }
}
