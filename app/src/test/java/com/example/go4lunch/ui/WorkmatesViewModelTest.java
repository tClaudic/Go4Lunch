package com.example.go4lunch.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.workmatesView.WorkmatesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.BDDMockito.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WorkmatesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public UserRepository userRepository;

    private WorkmatesViewModel workmatesViewModel;

    @Before
    public void setup() {
        MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();
        given(userRepository.getUsersListMutableLiveData()).willReturn(usersMutableLiveData);
        usersMutableLiveData.setValue(getUsersList());
        workmatesViewModel = new WorkmatesViewModel(userRepository);
    }

    @Test
    public void getUsersListMutableLiveData_onSuccess_returnUsersList() {
        LiveDataTestUtils.observeForTesting(workmatesViewModel.userListMutableLiveData, liveData -> {
            assertEquals(getUsersList(),liveData.getValue());
        });
    }

    public List<User> getUsersList() {
        List<User> userList = new ArrayList<>();
        return userList;
    }

}
