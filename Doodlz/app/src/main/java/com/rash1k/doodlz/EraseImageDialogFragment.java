package com.rash1k.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class EraseImageDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.message_erase);
        builder.setPositiveButton(R.string.button_erase, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDoodleFragment().getDoodleView().clear();
            }
        }).setNegativeButton(android.R.string.cancel, null);
        return builder.create();

    }
}
