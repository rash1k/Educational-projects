package ua.example.rash1k.cannongame;

import android.graphics.Canvas;
import android.graphics.Rect;

public class CannonBall extends GameElement {

    private float velocityX;
    private boolean onScreen;

    public CannonBall(CannonView view, int black, int cannonSoundId,
                      int x, int y, int radius, int velocityX, int velocityY) {

        super(view, black, cannonSoundId, x, y, 2 * radius, 2 * radius, velocityY);
        this.velocityX = velocityX;
        onScreen = true;
    }


    //Вычисляем радиус ядра
    private int getRadius() {
        return (shape.right - shape.left) / 2;
    }

    //Проверяем столкнулось ли ядро с елементом GameElement
    public boolean collidesWith(GameElement element) {
        return (Rect.intersects(shape, element.shape) && velocityX > 0);
    }

    //Проверяем находится ли ядро на экране

    public boolean isOnScreen() {
        return onScreen;
    }

    //Инвертируем горизонтальную скорость ядра
    public void reverseVelosityX() {
        velocityX *= -1;
    }

    @Override
    public void update(double interval) {
        super.update(interval);

        //Обновление горизонтальной позиции
        shape.offset((int) (velocityX * interval), 0);

        //Если ядро уходит за пределы экрана
        if (shape.left < 0 || shape.top < 0 ||
                shape.right > mView.getScreeWidth()
                || shape.bottom > mView.getScreenHeight()) {
            onScreen = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(shape.left + getRadius(),
                shape.top + getRadius(), getRadius(), mPaint);
    }
}
