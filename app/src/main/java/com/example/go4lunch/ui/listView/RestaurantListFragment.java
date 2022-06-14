package com.example.go4lunch.ui.listView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListFragment extends Fragment {
    FragmentListViewBinding binding;
    RestaurantListViewModel restaurantListViewModel;
    public RecyclerView recyclerView;
    RestaurantListRecyclerViewAdapter restaurantListRecyclerViewAdapter;
    String location = "47.6507, -2.0839";
    public List<PlaceDetail> nearbyRestaurantList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureRecyclerView();
        initViewModel();
        return binding.getRoot();
    }

    private void initViewModel(){
        restaurantListViewModel = new ViewModelProvider(this).get(RestaurantListViewModel.class);
        restaurantListViewModel.init();
        restaurantListViewModel.searchNearbyRestaurants(location,5000,"restaurant");
        restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
            }
        });


    }




    private void configureRecyclerView(){
        recyclerView = binding.rcListView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        restaurantListRecyclerViewAdapter = new RestaurantListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(restaurantListRecyclerViewAdapter);
    }


}
