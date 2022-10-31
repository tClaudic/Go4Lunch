package com.example.go4lunch.ui.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentLoginBinding;
import com.example.go4lunch.ui.ViewModelFactory;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.Arrays;

public class loginFragment extends Fragment {


    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    private final CallbackManager callbackManager = CallbackManager.Factory.create();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private final ActivityResultLauncher<Intent> googleSignInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    GoogleSignInAccount googleSignInAccount = task.getResult();
                    if (googleSignInAccount != null) {
                        getGoogleAuthCredential(googleSignInAccount);
                    }
                } else {
                    Toast.makeText(requireActivity(), R.string.google_activity_result_error_message, Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleSignInClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        initSignInButton();
        initTwitterLoginButton();
        initFacebookLoginButton();
        initAuthViewModel();
        initEmailLoginButton();
        setOnBackPressed();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleFacebookLoginResult();
    }

    private void setOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    private void goToMainFragment() {

        NavHostFragment.findNavController(this).navigate(R.id.nav_mapView);
    }

    private void initTwitterLoginButton() {
        binding.btnTwitterLogin.setOnClickListener(view -> initTwitterLogin());
    }

    private void initSignInButton() {
        binding.ntmGoogleLogin.setOnClickListener(view -> setGoogleSignInActivityResultLauncher());
    }

    private void initEmailLoginButton() {
        binding.btnEmailLogin.setOnClickListener(view -> Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_SignInFragment));
    }


    private void initTwitterLogin() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder(getString(R.string.twitter_provider_id));
        provider.addCustomParameter(getString(R.string.ProviderLangKeyParam), getString(R.string.providerFrParamValue));
        firebaseAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener(authResult -> getTwitterCredentialsAndSignIn(authResult.getCredential()))
                .addOnFailureListener(e -> {
                            Log.e("TwitterError", e.getMessage());
                            Toast.makeText(requireActivity(),
                                    R.string.twitter_authentication_error_message,
                                    Toast.LENGTH_LONG).show();
                        }
                );
    }

    private void initFacebookLoginButton() {
        binding.btnFacebookLogin.setOnClickListener(view ->
                LoginManager.getInstance().logInWithReadPermissions(
                        loginFragment.this,
                        callbackManager,
                        Arrays.asList(getString(R.string.facebook_public_profile_permission), getString(R.string.facebook_email_permission))));
    }


    private void getTwitterCredentialsAndSignIn(AuthCredential authCredential) {
        authViewModel.signInWithAuthCredential(authCredential).observeForever(result -> {
            if (result.equals(SUCCESS)) {
                goToMainFragment();
            } else if (result.equals(ERROR)) {
                Toast.makeText(requireActivity(), getString(R.string.error_during_authentication), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void handleFacebookLoginResult() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookCredential(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(requireActivity(), R.string.Facebook_auth_cancel, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.e(getString(R.string.FacebookAuthErrorLog), e.getMessage());
                Toast.makeText(requireActivity(), R.string.facebook_auth_credential_error_message, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getFacebookCredential(AccessToken token) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithFacebookAuthCredential(authCredential);
    }

    private void signInWithFacebookAuthCredential(AuthCredential authCredential) {
        authViewModel.signInWithAuthCredential(authCredential).observeForever(result -> {
            if (result.equals(SUCCESS)) {
                goToMainFragment();
            } else if (result.equals(ERROR)) {
                Toast.makeText(requireActivity(),
                        R.string.facebook_auth_credential_error_message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);
    }


    private void setGoogleSignInActivityResultLauncher() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInActivityResultLauncher.launch(signInIntent);
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(authCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential authCredential) {
        authViewModel.signInWithAuthCredential(authCredential).observe(getViewLifecycleOwner(), result -> {
            if (result.equals(SUCCESS)) {
                goToMainFragment();
            } else if (result.equals(ERROR)) {
                Toast.makeText(requireActivity(), R.string.google_activity_result_error_message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
