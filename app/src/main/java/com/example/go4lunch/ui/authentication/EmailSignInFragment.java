package com.example.go4lunch.ui.authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSignInBinding;
import com.example.go4lunch.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignInFragment extends Fragment {

    FragmentSignInBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AuthViewModel authViewModel;
    private NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(getLayoutInflater());
        initViewModel();
        setupLoginButton();
        setupNavToSignUp();
        test();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    public void goToMainFragment(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
    }

    public void test(){
        authViewModel.testUser.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.isAuthenticated){
                    goToMainFragment();
                }
                else {
                    Toast.makeText(getContext(),"Check your email or your password",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void observeLoggedUser(){
        authViewModel.checkIfUserIsAuthenticated();
        authViewModel.authenticatedUserLiveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.isAuthenticated){
                    goToMainFragment();
                }
                Log.e("test","LoggedUser" + user.email);
                goToMainFragment();
            }
        });
    }

    private void setupNavToSignUp() {
        binding.tvGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_nav_SignInFragment_to_emailSignUpFragment);
            }
        });
    }

    private void setupLoginButton() {
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetErrorMessage();
                if (emailFormatVerification() && passwordVerification()) {
                    Toast.makeText(getContext(), "all is okay", Toast.LENGTH_LONG).show();
                    authViewModel.testEmailSignIn(binding.etRegisterEmail.getText().toString(), binding.etRegisterPassword.getText().toString());
                }


            }
        });

    }

    public void resetErrorMessage() {
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
    }

    public Boolean passwordVerification() {
        boolean passwordVerification = false;
        if (binding.etRegisterPassword.getText().toString().isEmpty()) {
            binding.tilRegisterPassword.setError("you cannot let this field empty");
        } else {
            passwordVerification = true;
        }
        return passwordVerification;
    }


    public Boolean emailFormatVerification() {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(binding.etRegisterEmail.getText().toString());
        if (binding.etRegisterEmail.getText().toString().isEmpty()) {
            binding.tilRegisterEmail.setError("you cannot let this field empty");
            return false;

        }
        if (!matcher.matches()) {
            binding.tilRegisterEmail.setError("The enmail is the wronf format please follow as aaaa@aaaa.com");
            return false;
        }
        return true;
    }

}
