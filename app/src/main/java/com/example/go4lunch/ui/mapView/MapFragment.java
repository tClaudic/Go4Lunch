package com.example.go4lunch.ui.mapView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.util.RestaurantListHelper;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final Integer RADIUS = 1500;
    public static final String TYPE = "restaurant";
    private static final int SEARCH_QUERY_THRESHOLD = 3;
    public FragmentMapBinding binding;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public String locationString;
    private GoogleMap googleMap;
    private RestaurantListViewModel restaurantListViewModel;
    private List<User> userList;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        setHasOptionsMenu(true);
        initRestaurantListViewModel();
        observeUsersList();
        initLocationButton();
        setFusedLocationProviderClient();
        checkLocationPermissions();
        setupOnBackPressedCallback();

        return binding.getRoot();
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }

    private void initLocationButton() {
        binding.btnLocation.setOnClickListener(view -> getUserLocation());
    }

    private void initRestaurantListViewModel() {
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);
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
                    searchNearbyRestaurantWithAutocomplete(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= SEARCH_QUERY_THRESHOLD) {
                    searchNearbyRestaurantWithAutocomplete(newText);
                }
                return true;
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

    private void searchNearbyRestaurantWithAutocomplete(String query) {
        restaurantListViewModel.getAutoCompleteNearbyRestaurantList(query, locationString, RADIUS).observe(getViewLifecycleOwner(), placeDetails -> {
            if (placeDetails != null) {
                updateMapWithRestaurantMarker(placeDetails, userList);
                updateMapCameraWithAutocompleteResult(placeDetails.get(0));
            }
        });
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

    private void observeUsersList() {
        restaurantListViewModel.usersListMutableLiveData.observe(getViewLifecycleOwner(), users -> {
            userList = users;
            Log.e("usersize", String.valueOf(userList.size()));
        });
    }

    private void observeNearbyRestaurant(Location location) {
        String userLocation = location.getLatitude() + "," + location.getLongitude();
        Log.e("LocationString", userLocation);
        restaurantListViewModel.getAllRestaurants(userLocation, RADIUS, TYPE).observe(getViewLifecycleOwner(), placeDetails -> {
            if (placeDetails != null) {
                updateMapWithRestaurantMarker(placeDetails, userList);
            } else {
                Toast.makeText(requireActivity(), "please check your internet connexion", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                locationString = location.getLatitude() + "," + location.getLongitude();
                updateCameraZoomWithNewLocation(location);
                observeNearbyRestaurant(location);
            } else {
                getCurrentLocation();
            }
        }).addOnFailureListener(e -> getUserLocation());
    }

    private void updateMapCameraWithAutocompleteResult(PlaceDetail placeDetail) {
        LatLng latLng = new LatLng(placeDetail.getResult().getGeometry().getLocation().getLat(), placeDetail.getResult().getGeometry().getLocation().getLng());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        fusedLocationProviderClient.getCurrentLocation(currentLocationRequest, null).addOnSuccessListener(location -> {
            if (location != null) {
                locationString = location.getLatitude() + "," + location.getLongitude();
                Log.e("locationRequestTest", locationString);
                updateCameraZoomWithNewLocation(location);
                observeNearbyRestaurant(location);
            } else {
                Toast.makeText(requireActivity(), "Check if your GPS is enable", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(requireActivity(), "Can't access to your location retry later", Toast.LENGTH_LONG).show());
    }


    private void updateCameraZoomWithNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
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
            Objects.requireNonNull(googleMap.addMarker(options)).setTag(placeDetail);
            googleMap.setOnInfoWindowClickListener(marker -> {
                if (isNetworkEnable()) {
                    restaurantListViewModel.select((PlaceDetail) marker.getTag());
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_restaurantDetail);
                } else {
                    Toast.makeText(requireActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @SuppressLint("MissingPermission")
    private Boolean isNetworkEnable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style));
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurantListViewModel.getUsersLists();
    }
}
