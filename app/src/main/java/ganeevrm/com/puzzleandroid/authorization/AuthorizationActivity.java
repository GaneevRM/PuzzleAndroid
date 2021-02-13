package ganeevrm.com.puzzleandroid.authorization;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.admin.MainAdminActivity;
import ganeevrm.com.puzzleandroid.user.MainUserActivity;

public class AuthorizationActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_authorization);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //Открываем подключение к БД
        db = databaseHelper.getReadableDatabase();
    }

    /**
     * Нажатие кнопки входа
     * @param view - View
     */
    public void onInput(View view) {
        //Получаем данные из БД в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        //Ставим курсор на первую запись
        if(userCursor.moveToFirst()){
            //Наличие регистрации
            boolean registration = true;
            do{
                //Блокировка пользователя
                int block = userCursor.getInt(1);
                //Логин пользователя
                String login = userCursor.getString(2);
                //Пароль пользователя
                String pass = userCursor.getString(3);
                //Если админ, то открываем активити с меню для админа
                if ((loginField.getText().toString().equals("admin")) && (passField.getText().toString().equals("admin"))) {
                    Toast.makeText(getApplicationContext(), "Добро пожаловать Admin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainAdminActivity.class);
                    startActivity(intent);
                    registration = false;
                    break;
                }
                //Если не админ, то открываем активити с меню для игрока
                if ((loginField.getText().toString().equals(login)) && (passField.getText().toString().equals(pass))) {
                    //Проверяем пользователя на блокировку
                    if(block==1){
                        Toast.makeText(getApplicationContext(), "Вы заблокированы", Toast.LENGTH_SHORT).show();
                        registration = false;
                        break;
                    }
                    Toast.makeText(getApplicationContext(), "Добро пожаловать", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainUserActivity.class);
                    startActivity(intent);
                    registration = false;
                    break;
                }
            }
            while (userCursor.moveToNext());
            if(registration) Toast.makeText(getApplicationContext(), "Вы не зарегистрированы", Toast.LENGTH_SHORT).show();
        }
        userCursor.close();
    }

    /**
     * Нажатие кнопки регистрации
     * @param view - View
     */
    public void onRegistration(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        //Закрываем подключение
        if (db.isOpen()) db.close();
    }
}