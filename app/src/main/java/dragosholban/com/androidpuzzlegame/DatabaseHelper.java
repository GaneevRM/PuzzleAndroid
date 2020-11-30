package dragosholban.com.androidpuzzlegame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**Название БД*/
    private static final String DATABASE_NAME = "person.db";
    /**Версия БД*/
    private static final int SCHEMA = 1;
    /**Название таблицы в БД*/
    static final String TABLE = "users";
    /**Столбец - login*/
    public static final String COLUMN_LOGIN = "login";
    /**Столбец - password*/
    public static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    //Если база данных отсутствует или ее версия (которая задается в переменной SCHEMA) выше текущей,
    // то срабатывает метод onCreate()
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Создание таблицы с двумя колонками
        db.execSQL("CREATE TABLE users (" + COLUMN_LOGIN + " TEXT, " + COLUMN_PASSWORD + " TEXT);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES ('admin', 'admin');");
    }

    //Обновление БД. Примитивный подход с удалением предыдущей БД
    // с помощью sql-выражения DROP и последующим ее созданием
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}

