package com.example.go4lunch;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.UserRepositoryTest;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Mock
    CollectionReference collectionReference;

    @Mock
    private FirebaseFirestore firebaseFirestore;

    @InjectMocks
    private UserRepositoryTest userRepositoryTest;

    @Before
    public void setup() {

        when(firebaseFirestore.collection(anyString())).thenReturn(collectionReference);
        given(firebaseFirestore.collection(anyString()).get()).willReturn(mockedTask);
        when(mockedTask.getResult()).thenReturn(mockedDocumentSnapshot);


    }

    @Test
    public void getAllUsersTest() {
        MutableLiveData<List<User>> usersList = userRepositoryTest.getFirebaseUsersList();
        verify(firebaseFirestore).collection(anyString()).get().addOnCompleteListener(onCompleteListenerArgumentCaptor.capture());
        onCompleteListenerArgumentCaptor.getValue().onComplete(mockedTask);

        LiveDataTestUtils.observeForTesting(usersList, liveData -> {
            assertEquals(mockedUsersList, liveData.getValue());
        });


    }

    @Captor
    private ArgumentCaptor<OnCompleteListener<QuerySnapshot>> onCompleteListenerArgumentCaptor;

    @Mock
    private List<User> mockedUsers;

    @Mock
    private DocumentSnapshot mockedDocumentSnapShotList;

    @Mock
    private QuerySnapshot mockedDocumentSnapshot;

    @Mock
    private Task<QuerySnapshot> mockedTask;

    @Mock
    private MutableLiveData<List<User>> mockedUsersListLiveData = new MutableLiveData<>();

    @Mock
    private List<User> mockedUsersList;

}
