package com.rash1k.flagquiz;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

public class MainActivityFragment extends Fragment {

//    region
    private static final String TAG  = "FlagQuiz Activity";
    private static final int FLAGS_IN_QUIZ = 10;

//    Имена файлов с флагами
    private List<String> fileNameList;

//    Страны текущей викторины
    private List<String> quizCountriesList;

//    Регионы текущей викторины
    private Set<String> regionsSet;

//    Правильная страна для текущего флага
    private String correctAnswer;

//    Количество попыток
    private int totalGuesses;

//    Количество правильных ответов
    private int correctAnswers;

//    Количество строк с кнопками вариантов
    private int guessRows;

//    генератор случайных чисел
    private SecureRandom random;

//    Для задержки загрузки следующего флага
    private Handler handler;

//    Анимация неправильного ответа
    private Animation shakeAnimation;

//    Макет с викториной
    private LinearLayout quizLinearLayout;

//    Номер текущего вопроса
    private TextView questionNumberTextView;

//    Для вывода флага
    private ImageView flagImageView;

//    Строки с кнопками
    private LinearLayout[] guessLinearLayout;

//    Для правильного ответа
    private TextView answerTextView;

//    endregion


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
