package com.example.go4lunch.ui.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.go4lunch.databinding.FragmentSignUpBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignUpFragment extends Fragment {

    FragmentSignUpBinding binding;
    AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        initViewModel();
        setRegisterBtn();
        return binding.getRoot();

    }


    public void initViewModel(){
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

    }
    public void setRegisterBtn(){
        binding.btnRegisterConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.edtRegisterUsername.toString();
                String userEmail = binding.edtRegisterEmail.toString();
                String userEmailConfirmation = binding.edtRegisterEmailConfirmation.toString();
                String password = binding.edtRegisterPassword.toString();
                String passwordConfirmation = binding.edtRegisterPasswordConfirmation.toString();

            }
        });
    }

    public boolean emailFormatVerification(String userEmail, String userEmailConfirmation){
       String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        if (userEmail.trim().equals(userEmailConfirmation.trim())){
            if (matcher.matches()){
                return true;
            }else {
                Toast.makeText(getContext(),"Email is in the wrong format",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getContext(),"Email adress are different",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public boolean editTextVerification(String stringVerification){
        if (stringVerification.isEmpty()){
            Toast.makeText(getContext(),"Some field are not filled",Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }

    public void passwordLengthCheck(){
        
    }
}
