package ganeevrm.com.puzzleandroid.admin;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.gallery.GlideGalleryActivity;
import ganeevrm.com.puzzleandroid.game.PuzzleActivity;

public class GameMenuActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private Cursor gameCursor;
    private Cursor picCursor;
    private Cursor levelCursor;
    private int selectRadioType = 0;
    private int selectRadioMode = 0;
    private SimpleCursorAdapter scAdapter;

    static final private int CHOOSE_IDS = 0;
    private long idLevel = -1;
    private long idPic = 0;
    private boolean click = false;
    private int positionList = -1;
    private long gameid = 0;

    ListView gameList;
    ImageView imageViewGame;
    TextView tvheader;

    /**Для получения значения из Bundle*/
    private String who;
    /**Интерфейс для админа*/
    private LinearLayout adminLayout;
    /**Интерфейс для игрока*/
    private LinearLayout userLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        adminLayout = findViewById(R.id.adminLayout);
        userLayout = findViewById(R.id.userLayout);
        gameList = findViewById(R.id.gameList);
        imageViewGame = findViewById(R.id.imageViewGame);
        tvheader = findViewById(R.id.header);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        
        db = databaseHelper.getReadableDatabase();
        gameCursor = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_GAME, null);

        Bundle arguments = getIntent().getExtras();
        who = (String) arguments.get("Who");

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                gameCursor.moveToPosition(position);
                if(!click){
                    v.setBackgroundColor(Color.LTGRAY);
                    positionList = position;
                    gameid =  id;
                    click = true;
                    tvheader.setText("Выбрана игра: " + (positionList+1));
                }else {
                    if (position == positionList) {
                        v.setBackgroundColor(Color.TRANSPARENT);
                        positionList = -1;
                        click = false;
                        tvheader.setText("Найдено элементов: " + gameCursor.getCount());
                    }
                }
            }
        };
        gameList.setOnItemClickListener(itemListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        positionList=-1;
        click=false;
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
        gameList.setAdapter(scAdapter);
        tvheader.setText("Найдено элементов: " + gameCursor.getCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHOOSE_IDS && resultCode == RESULT_OK){
            idLevel = data.getLongExtra("idlevel",0);
            idPic = data.getLongExtra("idpic",0);
            if(!(idLevel==-1||idPic==0)){
                picCursor = db.query("picture", new String[] {"_id","link"}, "_id=?", new String[]{String.valueOf(idPic)},null,null,null);
                picCursor.moveToFirst();
                levelCursor = db.query("level", new String[] {"_id","hard","col_pieces","form"}, "_id=?", new String[]{String.valueOf(idLevel)},null,null,null);
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
                gameCursor.requery();
                scAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onAdd(View view) {

        Intent galleryIntent = new Intent(this, GlideGalleryActivity.class);
       // galleryIntent.putExtra("level", selectLevel );
        startActivityForResult(galleryIntent, CHOOSE_IDS);
    }

    public void onRadioButtonClicked(View view) {
        // если переключатель отмечен
        boolean checked = ((RadioButton) view).isChecked();
        // Получаем нажатый переключатель
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

    public void onType(View view) {
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_type, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
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

    public void startGame(View view) {
        if(tvheader.getText().toString().contains("Найдено")){
            Toast.makeText(getApplicationContext(), "Выберите игру", Toast.LENGTH_SHORT).show();
        }else {
            gameCursor.moveToPosition(positionList);
            Intent intent = new Intent(this, PuzzleActivity.class);
            if (gameCursor.getString(3).contains("content") || gameCursor.getString(3).contains("storage")) {
                intent.putExtra("mCurrentPhotoPath", gameCursor.getString(3));
            } else {
                String[] word = gameCursor.getString(3).split("/");
                intent.putExtra("assetName", word[3]);
            }
            intent.putExtra("form", gameCursor.getString(6));
            intent.putExtra("colPiec", gameCursor.getInt(5));
            intent.putExtra("type", selectRadioType);
            intent.putExtra("mode", selectRadioMode);
            startActivity(intent);
        }
    }

    public void onDel(View view) {
        if(tvheader.getText().toString().contains("Найдено")){
            Toast.makeText(getApplicationContext(), "Выберите игру", Toast.LENGTH_SHORT).show();
        }else {
            //Извлекаем id записи и удаляем соответствующую запись в БД
            db.delete(DatabaseHelper.TABLE_GAME, "_id = ?", new String[]{String.valueOf(gameid)});
            Toast.makeText(getApplicationContext(), "Игра удалена", Toast.LENGTH_SHORT).show();
            //Обновляем курсор и адаптер (userCursor.requery устарел, но на данный момент асинхронность не нужна, поэтому использую его)
            gameCursor.requery();
            tvheader.setText("Найдено элементов: " + gameCursor.getCount());
            scAdapter.notifyDataSetChanged();
            positionList=-1;
            click=false;
        }
    }

    public void onBuild(View view) {
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_build, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
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
}