package ganeevrm.com.puzzleandroid.authorization;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.user.MainUserActivity;

public class RegistrationActivity extends AppCompatActivity {
    /**Введите логин*/
    private EditText loginField;
    /**Введите пароль*/
    private EditText passField;
    /**Helper*/
    private DatabaseHelper databaseHelper;
    /**БД*/
    private SQLiteDatabase db;
    /**Курсор пользователей*/
    private Cursor userCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //Открываем подключение к БД
        db = databaseHelper.getReadableDatabase();

    }

    /**
     * Нажатие кнопки "Отмена"
     * @param view - View
     */
    public void onBack(View view){
        finish();
    }

    /**
     * Нажатие кнопки "Подтвердить"
     * @param view - View
     */
    public void onOk(View view) {
        //Получаем данные из БД в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        //Проверка на условия
        if ((loginField.getText().length()<4)){
            Toast.makeText(getApplicationContext(), "Минимальная длина"+"\n"+ "логина 4 символа", Toast.LENGTH_SHORT).show();
        } else if ((loginField.getText().length()>10)){
            Toast.makeText(getApplicationContext(), "Максимальная длина"+"\n"+ "логина 10 символов", Toast.LENGTH_SHORT).show();
        } else if ((passField.getText().length()<6)){
            Toast.makeText(getApplicationContext(), "Минимальная длина" +"\n"+ "пароля 6 символов", Toast.LENGTH_SHORT).show();
        }else if ((passField.getText().length()>8)){
            Toast.makeText(getApplicationContext(), "Максимальная длина"+"\n"+ "пароля 8 символов", Toast.LENGTH_SHORT).show();
        }else {
            //Ставим курсор на первую запись
            if (userCursor.moveToFirst()) {
                //Наличие дубликата
                boolean duplicate = false;
                do {
                    String login = userCursor.getString(2);
                    //Если дубликат есть, то выводим сообщение
                    if ((loginField.getText().toString().equals(login))) {
                        Toast.makeText(getApplicationContext(), "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
                        duplicate = true;
                        break;
                    }
                }
                while (userCursor.moveToNext());
                //Если дубликата нет, то заносим данные в БД и запускаем активити
                if (!duplicate) {
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_LOGIN, loginField.getText().toString());
                    cv.put(DatabaseHelper.COLUMN_PASSWORD, passField.getText().toString());
                    db.insert(DatabaseHelper.TABLE, null, cv);
                    Toast.makeText(getApplicationContext(), "Вы зарегистрированы", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainUserActivity.class);
                    startActivity(intent);
                }
            }
        }
        userCursor.close();
    }

    public void onDestroy(){
        super.onDestroy();
        //Закрываем подключение
        if (db.isOpen()) db.close();
    }

}