package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.krata.treninky_denik.Callbacks.GetPlayersCallBack;
import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;

import java.util.ArrayList;

public class SearchFrg extends Fragment implements View.OnClickListener {

    ListView lv;
    EditText etSearch;

    UserLocalStore userLocalStore;
    String text = "";

    ArrayList<String> players = new ArrayList<>();
    ArrayList<String> playersKnown = new ArrayList<>();

    ArrayAdapter<String> adapter;

    Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_searchplayer, container, false);
        Data.fragments.push(getClass().getName());
        lv = (ListView) v.findViewById(R.id.search_list);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, players);
        lv.setAdapter(adapter);

        btn = (Button)v.findViewById(R.id.btn_selectPlayer);
        btn.setOnClickListener(this);

        etSearch = (EditText) v.findViewById(R.id.search_textview);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = etSearch.getText().toString();
                ArrayList<Integer> indexes = new ArrayList<>();
                players = playersKnown;
                for(int i = 0; i < players.size(); i++)
                {
                    if (!players.get(i).startsWith(text))
                    {
                        indexes.add(i);
                    }
                }

                for(int i = 0; i < indexes.size(); i++)
                {
                    players.remove(indexes.get(i));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        getPlayers(lv);

        //getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();

        userLocalStore = new UserLocalStore(getContext());

        return v;
    }

    private void getPlayers(final ListView lv)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.fetchAllPlayersInBackground(new GetPlayersCallBack() {
            @Override
            public void done(ArrayList<String> nicks) {
                players = nicks;
                playersKnown = nicks;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btn_selectPlayer:
                Data.writeTo = etSearch.getText().toString();
                getFragmentManager().beginTransaction().replace(R.id.frg_container, new ChatFrg()).commit();
                break;
        }
    }


}

