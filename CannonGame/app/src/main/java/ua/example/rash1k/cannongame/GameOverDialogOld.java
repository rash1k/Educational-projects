package ua.example.rash1k.cannongame;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class GameOverDialogOld extends DialogFragment {


    private CannonView mCannonView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int[] values = getArguments().getIntArray("messageId");
        if (values != null) {
            builder.setTitle(getString(getArguments().getInt("messageId")));
            //Вывод количества выстрелов и затраченного времени
            builder.setMessage(getString(R.string.results_format,
                    mCannonView.getShotsFired(), mCannonView.getTotalElapsedTime()));
        }

        builder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCannonView.setDialogIsDisplayed(false);
                mCannonView.newGame();
            }
        });
        return builder.create();
    }

    public static GameOverDialogOld instanceFragment(int messageId) {

        GameOverDialogOld dialog = new GameOverDialogOld();
        Bundle arguments = new Bundle();
        arguments.putInt("messageId", messageId);
        dialog.setArguments(arguments);
        return dialog;
    }


    public CannonView getCannonView(CannonView cannonView) {
        return cannonView;
    }

    public void setCannonView(CannonView cannonView) {
        mCannonView = cannonView;
    }
}
