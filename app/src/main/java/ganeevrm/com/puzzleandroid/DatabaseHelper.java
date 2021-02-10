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
    /**Столбец - col_pieces*/
    public static final String COLUMN_COL_PIECES = "col_pieces";
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

    /**Таблица игр в БД*/
    public static final String TABLE_GAME = "games";
    /**Столбец - _id*/
    public static final String COLUMN_ID_G = "_id";
    /**Столбец - _id_pic*/
    public static final String COLUMN_PIC_ID = "_id_pic";
    /**Столбец - _id_level*/
    public static final String COLUMN_LEVEL_ID = "_id_level";
    /**Столбец - link_pick_g*/
    public static final String COLUMN_LINK_PIC_G = "link_pick_g";
    /**Столбец - level_g*/
    public static final String COLUMN_LEVEL_G = "level_g";
    /**Столбец - col_pieces_g*/
    public static final String COLUMN_COL_PIECES_G = "col_pieces_g";
    /**Столбец - form_g*/
    public static final String COLUMN_FORM_G = "form_g";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    //Если база данных отсутствует или ее версия (которая задается в переменной SCHEMA) выше текущей,
    // то срабатывает метод onCreate()
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Создание таблицы пользователей с колонками
        db.execSQL("CREATE TABLE users (" + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_BLOCK + " INTEGER," + COLUMN_LOGIN + " TEXT UNIQUE," + COLUMN_PASSWORD + " TEXT);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'admin', 'admin');");
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'Сергей', 'Сергей');");
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'Андрей', 'Андрей');");
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'Мария', 'Мария');");
        db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_BLOCK + ", " + COLUMN_LOGIN + ", " + COLUMN_PASSWORD  + ") VALUES (0, 'Виктор', 'Виктор');");

        //Создание таблицы картинок с колонками
        db.execSQL("CREATE TABLE picture (" + COLUMN_ID_P + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_LINK + " TEXT UNIQUE," + COLUMN_COMPLEXITY + " INTEGER);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/1_pic.jpg', 1);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/2_pic.jpg', 4);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/3_pic.jpg', 4);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/4_pic.jpg', 4);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/5_pic.jpg', 10);");
        db.execSQL("INSERT INTO "+ TABLE_PICTURE +" (" + COLUMN_LINK + ", " + COLUMN_COMPLEXITY  + ") VALUES ('/android_asset/img/6_pic.jpg', 10);");

        //Создание таблицы уровней с колонками
        db.execSQL("CREATE TABLE level (" + COLUMN_ID_L + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"  + COLUMN_LEVEL + " INTEGER UNIQUE," + COLUMN_COL_PIECES + " INTEGER," + COLUMN_FORM + " TEXT);");
        //Добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (1, 16, 'Квадрат');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (3, 36, 'Квадрат');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (5, 64, 'Квадрат');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (7, 36, 'Треугольник');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (9, 64, 'Треугольник');");
        db.execSQL("INSERT INTO "+ TABLE_LEVEL +" (" + COLUMN_LEVEL + ", " + COLUMN_COL_PIECES  + ", " + COLUMN_FORM  + ") VALUES (10, 64, 'Фигура');");

        //Создание таблицы игр с колонками
        db.execSQL("CREATE TABLE games (" + COLUMN_ID_G + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "  + COLUMN_PIC_ID + " INTEGER NOT NULL, " + COLUMN_LEVEL_ID + " INTEGER NOT NULL, "
                + COLUMN_LINK_PIC_G + " TEXT, " + COLUMN_LEVEL_G + " INTEGER, "
                + COLUMN_COL_PIECES_G + " INTEGER, " + COLUMN_FORM_G + " TEXT, " + "FOREIGN KEY (_id_level) REFERENCES level(_id), "  + "FOREIGN KEY (_id_pic) REFERENCES picture(_id));");

        db.execSQL("INSERT INTO "+ TABLE_GAME +" (" + COLUMN_PIC_ID + ", " + COLUMN_LEVEL_ID  + ", " + COLUMN_LINK_PIC_G + ", " + COLUMN_LEVEL_G + ", " + COLUMN_COL_PIECES_G + ", " + COLUMN_FORM_G +") VALUES (1, 1, '/android_asset/img/1_pic.jpg', 1, 16, 'Квадрат');");
        db.execSQL("INSERT INTO "+ TABLE_GAME +" (" + COLUMN_PIC_ID + ", " + COLUMN_LEVEL_ID  + ", " + COLUMN_LINK_PIC_G + ", " + COLUMN_LEVEL_G + ", " + COLUMN_COL_PIECES_G + ", " + COLUMN_FORM_G +") VALUES (4, 4, '/android_asset/img/4_pic.jpg', 7, 36, 'Треугольник');");
        db.execSQL("INSERT INTO "+ TABLE_GAME +" (" + COLUMN_PIC_ID + ", " + COLUMN_LEVEL_ID  + ", " + COLUMN_LINK_PIC_G + ", " + COLUMN_LEVEL_G + ", " + COLUMN_COL_PIECES_G + ", " + COLUMN_FORM_G +") VALUES (6, 6, '/android_asset/img/6_pic.jpg', 10, 64, 'Фигура');");
    }

    //Обновление БД. Примитивный подход с удалением предыдущей БД (таблицы)
    // с помощью sql-выражения DROP и последующим ее созданием
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}

