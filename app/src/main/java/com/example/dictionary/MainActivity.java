package com.example.dictionary;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    private TextView textView;  //текстовое поле с переводимым словом
    private RadioGroup radioGroup;  //группа радио переключателей
    private RadioButton radioButton1;   //радио переключатель 1
    private RadioButton radioButton2;   //радио переключатель 2
    private RadioButton radioButton3;   //радио переключатель 3
    private Button button;              //кнопка следующего раунда
    private Map<String, NoteDictionary> dictionaryMap; //запись словаря
    private ImageView imageView;
    private final Handler handler = new Handler();
    Dictionary dictionary;

    protected static final String LOG_TAG = "----Logs----";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLinkElements();  //получил ссылки на элементы

        //создаётся словарь из БД
        dictionary = new Dictionary();
        dictionaryMap = dictionary.getDictionaryMapDB(this);

        replace();          //заполнил поля виджета



//        //слушаем кнопку аутентификации
//        buttonAuthentication.setOnClickListener(v->{
//            requestF(intent, 1);
//        });

        //слушаем кнопку
        button.setOnClickListener(v->{
            if (radioGroup.getCheckedRadioButtonId() != -1){
                RadioButton select = findViewById(radioGroup.getCheckedRadioButtonId());
                if (Objects.requireNonNull(Objects.requireNonNull(dictionaryMap.get(textView.getText().toString())).getValue()).equals(String.valueOf(select.getText()))){
                    Objects.requireNonNull(dictionaryMap.get(textView.getText().toString())).addOneCorrect();
                    v.setBackgroundColor(Color.GREEN);
                } else {
                    Objects.requireNonNull(dictionaryMap.get(textView.getText().toString())).addOneUnCorrect();
                    v.setBackgroundColor(Color.RED);
                }
                //пауза для цвета кнопки
                handler.postDelayed(() -> {
                    v.setBackgroundColor(Color.BLUE);
                    radioGroup.clearCheck();
                    replace();
                }, 500);
            }
        });
    }
    //обработчик кнопки назад
    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        openQuitDialog();
    }

    //кнопка назад или выход
    private void openQuitDialog(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", (dialog, which) -> {
            try {
                saveProgress(); //сохраняем прогресс
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();   //выход
        });

        quitDialog.setNegativeButton("Нет", (dialog, which) -> {
        });

        quitDialog.show();
    }

    private void saveProgress() throws IOException {
        //открываем существующую или создаем новую БД и получаем ссылку на нее
        SQLiteDatabase db = dictionary.createOrGetDB(this);
        //для сохранения
            //необходимо передать новую коллекцию и ссылку на БД
        dictionary.saveInDB(dictionaryMap,db);
        db.close();
    }

    //генератор случайных чисел для словаря
    private int rnd(int length){
        Random random = new Random();
        return random.nextInt(length);
    }

    private void getLinkElements(){
        //Получаю ссылки на элементы
        textView = findViewById(R.id.textView);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = new RadioButton(this);
        radioButton2 = new RadioButton(this);
        radioButton3 = new RadioButton(this);
        button = findViewById(R.id.button);
//        buttonAuthentication = findViewById(R.id.buttonAuthentication);
        imageView = findViewById(R.id.imageView3);
    }

    //перерисовка окна
    private void replace(){
        //Поместим в текстовое поле случайный элемент из словаря
        textView.setText((String) dictionaryMap.keySet().toArray()[rnd(dictionaryMap.size())]);

        //расположим ответы
            //копия словаря как кэш
        Map<String, NoteDictionary> cashMap = new HashMap<>(dictionaryMap);
            //ставим первый радио текст
        radioButton1.setText(Objects.requireNonNull(dictionaryMap.get(textView.getText().toString())).getValue());
            //удаляем элемент с текстом из кэшСписка
        cashMap.remove(textView.getText().toString());
            //создаем список ответов
        ArrayList<NoteDictionary> ans = new ArrayList<>(cashMap.values());
            //перемешиваем его
        Collections.shuffle(ans);
            //устанавливаем второй радио элемент из списка
        NoteDictionary tmp = ans.get(rnd(ans.size()));
        radioButton2.setText(tmp.getValue());
            //удаляем установленный элемент из кэша
        ans.remove(tmp);
            //перемешиваем
        Collections.shuffle(ans);
            //установка радио
        radioButton3.setText(ans.get(rnd(ans.size())).getValue());

        //получим массив переключателей
        ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
        radioButtonArrayList.add(radioButton1);
        radioButtonArrayList.add(radioButton2);
        radioButtonArrayList.add(radioButton3);
        //перемешиваем
        Collections.shuffle(radioButtonArrayList);
        //размещаем в RadioGroup
            //очистка контейнера
        radioGroup.removeAllViews();
            //подготовка звезд
        setStar();
        radioGroup.addView(imageView);
        for (RadioButton rb : radioButtonArrayList) {
            radioGroup.addView(rb);
        }
    }

    //простановка звезд для слова
    private void setStar(){
        float rating = 100 * (Objects.requireNonNull(dictionaryMap.get(textView.getText().toString()))
                .getCorrect() / (Objects.requireNonNull(dictionaryMap.get(textView.getText().toString()))
                .getCorrect() + Objects.requireNonNull(dictionaryMap.get(textView.getText().toString()))
                .getUnCorrect()));
        rating = rating /20;
        if (rating >= 0 & rating <= 1){
            imageView.setImageResource(R.drawable.one);
        }else if (rating > 1 & rating <= 2){
            imageView.setImageResource(R.drawable.two);
        }else if (rating > 2 & rating <= 3){
            imageView.setImageResource(R.drawable.three);
        }else if (rating > 3 & rating <= 4) {
            imageView.setImageResource(R.drawable.four);
        }else {
            imageView.setImageResource(R.drawable.five);
        }
    }
}