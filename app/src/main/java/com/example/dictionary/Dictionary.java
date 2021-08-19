package com.example.dictionary;

import static com.example.dictionary.MainActivity.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**класс для создания колекции словаря*/

public class Dictionary {

    private Map<String, NoteDictionary> dictionaryMap;


    public Map<String, NoteDictionary> getDictionaryMap() {

        //добавим элементы в словарь
        dictionaryMap.put("груша", new NoteDictionary("pear", 1, 1));
        dictionaryMap.put("дыня", new NoteDictionary("melon", 1, 1));
        dictionaryMap.put("персик", new NoteDictionary("peach", 1, 1));
        dictionaryMap.put("а, и", new NoteDictionary("and", 1, 1));
        dictionaryMap.put("должен", new NoteDictionary("must", 1, 1));
        dictionaryMap.put("идти", new NoteDictionary("go", 1, 1));
        dictionaryMap.put("иметь", new NoteDictionary("have", 1, 1));
        dictionaryMap.put("картинка", new NoteDictionary("picture", 1, 1));
        dictionaryMap.put("любить, нравиться", new NoteDictionary("like", 1, 1));
        dictionaryMap.put("мальчик", new NoteDictionary("boy", 1, 1));
        dictionaryMap.put("много", new NoteDictionary("many", 1, 1));
        dictionaryMap.put("молоко", new NoteDictionary("milk", 1, 1));
        dictionaryMap.put("но", new NoteDictionary("but", 1, 1));
        dictionaryMap.put("сахар", new NoteDictionary("sugar", 1, 1));
        dictionaryMap.put("сейчас", new NoteDictionary("now", 1, 1));
        dictionaryMap.put("собака", new NoteDictionary("dog", 1, 1));
        dictionaryMap.put("три", new NoteDictionary("three", 1, 1));
        dictionaryMap.put("цветок", new NoteDictionary("flower", 1, 1));
        dictionaryMap.put("ананас", new NoteDictionary("pineapple", 1, 1));
        dictionaryMap.put("апельсин", new NoteDictionary("orange", 1, 1));
        dictionaryMap.put("бабушка", new NoteDictionary("grandmother", 1, 1));
        dictionaryMap.put("банан", new NoteDictionary("banana", 1, 1));
        dictionaryMap.put("белый", new NoteDictionary("white", 1, 1));
        dictionaryMap.put("большой", new NoteDictionary("big, large", 1, 1));
        dictionaryMap.put("брат ", new NoteDictionary("brother", 1, 1));
        dictionaryMap.put("в ", new NoteDictionary("in", 1, 1));
        dictionaryMap.put("в школе", new NoteDictionary("at school", 1, 1));
        dictionaryMap.put("город большой", new NoteDictionary("city", 1, 1));
        dictionaryMap.put("город небольшой", new NoteDictionary("town", 1, 1));
        dictionaryMap.put("да", new NoteDictionary("yes", 1, 1));
        dictionaryMap.put("девушка", new NoteDictionary("girl", 1, 1));
        dictionaryMap.put("дедушка", new NoteDictionary("grandfather", 1, 1));
        dictionaryMap.put("дерево", new NoteDictionary("tree", 1, 1));
        dictionaryMap.put("дети", new NoteDictionary("children", 1, 1));




        return dictionaryMap;
    }



    public SQLiteDatabase createOrGetDB(Context context ){
        //получим список БД доступных для приложения
        String[] dbList = context.databaseList();
        //найдем созданную ранее БД
        for (String s : dbList) {
            if (s.equals("dicApp.db")) {
                Log.d(LOG_TAG, "we have DB = " + s);
                //получаем ссылку на вновь созданную или существующую БД
                return context.openOrCreateDatabase("dicApp.db", Context.MODE_PRIVATE, null);
            }
        }
        //если БД не найдена
        Log.d(LOG_TAG, "we have NOT DB = ");
        //нужно ее создать
        SQLiteDatabase db = context.openOrCreateDatabase("dicApp.db", Context.MODE_PRIVATE, null);
        Log.d(LOG_TAG, "Create db SQLiteDatabase " + db.isOpen());
        //создать поля в БД
        db.execSQL("CREATE TABLE IF NOT EXISTS dictionary (word TEXT primary key, translate TEXT, correct INTEGER, unCorrect INTEGER)");
        Log.d(LOG_TAG, "Create TABLES and values by dictionary");
        //добавить элементы
            // создаем объект для данных
        ContentValues cv = new ContentValues();
        //берем штатную колекцию
        Log.d(LOG_TAG, "берем штатную колекцию");
        dictionaryMap = new HashMap<>();
        dictionaryMap = getDictionaryMap();

        Log.d(LOG_TAG, "взяли штатную колекцию");
            //в цикле запишем в БД поля коллекции
        for (int i = 0; i < dictionaryMap.size(); i++){
            cv.put("word", dictionaryMap.keySet().toArray()[i].toString());
            NoteDictionary nd = (NoteDictionary)dictionaryMap.values().toArray()[i];
            cv.put("translate", nd.getValue() );
            cv.put("correct", nd.getCorrect());
            cv.put("unCorrect", nd.getUnCorrect());
            //передаем в БД
            long rowID = db.insert("dictionary", null, cv);
            //выводим лог
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
        }

        return db;
    }


    public void saveInDB(Map<String, NoteDictionary> newDictionaryMap, SQLiteDatabase db) {
        // создаем объект для данных
        ContentValues cv = new ContentValues();

        //в цикле запишем в БД поля коллекции
        for (int i = 0; i < newDictionaryMap.size(); i++){
            cv.put("word", newDictionaryMap.keySet().toArray()[i].toString());
            NoteDictionary nd = (NoteDictionary)newDictionaryMap.values().toArray()[i];
            cv.put("translate", nd.getValue() );
            cv.put("correct", nd.getCorrect());
            cv.put("unCorrect", nd.getUnCorrect());
            int updCount = db.update("dictionary", cv, "word = ?",
                    new String[] {(newDictionaryMap.keySet().toArray()[i]).toString()});
            Log.d(LOG_TAG, "updated rows count = " + updCount);
        }
    }

    public Map<String, NoteDictionary> getDictionaryMapDB(SQLiteDatabase db){
        //чтение из БД
                dictionaryMap = new HashMap<>();

                Log.d(LOG_TAG, "--- Rows in dictionary: ---");
                // делаем запрос всех данных из таблицы dictionary, получаем Cursor
                Cursor c = db.query("dictionary", null, null, null, null, null, null);
                Log.d(LOG_TAG, "Cursor " + c.getCount());

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (c.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idWord = c.getColumnIndex("word");
                    int translateIndex = c.getColumnIndex("translate");
                    int correctIndex = c.getColumnIndex("correct");
                    int unCorrectIndex = c.getColumnIndex("unCorrect");

                    do {
                        // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "word = " + c.getString(idWord) +
                                        ", translate = " + c.getString(translateIndex) +
                                        ", correct = " + c.getInt(correctIndex) +
                                        ", unCorrect = " + c.getInt(unCorrectIndex));
                        //записываем в обновленную колекцию
                        dictionaryMap.put(c.getString(idWord), new NoteDictionary(c.getString(translateIndex), c.getInt(correctIndex), c.getInt(unCorrectIndex)));
                        // переход на следующую строку
                        // а если следующей нет (текущая - последняя), то false - выходим из цикла
                    } while (c.moveToNext());
                } else
                    Log.d(LOG_TAG, "0 rows");
                c.close();
                return dictionaryMap;
    }

}
