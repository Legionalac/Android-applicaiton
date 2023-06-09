package nikola.pavicevic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View v =inflater.inflate(R.layout.fragment_register, container, false);

        Button register = v.findViewById(R.id.buttonRegisterConfirmation);
        EditText username = v.findViewById(R.id.registerUsername);
        EditText email = v.findViewById(R.id.registerEmail);
        EditText password = v.findViewById(R.id.registerPassword);
        LoginDbHelper dbHelper = new LoginDbHelper(getActivity(),getString(R.string.DB_NAME),null,1);
        dbHelper.setContext(getActivity());

        HttpHelper httpHelper = new HttpHelper();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int usernameSize = username.getText().length();
                int emailSize = email.getText().length();
                int passwordSize = password.getText().length();
                String url = getString(R.string.ipAdress) + "users";
                if(usernameSize > 0 && emailSize > 0 && passwordSize > 0) {
                    boolean retCode = dbHelper.insert(username.getText().toString(),email.getText().toString(),password.getText().toString());
                    String usernameHttp = username.getText().toString();
                    String passwordHttp = password.getText().toString();
                    String emailHttp = email.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(retCode) {
                                int httlpRetCode =httpHelper.registerUser(url,usernameHttp,passwordHttp,emailHttp);
                                Log.d("httlp",String.valueOf(httlpRetCode));
                                if(httlpRetCode == 200){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Button loginButton = getActivity().findViewById(R.id.buttonLogin);
                                            loginButton.setVisibility(View.VISIBLE);
                                            Button registerButton = getActivity().findViewById(R.id.buttonRegister);
                                            registerButton.setVisibility(View.VISIBLE);
                                            getFragmentManager().popBackStack();
                                        }
                                    });

                                }
                                else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = new Toast(getActivity());
                                            toast.setText("ERROR WHILE CONNECTING TO SERVER");
                                            toast.show();
                                        }
                                    });

                                }

                            }
                        }
                    }).start();


                }
                else{

                    Toast toast = new Toast(getActivity());
                    toast.setText("FILL ALL FIELDS");
                    toast.show();
                }

            }
        });

        return v;
    }
}