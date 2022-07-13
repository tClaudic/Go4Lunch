package com.example.go4lunch.ui.listView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;

import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
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
    private SwipeRefreshLayout swipeRefreshLayout;

    public List<PlaceDetail> nearbyRestaurantList = new ArrayList<>();
    public FusedLocationProviderClient fusedLocationProviderClient;
    int SEARCH_QUERY_THRESHOLD = 3;
    String locationString;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());

        configureRecyclerView();
        setHasOptionsMenu(true);
        initViewModel();
        observeUsersList();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();
        initOnClickRecyclerView();
        configureOnSwipeFresh();
        observeSearchAutoCompleteResult();
        return binding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main,menu);
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("OnQueryTextSubmit","true");
                if (query.length() >= SEARCH_QUERY_THRESHOLD){
                    searchNearbyRestaurantWithAutocomplete(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("OnQeuryTextChange","true");
                if (newText.length() >= SEARCH_QUERY_THRESHOLD){
                    searchNearbyRestaurantWithAutocomplete(newText);
                }
                return true;
            }
        });
    }

    private void observeSearchAutoCompleteResult(){
        restaurantListViewModel.autoCompleteNearbyRestaurantList.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                Log.e("autoCompleteTest", String.valueOf(placeDetails.size()));
                restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
            }
        });
    }

    private void searchNearbyRestaurantWithAutocomplete(String query){
        restaurantListViewModel.searchNearbyRestaurantWithAutocomplete(query,locationString,5000);
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

    private void configureOnSwipeFresh(){
        swipeRefreshLayout = binding.srlRestaurantSwipe;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
                    @Override
                    public void onChanged(List<PlaceDetail> placeDetails) {
                        restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
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

    private void observeUsersList(){
        restaurantListViewModel.usersListMutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                restaurantListRecyclerViewAdapter.setUsersList(users);
            }
        });
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
            locationString = String.valueOf(location.getLatitude() +","+ location.getLongitude());
            Log.e("testLocationString",locationString);
            restaurantListRecyclerViewAdapter.setUserLocation(location);
            observeNearbyRestaurant(location);
        });
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},44);
    }


}
