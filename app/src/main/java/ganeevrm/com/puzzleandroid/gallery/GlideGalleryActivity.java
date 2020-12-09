package ganeevrm.com.puzzleandroid.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import ganeevrm.com.puzzleandroid.DatabaseHelper;
import ganeevrm.com.puzzleandroid.R;


public class GlideGalleryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor picCursor;
    private LinkedList<String> picMass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glid_gallery);

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Открываем подключение
        db = databaseHelper.getReadableDatabase();
        //Получаем данные из бд в виде курсора
        picCursor =  db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_PICTURE, null);
        picMass = new LinkedList<String>();
        if(picCursor.moveToFirst()){
            do{
                String link = picCursor.getString(1);
                picMass.add(link);
            }
            while (picCursor.moveToNext());
        }
        picCursor.close();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        GlideGalleryActivity.ImageGalleryAdapter adapter = new GlideGalleryActivity.ImageGalleryAdapter(this, picMass);
        recyclerView.setAdapter(adapter);
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

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
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
        }

        private LinkedList<String> mGlidePhotos;
        private Context mContext;

        public ImageGalleryAdapter(Context context, LinkedList<String> glidePhotos) {
            mContext = context;
            mGlidePhotos = glidePhotos;
        }
    }
}
