package ua.example.rash1k.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public  class GameElement {

    protected CannonView mView; //Пердставление, содержащие GameElement
    protected Paint mPaint; // Объект для рисования (кисть)
    protected Rect shape; //Ограничивающий прямоугольник
    private float velocityVertical; //Вертикальная скорость элементов Blocker и Target
    private int soundId; //Звук связанный с GameElement

    public GameElement(CannonView view, int color, int soundId, int x,
                       int y, int width, int length, float velocity) {
        this.mView = view;
        this.soundId = soundId;
        this.velocityVertical = velocity;
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        shape = new Rect(x, y, x + width, y + length);
    }

    public void update(double interval) {
        shape.offset(0, (int) (velocityVertical * interval));

        //Если сталкивается со стеной, изменить направление
        if (shape.top < 0 && velocityVertical < 0 ||
                shape.bottom > mView.getScreenHeight() && velocityVertical > 0) {
            velocityVertical *= -1;
        }
    }


    public void draw(Canvas canvas) {
        canvas.drawRect(shape, mPaint);
    }

    public void playSound() {
        mView.playSound(soundId);
    }

}
