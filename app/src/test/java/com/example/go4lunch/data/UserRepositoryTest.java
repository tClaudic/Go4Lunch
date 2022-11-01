package com.example.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public FirebaseFirestore firebaseFirestore;

    @Mock
    FirebaseAuth firebaseAuth;

    @InjectMocks
    public UserRepository userRepository;

    @Mock
    CollectionReference collectionReference;

    @Mock
    DocumentReference documentReference;

    @Mock
    QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    DocumentSnapshot documentSnapshot;

    @Mock
    List<DocumentSnapshot> queryDocumentSnapshotList;

    @Mock
    Iterator<QueryDocumentSnapshot> querySnapshotIterator;

    @Mock
    Query query;

    @Mock
    FirebaseUser firebaseUser;



    @Before
    public void setup() {


        when(firebaseFirestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(mockedTask);
        when(collectionReference.whereEqualTo(anyString(), anyString())).thenReturn(query);
        when(query.get()).thenReturn(mockedTask);
        when(mockedTask.getResult()).thenReturn(querySnapshot);
        when(querySnapshot.iterator()).thenReturn(querySnapshotIterator);
        when(querySnapshot.getDocuments()).thenReturn(queryDocumentSnapshotList);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
        when(documentReference.update(anyString(),anyString())).thenReturn(mockedVoidTask);
        when(mockedVoidTask.getResult()).thenReturn(Tresult);
        when(documentReference.update(anyString(), eq(FieldValue.arrayRemove("")))).thenReturn(mockedVoidTask);
        when(documentReference.get()).thenReturn(mockedTaskDocumentSnapshot);
        when(mockedTaskDocumentSnapshot.getResult()).thenReturn(documentSnapshot);


    }


    List<User> usersList = new ArrayList<>();

    @Test
    public void getUsersListMutableLiveData_onComplete_returnUsersList() {
        MutableLiveData<List<User>> result = userRepository.getUsersListFromFirebase();
        verify(firebaseFirestore.collection(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(mockedTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(usersList, liveData.getValue()));
    }

    @Test
    public void getUsersFilteredListMutableLiveData() {
        MutableLiveData<List<User>> result = userRepository.getUsersFilteredListFromFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).whereEqualTo(anyString(), anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(mockedTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals(usersList, liveData.getValue()));
    }

    @Test
    public void removeUserLike_onSuccess_returnSuccessString() {
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeUserLikeInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), FieldValue.arrayRemove(anyString()))).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("success", liveData.getValue()));
    }

    @Test
    public void removeUserLike_onCanceled_returnErrorString() {
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeUserLikeInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), FieldValue.arrayRemove(anyString()))).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("error", liveData.getValue()));

    }

    @Test
    public void addLike_onSuccess_returnSuccessString(){
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserLikeInFirebase("","");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), FieldValue.arrayUnion(anyString()))).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertEquals("success",liveData.getValue()));
    }

    @Test
    public void addLike_onCanceled_returnErrorString(){
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserLikeInFirebase("","");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), FieldValue.arrayUnion(anyString()))).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertEquals("error",liveData.getValue()));
    }

    @Test
    public void addRestaurant_onSuccess_returnSuccessString() {
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserRestaurantChoiceInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("success", liveData.getValue()));
    }

    @Test
    public void addRestaurant_onCanceled_returnErrorString() {
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserRestaurantChoiceInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("error", liveData.getValue()));
    }

    @Test
    public void addRestaurantChoiceName_onSuccess_returnSuccessString() {
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserRestaurantChoiceNameInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("success", liveData.getValue()));
    }

    @Test
    public void addRestaurantChoiceName_onCanceled_returnErrorString() {
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.addUserRestaurantChoiceNameInFirebase("", "");
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("error", liveData.getValue()));
    }

    @Test
    public void removeRestaurantChoiceName_onSuccess_returnErrorString(){
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeUserRestaurantChoiceNameInFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("success", liveData.getValue()));
    }

    @Test
    public void removeRestaurantChoiceName_onError_returnErrorString(){
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeUserRestaurantChoiceNameInFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("error", liveData.getValue()));
    }


    @Test
    public void removeRestaurantChoice_onSuccess_returnSuccessString() {
        when(mockedVoidTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeRestaurantChoiceInFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("success", liveData.getValue()));
    }
    @Test
    public void removeRestaurantChoice_onCanceled_returnErrorString() {
        when(mockedVoidTask.isCanceled()).thenReturn(true);
        MutableLiveData<String> result = userRepository.removeRestaurantChoiceInFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).update(anyString(), anyString())).addOnCompleteListener(voidArgumentCaptor.capture());
        voidArgumentCaptor.getValue().onComplete(mockedVoidTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> assertEquals("error", liveData.getValue()));
    }

    @Test
    public void getAuthenticatedUserMutableLiveData_onSuccess_returnUserMutableLiveData(){
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("");
        when(mockedTaskDocumentSnapshot.isSuccessful()).thenReturn(true);
        MutableLiveData<User> result = userRepository.getAuthenticatedUserFromFirebase();
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(addOnCompleteListenerDocumentSnapshot.capture());
        addOnCompleteListenerDocumentSnapshot.getValue().onComplete(mockedTaskDocumentSnapshot);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertEquals(mockedUser,liveData.getValue()));
    }
    @Test
    public void getAuthenticatedUserMutableLiveData_onCanceled_returnNull(){
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("");
        when(mockedTaskDocumentSnapshot.isCanceled()).thenReturn(true);
        MutableLiveData<User> result = userRepository.getAuthenticatedUserFromFirebase();
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(addOnCompleteListenerDocumentSnapshot.capture());
        addOnCompleteListenerDocumentSnapshot.getValue().onComplete(mockedTaskDocumentSnapshot);
        LiveDataTestUtils.observeForTesting(result,liveData -> assertNull(liveData.getValue()));
    }
    @Test
    public void getAuthenticatedUserMutableLiveData_whenFirebaseUserIsNull_returnNull(){
        when(firebaseAuth.getCurrentUser()).thenReturn(null);
        when(firebaseUser.getUid()).thenReturn("");
        MutableLiveData<User> result = userRepository.getAuthenticatedUserFromFirebase();
        LiveDataTestUtils.observeForTesting(result,liveData -> assertNull(liveData.getValue()));
    }






    public User mockedUser;


    @Mock
    public Task<Void> mockedVoidTask;

    @Mock
    Void Tresult;

    @Captor
    ArgumentCaptor<OnCompleteListener<DocumentSnapshot>> addOnCompleteListenerDocumentSnapshot;

    @Captor
    ArgumentCaptor<OnCompleteListener<Void>> voidArgumentCaptor;

    @Captor
    ArgumentCaptor<OnCompleteListener<QuerySnapshot>> argumentCaptor;

    @Mock
    Task<QuerySnapshot> mockedTask;

    @Mock
    Task<DocumentSnapshot> mockedTaskDocumentSnapshot;

    @Mock
    QuerySnapshot querySnapshot;

}
