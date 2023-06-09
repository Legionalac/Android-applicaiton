package nikola.pavicevic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LoginDbHelper extends SQLiteOpenHelper
{
    private Context mContext;
    private final String TABLE_NAME_USERS = "USERS";
    public final String COLUMN_USERNAME="USERNAME";
    public final String COLUMN_EMAIL="EMAIL";
    public final String COLUMN_PASSWORD="PASSWORD";
    public final String TABLE_NAME_LISTS="LISTS";
    public final String COLUMN_NAME="NAME";
    public final String COLUMN_SHARED="SHARED";
    public final String TABLE_NAME_ITEMS="ITEMS";
    public final String COLUMN_LIST_NAME="LIST_NAME";
    public final String COLUMN_ID = "ID";

    public LoginDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_USERS +"(" +
                COLUMN_USERNAME + " TEXT ," +
                COLUMN_EMAIL + " TEXT ," +
                COLUMN_PASSWORD + " TEXT ," +
                "PRIMARY KEY (" + COLUMN_USERNAME + ")" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_LISTS +"(" +
                COLUMN_NAME + " TEXT ," +
                COLUMN_USERNAME + " TEXT ," +
                COLUMN_SHARED + " INTEGER ," +
                "PRIMARY KEY (" + COLUMN_NAME + ")" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_ITEMS +"(" +
                COLUMN_NAME + " TEXT ," +
                COLUMN_LIST_NAME + " TEXT ," +
                COLUMN_SHARED + " INTEGER ," +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean insert(String username , String email , String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASSWORD, password);
            db.insertOrThrow(TABLE_NAME_USERS,null,values);
            close();
            return true;
        }
        catch (SQLiteConstraintException e){
            Toast toast = new Toast(mContext);
            toast.setText("Username or Password already exists");
            toast.show();
            close();
            return false;
        }
    }
    public boolean insertAllUsers(User[] users){
        for(User i : users) {
            insert(i.getUsername(),i.getEmail(),i.getPassword());
        }
        return true;
    }

    public boolean Authorize(String username , String password){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USERS,null,COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",new String[]{username,password},null,null,null );
        if(cursor.getCount() == 1) {
            close();
            return true;
        }
        else {
            close();
            return false;
        }
    }

}
