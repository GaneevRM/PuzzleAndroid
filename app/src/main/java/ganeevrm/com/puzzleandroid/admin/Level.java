package ganeevrm.com.puzzleandroid.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ganeevrm.com.puzzleandroid.R;

public class Level {
    private int level; // сложность
    private int width;  // ширина
    private int height; // высота
    private String form; // форма

    public Level(int level, int width, int height, String form){

        this.level=level;
        this.width=width;
        this.height=height;
        this.form=form;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }
}
