package dragosholban.com.androidpuzzlegame;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {
    /**Введите логин*/
    private EditText loginField;
    /**Введите пароль*/
    private EditText passField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loginField = findViewById(R.id.editTextTextPersonName);
        passField = findViewById(R.id.editTextTextPassword);
    }

    public void onBack(View view){
        finish();
    }

    public void onOk(View view) {
        Toast.makeText(getApplicationContext(), "Добро пожаловать", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}