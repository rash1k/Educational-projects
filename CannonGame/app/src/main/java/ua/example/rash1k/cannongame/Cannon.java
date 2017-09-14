package ua.example.rash1k.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {

    private int baseRadius; //Радиус основания пушки
    private int barrelLength; //Длинна ствола
    private Point barrelEnd = new Point();//Конечная точка ствола
    private double barrelAngel; //Угол наклона пушки
    private CannonBall cannonBall;
    private Paint paint = new Paint(); // Кисть
    private CannonView view; //View в котором находится пушка

    public Cannon(CannonView view, int baseRadius, int barrelLength, int barrelWidth) {
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(barrelWidth); //Назначение толщины ствола
        align(Math.PI / 2);//Ствол обращен вправо
    }

    public void align(double barrelAngel) {
        this.barrelAngel = barrelAngel;
        barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngel));
        barrelEnd.y = (int) (-barrelLength * Math.cos(barrelAngel)) +
                view.getScreenHeight() / 2;
    }

    public void fireCannonball() {
        //Вычисление горизонтальной составляющей скорости ядра
        int velocityX = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
                view.getScreeWidth() * Math.sin(barrelAngel));

        //Вычисление вертикальной составляющей скорости ядра
        int velocityY = (int) (CannonView.CANNONBALL_SPEED_PERCENT *
                view.getScreeWidth() * -Math.cos(barrelAngel));

        //Вычисление радиуса ядра
        int radius = (int) (view.getScreenHeight() * CannonView.CANNONBALL_RADIUS_PERCENT);

        //Построение ядра и размещение его в стволе
        cannonBall = new CannonBall(view, Color.BLACK, CannonView.CANNON_SOUND_ID,
                -radius, view.getScreenHeight() / 2 - radius, radius, velocityX, velocityY);

        cannonBall.playSound();
    }

    //Рисуем пушку на Canvas
    public void draw(Canvas canvas) {
        //Рисуем ствол
        canvas.drawLine(0, view.getScreenHeight() / 2, barrelEnd.x, barrelEnd.y, paint);

        //Рисуем основание пушки
        canvas.drawCircle(0, (int) view.getScreenHeight() / 2, (int) baseRadius, paint);
    }

    public CannonBall getCannonBall() {
        return cannonBall;
    }

    public void removeCannonBall() {
        cannonBall = null;
    }
}
