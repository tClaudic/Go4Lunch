package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Rertrofit.Go4LunchStreams;
import com.example.go4lunch.Rertrofit.RetrofitService;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance(){
        if (factory == null){
            synchronized (ViewModelFactory.class){
                if (factory == null){
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private final Go4LunchStreams go4LunchStreams = new Go4LunchStreams(RetrofitService.getGooglePlaceApiCall());

    private final UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance());

    private final PlaceDetailRepository placeDetailRepository = new PlaceDetailRepository(go4LunchStreams);

    private ViewModelFactory(){}

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)){
            return (T) new RestaurantDetailViewModel(placeDetailRepository,userRepository);
        }
        if (modelClass.isAssignableFrom(RestaurantListViewModel.class)){
            return (T) new RestaurantListViewModel(placeDetailRepository,userRepository);
        }
        throw new IllegalArgumentException("unknown ViewModel class");
    }

}
