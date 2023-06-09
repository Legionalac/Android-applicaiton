package nikola.pavicevic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TaskDbHelper extends SQLiteOpenHelper {
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

    public TaskDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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
    public Task createTask(Cursor cursor){
        String Name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        int shared = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SHARED));
        int ID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        if(shared == 1)
            return new Task(Name,true,ID);
        else
            return new Task(Name,false,ID);
    }
    public Task getLastTask(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ITEMS,null,COLUMN_LIST_NAME + "=?" ,new String[]{username},null,null,"ID DESC","1");
        cursor.moveToFirst();
        Task task = createTask(cursor);
        return task;
    }
    public void removeTask(String ID){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_ITEMS,COLUMN_ID + "=?",new String[]{ID});
        close();
    }

    public void updateTask(int ID,boolean shared){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (shared) {
            values.put(COLUMN_SHARED,1);
        }
        else
            values.put(COLUMN_SHARED,0);

        db.update(TABLE_NAME_ITEMS,values,COLUMN_ID + "=?",new String[]{String.valueOf(ID)});
        close();
    }
    public Task[] readTasks(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_ITEMS,null,COLUMN_LIST_NAME + "=?" ,new String[]{username},null,null,null);
        Task[] tasks = new Task[cursor.getCount()];
        int i=0;
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            tasks[i++] = createTask(cursor);
        }
        close();
        return tasks;
    }
    public void insert(String Name, String ListName , int Shared){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,Name);
        values.put(COLUMN_LIST_NAME,ListName);
        values.put(COLUMN_SHARED,Shared);

        db.insert(TABLE_NAME_ITEMS,null,values);
        close();
    }
    public void insertWithID(String Name, String ListName , int Shared , int id ){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,Name);
        values.put(COLUMN_LIST_NAME,ListName);
        values.put(COLUMN_SHARED,Shared);
        values.put(COLUMN_ID, id);
        db.insert(TABLE_NAME_ITEMS,null,values);
        close();
    }
}
