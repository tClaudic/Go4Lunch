package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Rertrofit.Go4LunchStreams;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

@RunWith(MockitoJUnitRunner.class)
public class PlaceDetailRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();



    @Mock
    private PlaceDetailRepository placeDetailRepository;

    @Before
    public void setup() {
        given(Go4LunchStreams.streamFetchRestaurantsDetails(anyString(), anyInt(), anyString())).willReturn(mockedPlaceDetailsResult);
    }

    @Test
    public void nominal_case() {
        LiveData<List<PlaceDetail>> result = placeDetailRepository.getNearbyRestaurantsLiveData(anyString(), anyInt(), anyString());

        verify(Go4LunchStreams.streamFetchRestaurantsDetails(anyString(), anyInt(), anyString())).subscribeWith(disposableObserverArgumentCaptor.capture());
        disposableObserverArgumentCaptor.getValue().onSuccess(mockedPlaceDetailList);

        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(mockedPlaceDetailList, result.getValue());
        });

    }

    @Captor
    ArgumentCaptor<DisposableSingleObserver<List<PlaceDetail>>> disposableObserverArgumentCaptor;

    @Mock
    private Single<List<PlaceDetail>> mockedPlaceDetailsResult;
    @Mock
    private List<PlaceDetail> mockedPlaceDetailList;

}
