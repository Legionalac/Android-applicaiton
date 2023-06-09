package nikola.pavicevic.shoppinglist;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class Task {
    private String mName;
    private boolean mCheck;
    int mID;


    public Task(String name, boolean check , int ID) {
        this.mName = name;
        this.mCheck = check;
        this.mID=ID;
    }
    public int getID() {
        return mID;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public boolean isCheck() {
        return mCheck;
    }

    public void setCheck(boolean check) {
        this.mCheck = check;
        Log.d("Task",String.valueOf(check));
    }

}
