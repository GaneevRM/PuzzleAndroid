package ganeevrm.com.puzzleandroid.admin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;

public class AccountsActivity extends AppCompatActivity {
    /**GridView для аккаунтов*/
    private GridView userGrid;
    /**Адаптер*/
    private SimpleCursorAdapter userAdapter;
    /**Надпись "Найдено элементов:"*/
    private TextView searchText;
    /**Helper*/
    private DatabaseHelper databaseHelper;
    /**БД*/
    private SQLiteDatabase db;
    /**Курсор пользователей*/
    private Cursor userCursor;
    /**Наличие нажатия на элемент GridView*/
    private boolean click;
    /**id выбранного аккаунта*/
    private long selectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        searchText = findViewById(R.id.searchText);
        userGrid = findViewById(R.id.userGrid);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //Создаём Listener
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                click = true;
                selectUser = id;
            }
        };
        //Устанавливаем Listener на userGrid
        userGrid.setOnItemClickListener(itemListener);
    }

    @SuppressLint("SetTextI18n")
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
        searchText.setText("Найдено элементов: " + userCursor.getCount());
        userGrid.setAdapter(userAdapter);
    }

    /**
     * Нажатие кнопки "Удалить"
     * @param view - View
     */
    public void onDelete(View view){
        //Проверка на наличие нажатия
        if(click){
            //Если выбран первый аккаунт
            if(selectUser == 1){
                Toast.makeText(getApplicationContext(), "Администратора нельзя удалить", Toast.LENGTH_SHORT).show();
            }else {
                db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(selectUser)});
                Toast.makeText(getApplicationContext(), "Пользователь удалён", Toast.LENGTH_SHORT).show();
                recreate();
            }
        } else Toast.makeText(getApplicationContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show();

    }

    /**
     * Нажатие кнопки "Блокировать"
     * @param view - View
     */
    public void onBlock(View view){
        //Проверка на наличие нажатия
        if(click){
            //Если выбран первый аккаунт
            if(selectUser == 1){
                Toast.makeText(getApplicationContext(), "Администратора нельзя заблокировать", Toast.LENGTH_SHORT).show();
            }else {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_BLOCK, 1);
                db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + selectUser, null);
                Toast.makeText(getApplicationContext(), "Пользователь заблокирован", Toast.LENGTH_SHORT).show();
                recreate();
            }

        } else Toast.makeText(getApplicationContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show();

    }

    /**
     * Нажатие кнопки "Добавить"
     * @param view - View
     */
    public void onAdd(View view){
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText loginInput = promptsView.findViewById(R.id.input_login);

        final EditText passwordInput =promptsView.findViewById(R.id.input_password);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(loginInput.getText().length()!=0 && passwordInput.getText().length()!=0){
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseHelper.COLUMN_BLOCK, Boolean.FALSE);
                                    cv.put(DatabaseHelper.COLUMN_LOGIN, loginInput.getText().toString());
                                    cv.put(DatabaseHelper.COLUMN_PASSWORD, passwordInput.getText().toString());
                                    db.insert(DatabaseHelper.TABLE, null, cv);
                                    Toast.makeText(getApplicationContext(), "Пользователь добавлен", Toast.LENGTH_SHORT).show();
                                    //Обновляем курсор и адаптер (requery устарел, поэтому нужно будет исправить)
                                    userCursor.requery();
                                    userAdapter.notifyDataSetChanged();
                                }else Toast.makeText(getApplicationContext(), "Данные не введены", Toast.LENGTH_SHORT).show();
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

        //И отображаем его:
        alertDialog.show();
    }

    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}