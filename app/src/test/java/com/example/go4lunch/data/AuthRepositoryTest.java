package com.example.go4lunch.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.AuthRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public FirebaseFirestore firebaseFirestore;

    @Mock
    public FirebaseAuth firebaseAuth;

    @Spy
    @InjectMocks
    public AuthRepository authRepository;

    @Mock
    public FirebaseUser firebaseUser;

    @Mock
    public AuthCredential authCredential;

    @Mock
    public CollectionReference collectionReference;

    @Mock
    DocumentReference documentReference;

    @Mock
    public AuthResult authResult;

    @Mock
    public AdditionalUserInfo additionalUserInfo;
    String uid = "12345678";


    public User user;

    @Before
    public void setup() {
        when(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockedAuthResultTask);
        when(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockedAuthResultTask);
        when(firebaseAuth.signInWithCredential(authCredential)).thenReturn(mockedAuthResultTask);
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseFirestore.collection(anyString())).thenReturn(collectionReference);
        when(collectionReference.document(anyString())).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(task);
        //when(documentReference.set(any(User.class))).thenReturn(voidTask);
        when(task.getResult()).thenReturn(documentSnapshot);
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn(uid);
        when(mockedAuthResultTask.getResult()).thenReturn(authResult);
        when(mockedAuthResultTask.getResult().getUser()).thenReturn(firebaseUser);
        when(mockedAuthResultTask.getResult()).thenReturn(authResult);
        when(authResult.getAdditionalUserInfo()).thenReturn(additionalUserInfo);


    }


    @Test
    public void getAuthenticatedUser_onSuccess_returnAuthenticatedUserId() {
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn(uid);
        MutableLiveData<String> result = authRepository.getAuthenticatedUserId();
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals(uid, liveData.getValue());
        });
    }

    @Test
    public void getAuthenticatedUser_onError_returnNull() {
        when(firebaseAuth.getCurrentUser()).thenReturn(null);
        MutableLiveData<String> result = authRepository.getAuthenticatedUserId();
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignInWithEmailAndPassword_onSuccess_returnSuccessStringLiveData() {
        when(mockedAuthResultTask.isSuccessful()).thenReturn(true);
        MutableLiveData<String> result = authRepository.firebaseSignInWithMailAndPassword(anyString(), anyString());
        verify(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignInWithEmailAndPassword_onError_returnErrorStringLiveData() {
        when(mockedAuthResultTask.isSuccessful()).thenReturn(false);
        MutableLiveData<String> result = authRepository.firebaseSignInWithMailAndPassword(anyString(), anyString());
        verify(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }


    @Test
    public void getUserById_onTaskSuccessAndDocumentExist_returnUserLiveData() {
        when(task.isSuccessful()).thenReturn(true);
        when(documentSnapshot.exists()).thenReturn(true);
        MutableLiveData<User> userMutableLiveData = authRepository.getUserByIdFromFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(task);
        LiveDataTestUtils.observeForTesting(userMutableLiveData, liveData -> {
            assertEquals(user, liveData.getValue());
        });
    }

    @Test
    public void getUserById_onTaskSuccessButDocumentDoesntExist_returnNull() {
        when(task.isSuccessful()).thenReturn(true);
        when(documentSnapshot.exists()).thenReturn(false);
        MutableLiveData<User> userMutableLiveData = authRepository.getUserByIdFromFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(task);
        LiveDataTestUtils.observeForTesting(userMutableLiveData, liveData -> {
            assertNull(liveData.getValue());
        });
    }


    @Test
    public void getUserById_onTaskCancelled_returnNull() {
        when(task.isSuccessful()).thenReturn(false);
        MutableLiveData<User> userMutableLiveData = authRepository.getUserByIdFromFirebase(anyString());
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(task);
        LiveDataTestUtils.observeForTesting(userMutableLiveData, liveData -> {
            assertNull(liveData.getValue());
        });
    }

    @Test
    public void createUserInFirestoreIfNoExist_OnSuccessTaskAndIfDocumentAlreadyExist_returnSuccess() {
        MutableLiveData<String> result = authRepository.createUserInFirestoreIfNoExist(firebaseUser, null);
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult().exists()).thenReturn(true);
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(task);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void createUserInFirestoreIfNoExist_OnErrorTaskAndIf_returnError() {
        MutableLiveData<String> result = authRepository.createUserInFirestoreIfNoExist(firebaseUser, null);
        when(task.isSuccessful()).thenReturn(false);
        verify(firebaseFirestore.collection(anyString()).document(anyString()).get()).addOnCompleteListener(argumentCaptor.capture());
        argumentCaptor.getValue().onComplete(task);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignInWithAuthCredential_onSuccess_returnSuccess() {
        MutableLiveData<String> result = authRepository.firebaseSignInWithAuthCredential(authCredential);
        when(mockedAuthResultTask.isSuccessful()).thenReturn(true);
        verify(firebaseAuth.signInWithCredential(authCredential)).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignInWithAuthCredential_onError_returnError() {
        MutableLiveData<String> result = authRepository.firebaseSignInWithAuthCredential(authCredential);
        when(mockedAuthResultTask.isSuccessful()).thenReturn(false);
        verify(firebaseAuth.signInWithCredential(authCredential)).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignUpWithMailAndPassword_onSuccessAndUserIsNew_returnSuccess() {
        MutableLiveData<String> result = authRepository.firebaseSignUpWithMailAndPassword(anyString(), anyString(), anyString());
        when(mockedAuthResultTask.isSuccessful()).thenReturn(true);
        when(additionalUserInfo.isNewUser()).thenReturn(true);
        verify(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignUpWithMailAndPassword_onSuccessAndUserIsNotNew_returnSuccess() {
        MutableLiveData<String> result = authRepository.firebaseSignUpWithMailAndPassword(anyString(), anyString(), anyString());
        when(mockedAuthResultTask.isSuccessful()).thenReturn(true);
        when(additionalUserInfo.isNewUser()).thenReturn(false);
        verify(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void firebaseSignUpWithMailAndPassword_onError_returnError() {
        MutableLiveData<String> result = authRepository.firebaseSignUpWithMailAndPassword(anyString(), anyString(), anyString());
        when(mockedAuthResultTask.isSuccessful()).thenReturn(false);
        verify(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).addOnCompleteListener(onCompleteListenerArgumentCaptorAuthResult.capture());
        onCompleteListenerArgumentCaptorAuthResult.getValue().onComplete(mockedAuthResultTask);
        LiveDataTestUtils.observeForTesting(result, liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Captor
    ArgumentCaptor<OnCompleteListener<Void>> onVoidCompleteListenerArgumentCaptor;

    @Captor
    ArgumentCaptor<OnCompleteListener<DocumentSnapshot>> argumentCaptor;

    @Captor
    ArgumentCaptor<OnCompleteListener<AuthResult>> onCompleteListenerArgumentCaptorAuthResult;

    @Mock
    Task<Void> voidTask;

    @Mock
    Task<DocumentSnapshot> task;

    @Mock
    Task<AuthResult> mockedAuthResultTask;

    @Mock
    DocumentSnapshot documentSnapshot;


}
