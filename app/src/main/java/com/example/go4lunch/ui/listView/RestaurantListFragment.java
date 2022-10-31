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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.ui.ViewModelFactory;
import com.example.go4lunch.util.ItemClickSupport;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class RestaurantListFragment extends Fragment {

    public static final Integer RADIUS = 1500;
    public static final String TYPE = "restaurant";
    private final int SEARCH_QUERY_THRESHOLD = 3;
    public RecyclerView recyclerView;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public String locationString;
    private FragmentListViewBinding binding;
    private RestaurantListViewModel restaurantListViewModel;
    private RestaurantListRecyclerViewAdapter restaurantListRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureRecyclerView(); // init RecyclerView and set adapter to it
        setHasOptionsMenu(true);
        initViewModel();
        observeUsersList();
        setFusedLocationProviderClient();
        checkLocationPermissions();
        initOnClickRecyclerView();
        configureOnSwipeFresh();
        setupOnBackPressedCallback();
        return binding.getRoot();
    }

    private void initViewModel() {
        restaurantListViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantListViewModel.class);
    }

    private void setFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() >= SEARCH_QUERY_THRESHOLD) {
                    observeNearbyRestaurantsWithAutoComplete(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= SEARCH_QUERY_THRESHOLD) {
                    observeNearbyRestaurantsWithAutoComplete(newText);
                }
                return false;
            }
        });
    }


    private void setupOnBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void observeUsersList() {
        restaurantListViewModel.usersListMutableLiveData.observe(getViewLifecycleOwner(), users ->
                restaurantListRecyclerViewAdapter.setUsersList(users));
    }

    private void observeNearbyRestaurantsWithAutoComplete(String query) {
        restaurantListViewModel.getAutoCompleteNearbyRestaurantList(query, locationString, RADIUS).observe(getViewLifecycleOwner(), placeDetails -> {
            if (!placeDetails.isEmpty()) {
                restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
            }
        });
    }

    private void observeNearbyRestaurant(Location location) {
        String userLocation = location.getLatitude() + "," + location.getLongitude();
        Log.e("LocationString", userLocation);
        //restaurantListViewModel.searchNearbyRestaurants(userLocation,5000,"restaurant");
        restaurantListViewModel.getAllRestaurants(userLocation, RADIUS, TYPE).observe(getViewLifecycleOwner(), placeDetails -> {
            restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
            Log.e("onChanged", String.valueOf(placeDetails.size()));
        });
    }


    private void initOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, binding.rcListView.getId())
                .setOnItemClickListener((recyclerView1, position, v) -> {
                    restaurantListViewModel.select(restaurantListRecyclerViewAdapter.getPlaceDetail(position));
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_listView_to_nav_restaurantDetail);
                });
    }

    private void configureOnSwipeFresh() {
        swipeRefreshLayout = binding.srlRestaurantSwipe;
        swipeRefreshLayout.setOnRefreshListener(() -> restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), placeDetails -> {
            restaurantListRecyclerViewAdapter.setNearbyRestaurantList(placeDetails);
            swipeRefreshLayout.setRefreshing(false);
        }));
    }


    private void configureRecyclerView() {
        recyclerView = binding.rcListView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        restaurantListRecyclerViewAdapter = new RestaurantListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(restaurantListRecyclerViewAdapter);
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            askLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            locationString = location.getLatitude() + "," + location.getLongitude();
            restaurantListRecyclerViewAdapter.setUserLocation(location);
            observeNearbyRestaurant(location);
        });
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurantListViewModel.getUsersLists();
    }
}
