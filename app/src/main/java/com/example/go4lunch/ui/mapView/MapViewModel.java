package com.example.go4lunch.ui.mapView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.LocationModel;

public class MapViewModel extends AndroidViewModel {


    LiveData<LocationModel> locationModel;

    public MapViewModel(@NonNull Application application) {
        super(application);

    }




}
