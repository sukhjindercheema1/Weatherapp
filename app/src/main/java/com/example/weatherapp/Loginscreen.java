package com.example.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Loginscreen extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    FirebaseUser curUser;
    EditText edt_email,edt_pass;
    Button btn_log;
    TextView txt_reg;

    public Loginscreen() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On Create called");

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("On Start called");
        curUser = mAuth.getCurrentUser();

        if(curUser != null)
        {
            updateUI(curUser);
            Toast.makeText(getActivity().getApplicationContext(),"User Already Signing",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edt_email = view.findViewById(R.id.edt_username);
        edt_pass = view.findViewById(R.id.edt_lpass);
        btn_log = view.findViewById(R.id.btn_login);
        txt_reg = view.findViewById(R.id.txt_lreg);

        btn_log.setOnClickListener(this);
        txt_reg.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_login)
        {

            if (TextUtils.isEmpty(edt_email.getText().toString()))
            {

                edt_email.setError("Email cannot be blank!");
                edt_email.requestFocus();
                return;

            }else if (TextUtils.isEmpty(edt_pass.getText().toString())) {

                edt_pass.setError("Password cannot be blank!");
                edt_pass.requestFocus();
                return;

            }else {
                String email = edt_email.getText().toString();
                String pass = edt_pass.getText().toString();

                loginUser(email,pass);
            }


        }else if (id == R.id.txt_lreg)
        {
            NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);

            navController.navigate(R.id.register);
        }

    }

    public void loginUser(String email,String pass)
    {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    curUser = mAuth.getCurrentUser();
                    Toast.makeText(getActivity().getApplicationContext(),"Login Success!",Toast.LENGTH_LONG).show();
                    updateUI(curUser);

                }else
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Authentication Failed!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void updateUI(FirebaseUser user)
    {
        NavController navController = Navigation.findNavController(getActivity(),R.id.host_frag);
        Bundle b = new Bundle();
        b.putParcelable("user",user);
        navController.navigate(R.id.dashboard,b);

    }
}
