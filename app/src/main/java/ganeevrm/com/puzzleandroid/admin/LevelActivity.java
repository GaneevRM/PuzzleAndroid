package ganeevrm.com.puzzleandroid.admin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.gallery.GlideGalleryActivity;

public class LevelActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;
    private long selectLevel;
    private TextView header;
    private String selectRadio;
    private SimpleCursorAdapter scAdapter;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private int min = 4, max = 8, step = 2, minHard = 1, maxHard = 10, stepHard =1;


    ListView levelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        levelList = findViewById(R.id.levelList);
        header = findViewById(R.id.header);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectLevel = id;
            }
        };
        levelList.setOnItemClickListener(itemListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_LEVEL, null);
        // формируем столбцы сопоставления
        String[] from = new String[] { DatabaseHelper.COLUMN_LEVEL, DatabaseHelper.COLUMN_WIDTH, DatabaseHelper.COLUMN_HEIGHT, DatabaseHelper.COLUMN_FORM };
        int[] to = new int[] { R.id.level, R.id.width, R.id.height, R.id.form };
        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_level_item, userCursor, from, to, 0);
        levelList.setAdapter(scAdapter);
        //Назначаем контекстное меню для листа
        registerForContextMenu(levelList);
        header.setText("Найдено элементов: " + userCursor.getCount());
        userCursor.requery();
    }


    //Создание контекстного меню для списка
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
    }

    //Обработка нажатия пункта контекстного меню
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            //Получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            //Извлекаем id записи и удаляем соответствующую запись в БД
            db.delete(DatabaseHelper.TABLE_LEVEL, "_id = ?", new String[]{String.valueOf(acmi.id)});
            //Обновляем курсор и адаптер (userCursor.requery устарел, но на данный момент асинхронность не нужна, поэтому использую его)
            userCursor.requery();
            scAdapter.notifyDataSetChanged();
            return true;
        }
        if (item.getItemId() == CM_EDIT_ID) {
            //Получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            onEditContext(acmi);
            //Обновляем курсор и адаптер (userCursor.requery устарел, но на данный момент асинхронность не нужна, поэтому использую его)
            userCursor.requery();
            scAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onBack(View view){
        finish();
    }

    public void onOk(View view) {
        //Создание объекта Intent для запуска SecondActivity
        Intent answerIntent = new Intent();
        //Передача объекта с ключом "level" и значением selectLevel
        answerIntent.putExtra("level",selectLevel);
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    public void onRadioButtonClicked(View view) {
        // если переключатель отмечен
        boolean checked = ((RadioButton) view).isChecked();
        // Получаем нажатый переключатель
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
        }
    }

    public void onAddContext(View view){
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_level, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final SeekBar seekBarHard = promptsView.findViewById(R.id.seekBarHard);
        final TextView tvHardSB = promptsView.findViewById(R.id.seekBarHardProg);

        final SeekBar seekBarWidth = promptsView.findViewById(R.id.seekBarWidth);
        final TextView tvWidthSB = promptsView.findViewById(R.id.seekBarWidthProg);

        final SeekBar seekBarHeight = promptsView.findViewById(R.id.seekBarHeight);
        final TextView tvHeightSB = promptsView.findViewById(R.id.seekBarHeightProg);
        //Подключаем слушателя на SeekBar
        seekBarHard.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                tvHardSB.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Подключаем слушателя на SeekBar
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                tvWidthSB.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Подключаем слушателя на SeekBar
        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                tvHeightSB.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DatabaseHelper.COLUMN_LEVEL, Integer.parseInt(String.valueOf(seekBarHard.getProgress())));
                                    cv.put(DatabaseHelper.COLUMN_WIDTH, Integer.parseInt(String.valueOf(seekBarWidth.getProgress())));
                                    cv.put(DatabaseHelper.COLUMN_HEIGHT, Integer.parseInt(String.valueOf(seekBarHeight.getProgress())));
                                    cv.put(DatabaseHelper.COLUMN_FORM, selectRadio);
                                    db.insert(DatabaseHelper.TABLE_LEVEL, null, cv);
                                    Toast.makeText(getApplicationContext(), "Уровень добавлен", Toast.LENGTH_SHORT).show();
                                    recreate();
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

    public void onEditContext(final AdapterView.AdapterContextMenuInfo acmi){
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_level, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        userCursor.moveToPosition(acmi.position);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final RadioButton rbRec = promptsView.findViewById(R.id.radioTriangle);
        final RadioButton rbSq = promptsView.findViewById(R.id.radioSquare);
        final RadioButton rbFi = promptsView.findViewById(R.id.radioFigure);
        if(userCursor.getString(4).equals("Треугольник")){
            rbRec.setChecked(true);
            onRadioButtonClicked(rbRec);
        }else if(userCursor.getString(4).equals("Квадрат")){
            rbSq.setChecked(true);
            onRadioButtonClicked(rbSq);
        }else {
            rbFi.setChecked(true);
            onRadioButtonClicked(rbFi);
        }

        final SeekBar seekBarHard = promptsView.findViewById(R.id.seekBarHard);
        seekBarHard.setProgress((userCursor.getInt(1)-1));
        final TextView tvHardSB = promptsView.findViewById(R.id.seekBarHardProg);
        seekBarHard.setMax( (maxHard - minHard) / stepHard );
        tvHardSB.setText("" + userCursor.getInt(1));

        final SeekBar seekBarWidth = promptsView.findViewById(R.id.seekBarWidth);
        seekBarWidth.setProgress((userCursor.getInt(2)-4)/2);
        final TextView tvWidthSB = promptsView.findViewById(R.id.seekBarWidthProg);
        seekBarWidth.setMax( (max - min) / step );
        tvWidthSB.setText("" + userCursor.getInt(2));

        final SeekBar seekBarHeight = promptsView.findViewById(R.id.seekBarHeight);
        seekBarHeight.setProgress((userCursor.getInt(3)-4)/2);
        final TextView tvHeightSB = promptsView.findViewById(R.id.seekBarHeightProg);
        seekBarHeight.setMax( (max - min) / step );
        tvHeightSB.setText("" + userCursor.getInt(3));

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

        //Подключаем слушателя на SeekBar
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                double value = min + (progress * step);
                tvWidthSB.setText("" + (int)value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Подключаем слушателя на SeekBar
        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double value = min + (progress * step);
                tvHeightSB.setText("" + (int)value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                ContentValues cv = new ContentValues();
                                cv.put(DatabaseHelper.COLUMN_LEVEL, Integer.parseInt(tvHardSB.getText().toString()));
                                cv.put(DatabaseHelper.COLUMN_WIDTH, Integer.parseInt(tvWidthSB.getText().toString()));
                                cv.put(DatabaseHelper.COLUMN_HEIGHT, Integer.parseInt(tvHeightSB.getText().toString()));
                                cv.put(DatabaseHelper.COLUMN_FORM, selectRadio);
                                db.update(DatabaseHelper.TABLE_LEVEL, cv, DatabaseHelper.COLUMN_ID_L + "=" + String.valueOf(acmi.id), null);
                                Toast.makeText(getApplicationContext(), "Уровень добавлен", Toast.LENGTH_SHORT).show();
                                recreate();
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

}