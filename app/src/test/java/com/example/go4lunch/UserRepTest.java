package com.example.go4lunch;


import static org.mockito.ArgumentMatchers.anyString;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public FirebaseFirestore firebaseFirestore;

    @Mock
    public FirebaseAuth firebaseAuth;

    @InjectMocks
    public UserRepository userRepository;

    @Mock
    public CollectionReference collectionReference;

    @Mock
    public DocumentReference documentReference;


    @Mock
    DocumentSnapshot documentSnapshot;


    @Before
    public void setup(){
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("");
        when(firebaseFirestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(mockedTaskDocumentSnapshot);
        when(mockedTaskDocumentSnapshot.getResult()).thenReturn(documentSnapshot);
    }


    @Test
    public void test(){

        when(mockedTaskDocumentSnapshot.isSuccessful()).thenReturn(true);
        MutableLiveData<User> result = userRepository.getAuthenticatedUserFromFirebase();
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(addOnCompleteListenerDocumentSnapshot.capture());
        addOnCompleteListenerDocumentSnapshot.getValue().onComplete(mockedTaskDocumentSnapshot);
        LiveDataTestUtils.observeForTesting(result,liveData -> {
            assertEquals(mockedUser,liveData.getValue());
        });
    }


    public User mockedUser;

    @Mock
    public FirebaseUser firebaseUser;

    @Mock
    public Task<Void> mockedVoidTask;

    Void tresult;
    @Captor
    ArgumentCaptor<OnCompleteListener<DocumentSnapshot>> addOnCompleteListenerDocumentSnapshot;

    @Mock
    public Task<DocumentSnapshot> mockedTaskDocumentSnapshot;
}
