package com.example.go4lunch.ui.mapView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;
import com.example.go4lunch.util.RestaurantListHelper;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int SEARCH_QUERY_THRESHOLD = 3;
    public FragmentMapBinding binding;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    RestaurantListViewModel restaurantListViewModel;
    RestaurantDetailViewModel restaurantDetailViewModel;
    private List<User> userList;
    String locationString;


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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();

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

    private void searchNearbyRestaurantWithAutocomplete(String query) {
        restaurantListViewModel.getAutoCompleteNearbyRestaurantList(query,locationString,5000).observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
            @Override
            public void onChanged(List<PlaceDetail> placeDetails) {
                if (placeDetails != null){
                    updateMapWithRestaurantMarker(placeDetails,userList);
                    updateMapCameraWithAutocompleteResult(placeDetails.get(0));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume","ONREsume");
        //getUserLocation();
    }

    private void initLocationButton() {
        binding.btnLocation.setOnClickListener(view -> getUserLocation());
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
            if (location != null) {
                Log.e("Lattitude", String.valueOf(location.getLatitude()));
                Log.e("Longitude", String.valueOf(location.getLongitude()));
                locationString = String.valueOf(location.getLatitude() + "," + location.getLongitude());
                updateCameraZoomWithNewLocation(location);
                observeNearbyRestaurant(location);
            }else {
                if (!isLocationEnable()){
                    openLocationDialog();
                }else {
                    getCurrentLocation();

                }


            }
        });
    }

    private void updateMapCameraWithAutocompleteResult(PlaceDetail placeDetail){
        LatLng latLng = new LatLng(placeDetail.getResult().getGeometry().getLocation().getLat(),placeDetail.getResult().getGeometry().getLocation().getLng());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){

        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).setDurationMillis(3000).build();
        fusedLocationProviderClient.getCurrentLocation(currentLocationRequest,null).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                locationString = String.valueOf(location.getLatitude() + "," + location.getLongitude());
                Log.e("locationRequestTest",locationString);
                updateCameraZoomWithNewLocation(location);
                observeNearbyRestaurant(location);
            }
        });

    }



    private void openLocationDialog(){
        new AlertDialog.Builder(requireContext())
                .setMessage("enable location")
                .setPositiveButton("open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requireContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    private Boolean isLocationEnable(){
        LocationManager locationManager  = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
            Log.e("userlsitmaptest", String.valueOf(userList.contains(placeDetail.getResult().getPlaceId())));
            MarkerOptions options = new MarkerOptions().position(new LatLng(placeDetail.getResult().getGeometry().getLocation().getLat(), placeDetail.getResult().getGeometry().getLocation().getLng()))
                    .title(placeDetail.getResult().getName());
            if (RestaurantListHelper.UserGoIntoRestaurant(placeDetail, userList)) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin_green));

            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin));
            }
            googleMap.addMarker(options).setTag(placeDetail);
            googleMap.setOnInfoWindowClickListener( marker -> {
                restaurantListViewModel.select((PlaceDetail) marker.getTag());
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_restaurantDetail);
            });
        }
    }



    private void askLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }

    private void initRestaurantListViewModel() {
        restaurantListViewModel = new ViewModelProvider(requireActivity()).get(RestaurantListViewModel.class);



    }

    private void observeUsersList() {
        restaurantListViewModel.usersListMutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userList = users;
                Log.e("usersize", String.valueOf(userList.size()));
            }
        });
    }

    private void observeNearbyRestaurant(Location location) {
        String userLocation = location.getLatitude() + "," + location.getLongitude();
        Log.e("LocationString", userLocation);
        restaurantListViewModel.getAllRestaurants(userLocation,5000,"restaurant").observe(getViewLifecycleOwner(), new Observer<List<PlaceDetail>>() {
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
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style));
    }
}
