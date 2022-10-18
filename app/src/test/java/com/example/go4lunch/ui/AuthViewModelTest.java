package com.example.go4lunch.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.AuthRepository;
import com.example.go4lunch.Util.LiveDataTestUtils;
import com.example.go4lunch.model.User;
import com.example.go4lunch.ui.authentication.AuthViewModel;
import com.google.firebase.auth.AuthCredential;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    AuthCredential authCredential;

    @Mock
    AuthRepository authRepository;

    AuthViewModel authViewModel;

    @Before
    public void setup() {
        authViewModel = new AuthViewModel(authRepository);
    }

    @Test
    public void signInWithMailAndPassword_onSuccess_returnSuccessStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("success");
        given(authRepository.firebaseSignInWithMailAndPassword(anyString(), anyString())).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signInWithMailAndPassword(anyString(), anyString()), liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }
    @Test
    public void signInWithMailAndPassword_onError_returnErrorStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("error");
        given(authRepository.firebaseSignInWithMailAndPassword(anyString(), anyString())).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signInWithMailAndPassword(anyString(), anyString()), liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void signUpWithMailAndPassword_onSuccess_returnSuccessStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("success");
        given(authRepository.firebaseSignUpWithMailAndPassword(anyString(), anyString(), anyString())).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signUpWithEmailAndPassword(anyString(), anyString(), anyString()), liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }
    @Test
    public void signUpWithMailAndPassword_onError_returnErrorStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("error");
        given(authRepository.firebaseSignUpWithMailAndPassword(anyString(), anyString(), anyString())).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signUpWithEmailAndPassword(anyString(), anyString(), anyString()), liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void signInWithAuthCredential_onSuccess_returnSuccessStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("success");
        given(authRepository.firebaseSignInWithAuthCredential(authCredential)).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signInWithAuthCredential(authCredential), liveData -> {
            assertEquals("success", liveData.getValue());
        });
    }

    @Test
    public void signInWithAuthCredential_onError_returnErrorStringLiveData() {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        authResult.setValue("error");
        given(authRepository.firebaseSignInWithAuthCredential(authCredential)).willReturn(authResult);
        LiveDataTestUtils.observeForTesting(authViewModel.signInWithAuthCredential(authCredential), liveData -> {
            assertEquals("error", liveData.getValue());
        });
    }

    @Test
    public void getUserById_OnSuccess_returnUserLiveData(){
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        authenticatedUser.setValue(user);
        given(authRepository.getUserByIdFromFirebase(anyString())).willReturn(authenticatedUser);
        LiveDataTestUtils.observeForTesting(authViewModel.getUserById(anyString()),liveData -> {
            assertEquals(user,liveData.getValue());
        });
    }

    @Test
    public void getUserById_OnError_returnNullLiveData(){
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        authenticatedUser.setValue(null);
        given(authRepository.getUserByIdFromFirebase(anyString())).willReturn(authenticatedUser);
        LiveDataTestUtils.observeForTesting(authViewModel.getUserById(anyString()),liveData -> {
            assertNull(liveData.getValue());
        });
    }

    @Test
    public void checkIfUserIsAuthenticated_OnSuccess_returnUserId(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("userId");
        given(authRepository.getAuthenticatedUserId()).willReturn(result);
        LiveDataTestUtils.observeForTesting(authViewModel.checkIfUserIsAuthenticated(),liveData -> {
            assertEquals("userId",liveData.getValue());
        });
    }
    @Test
    public void checkIfUserIsAuthenticated_OnSuccess_returnSuccess(){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue("error");
        given(authRepository.getAuthenticatedUserId()).willReturn(result);
        LiveDataTestUtils.observeForTesting(authViewModel.checkIfUserIsAuthenticated(),liveData -> {
            assertEquals("error",liveData.getValue());
        });
    }



    @Mock
    public User user;


}
