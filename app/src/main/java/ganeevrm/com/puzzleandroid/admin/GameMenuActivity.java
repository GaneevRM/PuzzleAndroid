package ganeevrm.com.puzzleandroid.admin;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.gallery.GlideGalleryActivity;
import ganeevrm.com.puzzleandroid.game.PuzzleActivity;

public class GameMenuActivity extends AppCompatActivity {
    /**Helper*/
    private DatabaseHelper databaseHelper;
    /**БД*/
    private SQLiteDatabase db;
    /**Курсор*/
    private Cursor gameCursor;
    private SimpleCursorAdapter scAdapter;

    /**Выбранный RadioButton в диалоговом окне "Тип подсчёта"*/
    private int selectRadioType = 0;
    /**Выбранный RadioButton в диалоговом окне "Режим сборки"*/
    private int selectRadioMode = 0;

    /**Индикатор выбора одной игры*/
    private boolean click = false;
    /**Позиция в списке*/
    private int positionList = -1;
    /**Id игры*/
    private long gameid = 0;

    /**Список игр*/
    ListView gameList;
    /**Текст с количеством игр*/
    TextView tvheader;

    /**Для получения значения из Bundle*/
    private String who;
    /**Интерфейс для админа*/
    private LinearLayout adminLayout;
    /**Интерфейс для игрока*/
    private LinearLayout userLayout;

