package com.example.go4lunch.ui.authentication;

import android.os.Bundle;
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
import com.example.go4lunch.ui.ViewModelFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignInFragment extends Fragment {

    FragmentSignInBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(getLayoutInflater());
        initViewModel();
        setupLoginButton();
        setupNavToSignUp();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    public void goToMainFragment(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
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
                    authViewModel.signInWithMailAndPassword(binding.etRegisterEmail.getText().toString(), binding.etRegisterPassword.getText().toString()).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if (s.equals(SUCCESS)){
                                goToMainFragment();
                            }else if (s.equals(ERROR)){
                                Toast.makeText(requireActivity(),"erro",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

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
