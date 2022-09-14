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

import java.nio.file.attribute.UserPrincipalLookupService;

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
        authViewModel.isUserAuthenticatedLiveData.observe(getViewLifecycleOwner(), user -> {
            if (!user.isAuthenticated){
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_login);
            }
            else{
                getUserFromDatabase(user.uid);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
            }
        });
    }
    private void getUserFromDatabase(String uid){
        authViewModel.setUid(uid);
    }

}