    /**Константа для использования в качестве requestCode*/
    static final private int CHOOSE_IDS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        adminLayout = findViewById(R.id.adminLayout);
        userLayout = findViewById(R.id.userLayout);
        gameList = findViewById(R.id.gameList);
        tvheader = findViewById(R.id.header);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();
        //Получаем список игр из БД в курсор
        gameCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_GAME, null);

        //Присваиваем значение who
        Bundle arguments = getIntent().getExtras();
        who = (String) arguments.get("Who");

        //Создаём Listener
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                gameCursor.moveToPosition(position);
                //При клике на элемент списка проверяем булеву переменную, чтобы
                //была выбрана только одна игра. Далее окрашиваем элемент в серый цвет,
                //сохраняем позицию и id игры, изменяем значение булевой переменной
                if(!click){
                    v.setBackgroundColor(Color.LTGRAY);
                    positionList = position;
                    gameid =  id;
                    click = true;
                    tvheader.setText("Выбрана игра: " + (positionList+1));
                }else {
                    //Если клик был произведён по уже выбранному элементу, то
                    //убираем цвет и сбрасываем все значения переменных
                    if (position == positionList) {
                        v.setBackgroundColor(Color.TRANSPARENT);
                        positionList = -1;
                        gameid = 0;
                        click = false;
                        tvheader.setText("Найдено элементов: " + gameCursor.getCount());
                    }
                }
            }
        };
        //Устанаваливаем Listener у gameList
        gameList.setOnItemClickListener(itemListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataBase();

        positionList=-1;
        click=false;

        //Меняем интерфейс в зависимоти от значения who
        if(who.equals("user")){
            adminLayout.setVisibility(View.GONE);
            userLayout.setVisibility(View.VISIBLE);
        }else{
            adminLayout.setVisibility(View.VISIBLE);
            userLayout.setVisibility(View.GONE);
        }

        //Формируем столбцы сопоставления
        String[] from = new String[] { DatabaseHelper.COLUMN_LINK_PIC_G, DatabaseHelper.COLUMN_LEVEL_G, DatabaseHelper.COLUMN_COL_PIECES_G, DatabaseHelper.COLUMN_FORM_G };
        int[] to = new int[] { R.id.imageViewGame, R.id.tv_level_info, R.id.tv_piec_info, R.id.tv_form_info };
        //Создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_game_item, gameCursor, from, to, 0);
        //Назначаем свой обработчик
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){

            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if(view.getId() == R.id.imageViewGame){
                    Picasso.get().load("file://"+cursor.getString(i)).into((ImageView)view);
                    return true;
                }
                if(view.getId() == R.id.tv_level_info){
                    TextView text = (TextView)view;
                    text.setText("Сложность - " + cursor.getInt(i));
                    return true;
                }
                if(view.getId() == R.id.tv_piec_info){
                    TextView text = (TextView)view;
                    text.setText("Количество элементов - " + cursor.getInt(i));
                    return true;
                }
                if(view.getId() == R.id.tv_form_info){
                    TextView text = (TextView)view;
                    text.setText("Форма - " + cursor.getString(i));
                    return true;
                }
                return false;
            }
        });
        //Устанаваливаем адаптер у gameList
        gameList.setAdapter(scAdapter);
        tvheader.setText("Найдено элементов: " + gameCursor.getCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!gameCursor.isClosed()) gameCursor.close();
        if (db.isOpen()) {
            db.close();
            databaseHelper.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!gameCursor.isClosed()) gameCursor.close();
        if (db.isOpen()) {
            db.close();
            databaseHelper.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkDataBase();
        //Если вызов прошел успешно и requestCode равен константе CHOOSE_IDS,
        //то получаем данные для idLevel и idPic
        if(requestCode == CHOOSE_IDS && resultCode == RESULT_OK){
            long idLevel = data.getLongExtra("idlevel", -1);
            long idPic = data.getLongExtra("idpic", 0);
            //Если выбрана и картинка и уровень, то создаем запись с новой игрой в таблице games
            if(!(idLevel == -1 || idPic == 0)){
                Cursor picCursor = db.query("picture", new String[]{"_id", "link"}, "_id=?", new String[]{String.valueOf(idPic)}, null, null, null);
                picCursor.moveToFirst();
                Cursor levelCursor = db.query("level", new String[]{"_id", "hard", "col_pieces", "form"}, "_id=?", new String[]{String.valueOf(idLevel)}, null, null, null);
                levelCursor.moveToFirst();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_PIC_ID, picCursor.getInt(0));
                cv.put(DatabaseHelper.COLUMN_LEVEL_ID, levelCursor.getInt(0));
                cv.put(DatabaseHelper.COLUMN_LINK_PIC_G, picCursor.getString(1));
                cv.put(DatabaseHelper.COLUMN_LEVEL_G, levelCursor.getInt(1));
                cv.put(DatabaseHelper.COLUMN_COL_PIECES_G, levelCursor.getInt(2));
                cv.put(DatabaseHelper.COLUMN_FORM_G, levelCursor.getString(3));
                db.insert(DatabaseHelper.TABLE_GAME, null, cv);
                Toast.makeText(getApplicationContext(), "Обновление БД", Toast.LENGTH_SHORT).show();
                picCursor.close();
                levelCursor.close();
                gameCursor = databaseHelper.getNewCursor(db, DatabaseHelper.TABLE_GAME);
                scAdapter.changeCursor(gameCursor);
            }
        }
    }

    /**
     * Проверка закрытости бд, курсора и их инициализация
     */
    public void checkDataBase(){
        if(!db.isOpen()){
            databaseHelper = new DatabaseHelper(getApplicationContext());
            db = databaseHelper.getReadableDatabase();
        }

        if (gameCursor.isClosed()){
            gameCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_GAME, null);
        }
    }

    /**
     * Нажатие кнопки "Добавить"
     * @param view - View
     */
    public void onAdd(View view) {
        Intent galleryIntent = new Intent(this, GlideGalleryActivity.class);
        startActivityForResult(galleryIntent, CHOOSE_IDS);
    }

    /**
     * Нажатие кнопки "Удалить"
     * @param view - View
     */
    public void onDel(View view) {
        if(!click){
            Toast.makeText(getApplicationContext(), "Выберите игру", Toast.LENGTH_SHORT).show();
        }else {
            //Извлекаем id записи и удаляем соответствующую запись в БД
            db.delete(DatabaseHelper.TABLE_GAME, "_id = ?", new String[]{String.valueOf(gameid)});
            Toast.makeText(getApplicationContext(), "Игра удалена", Toast.LENGTH_SHORT).show();
            gameCursor = databaseHelper.getNewCursor(db, DatabaseHelper.TABLE_GAME);
            tvheader.setText("Найдено элементов: " + gameCursor.getCount());
            scAdapter.changeCursor(gameCursor);
            positionList=-1;
            click=false;
        }
    }

    /**
     * Нажатие кнопки "Режим сборки"
     * @param view - View
     */
    public void onBuild(View view) {
        //Получаем вид с файла context_build.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_build, null);

        //Создаем AlertDialog.Builder
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Назначаем AlertDialog.Builder вид из context_build.xml
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение трёх RadioButton
        final RadioButton rbField = promptsView.findViewById(R.id.onField);
        final RadioButton rbHeap = promptsView.findViewById(R.id.onHeap);
        final RadioButton rbTape = promptsView.findViewById(R.id.onTape);

        switch (selectRadioMode) {
            case 0:
                rbField.setChecked(true);
                onRadioButtonClicked(rbField);
                break;
            case 1:
                rbHeap.setChecked(true);
                onRadioButtonClicked(rbHeap);
                break;
            case 2:
                rbTape.setChecked(true);
                onRadioButtonClicked(rbTape);
                break;
        }

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Toast.makeText(getApplicationContext(), "Режим выбран", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Обработка включения RadioButton
     * @param view - View
     */
    public void onRadioButtonClicked(View view) {
        //Если RadioButton включен
        boolean checked = ((RadioButton) view).isChecked();
        //Получаем id нажатого RadioButton и присваиваем значение
        //переменной selectRadioType или selectRadioMode
        switch(view.getId()) {
            case R.id.onTime:
                if (checked){
                    selectRadioType = 0;
                }
                break;
            case R.id.onScore:
                if (checked){
                    selectRadioType = 1;
                }
                break;
            case R.id.onField:
                if (checked){
                    selectRadioMode = 0;
                }
                break;
            case R.id.onHeap:
                if (checked){
                    selectRadioMode = 1;
                }
                break;
            case R.id.onTape:
                if (checked){
                    selectRadioMode = 2;
                }
                break;
        }
    }

    /**
     * Нажатие кнопки "Тип подсчёта"
     * @param view - View
     */
    public void onType(View view) {
        //Получаем вид с файла context_type.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_type, null);

        //Создаем AlertDialog.Builder
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Назначаем AlertDialog.Builder вид из context_build.xml
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение двух RadioButton
        final RadioButton rbTime = promptsView.findViewById(R.id.onTime);
        final RadioButton rbScore = promptsView.findViewById(R.id.onScore);

        switch (selectRadioType) {
            case 0:
                rbTime.setChecked(true);
                onRadioButtonClicked(rbTime);
                break;
            case 1:
                rbScore.setChecked(true);
                onRadioButtonClicked(rbScore);
                break;
        }

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                    Toast.makeText(getApplicationContext(), "Тип выбран", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Нажатие кнопки "Начать игру"
     * @param view - View
     */
    public void startGame(View view) {
        if(!click){
            Toast.makeText(getApplicationContext(), "Выберите игру", Toast.LENGTH_SHORT).show();
        }else {
            gameCursor.moveToPosition(positionList);
            Intent intent = new Intent(this, PuzzleActivity.class);
            //Передаём полный путь картинки в PuzzleActivity
            //Ключ меняем в зависимости от пути картинки
            if (gameCursor.getString(3).contains("content") || gameCursor.getString(3).contains("storage")) {
                intent.putExtra("mCurrentPhotoPath", gameCursor.getString(3));
            } else {
                String[] word = gameCursor.getString(3).split("/");
                intent.putExtra("assetName", word[3]);
            }
            //Передаём остальные значения для последующей обработки
            intent.putExtra("form", gameCursor.getString(6));
            intent.putExtra("colPiec", gameCursor.getInt(5));
            intent.putExtra("type", selectRadioType);
            intent.putExtra("mode", selectRadioMode);
            gameCursor.close();
            startActivity(intent);
        }
    }
}