package com.example.go4lunch.ui.splashScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSplashScreenBinding;
import com.example.go4lunch.ui.ViewModelFactory;
import com.example.go4lunch.ui.authentication.AuthViewModel;

import java.util.Objects;

public class SplashFragment extends Fragment {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    AuthViewModel authViewModel;
    private FragmentSplashScreenBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        initAuthViewModel();
        checkIfUserIsAuthenticated();
        return binding.getRoot();
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    private void checkIfUserIsAuthenticated() {
        authViewModel.checkIfUserIsAuthenticated().observe(getViewLifecycleOwner(), result -> {
            if (!Objects.equals(result, ERROR)) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
            } else {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_login);
            }
        });
    }

}
