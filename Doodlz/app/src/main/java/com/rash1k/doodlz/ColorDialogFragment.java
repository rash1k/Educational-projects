package com.rash1k.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.SeekBar;

public class ColorDialogFragment extends BaseDialogFragment {

    private SeekBar alphaSeekBa;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBa;
    private SeekBar blueSeekBar;
    private View viewColor;
    int color;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Создание диалогового окна
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View colorDialogView = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_color, null);
        builder.setView(colorDialogView);

//        Назначение сообщений в диалоге
        builder.setTitle(R.string.title_color_dialog);

//        Получение значений и назначение слушателей
        alphaSeekBa = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        blueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);
        greenSeekBa = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        viewColor = colorDialogView.findViewById(R.id.colorView);

        alphaSeekBa.setOnSeekBarChangeListener(colorChangeListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        greenSeekBa.setOnSeekBarChangeListener(colorChangeListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getDrawingColor();
        alphaSeekBa.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        blueSeekBar.setProgress(Color.blue(color));
        greenSeekBa.setProgress(Color.green(color));

        builder.setPositiveButton(R.string.button_set_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doodleView.setDrawingColor(color);
            }
        });

        return builder.create();
    }

    private final SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser) {
                color = Color.argb(alphaSeekBa.getProgress(), redSeekBar.getProgress(), greenSeekBa.getProgress(),
                        blueSeekBar.getProgress());
                viewColor.setBackgroundColor(color);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}