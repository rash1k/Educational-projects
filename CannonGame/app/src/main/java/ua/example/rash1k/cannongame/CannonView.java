package ua.example.rash1k.cannongame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CannonView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "CannonView";

    //Игровые константы
    public static final int MISS_PENALTY = 2;//Штраф при промахе
    public static final int HIT_REWARD = 3;//Прибавка при попадании

    //Константы для рисования пушки
    public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;

    //Константы для рисования ядра
    public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
    public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;

    //Константы для рисования мишеней
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    public static final double TARGET_PIECES = 9;
    public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
    public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;

    //Константы для рисования блока
    public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
    public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
    public static final double BLOCKER_X_PERCENT = 1.0 / 2;
    public static final double BLOCKER_SPEED_PERCENT = 1.0;

    //Размер текста составляет 1/18 ширины экрана
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

    private CannonThread mCannonThread; //Управляет циклом игры
    private AppCompatActivity mActivity;
    private boolean dialogIsDisplayed;

    //Игровые объекты
    private Cannon mCannon;
    private Blocker mBlocker;
    private List<Target> mTargets;

    //Переменные размеровж
    private int screenWidth;
    private int screenHeight;

    //Перменные для игрового цикла и отслеживания игры
    private boolean gameOver;
    private double timeLeft; //Оставшееся время в секундах
    private int shotsFired; //Количество выстрелов
    private static double totalElapsedTime; //Затраты времени в секундах

    //Константы и переменные для управления звуком
    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    private SoundPool mSoundPool; //Воспроизведение звуков
    private SparseIntArray soundMap; //Связь идентификаторов с SoundPool

    //Pain для рисования
    private Paint textPaint; //Для вывода текста
    private Paint backgroundPaint; //Для стирания области рисования


    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (AppCompatActivity) context;///Ссылка MAinActivity

        //Регистрация слушателя SurfaceHolder.Callback
        getHolder().addCallback(this);

        //Настройка атрибутов для воспроизведения звука
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

            //Инициализвция SoundPool для воспроизведения звука
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();

            //Создание Mаp и предварительная загрузка звуков
            soundMap = new SparseIntArray(3);
            soundMap.put(TARGET_SOUND_ID, mSoundPool.load(context, R.raw.target_hit, 1));
            soundMap.put(CANNON_SOUND_ID, mSoundPool.load(context, R.raw.cannon_fire, 1));
            soundMap.put(BLOCKER_SOUND_ID, mSoundPool.load(context, R.raw.blocker_hit, 1));

            //Настройка кистей
            textPaint = new Paint();
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.WHITE);
        }
    }

    public boolean isDialogIsDisplayed() {
        return dialogIsDisplayed;
    }

    public void setDialogIsDisplayed(boolean dialogIsDisplayed) {
        this.dialogIsDisplayed = dialogIsDisplayed;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public double getTotalElapsedTime() {
        return totalElapsedTime;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        //Настройка свойств текста
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
        textPaint.setAntiAlias(true);
    }

    public void playSound(int soundId) {
        mSoundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1f);
    }

    //Запуск новой игры и сброс всех элементов
    public void newGame() {

//        Создание новой пушки
        mCannon = new Cannon(this,
                (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight),
                (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth),
                (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

        Random random = new Random();//Для случайных скоростей
        mTargets = new ArrayList<>(); //Список мишений

        //Инициализация точек targetX и Y для первой мишение
        int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);
        int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenHeight);

        //Добавление мишений в список

        for (int n = 0; n < TARGET_PIECES; n++) {
            //Получение слуяайной скорости для мишени n от min max

            double velocity = screenHeight * (random.nextDouble() *
                    (TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) +
                    TARGET_MIN_SPEED_PERCENT);

            //Цвет мишеней чередуется между белым и черным

            int color = (n % 2) == 0 ?
                    getResources().getColor(android.R.color.black) :
                    getResources().getColor(android.R.color.white);

            velocity *= -1; //Противоположная скорость следующей мишени

            mTargets.add(new Target(this, color, HIT_REWARD, targetX, targetY,
                    (int) (TARGET_WIDTH_PERCENT * screenWidth),
                    (int) (TARGET_LENGTH_PERCENT * screenHeight),
                    (int) velocity));

            //Увеличение следующей кооринаты х для смещения в право
            targetX += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) * screenWidth;
        }
        //Создание нового блока
        mBlocker = new Blocker(this, Color.BLACK, MISS_PENALTY,
                (int) (BLOCKER_X_PERCENT * screenWidth),
                (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
                (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
                (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
                (float) (BLOCKER_SPEED_PERCENT * screenHeight));

        timeLeft = 10; //Обратный отсчет

        shotsFired = 0; //Начальное количество выстрелов
        totalElapsedTime = 0; //Обнулить затраченое время

        if (gameOver) { //Начать новую игру после завершения предыдущей
            gameOver = false;

            mCannonThread = new CannonThread(getHolder(), this); //Создать поток
            mCannonThread.start(); //Запуск потока игрового цикла
        }
        hideSystemBars();

    }

    private void showSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void stopGame() {

        if (mCannonThread != null) {
            mCannonThread.setRunning(false);//Останавливаем поток
        }
    }

    public void releaseResources() {

        //Освобождаем ресурсы
        mSoundPool.release();
        mSoundPool = null;

    }

    public int getScreenHeight() {
        return screenHeight;

    }

    public int getScreeWidth() {
        return screenWidth;
    }

    // Вызывается при создании поверхности
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (!dialogIsDisplayed) {

            newGame(); //Создание новой игры

            mCannonThread = new CannonThread(holder, this); //Создание потока
            mCannonThread.setRunning(true);//Запуск игры
            mCannonThread.start();//Запуск потока игрового цикла
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        //Обеспечить корректную зависимость потока
        boolean retry = true;

        mCannonThread.setRunning(false);

        while (retry) {
            try {
                mCannonThread.join(); //Ожидать завершение потока исполнения mCannonThread
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                alignAndFireCannonball(event);
                break;
        }
        return true;
    }

    //Многократно вызываетс CannonThread для обновления элементов игры
    private void updatePosition(double elapsedTimeMS) {

        double interval = elapsedTimeMS / 1000; //Преобразуем в секунды

        //Обновление позиции ядра
        if (mCannon.getCannonBall() != null) {
            mCannon.getCannonBall().update(interval);
        }

        //Обновление позиции блока
        mBlocker.update(interval);

        //ОБновление позиции мишени
        for (GameElement target : mTargets) {
            target.update(interval);
        }

        timeLeft -= interval; //Уменьшение оставшегося времени

        //Если счетчие достиг нуля
        if (timeLeft <= 0) {
            timeLeft = 0.0;
            gameOver = true;
            mCannonThread.setRunning(false); //Завершение потока
            showGameOverDialog(R.string.lose);//Сообщение о проигрыше
        }

        //Если все мишени поражены
        if (mTargets.isEmpty()) {
            mCannonThread.setRunning(false);
            showGameOverDialog(R.string.lose);//Сообщение о выигрыше
            gameOver = true;
        }
    }

    public void drawGameElements(Canvas canvas) {
        //Очитска фона
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

        //Вывод оставшегося времени
        canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft), 50, 100, textPaint)
        ;

        mCannon.draw(canvas);//Рисуем пушку

        //Рисование игровых элементов
        if (mCannon.getCannonBall() != null &&
                mCannon.getCannonBall().isOnScreen()) {
            mCannon.getCannonBall().draw(canvas);
        }

        mBlocker.draw(canvas); //Рисование блока

        //Рисование всех мишений
        for (GameElement target : mTargets) {
            target.draw(canvas);
        }
    }

    //Проверка столкновений с блоком или мишенью и обработка
    public void testForCollisions() {
        //Удаление мишени с которым столкнулось ядро
        if (mCannon.getCannonBall() != null &&
                mCannon.getCannonBall().isOnScreen()) {

            for (int n = 0; n < mTargets.size(); n++) {

                if (mCannon.getCannonBall().collidesWith(mTargets.get(n))) {
                    mTargets.get(n).playSound();//Звук попадания мишени

                    //Прибавление награды к оставшемуся времени
                    timeLeft += mTargets.get(n).getHitReward();
                    mCannon.removeCannonBall(); //Удаление ядра из игры
                    mTargets.remove(n); //Удаление мишени

                    --n; //Чтобы не пропустить проверку новой мишени
                    break;
                }
            }

        } else {
            //Удаление ядра, если оно находится на экране
            mCannon.removeCannonBall();
        }

        //ПРоверка столкновений с блоком
        if (mCannon.getCannonBall() != null &&
                mCannon.getCannonBall().isOnScreen()) {
            mCannon.getCannonBall().collidesWith(mBlocker);
            mBlocker.playSound();

            //Изменение направления ядра
            mCannon.getCannonBall().reverseVelosityX();

            //Уменьшение оставшегося времени на величину штрафа
            timeLeft -= mBlocker.getMissPenalty();
        }

    }


    private void showGameOverDialog(@StringRes final int messageId) {

        //Объект DialogFragment для вывода статистики и начала новой игры
        final GameOverDialogOld gameResult = GameOverDialogOld.instanceFragment(messageId);
        gameResult.setCannonView(this);

        // В UI-потоке FragmentManager используется для вывода DialogFragment
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSystemBars(); // Выход из режима погружения
                dialogIsDisplayed = true;
                gameResult.setCancelable(false); // Модальное окно
                gameResult.show(mActivity.getSupportFragmentManager(), "results");
            }
        });

    }


    //Метод определяет угол наклона ствола и стреляет из пушки, если ядро находится за экраном
    public void alignAndFireCannonball(MotionEvent event) {
        //Получение точки касания в представлении
        PointF touchPoint = new PointF(event.getX(), event.getY());

        // Вычисление расстояния точки касания от центра экрана
        // по оси y
        double centerMinusY = (screenHeight / 2 - touchPoint.y);

        //Вычислить угол ствола относительно горизонтали
        double angle = Math.atan2(centerMinusY, touchPoint.x); // TODO: 13.03.2017

        //Ствол наводится в точку касания
        mCannon.align(angle);

        //Пушка стреляет, если ядро не на экране

        if (mCannon.getCannonBall() == null ||
                !mCannon.getCannonBall().isOnScreen()) {
            mCannon.fireCannonball();
            ++shotsFired;
        }
    }

    private static class CannonThread extends Thread {

        private final SurfaceHolder mSurfaceHolder;
        private boolean threadIsRunning = true;
        private CannonView mCannonView;

        public CannonThread(SurfaceHolder holder, CannonView cannonView) {
            mSurfaceHolder = holder;
            mCannonView = cannonView;
            setName("CannonThread");
        }

        public void setRunning(boolean threadIsRunning) {
            this.threadIsRunning = threadIsRunning;
        }

        @Override
        public void run() {

            Canvas canvas = null;
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning) {

                try {
                    // Получение Canvas для монопольного рисования из этого потока
                    canvas = mSurfaceHolder.lockCanvas();

                    // Блокировка surfaceHolder для рисования
                    synchronized (mSurfaceHolder) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        totalElapsedTime += elapsedTimeMS / 1000.0;
                        mCannonView.updatePosition(elapsedTimeMS);//Обновление состояния игры
                        mCannonView.testForCollisions(); //Проверка столкновений
                        mCannonView.drawGameElements(canvas);
                        previousFrameTime = currentTime;
                    }
                } finally {
                    // Вывести содержимое canvas на CannonView
                    // и разрешить использовать Canvas другим потокам
                    if (canvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}


















