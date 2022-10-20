package com.example.go4lunch.ui.restaurantDetail;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Notification.AlertReceiver;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentRestaurantDetailBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.ViewModelFactory;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.workmatesView.WorkmatesListRecyclerViewAdapter;

import java.util.Calendar;
import java.util.List;

public class RestaurantDetailFragment extends Fragment {

    FragmentRestaurantDetailBinding binding;
    RestaurantListViewModel restaurantListViewModel;
    RestaurantDetailViewModel restaurantDetailViewModel;
    User currentUser;
    PlaceDetail placeDetail;
    RecyclerView recyclerView;
    WorkmatesListRecyclerViewAdapter workmatesListRecyclerViewAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailBinding.inflate(getLayoutInflater());
        initViewModel();
        getSelectedPlaceDetail();
        getAuthenticatedUser();
        configureWorkmatesRecyclerView();
        return binding.getRoot();
    }

    private void getSelectedPlaceDetail(){
        restaurantListViewModel.getSelected().observe(getViewLifecycleOwner(),
                placeDetail1 -> {
                    RestaurantDetailFragment.this.updateLayoutWithRestaurantDetailData(placeDetail1);
                    placeDetail = placeDetail1;
                    restaurantDetailViewModel.getUsersListFilteredByRestaurantChoice(placeDetail1.getResult().getPlaceId()).observe(getViewLifecycleOwner(), users ->
                            workmatesListRecyclerViewAdapter.setUsersList(users));
                    Log.e("placedetail", placeDetail1.getResult().getPlaceId());
                });
    }


    private void getAuthenticatedUser(){
        restaurantDetailViewModel.getAuthenticatedLiveDataUser().observe(getViewLifecycleOwner(), user -> {
            currentUser = user;
            updateStarColor(user, placeDetail);
            updateRestaurantButton(currentUser);
        });
    }

    private void initViewModel() {
        restaurantDetailViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);
        restaurantListViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantListViewModel.class);
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
        setRestaurantChoiceBtn();
    }

    private void updateRestaurantButton(User user) {
        if (user.restaurantChoice != null && user.restaurantChoice.equalsIgnoreCase(placeDetail.getResult().getPlaceId())) {
            DrawableCompat.setTint(
                    binding.btnRestaurantChoice.getDrawable(),
                    ContextCompat.getColor(requireContext(), R.color.restaurantChoiceBtnColor)
            );
        } else {
            DrawableCompat.setTint(
                    binding.btnRestaurantChoice.getDrawable(),
                    ContextCompat.getColor(requireContext(), R.color.secondaryTextColor)
            );
        }

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

    private void setRestaurantChoiceBtn() {
        binding.btnRestaurantChoice.setOnClickListener(view -> {
            if (!currentUser.restaurantChoice.equalsIgnoreCase(placeDetail.getResult().getPlaceId())) {
                restaurantDetailViewModel.setPlaceId(currentUser.uid, placeDetail.getResult().getPlaceId());
                restaurantDetailViewModel.setRestaurantChoiceName(currentUser.uid, placeDetail.getResult().getName());
                setupNotification();
            } else {
                restaurantDetailViewModel.removePlaceId(currentUser.uid);
                currentUser.restaurantChoice = "";
                updateRestaurantButton(currentUser);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setupNotification() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 16);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void setWebsiteBtn(PlaceDetail placeDetail) {
        binding.btnWebsiteDetail.setOnClickListener(view -> {
            if (placeDetail.getResult().getWebsite() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetail.getResult().getWebsite()));
                startActivity(intent);
            }
        });
    }

    private void configureWorkmatesRecyclerView() {
        recyclerView = binding.rcWorkmatesPlaceDetail;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        workmatesListRecyclerViewAdapter = new WorkmatesListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(workmatesListRecyclerViewAdapter);
    }

    private void setLikeBtn(PlaceDetail placeDetail) {
        binding.btnLikeDetail.setOnClickListener(view -> {
            if (!currentUser.likes.contains(placeDetail.getResult().getPlaceId())) {
                restaurantDetailViewModel.addUserRestaurantLike(currentUser.uid, placeDetail.getResult().getPlaceId());
                currentUser.likes.add(placeDetail.getResult().getPlaceId());
            } else {
                restaurantDetailViewModel.removeUserRestaurantLike(currentUser.uid, placeDetail.getResult().getPlaceId());
                currentUser.likes.remove(placeDetail.getResult().getPlaceId());
            }
            updateStarColor(currentUser, placeDetail);
        });
    }
}
