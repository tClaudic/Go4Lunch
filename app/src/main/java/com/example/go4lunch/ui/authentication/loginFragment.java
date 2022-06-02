package com.example.go4lunch.ui.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentLoginBinding;
import com.example.go4lunch.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class loginFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        initSignInButton();
        initGoogleSignInClient();
        initAuthViewModel();
        return binding.getRoot();
    }


    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void initSignInButton() {
        SignInButton googleSignInButton = binding.ntmGoogleLogin;
        googleSignInButton.setOnClickListener(view -> signIn());

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInActivityResultLauncher.launch(signInIntent);


    }

    ActivityResultLauncher<Intent> googleSignInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("Login", "Succesfluyy logged");
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        GoogleSignInAccount googleSignInAccount = task.getResult();
                        if (googleSignInAccount != null) {
                            getGoogleAuthCredential(googleSignInAccount);
                        }
                    }

                }
            }
    );

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(authCredential);

    }

    private void signInWithGoogleAuthCredential(AuthCredential authCredential) {
        authViewModel.signInWithGoogle(authCredential);
        authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser ->{
            if (authenticatedUser.isNew){
                createNewUser(authenticatedUser);
            }else {
                goToMainFragment();

            }
        });
    }

    private void goToMainFragment() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_mapView);
    }

    private void createNewUser(User authenticatedUser) {
        authViewModel.createUser(authenticatedUser);
        authViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated){
                Toast.makeText(getActivity(),"Your account is successfully created",Toast.LENGTH_LONG).show();
            }
            goToMainFragment();
        });
    }



    private void logout() {

    }
}
