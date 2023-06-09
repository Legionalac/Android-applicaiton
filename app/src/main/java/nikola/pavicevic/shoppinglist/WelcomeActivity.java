package nikola.pavicevic.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
    private String username = "";
    private boolean shareToggle = false;
    private ListDbHelper dbHelper;
    ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF",Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","username");

        TextView usernameView = findViewById(R.id.welcomeUsername);
        usernameView.setText(username);
        //Buttons
        Button newList = findViewById(R.id.buttonNewList);
        Button seeList = findViewById(R.id.buttonSeeList);
        Button home = findViewById(R.id.welcomeHome);
        home.setOnClickListener(this);
        seeList.setOnClickListener(this);
        newList.setOnClickListener(this);



        //ListView
        ListView list = findViewById(R.id.welcomeList);
        adapter = new ListAdapter(this);
        list.setAdapter(adapter);
        //adapter.addTestElements();

        dbHelper = new ListDbHelper(this,getString(R.string.DB_NAME),null,1);

        List[] lists = dbHelper.readLists(usernameView.getText().toString(),false);
        adapter.addLists(lists);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ipAdress = getString(R.string.ipAdress);
                List temp = (List) adapter.getItem(i);
                dbHelper.removeList(temp.getName());
                if(temp.isShared()){

                    new Thread(new Runnable() {
                        String httpUsername;
                        @Override
                        public void run() {
                            if(shareToggle)
                                httpUsername = temp.getUsername();
                            else
                                httpUsername = username;
                            HttpHelper httpHelper = new HttpHelper();
                            try {
                                httpHelper.deleteList(ipAdress,temp.getName(),httpUsername);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }).start();
                }

                return adapter.removeList(i);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WelcomeActivity.this,ShowListActivity.class);
                Bundle bundle = new Bundle();
                List temp = (List) adapter.getItem(i);
                bundle.putString("name",temp.getName());
                bundle.putBoolean("shared",temp.isShared());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonNewList){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to create new list?")
                    .setTitle("New List Dialog")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(WelcomeActivity.this,NewListActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if(view.getId() == R.id.buttonSeeList){
            if(shareToggle){
                shareToggle = false;
                Button seeList = (Button) view;
                seeList.setText("See all lists");
                List[] lists = dbHelper.readLists(username,shareToggle);
                adapter.addLists(lists);
            }
            else
            {

                HttpHelper httpHelper = new HttpHelper();
                String url = getString(R.string.ipAdress) + "lists";
                shareToggle=true;
                Button seeList = (Button) view;
                seeList.setText("See my lists");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List[] lists = httpHelper.getSharedLists(url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.addLists(lists);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }
        }
        if(view.getId() == R.id.welcomeHome){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

    }

}