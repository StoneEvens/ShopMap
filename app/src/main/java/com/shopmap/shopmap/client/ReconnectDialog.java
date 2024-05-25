package com.shopmap.shopmap.client;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ReconnectDialog extends DialogFragment {
    Client client;
    static int multiplier;

    public ReconnectDialog() {

    }

    @SuppressLint("ValidFragment")
    public ReconnectDialog (Client client) {
        this.client = client;
        multiplier = 1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Connection Lost, Try Reconnect?")
                .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        client.getConnection();
                        multiplier = 1;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        multiplier = 6;
                    }
                });
        return builder.create();
    }

    public static int getMultiplier() {return multiplier;}
}
