package ganeevrm.com.puzzleandroid.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ganeevrm.com.puzzleandroid.R;
import ganeevrm.com.puzzleandroid.gallery.GlideGalleryActivity;

public class MainAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
    }

    /**
     * Нажатие кнопки "Учетные записи"
     * @param view - View
     */
    public void onAccounts(View view) {
        Intent intent = new Intent(this, AccountsActivity.class);
        startActivity(intent);
    }

    /**
     * Нажатие кнопки "Работа с играми"
     * @param view - View
     */
    public void onGame(View view) {
        Intent gameIntent = new Intent(this, GameMenuActivity.class);
        gameIntent.putExtra("Who","admin");
        startActivity(gameIntent);
    }

}