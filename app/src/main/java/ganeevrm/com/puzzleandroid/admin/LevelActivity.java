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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;

public class LevelActivity extends AppCompatActivity {
    /**Helper*/
    private DatabaseHelper databaseHelper;
    /**БД*/
    private SQLiteDatabase db;
    /**Курсор*/
    private Cursor levelCursor;
    private SimpleCursorAdapter scAdapter;

    /**Список уровней*/
    private ListView levelList;
    /**Текст с номером уровня*/
    private TextView tvlevel;
    /**Текст с количеством уровней*/
    private TextView header;

    /**Вид элемента*/
    private String selectRadio = "Треугольник";
    /**Количество элементов пазла*/
    private int selectRadioPiec = 16;

    /**Выбранный уровень*/
    private long selectLevel;
    /**Индикатор выбора одного уровня*/
    private boolean click = true;
    /**Позиция в списке*/
    private int positionList = -1;
    /**Id уровня*/
    private long idLevel = 0;

    /**Константа для контекстного меню ID=1*/
    private static final int CM_DELETE_ID = 1;
    /**Константа для контекстного меню ID=2*/
    private static final int CM_EDIT_ID = 2;
    /**Минимальная сложность*/
    private final int minHard = 1;
    /**Максимальная сложность*/
    private final int maxHard = 10;
    /**Шаг сложности*/
    private final int stepHard = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        levelList = findViewById(R.id.levelList);
        tvlevel = findViewById(R.id.tvlevel);
        header = findViewById(R.id.header);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из бд в виде курсора
        levelCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_LEVEL, null);

        //Присваиваем значение selectLevel
        Bundle arguments = getIntent().getExtras();
        selectLevel = (long) arguments.get("selectlevel");

        //Создаём Listener
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                levelCursor.moveToPosition(position);
                //При клике на элемент списка проверяем булеву переменную, чтобы
                //был выбран только один уровень. Далее сохраняем позицию и id уровня,
                //изменяем значение булевой переменной
                if(click){
                    positionList = position;
                    idLevel =  id;
                    selectLevel = levelCursor.getInt(1);
                    click = false;
                    tvlevel.setText("Выбран уровень: " + selectLevel);
                } else {
                    positionList = -1;
                    idLevel = 0;
                    selectLevel = 0;
                    click = true;
                    tvlevel.setText("Уровень не выбран");
                }
                scAdapter.notifyDataSetChanged();
            }
        };
        //Устанаваливаем Listener у levelList
        levelList.setOnItemClickListener(itemListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataBase();

        //Формируем столбцы сопоставления
        String[] from = new String[] { DatabaseHelper.COLUMN_LEVEL, DatabaseHelper.COLUMN_COL_PIECES, DatabaseHelper.COLUMN_FORM };
        int[] to = new int[] { R.id.level, R.id.col_pieces, R.id.form };
        //Создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_level_item, levelCursor, from, to, 0){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                //Для начального отображения используем переменную click = true, чтобы
                //все элементы списка не были окрашены
                if(click){
                    view.setBackgroundColor(Color.TRANSPARENT);
                    if(selectLevel!= 0 && selectLevel == levelCursor.getInt(1)){
                        click = false;
                        view.setBackgroundColor(Color.LTGRAY);
                    }
                } else {
                    //Если click = false, то окрасим только выбранный элемент списка
                    if(position == positionList){
                        view.setBackgroundColor(Color.LTGRAY);
                    }
                }
                return view;
            }
        };
        //Устанаваливаем адаптер у levelList
        levelList.setAdapter(scAdapter);
        //Назначаем контекстное меню для листа
        registerForContextMenu(levelList);
        if(selectLevel == 0){
            tvlevel.setText("Уровень не выбран");
        }else{
            tvlevel.setText("Выбран уровень: " + selectLevel);
        }
        header.setText("Найдено элементов: " + levelCursor.getCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!levelCursor.isClosed()) levelCursor.close();
        if (db.isOpen()) {
            db.close();
            databaseHelper.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!levelCursor.isClosed()) levelCursor.close();
        if (db.isOpen()) {
            db.close();
            databaseHelper.close();
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

        if (levelCursor.isClosed()){
            levelCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_LEVEL, null);
        }
    }

    /**
     * Создание контекстного меню для списка
     * @param menu ContextMenu
     * @param v View
     * @param menuInfo ContextMenu.ContextMenuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
    }

    /**
     * Обработка нажатия пункта контекстного меню
     * @param item MenuItem
     * @return boolean
     */
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            //Получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            //Извлекаем id записи и удаляем соответствующую запись в БД
            try {
                db.delete(DatabaseHelper.TABLE_LEVEL, "_id = ?", new String[]{String.valueOf(acmi.id)});
                Toast.makeText(getApplicationContext(), "Уровень удалён", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Сначала удалите игру", Toast.LENGTH_SHORT).show();
            }
            levelCursor = databaseHelper.getNewCursor(db, DatabaseHelper.TABLE_LEVEL);
            header.setText("Найдено элементов: " + levelCursor.getCount());
            scAdapter.changeCursor(levelCursor);
            return true;
        }
        if (item.getItemId() == CM_EDIT_ID) {
            //Получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            onEditContext(acmi);
            levelCursor = databaseHelper.getNewCursor(db, DatabaseHelper.TABLE_LEVEL);
            scAdapter.changeCursor(levelCursor);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Нажатие пункта "Редактировать запись" в контекстном меню
     * @param acmi AdapterView.AdapterContextMenuInfo
     */
    public void onEditContext(final AdapterView.AdapterContextMenuInfo acmi){
        dialogWindow(acmi.id, acmi.position, true);
    }

    /**
     * Нажатие кнопки "Отмена"
     * @param view - View
     */
    public void onBack(View view){
        finish();
    }

    /**
     * Нажатие кнопки "Добавить"
     * @param view - View
     */
    public void onAddContext(View view){
        dialogWindow(0,0, false);
    }

    /**
     * Нажатие кнопки "Ок"
     * @param view - View
     */
    public void onOk(View view) {
        //Создание объекта Intent для запуска SecondActivity
        Intent answerIntent = new Intent();
        //Передача объекта с ключом "level" и значением selectLevel
        answerIntent.putExtra("selectlevel",selectLevel);
        answerIntent.putExtra("idLevel",idLevel);
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void dialogWindow(final long acmiId, final int acmiPosition, boolean onEdit){
        //Получаем вид с файла context_level.xml, который применим для диалогового окна
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_level, null);

        //Создаем AlertDialog.Builder
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Назначаем AlertDialog.Builder вид из context_level.xml
        mDialogBuilder.setView(promptsView);

        //Если было включено редактирование, то устанавливаем все значения для RadioButton
        if(onEdit){
            //Настраиваем отображение RadioButton
            final RadioButton rbRec = promptsView.findViewById(R.id.radioTriangle);
            final RadioButton rbSq = promptsView.findViewById(R.id.radioSquare);
            final RadioButton rbFi = promptsView.findViewById(R.id.radioFigure);
            final RadioButton rb16 = promptsView.findViewById(R.id.radio16);
            final RadioButton rb36 = promptsView.findViewById(R.id.radio36);
            final RadioButton rb64 = promptsView.findViewById(R.id.radio64);

            levelCursor.moveToPosition(acmiPosition);
            switch (levelCursor.getString(3)) {
                case "Треугольник":
                    rbRec.setChecked(true);
                    onRadioButtonClicked(rbRec);
                    break;
                case "Квадрат":
                    rbSq.setChecked(true);
                    onRadioButtonClicked(rbSq);
                    break;
                case "Фигура":
                    rbFi.setChecked(true);
                    onRadioButtonClicked(rbFi);
                    break;
            }

            switch (levelCursor.getInt(2)) {
                case 16:
                    rb16.setChecked(true);
                    onRadioButtonClicked(rb16);
                    break;
                case 36:
                    rb36.setChecked(true);
                    onRadioButtonClicked(rb36);
                    break;
                case 64:
                    rb64.setChecked(true);
                    onRadioButtonClicked(rb64);
                    break;
            }
        }

        //Настройка отображения SeekBar
        final SeekBar seekBarHard = promptsView.findViewById(R.id.seekBarHard);
        seekBarHard.setProgress(0);
        final TextView tvHardSB = promptsView.findViewById(R.id.seekBarHardProg);
        seekBarHard.setMax( (maxHard - minHard) / stepHard );

        //Если было включено редактирование, то устанавливаем значение для SeekBar
        if(onEdit){
            tvHardSB.setText("" + levelCursor.getInt(1));
            seekBarHard.setProgress(levelCursor.getInt(1) - 1);
        }

        //Подключаем слушателя на SeekBar
        seekBarHard.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                double value = minHard + (progress * stepHard);
                tvHardSB.setText("" + (int)value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Если было включено редактирование, то выводим соответствующий диалог
        if(onEdit){
            //Настраиваем сообщение в диалоговом окне
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseHelper.COLUMN_LEVEL, Integer.parseInt(tvHardSB.getText().toString()));
                                    cv.put(DatabaseHelper.COLUMN_COL_PIECES, selectRadioPiec);
                                    cv.put(DatabaseHelper.COLUMN_FORM, selectRadio);
                                    try {
                                        db.update(DatabaseHelper.TABLE_LEVEL, cv, DatabaseHelper.COLUMN_ID_L + "=" + acmiId, null);
                                        Toast.makeText(getApplicationContext(), "Уровень отредактирован", Toast.LENGTH_SHORT).show();
                                    }catch (Exception ex){
                                        Toast.makeText(getApplicationContext(), "Данная сложность уже присутствует", Toast.LENGTH_SHORT).show();
                                    }
                                    recreate();
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
        } else {
            //Настраиваем сообщение в диалоговом окне
            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseHelper.COLUMN_LEVEL, Integer.parseInt(tvHardSB.getText().toString()));
                                    cv.put(DatabaseHelper.COLUMN_COL_PIECES, selectRadioPiec);
                                    cv.put(DatabaseHelper.COLUMN_FORM, selectRadio);
                                    if(db.insert(DatabaseHelper.TABLE_LEVEL, null, cv)!=-1){
                                        Toast.makeText(getApplicationContext(), "Уровень добавлен", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Данная сложность уже присутствует", Toast.LENGTH_SHORT).show();
                                    }
                                    levelCursor = databaseHelper.getNewCursor(db, DatabaseHelper.TABLE_LEVEL);
                                    scAdapter.changeCursor(levelCursor);
                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
        }

        //Создаем AlertDialog
        AlertDialog alertDialog = mDialogBuilder.create();
        
        //И отображаем его
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
        //переменной selectRadio или selectRadioPiec
        switch(view.getId()) {
            case R.id.radioTriangle:
                if (checked){
                    selectRadio = "Треугольник";
                }
                break;
            case R.id.radioSquare:
                if (checked){
                    selectRadio = "Квадрат";
                }
                break;
            case R.id.radioFigure:
                if (checked){
                    selectRadio = "Фигура";
                }
                break;
            case R.id.radio16:
                if (checked){
                    selectRadioPiec = 16;
                }
                break;
            case R.id.radio36:
                if (checked){
                    selectRadioPiec = 36;
                }
                break;
            case R.id.radio64:
                if (checked){
                    selectRadioPiec = 64;
                }
                break;
        }
    }

}