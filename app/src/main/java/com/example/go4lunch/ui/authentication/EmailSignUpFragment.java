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
import com.example.go4lunch.databinding.FragmentSignUpBinding;
import com.example.go4lunch.ui.ViewModelFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private AuthViewModel authViewModel;
    private NavController navController;
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

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

    public void initViewModel() {
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    public void goToMainFragment() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
    }

    public void setSignInNavTextView() {
        binding.tvGoToSignIn.setOnClickListener(view -> navController.navigate(R.id.action_emailSignUpFragment_to_nav_SignInFragment));
    }

    public void resetErrorMessage() {
        binding.tilRegisterUsername.setError(null);
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
        binding.tilRegisterEmailConfirmation.setError(null);
        binding.tilRegisterPasswordConfirmation.setError(null);
    }


    public void setRegisterBtn() {
        binding.btnSignUp.setOnClickListener(view -> {
            resetErrorMessage();
            if (usernameVerification() & emailFormatVerification() & passwordLengthCheck()) {
                Toast.makeText(getContext(), "all is okay", Toast.LENGTH_LONG).show();
                authViewModel.signUpWithEmailAndPassword
                                (
                                        Objects.requireNonNull(binding.edtRegisterEmail.getText()).toString(),
                                        Objects.requireNonNull(binding.etRegisterPassword.getText()).toString(),
                                        Objects.requireNonNull(binding.edtRegisterUsername.getText()).toString()
                                )
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result.equals(SUCCESS)) {
                                goToMainFragment();
                            } else if (result.equals(ERROR)) {
                                Toast.makeText(requireActivity(), getString(R.string.error_during_authentication), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }


    public boolean usernameVerification() {
        String regex = "^[a-z0-9_-]{3,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Objects.requireNonNull(binding.edtRegisterUsername.getText()).toString().trim());
        if (binding.edtRegisterUsername.getText().toString().isEmpty()) {
            binding.tilRegisterUsername.setError(getString(R.string.empty_field));
            return false;
        } else if (matcher.matches()) {
            return true;
        } else {
            binding.tilRegisterUsername.setError(getString(R.string.username_lenght_rule));
            return false;
        }
    }

    public boolean emailFormatVerification() {
        String regex = "^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Objects.requireNonNull(binding.edtRegisterEmail.getText()).toString());
        if (binding.edtRegisterEmail.getText().toString().trim().equals(Objects.requireNonNull(binding.edtRegisterEmailConfirmation.getText()).toString().trim())) {
            if (matcher.matches()) {
                return true;
            } else {
                binding.tilRegisterEmail.setError(getString(R.string.wrong_email_format));
                return false;
            }

        } else {
            binding.tilRegisterEmail.setError(getString(R.string.email_does_not_match));
            binding.tilRegisterEmailConfirmation.setError(getString(R.string.email_does_not_match));
            return false;
        }
    }


    public boolean passwordLengthCheck() {
        if (Objects.requireNonNull(binding.etRegisterPassword.getText()).toString().length() < 8 || binding.etRegisterPassword.getText().toString().isEmpty()) {
            binding.tilRegisterPassword.setError(getString(R.string.password_lenght_rule));
            return false;
        } else if (!binding.etRegisterPassword.getText().toString().equals(Objects.requireNonNull(binding.etRegisterPasswordConfirmation.getText()).toString())) {
            binding.tilRegisterPassword.setError(getString(R.string.password_does_not_match));
            binding.tilRegisterPasswordConfirmation.setError(getString(R.string.password_does_not_match));
            return false;
        }
        return true;
    }
}
