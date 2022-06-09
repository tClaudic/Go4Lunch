package com.example.go4lunch.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.LocationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationLiveData extends LiveData<LocationModel> {
    private Context context;
    public LocationLiveData(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }


    private  FusedLocationProviderClient fusedLocationProviderClient;



    private LocationModel setLocationData(Location location) {
        return new LocationModel(location.getLongitude(), location.getLatitude());
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                setLocationData(location);
            }
        }
    };

    private LocationRequest setLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        return locationRequest.setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(setLocationRequest(),
                locationCallback,
                null
        );
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this::setLocationData);
        startLocationUpdates();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
