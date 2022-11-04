package com.example.go4lunch.ui.workmatesView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentWorkmatesListBinding;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.ViewModelFactory;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;
import com.example.go4lunch.util.ItemClickSupport;

public class WorkmateListFragment extends Fragment {
    private FragmentWorkmatesListBinding binding;
    private WorkmatesViewModel workmatesViewModel;
    private RestaurantListViewModel restaurantListViewModel;
    private RecyclerView recyclerView;
    private WorkmatesListRecyclerViewAdapter workmatesListRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkmatesListBinding.inflate(getLayoutInflater());
        initViewModel();
        configureWorkmatesRecyclerView();
        observeUsersList();
        initOnClickRecyclerView();
        return binding.getRoot();
    }

    private void initViewModel() {
        workmatesViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);
        restaurantListViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantListViewModel.class);

    }

    private void configureWorkmatesRecyclerView() {
        recyclerView = binding.rcWorkmates;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        workmatesListRecyclerViewAdapter = new WorkmatesListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(workmatesListRecyclerViewAdapter);
    }

    private void observeUsersList() {
        workmatesViewModel.userListMutableLiveData.observe(getViewLifecycleOwner(), users -> workmatesListRecyclerViewAdapter.setUsersList(users));
    }

    private void initOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, binding.rcWorkmates.getId())
                .setOnItemClickListener((recyclerView1, position, v) -> {

                        User user = workmatesListRecyclerViewAdapter.getUser(position);
                        if (!user.restaurantChoice.isEmpty()) {
                            if (isNetworkEnable()) {
                                workmatesViewModel.getPlaceDetailByUserChoice(user.restaurantChoice).observe(getViewLifecycleOwner(), placeDetail -> {
                                    restaurantListViewModel.select(placeDetail);
                                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_workmatesView_to_nav_restaurantDetail);
                                });
                            }else {
                                Toast.makeText(requireActivity(),"Check your internet connection", Toast.LENGTH_LONG).show();
                            }
                        }

                });
    }

    @SuppressLint("MissingPermission")
    private Boolean isNetworkEnable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    @Override
    public void onResume() {
        super.onResume();
        workmatesViewModel.getUsersListFromFirebase();
    }
}
