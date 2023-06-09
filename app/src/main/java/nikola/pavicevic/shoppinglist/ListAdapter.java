package nikola.pavicevic.shoppinglist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ListAdapter extends BaseAdapter {
    ArrayList<List> mLists;
    Context mContext;

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
        mLists = new ArrayList<List>();
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int i) {
        Object output=null;
        try {
            output = mLists.get(i);
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
    public void addList(List list){
        mLists.add(list);
        notifyDataSetChanged();
    }
    public boolean removeList(int i){
        try {
            mLists.remove(i);
            notifyDataSetChanged();
            return true;
        }
        catch (Exception e){
            return false;
        }

    }
    public void addLists(List[] lists){
        mLists.clear();
        for (int i=0;i<lists.length;i++){
            mLists.add(lists[i]);

        }
        notifyDataSetChanged();
    }
    private class ViewHolder{
        TextView itemName;
        TextView itemShared;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listrow_element, null);
            viewHolder = new ViewHolder();
            viewHolder.itemName = view.findViewById(R.id.rowName);
            viewHolder.itemShared = view.findViewById(R.id.rowShared);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        List list = (List) getItem(i);
        viewHolder.itemName.setText(list.getName());
        viewHolder.itemShared.setText(String.valueOf(list.isShared()));

        return view;
    }
}
