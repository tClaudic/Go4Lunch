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
import com.example.go4lunch.databinding.FragmentLoginBinding;
import com.example.go4lunch.databinding.FragmentSplashScreenBinding;
import com.example.go4lunch.ui.authentication.AuthViewModel;

public class SplashFragment extends Fragment {

    private FragmentSplashScreenBinding binding;
    AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        initAuthViewModel();
        checkIfUserIsAuthenticated();
        return binding.getRoot();
    }

    private void initAuthViewModel(){
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void checkIfUserIsAuthenticated(){
        authViewModel.checkIfUserIsAuthenticated();
        authViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
            if (!user.isAuthenticated){
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_splashScreen_to_nav_login);
            }
            else{
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_splashScreen_to_nav_mapView);
            }
        });
    }
}
