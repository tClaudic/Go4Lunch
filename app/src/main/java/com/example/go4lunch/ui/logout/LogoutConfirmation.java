package com.example.go4lunch.ui.logout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutConfirmation extends DialogFragment {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Logout ?")
                .setPositiveButton(getString(R.string.yes),(dialogInterface, i) -> signOuFirebase())
                .setNegativeButton(getString(R.string.no),(dialogInterface, i) -> {

                })
                .create();
    }

    private void signOuFirebase(){
        firebaseAuth.signOut();
    }
}
