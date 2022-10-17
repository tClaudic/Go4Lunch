package com.example.go4lunch.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.listView.RestaurantListViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
@RunWith(MockitoJUnitRunner.class)
public class RestaurantListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PlaceDetailRepository placeDetailRepository;

    @Mock
    private UserRepository userRepository;

    private RestaurantListViewModel restaurantListViewModel;

    @Before
    public void setup(){
        MutableLiveData<List<PlaceDetail>> placeDetailList = new MutableLiveData<>();
        given(placeDetailRepository.getNearbyRestaurantsLiveData(anyString(),anyInt(),anyString())).willReturn(placeDetailList);
        List<PlaceDetail> getDefaultPlaceDetailList = new ArrayList<>();
        placeDetailList.setValue(getDefaultPlaceDetailList);
        given(placeDetailRepository.getNearbyRestaurantListWithAutoComplete(anyString(),anyString(),anyInt())).willReturn(placeDetailList);
        restaurantListViewModel = new RestaurantListViewModel(placeDetailRepository,userRepository);

    }

    @Test
    public void getAllRestaurants_onObserve_returnPlaceDetailList(){
        LiveDataTestUtils.observeForTesting(restaurantListViewModel.getAllRestaurants(anyString(),anyInt(),anyString()), liveData -> {
            assertEquals(getDefaultPlaceDetailList(),liveData.getValue());
        });
    }

    @Test
    public void getAutoCompleteNearbyRestaurantList_onObserve_returnPlaceDetailList(){
        MutableLiveData<List<PlaceDetail>> placeDetailList = new MutableLiveData<>();
        placeDetailList.setValue(getDefaultPlaceDetailList());
        given(placeDetailRepository.getNearbyRestaurantListWithAutoComplete(anyString(),anyString(),anyInt())).willReturn(placeDetailList);
        LiveDataTestUtils.observeForTesting(restaurantListViewModel.getAutoCompleteNearbyRestaurantList(anyString(),anyString(),anyInt()),liveData -> {
            assertEquals(getDefaultPlaceDetailList(),liveData.getValue());
        });
    }
    @Test
    public void getUsersList_onObserve_returnUsersList(){
        MutableLiveData<List<User>> userList = new MutableLiveData<>();
        userList.setValue(getDefaultUsersList());
        given(userRepository.getUsersListFromFirebase()).willReturn(userList);
        LiveDataTestUtils.observeForTesting(restaurantListViewModel.getUsersLists(),liveData -> {
            assertEquals(getDefaultUsersList(),liveData.getValue());
        });
    }

    @Test
    public void getSelected_onObserve_returnPlaceDetail(){
        MutableLiveData<PlaceDetail> selected = new MutableLiveData<>();
        restaurantListViewModel.select(placeDetail);
        LiveDataTestUtils.observeForTesting(restaurantListViewModel.getSelected(),liveData -> {
            assertEquals(placeDetail,selected.getValue());
        });
    }


    private PlaceDetail placeDetail;

    private List<User> getDefaultUsersList(){
        List<User> list = new ArrayList<>();
        return list;
    }

    private List<PlaceDetail> getDefaultPlaceDetailList(){
        List<PlaceDetail> list = new ArrayList<>();
        return list;
    }


}
