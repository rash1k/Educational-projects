package com.rash1k.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class LineWidthDialogFragment extends BaseDialogFragment {

    private ImageView widthImageView;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View viewWidthLine = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_line_with, null);

        builder.setView(viewWidthLine);
        builder.setTitle(R.string.title_line_width_dialog);
        widthImageView = (ImageView) viewWidthLine.findViewById(R.id.widthImageView);

//        Настройка SeekBar
        final DoodleView doodleView = getDoodleFragment().getDoodleView();

        final SeekBar widthSeekBar = (SeekBar) viewWidthLine.findViewById(R.id.widthSeekBar);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        widthSeekBar.setOnSeekBarChangeListener(lineWidthChanged);

        builder.setPositiveButton(R.string.button_set_line_width, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doodleView.setLineWidth(widthSeekBar.getProgress());
            }
        });
        return builder.create();
    }


    SeekBar.OnSeekBarChangeListener lineWidthChanged = new SeekBar.OnSeekBarChangeListener() {

        final Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            paint.setColor(getDoodleFragment().getDoodleView().getDrawingColor());
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(progress);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bitmap.eraseColor(getResources().getColor(android.R.color.transparent, getContext().getTheme()));
            } else {
                bitmap.eraseColor(getResources().getColor(android.R.color.transparent));
            }
            canvas.drawLine(30, 50, 370, 50, paint);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
