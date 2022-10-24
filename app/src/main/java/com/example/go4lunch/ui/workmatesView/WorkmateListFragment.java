package com.example.go4lunch.ui.workmatesView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.FragmentWorkmatesListBinding;
import com.example.go4lunch.ui.ViewModelFactory;

public class WorkmateListFragment extends Fragment {
    private FragmentWorkmatesListBinding binding;
    private WorkmatesViewModel workmatesViewModel;
    private WorkmatesListRecyclerViewAdapter workmatesListRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkmatesListBinding.inflate(getLayoutInflater());
        initViewModel();
        configureWorkmatesRecyclerView();
        observeUsersList();
        return binding.getRoot();
    }

    private void initViewModel() {
        workmatesViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);
    }

    private void configureWorkmatesRecyclerView() {
        RecyclerView recyclerView = binding.rcWorkmates;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        workmatesListRecyclerViewAdapter = new WorkmatesListRecyclerViewAdapter(Glide.with(this));
        recyclerView.setAdapter(workmatesListRecyclerViewAdapter);
    }

    private void observeUsersList() {
        workmatesViewModel.userListMutableLiveData.observe(getViewLifecycleOwner(), users -> workmatesListRecyclerViewAdapter.setUsersList(users));
    }


    @Override
    public void onResume() {
        super.onResume();
        workmatesViewModel.getUsersListFromFirebase();
    }
}
