package com.example.go4lunch.ui.mapView;

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

public class MapFragment extends Fragment {

    FragmentMapBinding binding;
    MapViewModel mapViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        initMapViewModel();
        startLocationUpdate();
        return binding.getRoot();
    }

    private void initMapViewModel() {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
    }

    private void startLocationUpdate(){
        mapViewModel.getLocationData();
        mapViewModel.locationModel.observe(getViewLifecycleOwner(),locationModel -> {
            Log.e("location", String.valueOf(locationModel.latitude));
        });
    }
}
