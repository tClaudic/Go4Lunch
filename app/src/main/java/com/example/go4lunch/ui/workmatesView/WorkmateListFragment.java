package com.example.go4lunch.ui.workmatesView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private RestaurantDetailViewModel restaurantDetailViewModel;
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
        restaurantDetailViewModel = new ViewModelProvider(requireActivity(),ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);

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
                    if (!user.restaurantChoice.isEmpty()){
                        restaurantDetailViewModel.getRestaurantDetailByUserChoice(user.restaurantChoice).observe(getViewLifecycleOwner(), placeDetail -> {
                            restaurantListViewModel.select(placeDetail);
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_nav_workmatesView_to_nav_restaurantDetail);
                        });
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        workmatesViewModel.getUsersListFromFirebase();
    }
}
