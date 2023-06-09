package nikola.pavicevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        Bundle bundle = getIntent().getExtras();
        TextView name = findViewById(R.id.showListName);
        name.setText(bundle.getString("name"));
        Button add = findViewById(R.id.showListAddButton);
        ListView list = findViewById(R.id.showListtasks);
        Button refresh = findViewById(R.id.refreshShowList);
        refresh.setVisibility(View.VISIBLE);
        Button home = findViewById(R.id.showListHome);
        home.setOnClickListener(this);

        TaskAdapter adapter = new TaskAdapter(this);
        list.setAdapter(adapter);
        TaskDbHelper dbHelper = new TaskDbHelper(this,getString(R.string.DB_NAME),null,1);
        boolean isListShared = bundle.getBoolean("shared");
        if(!isListShared) {
            adapter.addTasks(dbHelper.readTasks(name.getText().toString()));
            refresh.setVisibility(View.INVISIBLE);
        }
        else
            list.setEmptyView(refresh);
        adapter.setListShared(isListShared);
        adapter.setListName(name.getText().toString());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView taskName = findViewById(R.id.showListAddText);
                if(taskName.getText().length()>0){
                    dbHelper.insert(taskName.getText().toString(),name.getText().toString(),0);
                    Task task = dbHelper.getLastTask(name.getText().toString());
                    if(isListShared) {
                        String listName = name.getText().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String url = getString(R.string.ipAdress) + "tasks";
                                HttpHelper httpHelper = new HttpHelper();
                                httpHelper.addTask(url, task.getName(), listName, task.isCheck(), String.valueOf(task.getID()));
                            }
                        }).start();
                    }
                    adapter.addTask(task);

                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task task = (Task) adapter.getItem(i);
                String ID = String.valueOf(task.getID());
                dbHelper.removeTask(ID);
                if(isListShared) {
                    String address = getString(R.string.ipAdress);
                    String listName = name.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpHelper httpHelper = new HttpHelper();
                            try {
                                String httpId = httpHelper.findId(address + "tasks/",listName,task.getID());
                                httpHelper.deleteTask(address,httpId);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }
                return adapter.removeTask(i);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = getString(R.string.ipAdress) + "tasks/";
                String httpListName = name.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpHelper httpHelper = new HttpHelper();
                        try {
                            Task[] tasks = httpHelper.getTasks(address,httpListName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addTasks(tasks);
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }).start();

            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(ShowListActivity.this,MainActivity.class);
        startActivity(intent);
    }
}