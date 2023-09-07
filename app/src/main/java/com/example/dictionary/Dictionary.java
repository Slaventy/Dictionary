package com.example.dictionary;

import static com.example.dictionary.MainActivity.LOG_TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**класс для создания колекции словаря*/

public class Dictionary {
    private Map<String, NoteDictionary> dictionaryMap;
    private Map<String, NoteDictionary> dictionaryMapForRet;
    private SQLiteDatabase dictionarySQLiteDBData;

    private void openBook(){
        try {
            InputStream s = new FileInputStream("res/Dictionary.xlsx");
            XSSFWorkbook book = (XSSFWorkbook) WorkbookFactory.create(s);
            XSSFSheet sheet = book.getSheet("Лист1");
            Iterator<Row> ri = sheet.rowIterator();
            String word = "", translate = "";
            int rightAnswer = 0, wrongAnswer = 0;
            while (ri.hasNext()){
                XSSFRow row = (XSSFRow) ri.next();
                Iterator<Cell> ci = row.cellIterator();
                while (ci.hasNext()){
                    XSSFCell cell = (XSSFCell) ci.next();
                    //код
                    switch (cell.getColumnIndex()){
                        case 0: {translate = cell.getRichStringCellValue().getString();}
                        case 1: {word = cell.getStringCellValue();}
                        case 2: {rightAnswer = (int) cell.getNumericCellValue();}
                        case 3: {wrongAnswer = (int) cell.getNumericCellValue();}
                    }
                }
            dictionaryMap.put(word, new NoteDictionary(translate, rightAnswer, wrongAnswer));
                rightAnswer = 0;
                wrongAnswer = 0;
            }

            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Dictionary(){
        openBook();

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
//        //берем штатную колекцию
//        Log.d(LOG_TAG, "берем штатную колекцию");
//        dictionaryMap = new HashMap<>();
//        dictionaryMap = getDictionaryMap();

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
    public Map<String, NoteDictionary> getDictionaryMapDB(Context context){
        dictionarySQLiteDBData = createOrGetDB(context);
        dictionaryMapForRet = getDictionaryMapDB(dictionarySQLiteDBData);
        dictionarySQLiteDBData.close();
        return dictionaryMapForRet;
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
