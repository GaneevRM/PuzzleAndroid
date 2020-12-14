package ganeevrm.com.puzzleandroid.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;


public class GlideGalleryActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor picCursor;
    private LinkedList<String> picMass;
    private GlideGalleryActivity.ImageGalleryAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_gallery);

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из бд в виде курсора
        picCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PICTURE, null);
        picMass = new LinkedList<String>();
        if (picCursor.moveToFirst()) {
            do {
                String link = picCursor.getString(1);
                picMass.add(link);
            }
            while (picCursor.moveToNext());
        }
        // picCursor.close();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GlideGalleryActivity.ImageGalleryAdapter(this, picMass);
        recyclerView.setAdapter(adapter);
    }


    public void onSorting(View view) {
        //Получаем вид с файла context_sort.xml, который применим для диалогового окна
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.context_sort, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем context_sort.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение SeekBar и TextView в открытом диалоге:
        final SeekBar seekBar = promptsView.findViewById(R.id.seekBar);
        final TextView tvSeekBar = promptsView.findViewById(R.id.seekBarProgress);


        //Подключаем слушателя на SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //При изменении ползнука, будет меняться текст
                tvSeekBar.setText(String.valueOf(progress));
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
                                picMass.clear();
                                if(picCursor.moveToFirst()){
                                    do{
                                        //Получаем ссылку картинки
                                        String link = picCursor.getString(1);
                                        //Получаем уровень картинки
                                        int level = picCursor.getInt(2);
                                        //Если уровень картинки совпадает с выбранным уровненем, то записываем в массив ссылку
                                        if (level==seekBar.getProgress()){
                                            picMass.add(link);
                                        }
                                    }
                                    while (picCursor.moveToNext());
                                }
                                //Обновляем адаптер
                                adapter.notifyDataSetChanged();
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

    public void onBack(View view) {
        finish();
    }

    public void onOk(View view) {
        Toast.makeText(getApplicationContext(), "Игра добавлена", Toast.LENGTH_SHORT).show();
    }

    public void onSelectLevel(View view) {
        Toast.makeText(getApplicationContext(), "Уровень выбран", Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param requestCode - код вызванной функции
     * @param resultCode - код результата работы функции
     * @param data - выходные данные работы функции
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_LINK, mCurrentPhotoPath);
            cv.put(DatabaseHelper.COLUMN_COMPLEXITY, 5);
            db.insert(DatabaseHelper.TABLE_PICTURE, null, cv);
            Toast.makeText(getApplicationContext(), "Картинка добавлена", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();

        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_LINK, uri.toString());
            cv.put(DatabaseHelper.COLUMN_COMPLEXITY, 5);
            db.insert(DatabaseHelper.TABLE_PICTURE, null, cv);
            Toast.makeText(getApplicationContext(), "Картинка добавлена", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

    public void onGalleryClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            //Вызываем Activity с возвратом результата
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
    }

    public void onCameraClick(View view) {
        //Создание объекта Intent для вызова новой Activity (PuzzleActivity)
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Вызывается после использовании камеры
     * @return - Возвращает временный файл (фотку)
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        //Если нет разрешения на работу с памятью устройства, то сделать запрос
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            //После получения разрешения составляем название картинки
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            //Получаем директорию галереи смартфона
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            //Создаём временный файл имя файла + тип файла + дерикторя (откуда этот файл берём)
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            //Записываем путь к временному файлу, чтобы использовать в Intent (передать временный файл в Activity)
            mCurrentPhotoPath = image.getAbsolutePath();

            return image;
        }

        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraClick(new View(this));
                }
                return;
            }
        }
    }

    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the layout
            View photoView = inflater.inflate(R.layout.item_photo, parent, false);

            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {
            ImageView imageView = holder.mPhotoImageView;

            Glide.with(mContext)
                    .load(picMass.get(position))
                    .into(imageView);
        }

        @Override
        public int getItemCount() {
            return (mGlidePhotos.size());
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    String glidePhoto = picMass.get(position);
                    mPhotoImageView.setBackgroundResource(R.layout.border_element);
                    Intent intent = new Intent(mContext, GlidePhotoActivity.class);
                    intent.putExtra(GlidePhotoActivity.EXTRA_SPACE_PHOTO, glidePhoto);
                    startActivity(intent);
                }
            }

            @Override
            public boolean onLongClick(View view) {
              /*  int position = getAdapterPosition();
                db.delete(DatabaseHelper.TABLE_PICTURE, "_id = ?", new String[]{String.valueOf(position+1)});
                Toast.makeText(getApplicationContext(), "Картинка удалена", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();*/
                return false;
            }
        }


        private LinkedList<String> mGlidePhotos;
        private Context mContext;

        public ImageGalleryAdapter(Context context, LinkedList<String> glidePhotos) {
            mContext = context;
            mGlidePhotos = glidePhotos;
        }

    }
}
