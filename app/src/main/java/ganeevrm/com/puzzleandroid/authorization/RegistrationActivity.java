package ganeevrm.com.puzzleandroid.authorization;

import android.content.ContentValues;
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
import ganeevrm.com.puzzleandroid.user.MainUserActivity;

public class RegistrationActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_registration);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    public void onBack(View view){
        finish();
    }

    public void onOk(View view) {
        //Открываем подключение к БД
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из БД в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        if(userCursor.moveToFirst()){
            boolean duplicate = false;
            do{
                String login = userCursor.getString(2);
                //Если дубликат есть, то выводим сообщение
                if ((loginField.getText().toString().equals(login))) {
                    Toast.makeText(getApplicationContext(), "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
                    duplicate = true;
                    break;
                }
            }
            while (userCursor.moveToNext());
            if(!duplicate){
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_LOGIN, loginField.getText().toString());
                cv.put(DatabaseHelper.COLUMN_PASSWORD, passField.getText().toString());
                db.insert(DatabaseHelper.TABLE, null, cv);
                Toast.makeText(getApplicationContext(), "Вы зарегистрированы", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainUserActivity.class);
                startActivity(intent);
            }
        }
        userCursor.close();
    }

    public void onDestroy(){
        super.onDestroy();
        //Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }

}