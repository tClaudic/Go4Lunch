package com.example.go4lunch.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.authentication.AuthViewModel;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.logout.LogoutConfirmation;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;
    private User currentAuthenticatedUser;
    private RestaurantListViewModel restaurantListViewModel;
    private RestaurantDetailViewModel restaurantDetailViewModel;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        drawer = binding.drawerLayout;
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_mapView, R.id.nav_listView, R.id.nav_workmatesView, R.id.nav_restaurantDetail)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setNavigationViewItemSelected();
        setupNavControllerOnDestinationChanged();

    }

    private void initViewModel() {
        restaurantListViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantListViewModel.class);
        restaurantDetailViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);
        authViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthViewModel.class);
    }

    private void setNavigationViewItemSelected() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_restaurantDetail) {
                setupNavigationToRestaurantDetail();
            }
            if (item.getItemId() == R.id.nav_logout) {
                showLogOutDialogFragment();
                drawer.close();
            }
            if (item.getItemId() == R.id.nav_settings) {
                navController.navigate(R.id.nav_settings);
                hideBottomNavigationBar();
                drawer.close();
            }
            return true;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setupNavControllerOnDestinationChanged() {
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            switch (navDestination.getId()) {
                case R.id.nav_restaurantDetail:
                case R.id.nav_login:
                case R.id.emailSignUpFragment:
                case R.id.nav_splashScreen:
                case R.id.nav_SignInFragment:
                    hideBottomNavigationBar();
                    hideToolbar();
                    break;

                default:
                    showBottomNavigationBar();
                    showActionBar();
            }
        });
    }


    private void updateNavHeader() {
        View headerView = binding.navView.getHeaderView(0);
        ImageView userPicture = headerView.findViewById(R.id.nav_header_usermane_iv);
        TextView userName = headerView.findViewById(R.id.nav_header_username_tv);
        TextView userEmail = headerView.findViewById(R.id.nav_header_user_email_tv);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            authViewModel.getUserById(firebaseUser.getUid()).observe(this, user -> {
                currentAuthenticatedUser = user;
                userName.setText(user.name);
                userEmail.setText(user.email);
                Glide.with(this).load(user.urlPicture).circleCrop().into(userPicture);
            });

        }

    }


    private void setupNavigationToRestaurantDetail() {
        if (checkIfUserHasRestaurantChoice()) {
            Log.e("userRestaurantChoice", currentAuthenticatedUser.restaurantChoice);
            restaurantDetailViewModel.getRestaurantDetailByUserChoice(currentAuthenticatedUser.restaurantChoice).observe(this, placeDetail -> {
                restaurantListViewModel.select(placeDetail);
                hideBottomNavigationBar();
                hideToolbar();

                navController.navigate(R.id.nav_restaurantDetail);
                drawer.close();
            });

        } else {
            Toast.makeText(this,
                    R.string.user_no_restaurant_choosen,
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_login);
        } else {
            dbUserEvent();
        }
    }


    private void dbUserEvent() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final DocumentReference documentReference = rootRef.collection("users").document(Objects.requireNonNull(firebaseUser).getUid());
        documentReference.addSnapshotListener(this::onEvent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);

    }

    private void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
        if (value != null && value.exists()) {
            updateNavHeader();
        }
    }

    private Boolean checkIfUserHasRestaurantChoice() {
        return !currentAuthenticatedUser.restaurantChoice.isEmpty();
    }

    private void showLogOutDialogFragment() {
        new LogoutConfirmation().show(getSupportFragmentManager(), "");
    }

    private void showActionBar() {
        Objects.requireNonNull(getSupportActionBar()).show();
    }


    public void hideToolbar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
    }


    public void hideBottomNavigationBar() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationBar() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }


}