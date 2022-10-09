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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSignUpBinding;
import com.example.go4lunch.ui.ViewModelFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignUpFragment extends Fragment {

    FragmentSignUpBinding binding;
    AuthViewModel authViewModel;
    NavController navController;
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        initViewModel();
        setRegisterBtn();
        setSignInNavTextView();
        return binding.getRoot();

    }

    public void resetErrorMessage() {
        binding.tilRegisterUsername.setError(null);
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
        binding.tilRegisterEmailConfirmation.setError(null);
        binding.tilRegisterPasswordConfirmation.setError(null);
    }


    public void initViewModel() {
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    public void setSignInNavTextView() {
        binding.tvGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_emailSignUpFragment_to_nav_SignInFragment);
            }
        });
    }

    public void setRegisterBtn() {
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetErrorMessage();
                if (usernameVerification() & emailFormatVerification() & passwordLengthCheck()) {
                    Toast.makeText(getContext(), "all is okay", Toast.LENGTH_LONG).show();
                    authViewModel.signUpWithEmailAndPassword(binding.edtRegisterEmail.getText().toString(), binding.etRegisterPassword.getText().toString(), binding.edtRegisterUsername.getText().toString())
                            .observe(getViewLifecycleOwner(), result -> {
                                if (result.equals(SUCCESS)) {
                                    goToMainFragment();
                                } else if (result.equals(ERROR)) {
                                    Toast.makeText(requireActivity(), "ERROR", Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });
    }


    public void goToMainFragment() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
    }

    public boolean usernameVerification() {
        String regex = "^[a-z0-9_-]{3,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(binding.edtRegisterUsername.getText().toString().trim());
        if (binding.edtRegisterUsername.getText().toString().isEmpty()) {
            binding.tilRegisterUsername.setError("You cannot let this field empty");
            return false;
        } else if (matcher.matches()) {
            return true;
        } else {
            binding.tilRegisterUsername.setError("Your username should contain at least 3 characters");
            return false;
        }
    }

    public boolean emailFormatVerification() {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(binding.edtRegisterEmail.getText().toString());
        Log.e("equaltest", String.valueOf(binding.edtRegisterEmail.getText().toString().trim().equals(binding.edtRegisterEmailConfirmation.getText().toString().trim())));
        Log.e("mail1test", String.valueOf(binding.edtRegisterEmail.getText().toString().trim().length()));
        Log.e("mail2test", String.valueOf(binding.edtRegisterEmailConfirmation.getText().toString().trim().length()));
        if (binding.edtRegisterEmail.getText().toString().trim().equals(binding.edtRegisterEmailConfirmation.getText().toString().trim())) {
            if (matcher.matches()) {
                return true;
            } else {
                binding.tilRegisterEmail.setError("Email is the wrong format pls follow : aaaa@aaaa.aa");
                return false;
            }

        } else {
            binding.tilRegisterEmail.setError("Emails are not the same");
            binding.tilRegisterEmailConfirmation.setError("Emails are not the same");
            return false;
        }
    }


    public boolean passwordLengthCheck() {
        if (binding.etRegisterPassword.getText().toString().length() < 8 || binding.etRegisterPassword.getText().toString().isEmpty()) {
            binding.tilRegisterPassword.setError("Password must at least contain 8 character");
            return false;
        } else if (!binding.etRegisterPassword.getText().toString().equals(binding.etRegisterPasswordConfirmation.getText().toString())) {
            binding.tilRegisterPassword.setError("password are not the same");
            binding.tilRegisterPasswordConfirmation.setError("password are not the same");
            return false;
        }
        return true;
    }
}
