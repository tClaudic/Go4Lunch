package com.example.go4lunch.ui.listView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.util.ItemClickSupport;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListFragment extends Fragment {

    FragmentListViewBinding binding;
    RestaurantListViewModel restaurantListViewModel;
    public RecyclerView recyclerView;
    RestaurantListRecyclerViewAdapter restaurantListRecyclerViewAdapter;

    public List<PlaceDetail> nearbyRestaurantList = new ArrayList<>();
    public FusedLocationProviderClient fusedLocationProviderClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureRecyclerView();
        initViewModel();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();
        initOnClickRecyclerView();
        return binding.getRoot();
    }

    private void initViewModel() {
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);
        restaurantListViewModel.init();

    }

    private void initOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView,binding.rcListView.getId())
                .setOnItemClickListener((recyclerView1, position, v) -> {
                    Log.e("testclick",restaurantListRecyclerViewAdapter.getPlaceDetail(position).getResult().getName());
                   restaurantListViewModel.select(restaurantListRecyclerViewAdapter.getPlaceDetail(position));
                   Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_listView_to_nav_restaurantDetail);
                    
                });
    }


    private void configureRecyclerView() {
        recyclerView = binding.rcListView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        restaurantListRecyclerViewAdapter = new RestaurantListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(restaurantListRecyclerViewAdapter);
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            askLocationPermission();
        }
    }
    private void observeNearbyRestaurant(Location location){
        String userLocation = location.getLatitude() +","+ location.getLongitude();
        Log.e("LocationString",userLocation);
        restaurantListViewModel.searchNearbyRestaurants(userLocation,5000,"restaurant");
        restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
                Log.e("onChanged", String.valueOf(placeDetails.size()));
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.e("Lattitude", String.valueOf(location.getLatitude()));
            Log.e("Longitude", String.valueOf(location.getLongitude()));
            restaurantListRecyclerViewAdapter.setUserLocation(location);
            observeNearbyRestaurant(location);
        });
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},44);
    }


}
