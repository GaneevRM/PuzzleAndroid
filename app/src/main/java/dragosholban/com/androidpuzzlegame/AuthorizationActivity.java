package dragosholban.com.androidpuzzlegame;

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

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);

        db = getBaseContext().openOrCreateDatabase("person.db", MODE_PRIVATE, null);

        db.delete("users",null,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users (login TEXT, password TEXT)");
        db.execSQL("INSERT INTO users VALUES ('admin', 'admin');");
        db.execSQL("INSERT INTO users VALUES ('user', 'user');");

    }

    public void onInput(View view) {
        Cursor query = db.rawQuery("SELECT * FROM users;", null);
        if(query.moveToFirst()){
            boolean flag = true;
            do{
                String login = query.getString(0);
                String pass = query.getString(1);
                if ((loginField.getText().toString().equals(login)) && (passField.getText().toString().equals(pass))) {
                    Toast.makeText(getApplicationContext(), "Добро пожаловать", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    flag = false;
                    break;
                }
            }
            while (query.moveToNext());
            if(flag) Toast.makeText(getApplicationContext(), "Вы не зарегистрированы", Toast.LENGTH_SHORT).show();
        }
        query.close();
    }

    public void onRegistration(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}