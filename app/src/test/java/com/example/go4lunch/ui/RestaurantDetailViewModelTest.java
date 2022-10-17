package com.example.go4lunch.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.restaurantDetail.RestaurantDetailViewModel;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public UserRepository userRepository;

    @Mock
    public PlaceDetailRepository placeDetailRepository;

    private RestaurantDetailViewModel restaurantDetailViewModel;

    @Before
    public void setup(){
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        given(userRepository.getAuthenticatedUserMutableLiveData()).willReturn(authenticatedUser);
        authenticatedUser.setValue(user);
        restaurantDetailViewModel = new RestaurantDetailViewModel(placeDetailRepository,userRepository);
    }

    @Test
    public void getAuthenticatedUser_onObserve_returnUser(){
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.authenticatedUser, liveData -> {
            assertEquals(user,liveData.getValue());
        });
    }

    @Test
    public void getUsersListFilteredByRestaurantChoice_onObserve_returnFilteredUsersList(){
        MutableLiveData<List<User>> getUserListByRestaurantChoice = new MutableLiveData<>();
        getUserListByRestaurantChoice.setValue(getDefaultUserList());
        given(userRepository.getUsersFilteredListFromFirebase(anyString())).willReturn(getUserListByRestaurantChoice);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.getUsersListFilteredByRestaurantChoice(anyString()),liveData -> {
            assertEquals(getDefaultUserList(),liveData.getValue());
        });
    }

    @Test
    public void getAuthenticatedLiveDataUser_onObserve_returnUser(){
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        authenticatedUser.setValue(user);
        given(userRepository.getAuthenticatedUserMutableLiveData()).willReturn(authenticatedUser);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.getAuthenticatedLiveDataUser(),liveData -> {
            assertEquals(user,liveData.getValue());
        });
    }

    @Test
    public void getRestaurantDetailByUserChoice_onObserve_returnPlaceDetail(){
        MutableLiveData<PlaceDetail> placeDetailMutableLiveData = new MutableLiveData<>();
        placeDetailMutableLiveData.setValue(placeDetail);
        given(placeDetailRepository.searchNearbyPlace(anyString())).willReturn(placeDetailMutableLiveData);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.getRestaurantDetailByUserChoice(anyString()),liveData -> {
            assertEquals(placeDetail,liveData.getValue());
        });
    }

    @Test
    public void setRestaurantChoiceName_OnSuccess_ReturnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("success");
        given(userRepository.addUserRestaurantChoiceNameInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.setRestaurantChoiceName(anyString(),anyString()),liveData -> {
            assertEquals("success",liveData.getValue());
        });
    }
    @Test
    public void setRestaurantChoiceName_OnError_ReturnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(userRepository.addUserRestaurantChoiceNameInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.setRestaurantChoiceName(anyString(),anyString()),liveData -> {
            assertEquals("error",liveData.getValue());
        });
    }

    @Test
    public void setPlaceId_OnSuccess_ReturnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("success");
        given(userRepository.addUserRestaurantChoiceInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.setPlaceId(anyString(),anyString()),liveData -> {
            assertEquals("success",liveData.getValue());
        });
    }
    @Test
    public void setPlaceId_OnError_ReturnLiveDataStringError(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(userRepository.addUserRestaurantChoiceInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.setPlaceId(anyString(),anyString()),liveData -> {
            assertEquals("error",liveData.getValue());
        });
    }

    @Test
    public void removePlaceId_onSuccess_returnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("success");
        given(userRepository.removeRestaurantChoice(anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.removePlaceId(anyString()),liveData -> {
            assertEquals("success",liveData.getValue());
        });
    }
    @Test
    public void removePlaceId_onError_returnLiveDataStringError(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(userRepository.removeRestaurantChoice(anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.removePlaceId(anyString()),liveData -> {
            assertEquals("error",liveData.getValue());
        });
    }

    @Test
    public void addUserRestaurantLike_onSuccess_returnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("success");
        given(userRepository.addUserLikeInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.addUserRestaurantLike(anyString(),anyString()),liveData -> {
            assertEquals("success",liveData.getValue());
        });
    }

    @Test
    public void addUserRestaurantLike_onError_returnLiveDataStringError(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(userRepository.addUserLikeInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.addUserRestaurantLike(anyString(),anyString()),liveData -> {
            assertEquals("error",liveData.getValue());
        });

    }

    @Test
    public void removeUserRestaurantLike_onSuccess_returnLiveDataStringSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("success");
        given(userRepository.removeUserLikeInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.removeUserRestaurantLike(anyString(),anyString()),liveData -> {
            assertEquals("success",liveData.getValue());
        });
    }

    @Test
    public void removeUserRestaurantLike_onError_returnLiveDataStringError(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(userRepository.removeUserLikeInFirebase(anyString(),anyString())).willReturn(result);
        LiveDataTestUtils.observeForTesting(restaurantDetailViewModel.removeUserRestaurantLike(anyString(),anyString()),liveData -> {
            assertEquals("error",liveData.getValue());
        });
    }







    public PlaceDetail placeDetail = new PlaceDetail();

    public User user = new User();

    public User getUser(){
        User user = new User();
        user.setName("Tristan");
        return user;
    }

    public List<User> getDefaultUserList(){
        List<User> list = new ArrayList<>();
        return list;
    }

}
