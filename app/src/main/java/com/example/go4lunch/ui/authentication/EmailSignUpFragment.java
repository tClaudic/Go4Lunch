package com.example.go4lunch.ui.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.go4lunch.databinding.FragmentSignUpBinding;

public class EmailSignUpFragment extends Fragment {

    FragmentSignUpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }


    public void setRegisterBtn(){
        binding.btnRegisterConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void editTextVerification(){

    }

    public void passwordLengthCheck(){
        
    }
}
