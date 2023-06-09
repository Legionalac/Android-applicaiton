package nikola.pavicevic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ListDbHelper extends SQLiteOpenHelper {
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
    public ListDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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

    public boolean insert(String Name, String Username, boolean shared){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int share;
        if(shared)
            share=1;
        else
            share=0;
        values.put(COLUMN_NAME,Name);
        values.put(COLUMN_USERNAME,Username);
        values.put(COLUMN_SHARED,share);
        try {
            db.insertOrThrow(TABLE_NAME_LISTS,null,values);
            close();
            return true;
        }
        catch (SQLiteConstraintException e){
            Toast toast = new Toast(mContext);
            toast.setText("Name already exists");
            toast.show();
            close();
            return false;
        }
    }
    public void removeList(String name){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME_LISTS,COLUMN_NAME + "=?",new String[]{name});
        close();
    }
    public List[] readLists(String username , boolean shared){
        SQLiteDatabase db = getReadableDatabase();
        String share;
        Cursor cursor;
        if(shared)
            cursor = db.query(TABLE_NAME_LISTS,new String[]{"DISTINCT " + COLUMN_NAME,COLUMN_SHARED},COLUMN_USERNAME + "=? OR " + COLUMN_SHARED +"=1",new String[]{username},null,null,null);
        else
            cursor = db.query(TABLE_NAME_LISTS,new String[]{"DISTINCT " + COLUMN_NAME,COLUMN_SHARED},COLUMN_USERNAME + "=?",new String[]{username},null,null,null);


        List[] lists = new List[cursor.getCount()];
        int i=0;
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }
        close();
        return lists;
    }
    public List[] readSharedLists(){
        SQLiteDatabase db = getReadableDatabase();
        String share;
        Cursor cursor;

        cursor = db.query(TABLE_NAME_LISTS,null,COLUMN_SHARED +"=1",null,null,null,null);

        List[] lists = new List[cursor.getCount()];
        int i=0;
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }
        close();
        return lists;
    }
    public void insertLists(List[] lists){
        for(List i : lists){
            insert(i.getName(),i.getUsername(),i.isShared());
        }
    }
    public void deleteSharedLists(){
        List[] lists = readSharedLists();
        for(List i : lists){
            removeList(i.getName());
        }
    }
    public List[] readListsFromUser(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME_LISTS,new String[]{"DISTINCT " + COLUMN_NAME,COLUMN_SHARED},COLUMN_USERNAME + "=?",new String[]{"admin"},null,null,null);
        List[] lists = new List[cursor.getCount()];
        int i=0;
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }
        close();
        return lists;
    }
    public List createList(Cursor cursor){
        String Name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        int shared = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SHARED));
        if(shared == 1)
            return new List(Name,true);
        else
            return new List(Name,false);

    }

}
