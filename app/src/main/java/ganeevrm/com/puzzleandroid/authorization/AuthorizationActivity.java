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
import ganeevrm.com.puzzleandroid.GalleryActivity;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.admin.MainAdminActivity;
import ganeevrm.com.puzzleandroid.user.MainUserActivity;

public class AuthorizationActivity extends AppCompatActivity {

    /**Введите логин*/
    private EditText loginField;
    /**Введите пароль*/
    private EditText passField;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    public void onInput(View view) {

        //Открываем подключение к БД
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из БД в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);

        if(userCursor.moveToFirst()){
            boolean registration = true;
            do{
                int block = userCursor.getInt(1);
                String login = userCursor.getString(2);
                String pass = userCursor.getString(3);
                //Если игрок, то открываем активити с меню для админа
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

    public void onRegistration(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        //Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}