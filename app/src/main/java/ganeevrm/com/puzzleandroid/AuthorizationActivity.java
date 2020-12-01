package ganeevrm.com.puzzleandroid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
            boolean flag = true;
            do{
                String login = userCursor.getString(0);
                String pass = userCursor.getString(1);
                if ((loginField.getText().toString().equals("admin")) && (passField.getText().toString().equals("admin"))) {
                    Toast.makeText(getApplicationContext(), "Добро пожаловать Admin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainAdmin.class);
                    startActivity(intent);
                    flag = false;
                    break;
                }
                if ((loginField.getText().toString().equals(login)) && (passField.getText().toString().equals(pass))) {
                    Toast.makeText(getApplicationContext(), "Добро пожаловать", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    flag = false;
                    break;
                }
            }
            while (userCursor.moveToNext());
            if(flag) Toast.makeText(getApplicationContext(), "Вы не зарегистрированы", Toast.LENGTH_SHORT).show();
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