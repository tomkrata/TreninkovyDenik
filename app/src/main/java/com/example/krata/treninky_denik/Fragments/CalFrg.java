package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;

import java.util.Calendar;

public class CalFrg extends Fragment implements View.OnClickListener{

    CalendarView calView;
    EditText et_userName, et_pass;
    TextView tw_register, head_nick, head_mail;

    UserLocalStore userLocalStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_cal, container, false);

        et_userName = (EditText) v.findViewById(R.id.et_userName);
        et_pass = (EditText) v.findViewById(R.id.et_pass);

        tw_register = (TextView) v.findViewById(R.id.tw_registerLink);
        tw_register.setOnClickListener(this);

        calView = (CalendarView)v.findViewById(R.id.cal_view);
        //getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();

        userLocalStore = new UserLocalStore(getContext());

        return v;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_login:
                String nick = et_userName.getText().toString();
                String pass = et_pass.getText().toString();

                User user = new User(nick, pass);


                authenticate(user);
                break;
            case R.id.tw_registerLink:
                getFragmentManager().beginTransaction().replace(R.id.frg_container, new RegisterFrg()).commit();
                break;
        }
    }

    private void authenticate(User user)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null)
                    showErrorMessage();
                else
                    logUserIn(returnedUser);
            }
        });
    }

    private void showErrorMessage()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setMessage("Špatné přihlašovací údaje.");
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser)
    {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        startActivity(new Intent(getContext(), MainActivity.class));
        //getFragmentManager().beginTransaction().replace(R.id.frg_container, new NewsFrg()).commit();
    }

}

