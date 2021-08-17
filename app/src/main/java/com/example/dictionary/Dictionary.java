package com.example.dictionary;

import static com.example.dictionary.MainActivity.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    private Map<String, NoteDictionary> dictionaryMap;

    public Map<String, NoteDictionary> getDictionaryMap() {
        //создаю словарь
        dictionaryMap = new HashMap<>();

        //добавим элементы в словарь
        dictionaryMap.put("груша", new NoteDictionary("pear", 1, 1));
        dictionaryMap.put("дыня", new NoteDictionary("melon", 1, 1));
        dictionaryMap.put("персик", new NoteDictionary("peach", 1, 1));


        return dictionaryMap;
    }

    public void saveInDB(Context context, Map<String, NoteDictionary> dictionaryMap) throws IOException {

        //ссылка на строителя БД
        DBHelper dbHelper = new DBHelper(context);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        //для преобразованной записи NoteDic
        byte[] bytesNoteDictionary;

        //обходим таблицу данных
        for (int i = 0; i < dictionaryMap.size(); i++){
            // получаем данные из полей ввода
            String word = dictionaryMap.keySet().toArray()[i].toString();
            Object noteDictionary = dictionaryMap.values().toArray()[i];

            //преобразуем объектные данные в байт массив
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(noteDictionary);
                out.flush();
                bytesNoteDictionary = bos.toByteArray();
            }

            Log.d(LOG_TAG, "--- Insert in my table: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение
            cv.put("word", word);
            cv.put("dictionaryNote", bytesNoteDictionary);
            // вставляем запись и получаем ее ID
            long rowID = db.insert("mytable", null, cv);
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);


        }
        dbHelper.close();
    }

    public Map<String, NoteDictionary> getDictionaryMapDB(Context context){
        //чтение из БД
        //ссылка на строителя БД
        DBHelper dbHelper = new DBHelper(context);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

                Log.d(LOG_TAG, "--- Rows in mytable: ---");
                // делаем запрос всех данных из таблицы mytable, получаем Cursor
                Cursor c = db.query("mytable", null, null, null, null, null, null);

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (c.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");

                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "ID = " + c.getInt(idColIndex) +
                                        ", name = " + c.getString(nameColIndex) +
                                        ", email = " + c.getString(emailColIndex));
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false - выходим из цикла
                    } while (c.moveToNext());
                } else
                    Log.d(LOG_TAG, "0 rows");
                c.close();
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // удаляем все записи
                int clearCount = db.delete("mytable", null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                break;
        // закрываем подключение к БД
        dbHelper.close();


    }
}
