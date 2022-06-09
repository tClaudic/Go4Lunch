package com.example.go4lunch.ui.mapView;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.LocationModel;
import com.example.go4lunch.util.LocationLiveData;

public class MapViewModel extends AndroidViewModel {

    private final LocationLiveData locationLiveData;
    LiveData<LocationModel> locationModel;

    public MapViewModel(@NonNull Application application) {
        super(application);
        locationLiveData = new LocationLiveData(application.getApplicationContext());
    }

    public void getLocationData(){
        locationModel = locationLiveData;
    }


}
