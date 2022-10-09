package com.example.go4lunch.ui.splashScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSplashScreenBinding;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.ViewModelFactory;
import com.example.go4lunch.ui.authentication.AuthViewModel;

import java.util.Objects;

public class SplashFragment extends Fragment {

    private FragmentSplashScreenBinding binding;
    AuthViewModel authViewModel;
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        initAuthViewModel();
        checkIfUserIsAuthenticated();
        return binding.getRoot();
    }

    private void initAuthViewModel(){
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    private void checkIfUserIsAuthenticated(){
        authViewModel.checkIfUserIsAuthenticated().observe(getViewLifecycleOwner(), result -> {
            if (!Objects.equals(result, ERROR)){
                getUserFromDatabase(result);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
            }else {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_login);
            }
        });
    }
    private void getUserFromDatabase(String uid){
        authViewModel.getUserById(uid);
    }

}
