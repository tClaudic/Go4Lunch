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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentSignInBinding;
import com.example.go4lunch.ui.ViewModelFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignInFragment extends Fragment {

    private FragmentSignInBinding binding;
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

    public void goToMainFragment() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
    }


    private void setupNavToSignUp() {
        binding.tvGoToSignIn.setOnClickListener(view -> navController.navigate(R.id.action_nav_SignInFragment_to_emailSignUpFragment));
    }

    private void setupLoginButton() {
        binding.btnSignUp.setOnClickListener(view -> {
            resetErrorMessage();
            if (emailFormatVerification() && passwordVerification()) {
                Toast.makeText(getContext(), "all is okay", Toast.LENGTH_LONG).show();
                authViewModel.signInWithMailAndPassword(Objects.requireNonNull(binding.etRegisterEmail.getText()).toString(), Objects.requireNonNull(binding.etRegisterPassword.getText()).toString())
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result.equals(SUCCESS)) {
                                goToMainFragment();
                            } else if (result.equals(ERROR)) {
                                Toast.makeText(requireActivity(), R.string.error_during_authentication, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    public void resetErrorMessage() {
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
    }

    public Boolean passwordVerification() {
        boolean passwordVerification = false;
        if (Objects.requireNonNull(binding.etRegisterPassword.getText()).toString().isEmpty()) {
            binding.tilRegisterPassword.setError(getString(R.string.empty_field));
        } else {
            passwordVerification = true;
        }
        return passwordVerification;
    }


    public Boolean emailFormatVerification() {
        String regex = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Objects.requireNonNull(binding.etRegisterEmail.getText()).toString());
        if (binding.etRegisterEmail.getText().toString().isEmpty()) {
            binding.tilRegisterEmail.setError(getString(R.string.empty_field));
            return false;

        }
        if (!matcher.matches()) {
            binding.tilRegisterEmail.setError(getString(R.string.wrong_email_format));
            return false;
        }
        return true;
    }

}
