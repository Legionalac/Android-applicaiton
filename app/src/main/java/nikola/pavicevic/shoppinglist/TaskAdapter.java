package nikola.pavicevic.shoppinglist;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {
    Context mContext;
    private ArrayList<Task> mTasks;
    private String listName;
    private boolean isListShared;
    public TaskAdapter(Context mContext) {
        this.mContext = mContext;
        mTasks = new ArrayList<Task>();
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Object getItem(int i) {
        Object output=null;
        try {
            output = mTasks.get(i);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setListShared(boolean listShared) {
        isListShared = listShared;
    }

    public void addTasks(Task[] tasks){
        mTasks.clear();
        for (int i=0;i<tasks.length;i++){
            mTasks.add(tasks[i]);
        }
        notifyDataSetChanged();
    }
    public void addTask(Task task){
        mTasks.add(task);
        notifyDataSetChanged();
    }
    public boolean removeTask(int i){
        try {
            mTasks.remove(i);
            notifyDataSetChanged();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    private class ViewHolder{
        TextView itemName;
        CheckBox itemCheck;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.taskrow_element, null);
            viewHolder = new ViewHolder();
            viewHolder.itemName = view.findViewById(R.id.taskName);
            viewHolder.itemCheck = view.findViewById(R.id.taskCheck);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Task task = (Task) getItem(i);
        viewHolder.itemName.setText(task.getName());
        viewHolder.itemCheck.setChecked(task.isCheck());
        if(task.isCheck()){
            viewHolder.itemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
            viewHolder.itemName.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

        viewHolder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    viewHolder.itemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                }
                else
                    viewHolder.itemName.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

            }
        });
        viewHolder.itemCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDbHelper dbHelper = new TaskDbHelper(mContext, mContext.getString(R.string.DB_NAME), null,1);
                boolean b = ((CheckBox) view).isChecked();
                task.setCheck(b);
                dbHelper.updateTask(task.getID(),b);
                if(isListShared) {
                    String address = mContext.getString(R.string.ipAdress);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpHelper httpHelper = new HttpHelper();
                            try {
                                String httpId = httpHelper.findId(address + "tasks/",listName,task.getID());
                                httpHelper.updateTask(address +"tasks/", httpId, b);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }).start();
                }
            }
        });
        return view;
    }
}
