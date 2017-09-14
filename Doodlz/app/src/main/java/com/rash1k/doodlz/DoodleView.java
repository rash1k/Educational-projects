package com.rash1k.doodlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class DoodleView extends View {

    //    Смещение для продолжения рисования
    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;// Область рисования для вывода или сохранения
    private Canvas bitmapCanvas;//Используется для рисования на Bitmap
    private final Paint paintScreen;//Для вывода Bitmap на экран
    private final Paint paintLine;//Для рисования линий на Bitmap

    //Данные нарисованных контуров Path и содержащихся в них точек
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();


    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintScreen = new Paint();// Вывод на экран

//        Исходные параметры линий
        paintLine = new Paint();
        paintLine.setAntiAlias(true);//Сглаживание краев линий
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);// Сплошная линия
        paintLine.setStrokeCap(Paint.Cap.ROUND);// Закругленные концы
        paintLine.setStrokeWidth(5);// Толщина линий
    }

//        Назначение цвета рисуемой линии

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    //    Получение цвета линии
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    //    Назначение толщины линии
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

//    Получение толщины линии

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

//    Стирание рисунка

    public void clear() {
        pathMap.clear();// Удаляем все контуры
        previousPointMap.clear();// Удаление точек
        bitmap.eraseColor(Color.WHITE);
        invalidate();// Перерисовать изображение
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);// Стирается белым цветом
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Перерисовка фона
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        //Collection<Path> c = pathMap.values();

       /* //Для каждой выводимой линии
        for (Path path : c) {
            canvas.drawPath(path, paintLine);
        }*/
        // TODO: 18.09.2016
        //Для каждой выводимой линии


        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine); //Рисование линии
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();// Тип действия
        int actionIndex = event.getActionIndex();// Указатель пальца его координаты

        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));

        } else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));

        } else {
            touchMoved(event);
        }
        invalidate();//Перерисовка
        return true;
    }

    private void touchStarted(float x, float y, int lineId) {
        Path path; // Для хранения контура с заданным id
        Point point; //Хранение последней точки

        if (pathMap.containsKey(lineId)) {
            path = pathMap.get(lineId);//Получаем Path
            path.reset();// Очистка с началом нового касания
            point = previousPointMap.get(lineId);
        } else {
            path = new Path();
            pathMap.put(lineId, path);
            point = new Point();
            previousPointMap.put(lineId, point);
        }

//        переход к координатам качание
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;

    }

    private void touchMoved(MotionEvent event) {

//        Для каждого пальца в объекте MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {

//            Получить идентификатор и индекс
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

//                Получить объект Path и Point
                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerIndex);

//                Вычисляем величину смещения от последнео обновления

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

//                Если расстояние достаточно велико

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {

//                    Расширение контура до новой точки
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

//                    Сохранение новых координат
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }

        }
    }

    private void touchEnded(int lineId) {
        Path path = pathMap.get(lineId);
        bitmapCanvas.drawPath(path, paintLine);
        path.reset();
    }


    //Печать текущего изображения
    public void printImage() {
//        Проверка доступночти печати на устройстве

        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(getContext());

//            Ихображение масштабируется и выводится на печать
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

            printHelper.printBitmap("Doodlz Image", bitmap);

        } else {
            Toast message = Toast.makeText(getContext(), R.string.message_error_printing, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }





    }

    //    Сохранение изображени

    public void saveImage() {
        final String name = "Doodlz" + System.currentTimeMillis() + " .jpg";

//        Сохранение изобрадения в галерее

        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), bitmap, name, "Doodlz Drawing");

        if (location != null) {
            Toast message = Toast.makeText(getContext(), R.string.message_saved, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        } else {
            Toast message = Toast.makeText(getContext(), R.string.message_error_saving, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }
    }


}
