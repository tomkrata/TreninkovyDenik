package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFrg extends Fragment implements View.OnClickListener {

    Button btnLogin, btnFace, btnInsta, btnGoogle;
    EditText et_userName, et_pass;
    TextView tw_register, head_nick, head_mail;
    UserLocalStore userLocalStore;
    CallbackManager callbackManager;

    LoginButton loginButton;


    private static final String EMAIL = "email";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_login, container, false);
        Data.fragments.push(getClass().getName());

        callbackManager = CallbackManager.Factory.create();

        et_userName = (EditText) v.findViewById(R.id.et_userName);
        et_pass = (EditText) v.findViewById(R.id.et_pass);

        tw_register = (TextView) v.findViewById(R.id.tw_registerLink);
        tw_register.setOnClickListener(this);

        btnLogin = (Button) v.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        btnFace = (Button) v.findViewById(R.id.btnFace);
        btnFace.setOnClickListener(this);


//        loginButton = (LoginButton)v.findViewById(R.id.facebook_login);
//        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
//        loginButton.setFragment(this);

        btnInsta = (Button) v.findViewById(R.id.btnInsta);
        btnInsta.setOnClickListener(this);

        btnGoogle = (Button) v.findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(this);
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
            case R.id.btnGoogle:
                break;
            case R.id.btnFace:
                break;
            case R.id.btnInsta:
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

