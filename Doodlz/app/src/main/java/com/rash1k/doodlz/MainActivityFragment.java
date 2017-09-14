package com.rash1k.doodlz;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //region
    private DoodleView mDoodleView; //Используется для обработки касания и рисования

    //Переменные изменения ускорения
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean isDialogOnScreen; //Присутствие окна на экране
    private static final int ACCELERATION_THRESHOLD = 100_000; //Порог встряхивания
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1; // Для идент. запросов на сохранение во внешнее хранилище
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true); // Есть команды меню
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mDoodleView = (DoodleView) view.findViewById(R.id.doodleView);
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening(); //Включение событий встряхивания
    }

    private void enableAccelerometerListening() {

        SensorManager sensorManager =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // Ресгистрация для прослушивания

        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    private void disableAccelerometerListening() {

        SensorManager sensorManager = (SensorManager) getActivity()
                .getSystemService(Context.SENSOR_SERVICE);

        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        //Проверка встряихивания устройства по показаниям акселерометра
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (!isDialogOnScreen) {
                // Параметр SensorEvent содержит информацию об изменениях датчика
                // Массив значений состояни акселерометра. Включает элементы ускорения x,y,z коорд.

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

//                Сохранить предыдущие данные ускорения так как они постоянно меняются
                lastAcceleration = currentAcceleration;

//                Вычисляем текущее ускорение
                currentAcceleration = x * x + y * y + z * z;

//                Вычисляем изменения ускорения
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);

                if (acceleration > ACCELERATION_THRESHOLD) {
                    confirmErase();
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //  Подтверждение стирания рисунка
    private void confirmErase() {

        new EraseImageDialogFragment()
                .show(getFragmentManager(), "erase dialog");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.color:
                new ColorDialogFragment().show(getFragmentManager(), "color dialog");
                return true;
            case R.id.line_width:
                new LineWidthDialogFragment().show(getFragmentManager(), "line width dialog");
                return true;
            case R.id.line_delete_drawing:
                confirmErase();
                return true;
            case R.id.save:
                saveImage();// Проверить разрешение и сохранить рисунок
                return true;
            case R.id.print:
                mDoodleView.printImage();// Напечатать рисунок
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //  Запрашивает разрешение или сохраняет изображение, если есть разрешение
    private void saveImage() {
//        Проверить, есть ли у приложения разрешение для сохранения

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

//                показывает, объяснение того, почему необходимо разрешение
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//                    Назначить сообщение диалогу
                    builder.setMessage(R.string.permission_explanation);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Запросить разрешение
                            requestPermissions(new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    SAVE_IMAGE_PERMISSION_REQUEST_CODE
                            );
                        }
                    });
//                    Отобразить диалоговое окно
                    builder.create().show();

                } else {
                    requestPermissions(new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                }
            } else {
                // Разрешение уже имеется
                mDoodleView.saveImage();
            }
        }
    }

    //    Вызывается системой когда пользователь отклоняет или предоставляет разрешение для сохранения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Выбрать действие в зависимотси от того какое разрешение запрошено
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDoodleView.saveImage();
                }
                break;
        }
    }

    public DoodleView getDoodleView() {
        return mDoodleView;
    }

//  Проверяет отображается ли диалоговое окно
    public void setDialogOnScreen(boolean dialogOnScreen) {
        isDialogOnScreen = dialogOnScreen;
    }
}
