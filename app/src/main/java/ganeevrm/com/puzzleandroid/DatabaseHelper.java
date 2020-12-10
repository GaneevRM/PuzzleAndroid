package ganeevrm.com.puzzleandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**Название БД*/
    private static final String DATABASE_NAME = "major.db";
    /**Версия БД*/
    private static final int SCHEMA = 1;

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
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('https://cdn.pixabay.com/photo/2016/11/08/05/18/hot-air-ballon-1807521_960_720.jpg', 1);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('https://cdn.pixabay.com/photo/2017/09/02/15/10/greece-2707528_960_720.jpg', 2);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('https://cdn.pixabay.com/photo/2016/11/29/12/55/architecture-1869661_960_720.jpg', 2);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('https://cdn.pixabay.com/photo/2016/01/19/17/35/rocky-mountains-1149778_960_720.jpg', 3);");

    }

    //Обновление БД. Примитивный подход с удалением предыдущей БД (таблицы)
    // с помощью sql-выражения DROP и последующим ее созданием
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}

