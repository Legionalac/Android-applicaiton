package nikola.pavicevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class NewListActivity extends AppCompatActivity implements View.OnClickListener {
    ListDbHelper dbHelper;
    String username="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        Button save = findViewById(R.id.buttonSaveList);
        Button ok = findViewById(R.id.buttonOkNewList);
        Button home = findViewById(R.id.newListHome);
        home.setOnClickListener(this);
        save.setOnClickListener(this);
        ok.setOnClickListener(this);
        dbHelper = new ListDbHelper(this,getString(R.string.DB_NAME),null,1);
        dbHelper.setContext(this);
        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF",Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","username");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSaveList){
            EditText Name = findViewById(R.id.newListName);
            RadioGroup shared = findViewById(R.id.newListRadio);
            RadioButton shareButton = findViewById(shared.getCheckedRadioButtonId());


            if(Name.getText().toString().length() > 0) {
                boolean share;
                if(shared.getCheckedRadioButtonId() == R.id.sharedListYes){
                    share = true;
                }
                else
                    share = false;
                Log.d("insert",String.valueOf(share));
                boolean retCode = dbHelper.insert(Name.getText().toString(),username,share);
                if(retCode) {
                    if(share) {
                        String httpName = Name.getText().toString();
                        String url = getString(R.string.ipAdress) + "lists";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpHelper httpHelper = new HttpHelper();
                                int httpRetCode = httpHelper.createList(url,httpName,username);
                            }
                        }).start();
                    }
                    Intent intent = new Intent(NewListActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }

            }
            else{
                Toast toast = new Toast(this);
                toast.setText("FILL ALL FIELDS");
                toast.show();
            }

        }
        if(view.getId() == R.id.buttonOkNewList){
            TextView naslov = findViewById(R.id.naslovNewList);
            EditText polje = findViewById(R.id.newListName);
            naslov.setText(polje.getText());
        }
        if(view.getId() == R.id.newListHome){
            Intent intent = new Intent(NewListActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}