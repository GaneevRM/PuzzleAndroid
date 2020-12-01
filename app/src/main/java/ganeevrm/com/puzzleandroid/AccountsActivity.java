package ganeevrm.com.puzzleandroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountsActivity extends AppCompatActivity {

    private ListView userList;
    private TextView header;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        header = findViewById(R.id.header);
        userList = findViewById(R.id.list);;

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_LOGIN, DatabaseHelper.COLUMN_PASSWORD};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " + userCursor.getCount());
        userList.setAdapter(userAdapter);
    }

    public void onDelete(View view){
        Toast.makeText(getApplicationContext(), "Удалить", Toast.LENGTH_SHORT).show();

    }

    public void onBlock(View view){
        Toast.makeText(getApplicationContext(), "Блочить", Toast.LENGTH_SHORT).show();

    }
    public void onAdd(View view){
        Toast.makeText(getApplicationContext(), "Добавить", Toast.LENGTH_SHORT).show();

    }

    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}