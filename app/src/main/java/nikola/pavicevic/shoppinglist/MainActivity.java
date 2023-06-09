package nikola.pavicevic.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.buttonLogin);
        Button register = findViewById(R.id.buttonRegister);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
       // this.deleteDatabase(getString(R.string.DB_NAME));
        //this.deleteDatabase("shared_list_app.db");

        Intent intent = new Intent(this,UpdateService.class);
        startService(intent);


    }

    @Override
    public void onClick(View view) {
        if(R.id.buttonLogin == view.getId()){
            view.setVisibility(View.INVISIBLE);
            Button register = findViewById(R.id.buttonRegister);
            register.setVisibility(View.INVISIBLE);
            LoginFragment fragment = LoginFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.loginFrame,fragment)
                    .addToBackStack(null)
                    .commit();
        }
        if(R.id.buttonRegister == view.getId()){
            view.setVisibility(View.INVISIBLE);
            Button login = findViewById(R.id.buttonLogin);
            login.setVisibility(View.INVISIBLE);
            RegisterFragment fragment = RegisterFragment.newInstance("","");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.loginFrame,fragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

}