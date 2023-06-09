package nikola.pavicevic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static int numberOfAttempts = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button login = v.findViewById(R.id.buttonLoginConfirmation);
        LoginDbHelper dbHelper = new LoginDbHelper(getActivity(),getString(R.string.DB_NAME),null,1);
        HttpHelper httpHelper = new HttpHelper();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JNI jni = new JNI();
                EditText username = v.findViewById(R.id.loginUsername);
                EditText password = v.findViewById(R.id.loginPassword);
                String url = getString(R.string.ipAdress) + "login";
                String usernameHttp = username.getText().toString();
                String passwordHttp = password.getText().toString();
//                if(dbHelper.Authorize(username.getText().toString(),password.getText().toString())){
//                    Intent intent = new Intent(getActivity(),WelcomeActivity.class);
//                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREF",Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("username",username.getText().toString());
//                    editor.apply();
//                    startActivity(intent);
//                }
//                else {
//                    Toast toast = new Toast(getActivity());
//                    toast.setText("WRONG USERNAME OR PASSWORD");
//                    toast.show();
//                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int httpRetCode = httpHelper.loginUser(url,usernameHttp,passwordHttp);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(httpRetCode == 200) {
                                    Intent intent = new Intent(getActivity(),WelcomeActivity.class);
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREF",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username",username.getText().toString());
                                    editor.apply();
                                    startActivity(intent);
                                }
                                else if(httpRetCode == 401){
                                    Toast toast = new Toast(getActivity());
                                    toast.setText("WRONG USERNAME OR PASSWORD");
                                    numberOfAttempts = jni.increment(numberOfAttempts);
                                    if(numberOfAttempts > 2){
                                        Button loginButton = (Button) view;
                                        loginButton.setEnabled(false);
                                    }
                                    toast.show();
                                }
                                else {
                                    Toast toast = new Toast(getActivity());
                                    toast.setText("ERROR WHILE CONNECTING TO SERVER");
                                    toast.show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        return v;
    }
}