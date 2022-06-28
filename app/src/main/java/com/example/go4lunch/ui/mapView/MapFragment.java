package com.example.go4lunch.ui.mapView;

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

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public FragmentMapBinding binding;
    public MapViewModel mapViewModel;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    RestaurantListViewModel restaurantListViewModel;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initMapViewModel();
        initRestaurantListViewModel();
        initLocationButton();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();

        return binding.getRoot();
    }

    private void initLocationButton(){
        binding.btnLocation.setOnClickListener(view -> {
            getUserLocation();
        });
    }


    private void initMapViewModel() {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
    }

    private void checkLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            getUserLocation();
        }else {
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

    private void updateCameraZoomWithNewLocation(Location location){
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    private void updateMapWithRestaurantMarker(List<PlaceDetail> placeDetails) {
        if (googleMap != null){
            googleMap.clear();
        }
        for (PlaceDetail placeDetail:placeDetails){

            MarkerOptions options = new MarkerOptions().position(new LatLng(placeDetail.getResult().getGeometry().getLocation().getLat(),placeDetail.getResult().getGeometry().getLocation().getLng()))
                    .title(placeDetail.getResult().getName());
            googleMap.addMarker(options).setTag(placeDetail.getResult().getPlaceId());
        }





    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},44);
    }

    private void initRestaurantListViewModel(){
        restaurantListViewModel = new ViewModelProvider(this).get(RestaurantListViewModel.class);
        restaurantListViewModel.init();

    }
    private void observeNearbyRestaurant(Location location){
        String userLocation = location.getLatitude() +","+ location.getLongitude();
        Log.e("LocationString",userLocation);
        restaurantListViewModel.searchNearbyRestaurants(userLocation,5000,"restaurant");
        restaurantListViewModel.nearbyRestaurantsLiveData.observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                updateMapWithRestaurantMarker(placeDetails);
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
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.map_style));
    }
}
