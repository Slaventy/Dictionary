package com.example.dictionary;

import java.io.Serializable;

//элемент записи для словаря
public class NoteDictionary implements Serializable {

    //слово значение
    private final String value;
    //рейтинг правильных ответов
    private float correct;
    //рейтинг неправильных ответов
    private float unCorrect;

    NoteDictionary(String val, float cor, float unCor){
        value = val;
        correct = cor;
        unCorrect = unCor;
    }

    public String getValue() {
        return value;
    }

    public float getCorrect() {
        return correct;
    }

    public float getUnCorrect() {
        return unCorrect;
    }

    public void addOneCorrect() {
        this.correct += 1;
    }

    public void addOneUnCorrect() {
        this.unCorrect += 1;
    }
}
