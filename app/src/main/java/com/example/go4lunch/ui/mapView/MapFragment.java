package com.example.go4lunch.ui.mapView;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapFragment extends Fragment {

    public FragmentMapBinding binding;
    public MapViewModel mapViewModel;
    public FusedLocationProviderClient fusedLocationProviderClient;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        initMapViewModel();
        initLocationButton();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return binding.getRoot();
    }

    private void initLocationButton(){
        binding.btnLocation.setOnClickListener(view -> {
            startLocationUpdate();
        });
    }


    private void initMapViewModel() {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.getLocationData();
    }

    private void startLocationUpdate(){
        Log.e("Hello","Hello");
        mapViewModel.locationModel.observe(getViewLifecycleOwner(),locationModel -> {
            Log.e("location", String.valueOf(locationModel.latitude));
        });
    }


}
