package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
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

public class ManageFrg extends Fragment implements View.OnClickListener{


    UserLocalStore userLocalStore;

    CardView set, write, cancel, view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_manager_test, container, false);
        Data.fragments.push(getClass().getName());

        //getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();

        userLocalStore = new UserLocalStore(getContext());

        set = (CardView)v.findViewById(R.id.card_plan);
        set.setOnClickListener(this);
        write = (CardView)v.findViewById(R.id.card_write);
        write.setOnClickListener(this);
        cancel = (CardView)v.findViewById(R.id.card_cancel);
        cancel.setOnClickListener(this);
        view = (CardView)v.findViewById(R.id.card_mytrains);
        view.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.card_plan:
                if (userLocalStore.isTrainer())
                    getFragmentManager().beginTransaction().replace(R.id.frg_container, new EditFrg()).commit();
                else
                    Toast.makeText(getContext(), "Nemůžete plánovat tréninky", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_write:
                if (userLocalStore.isTrainer())
                    getFragmentManager().beginTransaction().replace(R.id.frg_container, new WriteFrg()).commit();
                else
                    Toast.makeText(getContext(), "Nemůžete zapisovat tréninky", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_cancel:

                break;
            case R.id.card_mytrains:
                getFragmentManager().beginTransaction().replace(R.id.frg_container, new MyTrainsFrg()).commit();
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

