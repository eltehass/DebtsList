package com.example.leonid.debterautocorrect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;

    private static final String DB_TABLE1 = "mytab1";
    private static final String DB_TABLE2 = "mytab2";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TXT1 = "txt1";
    public static final String COLUMN_TXT2 = "txt2";
    public static final String COLUMN_TXT3 = "txt3";

    private static final String DB_CREATE1 =
            "create table " + DB_TABLE1 + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TXT1 + " text, " +
                    COLUMN_TXT2 + " text, " +
                    COLUMN_TXT3 + " text" +
                    ");";

    private static final String DB_CREATE2 =
            "create table " + DB_TABLE2 + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TXT1 + " text, " +
                    COLUMN_TXT2 + " text, " +
                    COLUMN_TXT3 + " text" +
                    ");";


    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(String DB_TABLE) {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(String DB_TABLE,String txt1, String txt2, String txt3) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TXT1, txt1);
        cv.put(COLUMN_TXT2, txt2);
        cv.put(COLUMN_TXT3, txt3);
        mDB.insert(DB_TABLE, null, cv);
    }

    // удалить запись из DB_TABLE
    public void delRec(String DB_TABLE,long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE1);
            db.execSQL(DB_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}