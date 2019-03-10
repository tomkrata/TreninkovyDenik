package com.example.krata.treninky_denik.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;

import java.util.regex.Pattern;

public class RegisterFrg extends Fragment implements View.OnClickListener
{
    Button btnRegister;
    EditText et_userName, et_pass, et_passAgain, et_mail;
    RadioGroup rGroup;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_register, container, false);

        et_userName = (EditText) v.findViewById(R.id.et_userNameReg);
        et_mail = (EditText) v.findViewById(R.id.et_mailReg);
        et_pass = (EditText) v.findViewById(R.id.et_passReg);
        et_passAgain = (EditText) v.findViewById(R.id.et_passAgainReg);
        rGroup = (RadioGroup) v.findViewById(R.id.rBtnGroup_reg);

        btnRegister = (Button) v.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_register:
                String pass = et_pass.getText().toString();
                String passAgain = et_passAgain.getText().toString();
                String mail = et_mail.getText().toString();
                String nick = et_userName.getText().toString();
                if (nick.length() <= 0)
                {
                    Toast.makeText(getContext(), "Zadejte prosím přezdívku", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isEmailValid(mail))
                {
                    Toast.makeText(getContext(), "E-mail musí mít skutečný formát", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(passAgain) || pass.equals(""))
                {
                    Toast.makeText(getContext(), "Hesla se musí shodovat!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nick.contains(","))
                {
                    Toast.makeText(getContext(), "Znak \",\" nemůžete používat", Toast.LENGTH_SHORT).show();
                    return;
                }
                int selectedRBtn = rGroup.getCheckedRadioButtonId();
                boolean trainer = R.id.rBtn_player != selectedRBtn ? true : false;


                User user = new User(nick, mail, pass, trainer);

                registerUser(user);
                break;
        }
    }

    private void registerUser(User user)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                getFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();
            }
        });
    }

    private boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
