package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.Repositories.AuthRepository;
import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Retrofit.Go4LunchStreams;
import com.example.go4lunch.Retrofit.RetrofitService;
import com.example.go4lunch.ui.authentication.AuthViewModel;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;
import com.example.go4lunch.ui.workmatesView.WorkmatesViewModel;
import com.google.firebase.auth.FirebaseAuth;
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

    private final UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());

    private final PlaceDetailRepository placeDetailRepository = new PlaceDetailRepository(go4LunchStreams);

    private final AuthRepository authRepository = new AuthRepository(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance());

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
        if (modelClass.isAssignableFrom(WorkmatesViewModel.class)){
            return (T) new WorkmatesViewModel(userRepository);
        }
        if (modelClass.isAssignableFrom(AuthViewModel.class)){
            return (T) new AuthViewModel(authRepository);
        }
        throw new IllegalArgumentException("unknown ViewModel class");
    }

}
