package com.example.go4lunch.ui.logout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.go4lunch.R;

public class LogoutConfirmation extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setMessage("Logout ?")
                .setPositiveButton(getString(R.string.yes),(dialogInterface, i) -> {})
                .setNegativeButton(getString(R.string.no),(dialogInterface, i) -> {})
                .create();
    }
}
