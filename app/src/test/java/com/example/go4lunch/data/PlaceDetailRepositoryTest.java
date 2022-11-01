package com.example.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Retrofit.Go4LunchStreams;
import com.example.go4lunch.Retrofit.GooglePlaceApiCall;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

@RunWith(MockitoJUnitRunner.class)
public class PlaceDetailRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GooglePlaceApiCall googlePlaceApiCall;

    @Mock
    private Go4LunchStreams go4LunchStreams = new Go4LunchStreams(googlePlaceApiCall);

    @InjectMocks
    private PlaceDetailRepository placeDetailRepository;

    @Before
    public void setup() {
        when(go4LunchStreams.streamFetchRestaurantsDetails(anyString(),anyInt(),anyString())).thenReturn(mockedPlaceDetailsResult);
        when(go4LunchStreams.streamFetchAutoCompleteRestaurantDetails(anyString(),anyString(),anyInt())).thenReturn(mockedPlaceDetailsResult);
        when(go4LunchStreams.streamFetchPlaceDetail(anyString())).thenReturn(mockedPlaceDetail);
    }

    @Test
    public void getNearbyRestaurantDetails_onSuccess_returnLiveDataPlaceDetailList() {
        LiveData<List<PlaceDetail>> result = placeDetailRepository.getNearbyRestaurantsList(anyString(), anyInt(), anyString());
        verify(go4LunchStreams.streamFetchRestaurantsDetails(anyString(), anyInt(), anyString())).subscribe(disposableObserverArgumentCaptor.capture());
        disposableObserverArgumentCaptor.getValue().onSuccess(mockedPlaceDetailList);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(mockedPlaceDetailList, liveData.getValue()));
    }

    @Test
    public void getNearbyRestaurantsDetails_onError_returnNull(){
        LiveData<List<PlaceDetail>> result = placeDetailRepository.getNearbyRestaurantsList(anyString(), anyInt(), anyString());
        verify(go4LunchStreams.streamFetchRestaurantsDetails(anyString(), anyInt(), anyString())).subscribe(disposableObserverArgumentCaptor.capture());
        disposableObserverArgumentCaptor.getValue().onError(mock(Throwable.class));
        LiveDataTestUtils.observeForTesting(result, liveData -> assertNull(liveData.getValue()));
    }
    @Test
    public void searchNearbyRestaurantWithAutocomplete_onSuccess_returnLiveDataPlaceDetailList(){
        LiveData<List<PlaceDetail>> result = placeDetailRepository.getNearbyRestaurantListWithAutoComplete(anyString(),anyString(),anyInt());
        verify(go4LunchStreams.streamFetchAutoCompleteRestaurantDetails(anyString(),anyString(),anyInt())).subscribe(disposableObserverArgumentCaptor.capture());
        disposableObserverArgumentCaptor.getValue().onSuccess(mockedPlaceDetailList);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertEquals(mockedPlaceDetailList,liveData.getValue()));
    }

    @Test
    public void searchNearbyRestaurantWithAutocomplete_onError_returnNull(){
        LiveData<List<PlaceDetail>> result = placeDetailRepository.getNearbyRestaurantListWithAutoComplete(anyString(),anyString(),anyInt());
        verify(go4LunchStreams.streamFetchAutoCompleteRestaurantDetails(anyString(),anyString(),anyInt())).subscribe(disposableObserverArgumentCaptor.capture());
        disposableObserverArgumentCaptor.getValue().onError(mock(Throwable.class));
        LiveDataTestUtils.observeForTesting(result,liveData -> assertNull(liveData.getValue()));
    }

    @Test
    public void searchNearbyPlace_onSuccess_returnLiveDataPlaceDetail(){
        LiveData<PlaceDetail> result = placeDetailRepository.getPlaceDetailByPlaceId(anyString());
        verify(go4LunchStreams.streamFetchPlaceDetail(anyString())).subscribe(observerArgumentCaptor.capture());
        observerArgumentCaptor.getValue().onNext(mockedPlaceDetailResult);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertEquals(mockedPlaceDetailResult,liveData.getValue()));
    }

    @Test
    public void searchNearbyPlace_onError_returnNull(){
        LiveData<PlaceDetail> result = placeDetailRepository.getPlaceDetailByPlaceId(anyString());
        verify(go4LunchStreams.streamFetchPlaceDetail(anyString())).subscribe(observerArgumentCaptor.capture());
        observerArgumentCaptor.getValue().onError(mock(Throwable.class));
        LiveDataTestUtils.observeForTesting(result,liveData -> assertNull(liveData.getValue()));
    }


    @Captor
    ArgumentCaptor<DisposableSingleObserver<List<PlaceDetail>>> disposableObserverArgumentCaptor;

    @Captor
    ArgumentCaptor<DisposableObserver<PlaceDetail>> observerArgumentCaptor;

    @Mock
    private Observable<PlaceDetail> mockedPlaceDetail;

    @Mock
    private Single<List<PlaceDetail>> mockedPlaceDetailsResult;
    @Mock
    private List<PlaceDetail> mockedPlaceDetailList;

    @Mock
    private PlaceDetail mockedPlaceDetailResult;

}
