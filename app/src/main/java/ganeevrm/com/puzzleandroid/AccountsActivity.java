package ganeevrm.com.puzzleandroid;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountsActivity extends AppCompatActivity {

    private GridView userGrid;
    private TextView header;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private SimpleCursorAdapter userAdapter;
    private boolean click;
    private int selectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        header = findViewById(R.id.header);
        userGrid = findViewById(R.id.usergrid);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                click = true;
                selectUser = position;
            }
        };
        userGrid.setOnItemClickListener(itemListener);
    }

    public void onResume() {
        super.onResume();
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();

        //Получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE, null);
        //Определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_LOGIN, DatabaseHelper.COLUMN_PASSWORD};
        //Создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " + userCursor.getCount());
        userGrid.setAdapter(userAdapter);
    }



    public void onDelete(View view){
        if(click){

        } else Toast.makeText(getApplicationContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show();

    }

    public void onBlock(View view){
        if(click){

        } else Toast.makeText(getApplicationContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show();

    }
    public void onAdd(View view){
        if(click){

            //Получаем вид с файла prompt.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompt, null);

            //Создаем AlertDialog
            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

            //Настраиваем prompt.xml для нашего AlertDialog:
            mDialogBuilder.setView(promptsView);

            //Настраиваем отображение поля для ввода текста в открытом диалоге:
            final EditText loginInput = (EditText) promptsView.findViewById(R.id.input_login);

            final EditText passwordInput = (EditText) promptsView.findViewById(R.id.input_password);


            //Настраиваем сообщение в диалоговом окне:
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //Вводим текст и отображаем в строке ввода на основном экране:
                                    Toast.makeText(getApplicationContext(), "Добавление в БД", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            //Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();

            //и отображаем его:
            alertDialog.show();
        } else Toast.makeText(getApplicationContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show();
    }

    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}