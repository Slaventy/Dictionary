package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textView;  //текстовое поле с переводимым словом
    private RadioGroup radioGroup;  //группа радио переключателей
    private RadioButton radioButton1;   //радио переключатель 1
    private RadioButton radioButton2;   //радио переключатель 2
    private RadioButton radioButton3;   //радио переключатель 3
    private Button button;              //кнопка следующего раунда
    private Map<String, NoteDictionary> dictionary; //запись словаря
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLinkElements();  //получил ссылки на элементы
        buildDictionary();  //создал словарь
        replace();          //заполнил поля

        //слушаем кнопку
        button.setOnClickListener(v->{
            RadioButton select = findViewById(radioGroup.getCheckedRadioButtonId());
            if (Objects.requireNonNull(Objects.requireNonNull(dictionary.get(textView.getText().toString())).getValue()).equals(String.valueOf(select.getText()))){
                Objects.requireNonNull(dictionary.get(textView.getText().toString())).addOneCorrect();
                String s = "OK" + "correct " + Objects.requireNonNull(dictionary.get(textView.getText().toString())).getCorrect();
                Toast toastOk = Toast.makeText(this, s, Toast.LENGTH_SHORT);
                toastOk.show();
            } else {
                Objects.requireNonNull(dictionary.get(textView.getText().toString())).addOneUnCorrect();
                String s = "NOT" + "unCorrect " + Objects.requireNonNull(dictionary.get(textView.getText().toString())).getUnCorrect();
                Toast toastNo = Toast.makeText(this, s, Toast.LENGTH_SHORT);
                toastNo.show();
            }
            radioGroup.clearCheck();
            replace();
        });
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
        imageView = findViewById(R.id.imageView3);

    }
    private void buildDictionary(){
        //создаю словарь
        dictionary = new HashMap<>();

        //добавим элементы в словарь
        dictionary.put("груша", new NoteDictionary("pear", 1, 1));
        dictionary.put("дыня", new NoteDictionary("melon", 1, 1));
        dictionary.put("персик", new NoteDictionary("peach", 1, 1));
    }

    private void replace(){
        //Поместим в текстовое поле случайный элемент из словаря
        textView.setText((String) dictionary.keySet().toArray()[rnd(dictionary.size())]);

        //расположим ответы
            //копия словаря как кэш
        Map<String, NoteDictionary> cashMap = new HashMap<>(dictionary);
            //ставим первый радио текст
        radioButton1.setText(Objects.requireNonNull(dictionary.get(textView.getText().toString())).getValue());
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
    private void setStar(){
        float rating = 100 * (Objects.requireNonNull(dictionary.get(textView.getText().toString())).getCorrect() / (Objects.requireNonNull(dictionary.get(textView.getText().toString())).getCorrect() + Objects.requireNonNull(dictionary.get(textView.getText().toString())).getUnCorrect()));
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