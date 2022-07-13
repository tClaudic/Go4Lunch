package com.example.go4lunch.ui.mapView;

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

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.util.RestaurantListHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int SEARCH_QUERY_THRESHOLD = 3;
    public FragmentMapBinding binding;
    public MapViewModel mapViewModel;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    RestaurantListViewModel restaurantListViewModel;
    private List<User> userList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initRestaurantListViewModel();
        initLocationButton();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();
        observeUsersList();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("OnQueryTextSubmit", "true");
                if (query.length() >= SEARCH_QUERY_THRESHOLD) {
                    searchNearbyRestaurantWithAutocomplete(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("OnQeuryTextChange", "true");
                if (newText.length() >= SEARCH_QUERY_THRESHOLD) {
                    searchNearbyRestaurantWithAutocomplete(newText);
                }
                return true;
            }
        });
    }

    private void searchNearbyRestaurantWithAutocomplete(String query) {

    }

    private void initLocationButton() {
        binding.btnLocation.setOnClickListener(view -> getUserLocation());
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

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            Log.e("Lattitude", String.valueOf(location.getLatitude()));
            Log.e("Longitude", String.valueOf(location.getLongitude()));
            updateCameraZoomWithNewLocation(location);
            observeNearbyRestaurant(location);
        });
    }

    private void updateCameraZoomWithNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void updateMapWithRestaurantMarker(List<PlaceDetail> placeDetails, List<User> userList) {
        if (googleMap != null) {
            googleMap.clear();
        }

        for (PlaceDetail placeDetail : placeDetails) {
            MarkerOptions options = new MarkerOptions().position(new LatLng(placeDetail.getResult().getGeometry().getLocation().getLat(), placeDetail.getResult().getGeometry().getLocation().getLng()))
                    .title(placeDetail.getResult().getName());
            if (RestaurantListHelper.UserGoIntoRestaurant(placeDetail, userList)) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin_green));

            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin));
            }
            googleMap.addMarker(options).setTag(placeDetail.getResult().getPlaceId());
        }
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }

    private void initRestaurantListViewModel() {
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);
        restaurantListViewModel.init();


    }

    private void observeUsersList() {
        restaurantListViewModel.usersListMutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList = users;
            }
        });
    }

    private void observeNearbyRestaurant(Location location) {
        String userLocation = location.getLatitude() + "," + location.getLongitude();
        Log.e("LocationString", userLocation);
        restaurantListViewModel.searchNearbyRestaurants(userLocation, 5000, "restaurant");
        restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                updateMapWithRestaurantMarker(placeDetails, userList);
                Log.e("onChanged", String.valueOf(placeDetails.size()));
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
    }
}
