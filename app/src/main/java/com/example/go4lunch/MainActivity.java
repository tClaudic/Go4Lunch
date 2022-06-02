package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.ui.logout.LogoutConfirmation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onStart","onCreate");


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_mapView, R.id.nav_listView, R.id.nav_workmatesView)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            switch (navDestination.getId()) {

                case R.id.nav_logout:
                    showLogOutDialogFragment();
                    break;
                case R.id.nav_restaurantDetail:
                    hideBottomNavigationBar();
                    break;
                case R.id.nav_login:
                case R.id.nav_splashScreen:
                    hideBottomNavigationBar();
                    hideToolbar();
                    break;

                default:
                    showBottomNavigationBar();
                    showActionBar();


            }

        });

    }

    private void showLogOutDialogFragment(){
        new LogoutConfirmation().show(getSupportFragmentManager(),"");
    }

    private void showActionBar() {
        getSupportActionBar().show();
    }


    public void hideToolbar() {
        getSupportActionBar().hide();
    }


    public void hideBottomNavigationBar() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNavigationBar() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        if (firebaseUser == null){
            Navigation.findNavController(this,R.id.nav_host_fragment_content_main).navigate(R.id.nav_login);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","onStart");
        firebaseAuth.addAuthStateListener(this);

    }
}