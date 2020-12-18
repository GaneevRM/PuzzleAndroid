package ganeevrm.com.puzzleandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**Название БД*/
    private static final String DATABASE_NAME = "major.db";
    /**Версия БД*/
    private static final int SCHEMA = 1;

    /**Таблица Уровней в БД*/
    public static final String TABLE_LEVEL = "level";
    /**Столбец - _id*/
    public static final String COLUMN_ID_L = "_id";
    /**Столбец - level*/
    public static final String COLUMN_LEVEL = "hard";
    /**Столбец - width*/
    public static final String COLUMN_WIDTH = "width";
    /**Столбец - height*/
    public static final String COLUMN_HEIGHT = "height";
    /**Столбец - form*/
    public static final String COLUMN_FORM = "form";


    /**Таблица Картинок в БД*/
    public static final String TABLE_PICTURE = "picture";
    /**Столбец - _id*/
    public static final String COLUMN_ID_P = "_id";
    /**Столбец - link*/
    public static final String COLUMN_LINK = "link";
    /**Столбец - complexity*/
    public static final String COLUMN_COMPLEXITY = "complexity";

    /**Таблица пользовтелей в БД*/
    public static final String TABLE = "users";
    /**Столбец - _id*/
    public static final String COLUMN_ID = "_id";
    /**Столбец - block*/
    public static final String COLUMN_BLOCK = "block";
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

        //Создание таблицы пользователей с колонками
        db.execSQL("CREATE TABLE users (" + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_BLOCK + " INTEGER," + COLUMN_LOGIN + " TEXT UNIQUE," + COLUMN_PASSWORD + " TEXT);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'admin', 'admin');");

        //Создание таблицы картинок с колонками
        db.execSQL("CREATE TABLE picture (" + COLUMN_ID_P + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_LINK + " TEXT UNIQUE," + COLUMN_COMPLEXITY + " INTEGER);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('//android_asset/img/bobby-burch-145906-unsplash.jpg', 1);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('//android_asset/img/macie-jones-287939-unsplash.jpg', 2);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('//android_asset/img/ricardo-gomez-angel-273521-unsplash.jpg', 2);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('//android_asset/img/spencer-bryan-1891-unsplash.jpg', 3);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('//android_asset/img/yuriy-garnaev-395879-unsplash.jpg', 2);");

        //Создание таблицы уровней с колонками
        db.execSQL("CREATE TABLE level (" + COLUMN_ID_L + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_LEVEL + " INTEGER," + COLUMN_WIDTH + " INTEGER," + COLUMN_HEIGHT + " INTEGER," + COLUMN_FORM + " TEXT);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_WIDTH  + ", " + COLUMN_HEIGHT  + ", " + COLUMN_FORM  + ") VALUES (1, 4, 3, 'Квадрат');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_WIDTH  + ", " + COLUMN_HEIGHT  + ", " + COLUMN_FORM  + ") VALUES (2, 5, 4, 'Треугольник');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_WIDTH  + ", " + COLUMN_HEIGHT  + ", " + COLUMN_FORM  + ") VALUES (3, 6, 6, 'Фигурный');");
    }

    //Обновление БД. Примитивный подход с удалением предыдущей БД (таблицы)
    // с помощью sql-выражения DROP и последующим ее созданием
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}

