package com.weather.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.weather.R;

/**
 * Shows dialog to user.
 *
 * This DialogFragment is customizable, being possible to set a Title, a Message and a
 * Positive Button.
 * */
public class ApplicationDialogFragment extends DialogFragment {

    private String titleString;
    private String messageString;

    private DialogInterface.OnClickListener positiveButtonOnClickListener;

    public static ApplicationDialogFragment newInstance() {
        return new ApplicationDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        alertDialogBuilder.setTitle(titleString);
        alertDialogBuilder.setMessage(messageString);
        alertDialogBuilder.setPositiveButton("OK", positiveButtonOnClickListener);

        return alertDialogBuilder.create();
    }

    /**
     * Sets the title of the dialog
     * */
    public void setTitle(String titleString) {
        this.titleString = titleString;
    }

    /**
     * Sets a message to be shown in the dialog
     * */
    public void setMessage(String messageString) {
        this.messageString = messageString;
    }

    /** Sets an OnClickListener which is set from the class which created the dialog instance */
    public void setPositiveButtonOnClickListener(DialogInterface.OnClickListener positiveButtonOnClickListener) {
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;
    }
}
