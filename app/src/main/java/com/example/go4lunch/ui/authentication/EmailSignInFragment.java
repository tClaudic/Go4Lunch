package com.example.go4lunch.ui.authentication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.go4lunch.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;

public class EmailSignInFragment extends Fragment {

    FragmentSignInBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AuthViewModel authViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(getLayoutInflater());
        initViewModel();
        setupLoginButton();
        return binding.getRoot();
    }


    private void initViewModel(){
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupLoginButton(){
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailLogin = binding.etRegisterEmail.getText().toString();
                Log.e("email",emailLogin);
                String passwordLogin = binding.etRegisterPassword.getText().toString();
                Log.e("password",passwordLogin);
                authViewModel.signInWithMailAndPassword(emailLogin,passwordLogin);
            }
        });

    }

    private void setupRegisterButton(){
        binding.btnSignUp .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailRegister = binding.etRegisterEmail.getText().toString();
                String passwordRegister = binding.etRegisterPassword.getText().toString();

            }
        });
    }
}
